package uk.ac.brighton.uni.ab607.mmorpg.server;

import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import uk.ac.brighton.uni.ab607.libs.io.Resources;
import uk.ac.brighton.uni.ab607.libs.main.Out;
import uk.ac.brighton.uni.ab607.libs.net.*;
import uk.ac.brighton.uni.ab607.libs.search.AStarLogic;
import uk.ac.brighton.uni.ab607.libs.search.AStarNode;
import uk.ac.brighton.uni.ab607.mmorpg.client.ui.animation.Animation;
import uk.ac.brighton.uni.ab607.mmorpg.client.ui.animation.TextAnimation;
import uk.ac.brighton.uni.ab607.mmorpg.client.ui.animation.TextAnimation.TextAnimationType;
import uk.ac.brighton.uni.ab607.mmorpg.common.*;
import uk.ac.brighton.uni.ab607.mmorpg.common.ai.AgentBehaviour;
import uk.ac.brighton.uni.ab607.mmorpg.common.ai.AgentBehaviour.*;
import uk.ac.brighton.uni.ab607.mmorpg.common.ai.AgentGoalTarget;
import uk.ac.brighton.uni.ab607.mmorpg.common.ai.AgentRule;
import uk.ac.brighton.uni.ab607.mmorpg.common.item.Chest;
import uk.ac.brighton.uni.ab607.mmorpg.common.object.Enemy;
import uk.ac.brighton.uni.ab607.mmorpg.common.object.GameMap;
import uk.ac.brighton.uni.ab607.mmorpg.common.object.ID;
import uk.ac.brighton.uni.ab607.mmorpg.common.object.ObjectManager;

class Point implements java.io.Serializable, AgentGoalTarget {
    /**
     *
     */
    private static final long serialVersionUID = 5721555806534123308L;
    private int x, y;
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }
    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }
}

public class GameServer {
    private AStarLogic logic = new AStarLogic();
    private List<AStarNode> closed = new ArrayList<AStarNode>();
    private AStarNode n = null;

    private AStarNode targetNode;

    /*package-private*/ static final int ATK_INTERVAL = 50;
    /*package-private*/ static final int STARTING_X = 25*40;
    /*package-private*/ static final int STARTING_Y = 15*40;

    private int playerRuntimeID = 1000;

    private UDPServer server = null;

    //private ArrayList<Player> players = new ArrayList<Player>();

    private ArrayList<AgentRule> aiRules = new ArrayList<AgentRule>();
    private HashMap<Point, Float> locationFacts = new HashMap<Point, Float>();
    
    private ServerActionHandler actionHandler;
    
    private ArrayList<GameMap> maps = new ArrayList<GameMap>();
    
    private long start = 0, upTime;

    public GameServer() throws SocketException {
        actionHandler = new ServerActionHandler(this);
        
        // init world
        initGameMaps();
        initAI();

        // init server connection
        server = new UDPServer(55555, new ClientQueryParser());

        // start main server loop
        new Thread(new ServerLoop()).start();
        
        // test
        spawnChest(new Chest(1000, 680, 1000, 
                ObjectManager.getWeaponByID(ID.Weapon.IRON_SWORD),
                ObjectManager.getArmorByID(ID.Armor.CHAINMAL)), "map1.txt");
    }

