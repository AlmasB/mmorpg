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
import uk.ac.brighton.uni.ab607.mmorpg.client.ui.Animation;
import uk.ac.brighton.uni.ab607.mmorpg.common.*;
import uk.ac.brighton.uni.ab607.mmorpg.common.StatusEffect.Status;
import uk.ac.brighton.uni.ab607.mmorpg.common.ai.AgentBehaviour;
import uk.ac.brighton.uni.ab607.mmorpg.common.ai.AgentBehaviour.*;
import uk.ac.brighton.uni.ab607.mmorpg.common.ai.AgentGoalTarget;
import uk.ac.brighton.uni.ab607.mmorpg.common.ai.AgentRule;
import uk.ac.brighton.uni.ab607.mmorpg.common.item.Chest;
import uk.ac.brighton.uni.ab607.mmorpg.common.item.EquippableItem;
import uk.ac.brighton.uni.ab607.mmorpg.common.item.GameItem;
import uk.ac.brighton.uni.ab607.mmorpg.common.item.UsableItem;
import uk.ac.brighton.uni.ab607.mmorpg.common.object.Armor;
import uk.ac.brighton.uni.ab607.mmorpg.common.object.Enemy;
import uk.ac.brighton.uni.ab607.mmorpg.common.object.ID;
import uk.ac.brighton.uni.ab607.mmorpg.common.object.ObjectManager;
import uk.ac.brighton.uni.ab607.mmorpg.common.object.Skill;
import uk.ac.brighton.uni.ab607.mmorpg.common.object.Weapon;

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

    private int mapWidth;
    private int mapHeight;

    private AStarLogic logic = new AStarLogic();
    private AStarNode[][] map;
    private List<AStarNode> closed = new ArrayList<AStarNode>();


    private int index = 0;
    private AStarNode n = null;
    private AStarNode parent = null;

    private AStarNode targetNode;
    private AStarNode playerParent;

    /*public enum Command {
        ATTR_UP, EQUIP, UNEQUIP, REFINE;

        @Override
        public String toString() {
            return this.name();
        }
    }*/

    public static final String ATTR_UP = "ATTR_UP",
            SKILL_UP = "SKILL_UP",
            EQUIP = "EQUIP",
            UNEQUIP = "UNEQUIP",
            REFINE = "REFINE",
            USE_ITEM = "USE_ITEM",
            ATTACK = "ATTACK",
            SKILL_USE = "SKILL_USE",
            CHAT = "CHAT",
            MOVE = "MOVE";

    private static final int ATK_INTERVAL = 50;
    private static final int ENEMY_SIGHT = 320;

    private static final int STARTING_X = 25*40;
    private static final int STARTING_Y = 15*40;

    private int runtimeID = 1;

    private UDPServer server = null;

    private ArrayList<Player> players = new ArrayList<Player>();
    private ArrayList<Chest> chests = new ArrayList<Chest>();
    private ArrayList<Enemy> enemies = new ArrayList<Enemy>();

    private ArrayList<Animation> animations = new ArrayList<Animation>();

    private ArrayList<AgentRule> aiRules = new ArrayList<AgentRule>();
    private HashMap<Point, Float> locationFacts = new HashMap<Point, Float>();

    public GameServer() {

        initGameMap();
        initGameObjects();
        initAI();

        // init server connection
        try {
            server = new UDPServer(55555, new ClientQueryParser());
        }
        catch (SocketException e) {
            e.printStackTrace();
        }

        // start main server loop
        new Thread(new ServerLoop()).start();
    }

    class ClientQueryParser extends ClientPacketParser {
        @Override
        public void parseClientPacket(DataPacket packet) {
            if (packet.stringData.startsWith("CREATE_PLAYER")) {
                String name = packet.stringData.split(",")[1];
                addNewPlayer(name, STARTING_X, STARTING_Y);
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

            if (packet.multipleObjectData instanceof String[]) {
                try {
                    parseActions((String[]) packet.multipleObjectData);
                }
                catch (BadActionRequestException e) {
                    Out.err(e);
                }
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
            for (Player p : players)
                if (p.name.equals(name))
                    return true;

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
            for (Iterator<Player> iter = players.iterator(); iter.hasNext(); ) {
                if (iter.next().name.equals(playerName)) {
                    iter.remove();
                    break;
                }
            }
        }
    }

    /**
     * Parses any actions requested by game client
     *
     * General format for "action" string
     *
     * "ACTION_NAME,PLAYER_NAME,VALUES..."
     *
     * @param actions
     *                  an array containing all actions from 1 client
     * @throws BadActionRequestException
     */
    private void parseActions(String[] actions) throws BadActionRequestException {
        for (String action : actions) {
            String[] tokens = action.split(",");

            String cmd = tokens[0];
            Player player = getPlayerByName(tokens[1]);
            if (player == null)
                throw new BadActionRequestException("No player name found: " + tokens[1]);

            int value = 0;
            try {
                value = Integer.parseInt(tokens[2]);
            }
            catch (NumberFormatException e) {
                throw new BadActionRequestException("Bad value: " + tokens[2]);
            }

            int value2 = 0; // for skill use, specifies runtimeID of the target
            // also for movement value is x, value2 is y
            if (tokens.length == 4) {
                try {
                    value2 = Integer.parseInt(tokens[3]);
                }
                catch (NumberFormatException e) {
                    throw new BadActionRequestException("Bad value: " + tokens[2]);
                }
            }

            // if tokens.length == 5 chat

            switch (cmd) {
                case ATTR_UP:
                    player.increaseAttr(value);
                    break;
                case SKILL_UP:
                    player.increaseSkillLevel(value);
                    break;
                case EQUIP:
                    GameItem item = player.getInventory().getItem(value);
                    if (item != null) {
                        if (item instanceof Weapon) {
                            player.equipWeapon((Weapon) item);
                        }
                        else if (item instanceof Armor) {
                            player.equipArmor((Armor) item);
                        }
                        else
                            throw new BadActionRequestException("Item not equippable: " + value);
                    }
                    else
                        throw new BadActionRequestException("Item not found: " + value);
                    break;
                case UNEQUIP:
                    player.unEquipItem(value);
                    break;
                case REFINE:
                    GameItem itemToRefine = player.getInventory().getItem(value);

                    if (itemToRefine != null) {
                        if (itemToRefine instanceof EquippableItem) {
                            ((EquippableItem) itemToRefine).refine();
                        }
                        else
                            throw new BadActionRequestException("Item cannot be refined: " + value);
                    }
                    else
                        throw new BadActionRequestException("Item not found: " + value);
                    break;
                case MOVE:
                    moveObject(player, value, value2);
                    break;
                case ATTACK:
                    if (player.hasStatusEffect(Status.STUNNED))
                        break;

                    // at this stage client can only target enemies
                    // when players are added this check will go
                    GameCharacter tmpChar = getGameCharacterByRuntimeID(value);
                    if (tmpChar instanceof Enemy) {
                        Enemy target = (Enemy) tmpChar;
                        if (target != null && target.isAlive()
                                && distanceBetween(player, (GameCharacter)target) <= ((Weapon)player.getEquip(Player.RIGHT_HAND)).range) {

                            if (++player.atkTime >= ATK_INTERVAL / (1 + player.getTotalStat(GameCharacter.ASPD)/100.0)) {
                                int dmg = player.attack(target);
                                animations.add(new Animation(player.getX(), player.getY(), 0.5f, 0, 25, dmg+""));
                                player.atkTime = 0;
                                if (target.getHP() <= 0) {
                                    player.gainBaseExperience(target.experience);
                                    player.gainJobExperience(target.experience);
                                    player.gainStatExperience(target.experience);
                                    spawnChest(target.onDeath());

                                }
                            }

                            if (++target.atkTime >= ATK_INTERVAL / (1 + target.getTotalStat(GameCharacter.ASPD)/100.0)
                                    && !target.hasStatusEffect(Status.STUNNED)) {
                                int dmg = target.attack(player);
                                animations.add(new Animation(player.getX(), player.getY() + 80, 0.5f, 0, 25, dmg+""));
                                target.atkTime = 0;
                                if (player.getHP() <= 0) {
                                }
                            }
                        }
                    }
                    break;
                case SKILL_USE:
                    Enemy skTarget = (Enemy) getGameCharacterByRuntimeID(value2);
                    if (skTarget != null) {
                        value--;    // bring 1..9 to 0..8
                        player.useSkill(value, skTarget);

                        if (skTarget.getHP() <= 0) {
                            player.gainBaseExperience(skTarget.experience);
                            player.gainJobExperience(skTarget.experience);
                            player.gainStatExperience(skTarget.experience);
                            chests.add(skTarget.onDeath());
                        }
                    }
                    break;
                case USE_ITEM:
                    UsableItem itemToUse = (UsableItem) player.getInventory().getItem(value);
                    itemToUse.onUse(player);
                    break;
                case CHAT:
                    animations.add(new Animation(player.getX(), player.getY(), 2.0f, 0, 0, tokens[4]));
                    break;
                default:
                    throw new BadActionRequestException("No such command: " + tokens[0]);
            }
        }
    }

    /**
     *
     * @param name
     *              player name
     * @return
     *          player if name exists on the server, if not then null
     */
    private Player getPlayerByName(String name) {
        for (Player p : players)
            if (p.name.equals(name))
                return p;

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
    private GameCharacter getGameCharacterByRuntimeID(int id) {
        for (Enemy e : enemies)
            if (e.getRuntimeID() == id)
                return e;
        return null;
    }

    class ServerLoop implements Runnable {
        @Override
        public void run() {
            List<Player> tmpPlayers = new ArrayList<Player>();

            while (true) {
                tmpPlayers = new ArrayList<Player>(players);

                for (Iterator<Animation> it = animations.iterator(); it.hasNext(); ) {
                    Animation a = it.next();
                    a.duration -= 20.0f / 1000.0f;
                    if (a.duration <= 0)
                        it.remove();
                }

                // process AI
                for (Enemy e : enemies) {
                    AgentBehaviour ai = e.AI;
                    for (AgentRule rule : aiRules) {

                        if (rule.matches(ai.type, ai.currentGoal, ai.currentMode)) {
                            // disable AI
                            //rule.execute(e, ai.currentTarget);
                        }
                    }
                    e.update();
                }

                // process players
                for (Player p : tmpPlayers) {
                    if (locationFacts.size() == 0) {
                        locationFacts.put(new Point(p.getX(), p.getY()), 0.1f);
                    }

                    p.update();

                    // player - chest interaction
                    for (Chest c : chests) {
                        if (distanceBetween(p, c) < 1) {
                            c.open();
                            for (GameItem item : c.getItems())
                                p.getInventory().addItem(item);
                            p.incMoney(c.money);
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

                // clean chests, can be optimized by enclosing within player loop
                for (Iterator<Chest> it = chests.iterator(); it.hasNext(); ) {
                    if (it.next().isOpened()) {
                        it.remove();
                    }
                }

                // clean enemies
                for (Iterator<Enemy> it = enemies.iterator(); it.hasNext(); ) {
                    if (!it.next().isAlive()) {
                        it.remove();
                    }
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
                    animsToSend[i] = animations.get(i);


                try {
                    server.send(new DataPacket(toSend));
                    server.send(new DataPacket(chestsToSend));
                    server.send(new DataPacket(eneToSend));
                    server.send(new DataPacket(animsToSend));

                    Thread.sleep(20);   // maybe even 10
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private AgentGoalTarget getLastKnownLocation() {
        AgentGoalTarget targ = null;
        float max = 0;
        for (Point p : locationFacts.keySet()) {
            if (locationFacts.get(p) > max) {
                max = locationFacts.get(p);
                targ = p;
            }
        }
        return targ;
    }

    private void processBasicAttack(GameCharacter agent, AgentGoalTarget target) {
        if (agent != null && target != null && agent instanceof GameCharacter && target instanceof GameCharacter) {
            GameCharacter chAgent = (GameCharacter)agent;
            GameCharacter chTarget = (GameCharacter)target;
            if (distanceBetween(chAgent, chTarget) > 2)
                moveObject(chAgent, chTarget.getX(), chTarget.getY());
            else
                processBasicAttack((GameCharacter)agent, (GameCharacter)target);
        }
        else {
            if (agent != null && agent instanceof GameCharacter && target != null && target instanceof Point) {
                GameCharacter chAgent = (GameCharacter)agent;
                moveObject(chAgent, target.getX(), target.getY());  // use that maybe for all?
            }
        }
    }

    private void processBasicAttack(GameCharacter attacker, GameCharacter target) {
        if (distanceBetween(attacker, target) > 2)
            return;


        if (++attacker.atkTime >= ATK_INTERVAL / (1 + attacker.getTotalStat(GameCharacter.ASPD)/100.0)) {
            int dmg = attacker.attack(target);
            animations.add(new Animation(attacker.getX(), attacker.getY() + 80, 0.5f, 0, 25, dmg+""));
            attacker.atkTime = 0;
        }
    }

    /*public AStarNode getNext() {
        if (closed.size() == 0) return playerParent;

        if (index >= closed.size())
            index = closed.size() - 1;

        return closed.get(index++);
    }*/

    private void moveObject(GameCharacter ch, int x, int y) {
        x /= 40; y /= 40;

        if (x < 0 || x >= mapWidth || y < 0 || y >= mapHeight)
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

        closed = logic.getPath(map, startN, targetNode, busy);
        index = 0;

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

        /*if (move) {
        n = getNext();
        parent = playerParent;
        }

        if (n != parent) {
            move = false;

            if (player.getX() > n.getX() * 40)
                player.xSpeed = -10;
            if (player.getX() < n.getX() * 40)
                player.xSpeed = 10;
            if (player.getY() > n.getY() * 40)
                player.ySpeed = -10;
            if (player.getY() < n.getY() * 40)
                player.ySpeed = 10;

            // determine whether parent has changed

            if (player.getX() == n.getX()*40 && player.getY() == n.getY() *40) {
                playerParent = n;
                move = true;

                if (target != null && target == playerParent) {
                    target = null;
                }
            }

            renderX = player.getX() - 640;  // half of width
            renderY = player.getY() - 360;  // half of height
        }*/
    }

    private void addNewPlayer(String name, int x, int y) {
        Player p = new Player(name, GameCharacterClass.NOVICE, x, y);
        p.setRuntimeID(runtimeID++);
        players.add(p);
        Out.println(name + " has joined the game. RuntimeID: " + p.getRuntimeID());
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
    private void spawnEnemy(String id, int x, int y) {
        Enemy e = ObjectManager.getEnemyByID(id);
        e.setRuntimeID(runtimeID++);
        e.setX(x);
        e.setY(y);
        enemies.add(e);
    }

    private void spawnChest(Chest chest) {
        chests.add(chest);
    }

    private void initGameMap() {
        List<String> lines = Resources.getText("map1.txt");

        mapHeight = lines.size();
        mapWidth = lines.get(0).length();

        map = new AStarNode[mapWidth][mapHeight];

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            for (int j = 0; j < line.length(); j++) {
                map[j][i] = new AStarNode(j, i, 0, line.charAt(j) == '1' ? 1 : 0);
            }
        }
    }

    private void initGameObjects() {
        spawnChest(new Chest(25*40, 16*40, 1000, ObjectManager.getWeaponByID(ID.Weapon.GUT_RIPPER), ObjectManager.getWeaponByID(ID.Weapon.SOUL_REAPER)));
        spawnChest(new Chest(0, 80, 2033, ObjectManager.getArmorByID(ID.Armor.THANATOS_BODY_ARMOR), ObjectManager.getArmorByID(ID.Armor.DOMOVOI)));

        spawnEnemy("2001", 640, 160);
        spawnEnemy("2000", 720, 720);
        spawnEnemy("2000", 40, 40);
        spawnEnemy("2001", 40, 120);
        spawnEnemy("2001", 400, 120);
        spawnEnemy("2001", 320, 160);
        spawnEnemy("2001", 40, 360);
        spawnEnemy("2001", 600, 120);

        spawnEnemy(ID.Enemy.MINOR_WATER_SPIRIT, 360, 40);
    }

    private void initAI() {
        AgentRule rule = new AgentRule(AgentType.GUARD, AgentGoal.GUARD_OBJECT, AgentMode.PATROL) {
            @Override
            public void execute(Enemy agent, AgentGoalTarget target) {
                if (target == null) // nothing to guard, just chill
                    return;

                aiPatrol(agent, target);

                List<Player> tmpPlayers = new ArrayList<Player>(players);
                for (Player p : tmpPlayers) {
                    if (distanceBetween(p, target) < 2) {   // if any player comes close
                        agent.AI.setGoal(AgentGoal.KILL_OBJECT);    // change state
                        agent.AI.setTarget(p);
                        break;
                    }
                }
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
                if (t != null && !agent.canSee(t))
                    moveObject(agent, t.getX(), t.getY());


                List<Player> tmpPlayers = new ArrayList<Player>(players);
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
                }
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


                List<Player> tmpPlayers = new ArrayList<Player>(players);
                for (Player p : tmpPlayers) {
                    if (agent.canSee(p)) {
                        locationFacts.put(new Point(p.getX(), p.getY()), 1.0f);
                        target = p;
                        break;
                    }
                }

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
    private int distanceBetween(GameCharacter ch1, GameCharacter ch2) {
        return (Math.abs(ch1.getX() - ch2.getX()) + Math.abs(ch1.getY() - ch2.getY())) / 40;
    }

    private int distanceBetween(GameCharacter ch, Chest c) {
        return (Math.abs(ch.getX() - c.getX()) + Math.abs(ch.getY() - c.getY())) / 40;
    }

    private int distanceBetween(GameCharacter ch, AgentGoalTarget c) {
        return (Math.abs(ch.getX() - c.getX()) + Math.abs(ch.getY() - c.getY())) / 40;
    }
}
