package uk.ac.brighton.uni.ab607.mmorpg.server;

import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import uk.ac.brighton.uni.ab607.libs.main.Out;
import uk.ac.brighton.uni.ab607.libs.net.*;
import uk.ac.brighton.uni.ab607.libs.search.AStarLogic;
import uk.ac.brighton.uni.ab607.libs.search.AStarNode;
import uk.ac.brighton.uni.ab607.mmorpg.client.ui.animation.Animation;
import uk.ac.brighton.uni.ab607.mmorpg.common.*;
import uk.ac.brighton.uni.ab607.mmorpg.common.item.Chest;
import uk.ac.brighton.uni.ab607.mmorpg.common.object.GameMap;
import uk.ac.brighton.uni.ab607.mmorpg.common.object.ID;
import uk.ac.brighton.uni.ab607.mmorpg.common.object.ObjectManager;

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
    
    private ServerActionHandler actionHandler;
    
    private ArrayList<GameMap> maps = new ArrayList<GameMap>();
    
    private long start = 0, upTime;

    public GameServer() throws SocketException {
        actionHandler = new ServerActionHandler(this);
        
        // init world
        initGameMaps();

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
        return getMapByName(mapName).getEnemyByRuntimeID(id);
    }

    class ServerLoop implements Runnable {
        @Override
        public void run() {

            start = System.currentTimeMillis();
            
            while (true) {
                

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

}
