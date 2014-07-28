package uk.ac.brighton.uni.ab607.mmorpg.common.object;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.almasb.common.net.DataPacket;
import com.almasb.common.net.UDPServer;
import com.almasb.common.search.AStarNode;
import com.almasb.java.io.Resources;

import uk.ac.brighton.uni.ab607.mmorpg.client.ui.animation.Animation;
import uk.ac.brighton.uni.ab607.mmorpg.client.ui.animation.TextAnimation;
import uk.ac.brighton.uni.ab607.mmorpg.client.ui.animation.TextAnimation.TextAnimationType;
import uk.ac.brighton.uni.ab607.mmorpg.common.GameCharacter;
import uk.ac.brighton.uni.ab607.mmorpg.common.Inventory;
import uk.ac.brighton.uni.ab607.mmorpg.common.Player;
import uk.ac.brighton.uni.ab607.mmorpg.common.item.Chest;
import uk.ac.brighton.uni.ab607.mmorpg.common.math.GameMath;

public class GameMap {

    public final String name;
    public final int spriteID;
    public final int width, height;
    public final List<String> data;
    private AStarNode[][] map;

    private ArrayList< ArrayList<Enemy> > enemies = new ArrayList< ArrayList<Enemy> >();
    private SpawnInfo[] spawnInfo;
    private int enemyRuntimeID = 1; // it will keep going up

    private int enemyNumbers = 0;

    private ArrayList<Player> players = new ArrayList<Player>();
    public ArrayList<Chest> chests = new ArrayList<Chest>();

    public ArrayList<Animation> animations = new ArrayList<Animation>();

    /*package-private*/ GameMap(String name, int spriteID, SpawnInfo... info) {
        this.name = name;
        this.spriteID = spriteID;

        data = Resources.getText(name);

        height = data.size();
        width = data.get(0).length();

        map = new AStarNode[width][height];

        for (int i = 0; i < data.size(); i++) {
            String line = data.get(i);
            for (int j = 0; j < line.length(); j++) {
                map[j][i] = new AStarNode(j, i, 0, line.charAt(j) == '1' ? 1 : 0);
            }
        }

        spawnInfo = info;

        for (SpawnInfo sp : spawnInfo) {
            ArrayList<Enemy> list = new ArrayList<Enemy>();
            for (int i = 0; i < sp.number; i++) {
                Enemy e = ObjectManager.getEnemyByID(sp.enemyID);
                Point p = getRandomFreePos();
                e.setX(p.x);
                e.setY(p.y);
                e.setRuntimeID(enemyRuntimeID++);
                list.add(e);
                enemyNumbers++;
            }
            enemies.add(list);
        }
    }

    /*package-private*/ GameMap(GameMap copy) {
        this(copy.name, copy.spriteID, copy.spawnInfo);
    }

    public void update(UDPServer server) {
        List<Player> tmpPlayers = new ArrayList<Player>(players);

        // process animations
        for (Iterator<Animation> it = animations.iterator(); it.hasNext(); ) {
            Animation a = it.next();
            a.update(0.02f);    // that's how much we sleep
            if (a.hasFinished())
                it.remove();
        }

        // process enemies
        for (int i = 0; i < enemies.size(); i++) {
            ArrayList<Enemy> enemyList = enemies.get(i);

            for (Iterator<Enemy> it = enemyList.iterator(); it.hasNext(); ) {
                Enemy e = it.next();
                if (e.isAlive()) {
                    e.update();
                }
                else {
                    it.remove();
                }
            }
        }

        // respawn monsters if needed
        for (int j = 0; j < spawnInfo.length; j++) {
            ArrayList<Enemy> list = enemies.get(j);
            for (int i = 0; i < spawnInfo[j].number - list.size(); i++) {
                Enemy e = ObjectManager.getEnemyByID(spawnInfo[j].enemyID);
                Point p = getRandomFreePos();
                e.setX(p.x);
                e.setY(p.y);
                e.setRuntimeID(enemyRuntimeID++);
                list.add(e);
            }
        }


        // process players
        for (Player p : tmpPlayers) {
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
                            animations.add(new TextAnimation(c.getX(), c.getY(), c.money + " G", TextAnimationType.FADE));
                        }
                    }
                }
            }
        }

        // all objects to send
        Player[] toSend = new Player[tmpPlayers.size()];
        for (int i = 0; i < tmpPlayers.size(); i++)
            toSend[i] = tmpPlayers.get(i);

        Chest[] chestsToSend = new Chest[chests.size()];
        for (int i = 0; i < chests.size(); i++)
            chestsToSend[i] = chests.get(i);

        Animation[] animsToSend = new Animation[animations.size()];
        for (int i = 0; i < animations.size(); i++)
            animsToSend[i] = animations.get(i);

        Enemy[] enemyToSend = new Enemy[enemyNumbers];
        int j = 0;
        for (ArrayList<Enemy> list : enemies) {
            for (int i = 0; i < list.size(); i++) {
                enemyToSend[j++] = list.get(i);
            }
        }

        for (Player p : tmpPlayers) {
            try {

                server.send(new DataPacket(chestsToSend), p.ip, p.port);
                server.send(new DataPacket(enemyToSend), p.ip, p.port);
                server.send(new DataPacket(animsToSend), p.ip, p.port);
                server.send(new DataPacket(toSend), p.ip, p.port);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public Enemy getEnemyByRuntimeID(int id) {
        for (ArrayList<Enemy> list : enemies) {
            for (Enemy e : list)
                if (e.getRuntimeID() == id)
                    return e;
        }

        return null;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    /**
     *
     * @return
     *          x, y coords of random unoccupied cell
     */
    public Point getRandomFreePos() {
        int x, y;
        do {
            x = GameMath.random(width) - 1;
            y = GameMath.random(height) - 1;
        }
        while(data.get(y).charAt(x) == '1');

        return new Point(x*40, y*40);
    }

    public AStarNode[][] getGrid() {
        return map;
    }

    public void addPlayer(Player p) {
        players.add(p);
    }

    public void removePlayer(Player p) {
        players.remove(p);
    }

    public Player getPlayerByName(String name) {
        for (Player p : players)
            if (p.name.equals(name))
                return p;

        return null;
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

    static class SpawnInfo {
        public String enemyID;
        public int number;
        public SpawnInfo(String id, int number) {
            this.enemyID = id;
            this.number = number;
        }
    }
}
