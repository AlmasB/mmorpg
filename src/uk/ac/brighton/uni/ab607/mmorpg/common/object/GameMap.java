package uk.ac.brighton.uni.ab607.mmorpg.common.object;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import uk.ac.brighton.uni.ab607.mmorpg.common.GameCharacter;
import uk.ac.brighton.uni.ab607.mmorpg.common.Player;
import uk.ac.brighton.uni.ab607.mmorpg.common.Sys;
import uk.ac.brighton.uni.ab607.mmorpg.common.math.GameMath;
import uk.ac.brighton.uni.ab607.mmorpg.common.request.AnimationMessage;
import uk.ac.brighton.uni.ab607.mmorpg.common.request.ImageAnimationMessage;
import uk.ac.brighton.uni.ab607.mmorpg.common.request.MessageType;
import uk.ac.brighton.uni.ab607.mmorpg.common.request.TextAnimationMessage;

import com.almasb.common.graphics.Point2D;
import com.almasb.common.graphics.Rect2D;
import com.almasb.common.net.DataPacket;
import com.almasb.common.net.UDPServer;
import com.almasb.common.search.AStarNode;
import com.almasb.common.util.Out;
import com.almasb.java.io.ResourceManager;

public class GameMap {

    public final String name;
    public final int spriteID;
    public final int width, height;
    public final List<String> data;
    private AStarNode[][] map;

    private ArrayList< ArrayList<Enemy> > enemies = new ArrayList< ArrayList<Enemy> >();
    private SpawnInfo[] spawnInfo;
    private int enemyRuntimeID = 1; // it will keep going up

    private ArrayList<Player> players = new ArrayList<Player>();

    public ArrayList<TextAnimationMessage> animationsText = new ArrayList<TextAnimationMessage>();
    public ArrayList<ImageAnimationMessage> animationsImage = new ArrayList<ImageAnimationMessage>();

    private int tick = 0;

    /*package-private*/ GameMap(String name, int spriteID, SpawnInfo... info) {
        this.name = name;
        this.spriteID = spriteID;

        data = ResourceManager.loadText(name);

        if (data == null)
            Sys.logExceptionAndExit(new Exception("no map data"));

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
                Point2D p = getRandomFreePos();
                e.setX((int) p.getX());
                e.setY((int) p.getY());
                e.setRuntimeID(enemyRuntimeID++);
                list.add(e);
            }
            enemies.add(list);
        }
    }

    /*package-private*/ GameMap(GameMap copy) {
        this(copy.name, copy.spriteID, copy.spawnInfo);
    }

    public void update(UDPServer server) {
        List<Player> tmpPlayers = new ArrayList<Player>(players);

        // clean animations
        for (Iterator<TextAnimationMessage> it = animationsText.iterator(); it.hasNext(); ) {
            if (it.next().isSent())
                it.remove();
        }
        for (Iterator<ImageAnimationMessage> it = animationsImage.iterator(); it.hasNext(); ) {
            if (it.next().isSent())
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
                Point2D p = getRandomFreePos();
                e.setX((int) p.getX());
                e.setY((int) p.getY());
                e.setRuntimeID(enemyRuntimeID++);
                list.add(e);
            }
        }


        // process players
        for (Player p : tmpPlayers) {
            p.update();
        }

        // all objects to send
        ArrayList<Enemy> tmpList = new ArrayList<Enemy>();
        enemies.forEach(list -> list.forEach(enemy -> tmpList.add(enemy)));


        Stream<Player> playerStream = tmpPlayers.stream();
        Stream<TextAnimationMessage> animationStream = animationsText.stream();
        Stream<ImageAnimationMessage> animationStream2 = animationsImage.stream();
        Stream<Enemy> enemyStream = tmpList.stream();

        tmpPlayers.forEach(player -> {
            Rect2D playerVision = new Rect2D(player.getX() - 640, player.getY() - 360, 1280, 720);

            Player[] playersToSend = playerStream.filter(p -> playerVision.contains(new Point2D(p.getX(), p.getY()))).toArray(Player[]::new);
            TextAnimationMessage[] animationsToSend = animationStream.filter(anim -> playerVision.contains(new Point2D(anim.getX(), anim.getY()))).toArray(TextAnimationMessage[]::new);
            ImageAnimationMessage[] animationsToSend2 = animationStream2.filter(anim -> playerVision.contains(new Point2D(anim.getX(), anim.getY()))).toArray(ImageAnimationMessage[]::new);
            Enemy[] enemiesToSend = enemyStream.filter(enemy -> playerVision.contains(new Point2D(enemy.getX(), enemy.getY()))).toArray(Enemy[]::new);

            try {
                // send THE player
                if (tick == 0)
                    server.send(new DataPacket(player), player.ip, player.port);

                // send players / enemies
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                if (playersToSend.length > 0 || enemiesToSend.length > 0) {
                    baos.write((byte)MessageType.UPDATE_GAME_CHAR.ordinal());
                    for (int i = 0; i < playersToSend.length; i++) {
                        baos.write(playersToSend[i].toByteArray());
                    }
                    for (int i = 0; i < enemiesToSend.length; i++) {
                        baos.write(enemiesToSend[i].toByteArray());
                    }
                }

                server.sendRawBytes(baos.toByteArray(), player.ip, player.port);

                // send animations
                baos = new ByteArrayOutputStream();
                if (animationsToSend.length > 0) {
                    baos.write((byte)MessageType.ANIMATION_TEXT.ordinal());

                    for (int i = 0; i < animationsToSend.length; i++) {
                        baos.write(animationsToSend[i].toByteArray());
                        animationsToSend[i].setSent();
                    }
                }

                server.sendRawBytes(baos.toByteArray(), player.ip, player.port);

                baos = new ByteArrayOutputStream();
                if (animationsToSend2.length > 0) {
                    baos.write((byte)MessageType.ANIMATION_IMAGE.ordinal());

                    for (int i = 0; i < animationsToSend2.length; i++) {
                        baos.write(animationsToSend2[i].toByteArray());
                        animationsToSend2[i].setSent();
                    }
                }

                server.sendRawBytes(baos.toByteArray(), player.ip, player.port);

                //                tick++;
                //
                //                if (tick == 50)
                //                    tick = 0;
            }
            catch (Exception e) {
                Out.e("update", "Failed to send a packet", this, e);
            }
        });
    }

    public Enemy getEnemyByRuntimeID(int id) {
        for (ArrayList<Enemy> list : enemies) {
            for (Enemy e : list)
                if (e.getRuntimeID() == id)
                    return e;
        }

        return null;
    }

    public Enemy getEnemyByXY(int x, int y) {
        for (ArrayList<Enemy> list : enemies) {
            for (Enemy e : list)
                if (e.getX() == x && e.getY() == y)
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
    public Point2D getRandomFreePos() {
        int x, y;
        do {
            x = GameMath.random(width) - 1;
            y = GameMath.random(height) - 1;
        }
        while(data.get(y).charAt(x) == '1');

        return new Point2D(x*40, y*40);
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

    public Player getPlayerByRuntimeID(int runtimeID) {
        for (Player p : players)
            if (p.getRuntimeID() == runtimeID)
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

    static class SpawnInfo {
        public String enemyID;
        public int number;
        public SpawnInfo(String id, int number) {
            this.enemyID = id;
            this.number = number;
        }
    }
}