    class ClientQueryParser extends ClientPacketParser {
        @Override
        public void parseClientPacket(DataPacket packet) {
            if (packet.stringData.startsWith("LOGIN_PLAYER")) {
                String name = packet.stringData.split(",")[1];  // exception check ?
                // get data from game account
                GameAccount acc = GameAccount.getAccountByUserName(name);
                if (acc != null) {
                    int x = acc.getX(), y = acc.getY();
                    
                    try {
                        server.send(new DataPacket("LOGIN_OK," + acc.getMapName() + "," + x + "," + y), packet.getIP(), packet.getPort());
                        //loginPlayer(acc.getMapName(), name, x, y, packet.getIP(), packet.getPort());
                        Player p = acc.getPlayer();
                        p.ip = packet.getIP();
                        p.port = packet.getPort();
                        
                        loginPlayer(acc.getMapName(), p);
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    // this is purely for debugging when using local connection
                    // in release launch at this point wrong user name passed
                    try {
                        // test
                        GameAccount.addAccount("Almas", "", "test@mail.com");    // created new account
                        
                        server.send(new DataPacket("LOGIN_OK," + "map1.txt" + "," + 1000 + "," + 600), packet.getIP(), packet.getPort());
                        loginPlayer("map1.txt", name, 1000, 600, packet.getIP(), packet.getPort());
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            if (packet.stringData.startsWith("CHECK_PLAYER")) {
                String[] data = new String(packet.byteData).split(",");

                String user = data[0];
                String pass = data[1];

                if (GameAccount.getAccountByUserName(user) == null) {
                    GameAccount.addAccount(user, pass, "test@mail.com");    // created new account
                    try {
                        server.send(new DataPacket("New Account Created"), packet.getIP(), packet.getPort());
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                    return;
                }

                String response = "";

                if (!playerNameExists(user) && GameAccount.validateLogin(user, pass)) {
                    response = "CHECK_PLAYER_GOOD";
                }
                else {
                    response = "CHECK_PLAYER_BAD";
                }

                try {
                    server.send(new DataPacket(response), packet.getIP(), packet.getPort());
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (packet.stringData.startsWith("CLOSE")) {
                closePlayerConnection(packet.stringData.split(",")[1]);
            }

            // handle action requests from clients
            if (packet.multipleObjectData instanceof ActionRequest[]) {
                actionHandler.process((ActionRequest[]) packet.multipleObjectData);
            }
        }

        /**
         *
         * @param name
         *              player name
         * @return
         *          true if player name exists on server, false otherwise
         */
        private boolean playerNameExists(String name) {
            /*for (Player p : players)
                if (p.name.equals(name))
                    return true;*/

            return false;
        }

        /**
         * No longer tracks the player but client
         * still receives updates at this point
         *
         * @param playerName
         *                  name of the player to disconnect
         */
        private void closePlayerConnection(String playerName) {
            /*for (Iterator<Player> iter = players.iterator(); iter.hasNext(); ) {
                if (iter.next().name.equals(playerName)) {
                    iter.remove();
                    break;
                }
            }*/
        }
    }

    /**
     *
     * @param name
     *              player name
     * @return
     *          player if name exists on the server, if not then null
     */
    /*package-private*/ Player getPlayerByName(String name) {
        Player p = null;
        for (GameMap m : maps) {
            p = m.getPlayerByName(name);
            if (p != null)
                return p;
        }
        
        return null;
    }

    /**
     *
     * @param id
     *           runtime ID of the character
     * @return
     *          character (player, enemy or NPC) associated with this ID
     *          or null if ID doesn't exist
     */
    /*package-private*/ GameCharacter getGameCharacterByRuntimeID(int id, String mapName) {
        /*for (Enemy e : enemies)
            if (e.getRuntimeID() == id)
                return e;
        return null;*/
        return getMapByName(mapName).getEnemyByRuntimeID(id);
    }

    class ServerLoop implements Runnable {
        @Override
        public void run() {
            //List<Player> tmpPlayers = new ArrayList<Player>();

            start = System.currentTimeMillis();
            
            while (true) {
                /*tmpPlayers = new ArrayList<Player>(players);

                // process animations
                for (Iterator<Animation> it = animations.iterator(); it.hasNext(); ) {
                    Animation a = it.next();
                    a.update(0.02f);    // that's how much we sleep
                    if (a.hasFinished())
                        it.remove();
                }

                // process AI
                for (Iterator<Enemy> it = enemies.iterator(); it.hasNext(); ) {
                    Enemy e = it.next();
                    if (e.isAlive()) {
                        AgentBehaviour ai = e.AI;
                        for (AgentRule rule : aiRules) {
                            if (rule.matches(ai.type, ai.currentGoal, ai.currentMode)) {
                                //rule.execute(e, ai.currentTarget);
                            }
                        }
                        e.update();
                    }
                    else {
                        it.remove();
                    }
                }

                // process players
                for (Player p : tmpPlayers) {
                    if (locationFacts.size() == 0) {
                        locationFacts.put(new Point(p.getX(), p.getY()), 0.1f);
                    }

                    p.update();

                    // player - chest interaction
                    for (Iterator<Chest> it = chests.iterator(); it.hasNext(); ) {
                        Chest c = it.next();
                        if (c.isOpened()) {
                            it.remove();
                        }
                        else {
                            if (distanceBetween(p, c) < 1) {
                                if (p.getInventory().getSize() + c.getItems().size()
                                        <= Inventory.MAX_SIZE) {
                                    c.open();
                                    c.getItems().forEach(p.getInventory()::addItem);
                                    p.incMoney(c.money);
                                    addAnimation(new TextAnimation(c.getX(), c.getY(), c.money + " G", TextAnimationType.FADE));
                                }
                            }
                        }
                    }
                }

                // fuzzy stuff
                Iterator<Entry<Point, Float>> iter = locationFacts.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry<Point, Float> pairs = (Map.Entry<Point, Float>)iter.next();
                    pairs.setValue((float) (pairs.getValue() - 0.01));
                    if (pairs.getValue() < 0)
                        iter.remove();
                }

                // all objects to send
                Player[] toSend = new Player[tmpPlayers.size()];
                for (int i = 0; i < tmpPlayers.size(); i++)
                    toSend[i] = tmpPlayers.get(i);

                Chest[] chestsToSend = new Chest[chests.size()];
                for (int i = 0; i < chests.size(); i++)
                    chestsToSend[i] = chests.get(i);

                Enemy[] eneToSend = new Enemy[enemies.size()];
                for (int i = 0; i < enemies.size(); i++)
                    eneToSend[i] = enemies.get(i);

                Animation[] animsToSend = new Animation[animations.size()];
                for (int i = 0; i < animations.size(); i++)
                    animsToSend[i] = animations.get(i);*/

                for (GameMap map : maps)
                    map.update(server);
                

                upTime = System.currentTimeMillis() - start;
                if (upTime > 10000) {
                    //saveState();
                    //System.exit(0);
                }
                
                try {

                    // add delay calculation
                    Thread.sleep(20);   // maybe even 10
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private AgentGoalTarget getLastKnownLocation() {
        return locationFacts.keySet().stream()
                .max((Point o1, Point o2) -> (int)(10*(locationFacts.get(o1)-locationFacts.get(o2))))
                .orElse(null);
    }

    private void processBasicAttack(GameCharacter agent, AgentGoalTarget target) {
        if (agent != null && target != null && agent instanceof GameCharacter && target instanceof GameCharacter) {
            GameCharacter chAgent = (GameCharacter)agent;
            GameCharacter chTarget = (GameCharacter)target;
            if (distanceBetween(chAgent, chTarget) > 2) {
                //moveObject(chAgent, chTarget.getX(), chTarget.getY());
            }
            else
                processBasicAttack((GameCharacter)agent, (GameCharacter)target);
        }
        else {
            if (agent != null && agent instanceof GameCharacter && target != null && target instanceof Point) {
                GameCharacter chAgent = (GameCharacter)agent;
                //moveObject(chAgent, target.getX(), target.getY());  // use that maybe for all?
            }
        }
    }

    private void processBasicAttack(GameCharacter attacker, GameCharacter target) {
        if (distanceBetween(attacker, target) > 2)
            return;


        if (++attacker.atkTime >= ATK_INTERVAL / (1 + attacker.getTotalStat(GameCharacter.ASPD)/100.0)) {
            int dmg = attacker.attack(target);
            //addAnimation(new TextAnimation(attacker.getX(), attacker.getY() + 80, dmg+"", TextAnimationType.DAMAGE_ENEMY));
            //addAnimation(new ImageAnimation());
            attacker.atkTime = 0;
        }
    }
    
    /*package-private*/ GameMap getMapByName(String name) {
        for (GameMap m : maps)
            if (m.name.equals(name))
                return m;
        
        return null;
    }

    /*package-private*/ void moveObject(GameCharacter ch, String mapName, int x, int y) {
        x /= 40; y /= 40;
        
        GameMap m = getMapByName(mapName);
        
        if (x < 0 || x >= m.width || y < 0 || y >= m.height)
            return;
        
        AStarNode[][] grid = m.getGrid();

        targetNode = grid[x][y];
        AStarNode startN = grid[ch.getX()/40][ch.getY() / 40];

        for (int i = 0; i < m.width; i++)
            for (int j = 0; j < m.height; j++)
                grid[i][j].setHCost(Math.abs(x - i) + Math.abs(y - j));


        ArrayList<AStarNode> busyNodes = new ArrayList<AStarNode>();
        // find "busy" nodes
        //for (Enemy e : enemies) {
            //busyNodes.add(new AStarNode(e.getX()/40, e.getY()/40, 0, 1));
        //}

        AStarNode[] busy = new AStarNode[busyNodes.size()];
        for (int i = 0; i < busyNodes.size(); i++)
            busy[i] = busyNodes.get(i);

        closed = logic.getPath(grid, startN, targetNode, busy);

        /*if (x < 0 || x >= mapWidth || y < 0 || y >= mapHeight)
            return;

        targetNode = map[x][y];
        AStarNode startN = map[ch.getX()/40][ch.getY() / 40];

        for (int i = 0; i < mapWidth; i++)
            for (int j = 0; j < mapHeight; j++)
                map[i][j].setHCost(Math.abs(x - i) + Math.abs(y - j));


        ArrayList<AStarNode> busyNodes = new ArrayList<AStarNode>();
        // find "busy" nodes
        for (Enemy e : enemies) {
            busyNodes.add(new AStarNode(e.getX()/40, e.getY()/40, 0, 1));
        }

        AStarNode[] busy = new AStarNode[busyNodes.size()];
        for (int i = 0; i < busyNodes.size(); i++)
            busy[i] = busyNodes.get(i);

        closed = logic.getPath(map, startN, targetNode, busy);*/

        if (closed.size() > 0) {
            n = closed.get(0);

            if (ch.getX() > n.getX() * 40)
                ch.xSpeed = -5;
            if (ch.getX() < n.getX() * 40)
                ch.xSpeed = 5;
            if (ch.getY() > n.getY() * 40)
                ch.ySpeed = -5;
            if (ch.getY() < n.getY() * 40)
                ch.ySpeed = 5;

            ch.move();

            ch.xSpeed = 0;
            ch.ySpeed = 0;
        }
    }

    private void loginPlayer(String mapName, String name, int x, int y, String ip, int port) {
        Player p = new Player(name, GameCharacterClass.NOVICE, x, y, ip ,port);
        p.setRuntimeID(playerRuntimeID++);
        GameMap m = getMapByName(mapName);
        m.addPlayer(p);
        Out.println(name + " has joined the game. RuntimeID: " + p.getRuntimeID()
                + " Map: " + m.name);
    }
    
    private void loginPlayer(String mapName, Player p) {
        p.setRuntimeID(playerRuntimeID++);
        GameMap m = getMapByName(mapName);
        m.addPlayer(p);
        Out.println(p.name + " has joined the game. RuntimeID: " + p.getRuntimeID()
                + " Map: " + m.name);
    }

    /**
     * Spawns an enemy with given ID at x, y
     * Also assigns runtimeID to that enemy
     *
     * @param id
     *           ID of enemy to spawn
     * @param x
     *          x coord
     * @param y
     *          y coord
     */
    /*private Enemy spawnEnemy(String id, int x, int y) {
        Enemy e = ObjectManager.getEnemyByID(id);
        e.setRuntimeID(runtimeID++);
        e.setX(x);
        e.setY(y);
        enemies.add(e);
        return e;
    }*/

    /*package-private*/ Chest spawnChest(Chest chest, String mapName) {
        //chests.add(chest);
        getMapByName(mapName).chests.add(chest);
        return chest;
    }
    
    /*package-private*/ void addAnimation(Animation a, String mapName) {
        //animations.add(a);
        getMapByName(mapName).animations.add(a);
    }

    private void initGameMaps() {
        /*List<String> lines = Resources.getText("map1.txt");

        mapHeight = lines.size();
        mapWidth = lines.get(0).length();

        map = new AStarNode[mapWidth][mapHeight];

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            for (int j = 0; j < line.length(); j++) {
                map[j][i] = new AStarNode(j, i, 0, line.charAt(j) == '1' ? 1 : 0);
            }
        }*/
        
        maps.add(ObjectManager.getMapByName("map1.txt"));
    }
    
    public void saveState() {
        Out.debug("Server::shutdown()");
        
        for (GameMap m : maps) {
            for (Player p : m.getPlayers()) {
                GameAccount acc = GameAccount.getAccountByUserName(p.name);
                acc.setPlayer(p);
                acc.setMapName(m.name);
                acc.setXY(p.getX(), p.getY());
            }
        }
        
        DBAccess.saveDB();
    }

    private void initAI() {
        AgentRule rule = new AgentRule(AgentType.GUARD, AgentGoal.GUARD_OBJECT, AgentMode.PATROL) {
            @Override
            public void execute(Enemy agent, AgentGoalTarget target) {
                if (target == null) // nothing to guard, just chill
                    return;

                aiPatrol(agent, target);

                /*List<Player> tmpPlayers = new ArrayList<Player>(players);
                for (Player p : tmpPlayers) {
                    if (distanceBetween(p, target) < 2) {   // if any player comes close
                        agent.AI.setGoal(AgentGoal.KILL_OBJECT);    // change state
                        agent.AI.setTarget(p);
                        break;
                    }
                }*/
            }
        };

        AgentRule rule2 = new AgentRule(AgentType.GUARD, AgentGoal.KILL_OBJECT, AgentMode.PATROL) {
            @Override
            public void execute(Enemy agent, AgentGoalTarget target) {
                if (target == null)
                    return;

                if (agent.canSee(target)) {
                    processBasicAttack(agent, target);
                }

            }
        };

        AgentRule rule3 = new AgentRule(AgentType.SCOUT, AgentGoal.FIND_OBJECT, AgentMode.PASSIVE) {
            @Override
            public void execute(Enemy agent, AgentGoalTarget target) {
                AgentGoalTarget t = getLastKnownLocation();
                //if (t != null && !agent.canSee(t))
                   // moveObject(agent, t.getX(), t.getY());


                /*List<Player> tmpPlayers = new ArrayList<Player>(players);
                for (Player p : tmpPlayers) {
                    if (agent.canSee(p)) {
                        locationFacts.put(new Point(p.getX(), p.getY()), 1.0f);
                        if (p.getHP() / p.getTotalStat(Stat.MAX_HP) < 0.05) {
                            agent.AI.setGoal(AgentGoal.KILL_OBJECT);
                            agent.AI.setMode(AgentMode.AGGRESSIVE);
                            agent.AI.setTarget(p);
                        }
                        break;
                    }
                }*/
            }
        };

        AgentRule rule4 = new AgentRule(AgentType.SCOUT, AgentGoal.KILL_OBJECT, AgentMode.AGGRESSIVE) {
            @Override
            public void execute(Enemy agent, AgentGoalTarget target) {
                if (target == null)
                    return;

                processBasicAttack(agent, target);
            }
        };

        AgentRule rule5 = new AgentRule(AgentType.ASSASSIN, AgentGoal.KILL_OBJECT, AgentMode.AGGRESSIVE) {
            @Override
            public void execute(Enemy agent, AgentGoalTarget target) {
                if (target == null)
                    agent.AI.currentTarget = getLastKnownLocation();


                /*List<Player> tmpPlayers = new ArrayList<Player>(players);
                for (Player p : tmpPlayers) {
                    if (agent.canSee(p)) {
                        locationFacts.put(new Point(p.getX(), p.getY()), 1.0f);
                        target = p;
                        break;
                    }
                }*/

                if (target != null)
                    processBasicAttack(agent, target);

                if (target != null && distanceBetween(agent, target) < 1)
                    agent.AI.currentTarget = getLastKnownLocation();
            }
        };

        aiRules.add(rule);
        aiRules.add(rule2);
        aiRules.add(rule3);
        aiRules.add(rule4);
        aiRules.add(rule5);
    }

    private void aiPatrol(Enemy agent, AgentGoalTarget target) {
        if (distanceBetween(agent, target) > 3) {
            //moveObject(agent, target.getX(), target.getY());
        }
    }

    /**
     *
     * @param ch1
     *              character 1
     * @param ch2
     *              character 2
     * @return
     *          distance between 2 characters in number of cells
     */
    /*package-private*/ int distanceBetween(GameCharacter ch1, GameCharacter ch2) {
        return (Math.abs(ch1.getX() - ch2.getX()) + Math.abs(ch1.getY() - ch2.getY())) / 40;
    }

    /*package-private*/ int distanceBetween(GameCharacter ch, Chest c) {
        return (Math.abs(ch.getX() - c.getX()) + Math.abs(ch.getY() - c.getY())) / 40;
    }

    /*package-private*/ int distanceBetween(GameCharacter ch, AgentGoalTarget c) {
        return (Math.abs(ch.getX() - c.getX()) + Math.abs(ch.getY() - c.getY())) / 40;
    }
}
