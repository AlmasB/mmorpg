package uk.ac.brighton.uni.ab607.mmorpg.client;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

//import javax.swing.JTextField;






import uk.ac.brighton.uni.ab607.libs.io.Resources;
import uk.ac.brighton.uni.ab607.libs.main.Out;
import uk.ac.brighton.uni.ab607.libs.net.DataPacket;
import uk.ac.brighton.uni.ab607.libs.net.ServerPacketParser;
import uk.ac.brighton.uni.ab607.libs.net.UDPClient;
import uk.ac.brighton.uni.ab607.libs.ui.DoubleBufferWindow;
import uk.ac.brighton.uni.ab607.libs.search.*;
import uk.ac.brighton.uni.ab607.mmorpg.client.ui.Animation;
import uk.ac.brighton.uni.ab607.mmorpg.common.*;
import uk.ac.brighton.uni.ab607.mmorpg.common.item.Chest;

@SuppressWarnings("serial")
public class GUI extends DoubleBufferWindow {

    private int mapWidth;
    private int mapHeight;

    private AStarLogic logic = new AStarLogic();
    private AStarNode[][] map;
    private List<AStarNode> closed = new ArrayList<AStarNode>();
    private Mouse mouse = new Mouse();

    private AStarNode target;
    private AStarNode playerParent;

    private int index = 0;

    private int renderX = 0, renderY = 0;
    private String name = "";

    private UDPClient client = null;

    private ArrayList<Player> players = new ArrayList<Player>();
    private ArrayList<Player> tmpPlayers = new ArrayList<Player>();

    private ArrayList<Chest> chests = new ArrayList<Chest>();
    private ArrayList<Chest> tmpChests = new ArrayList<Chest>();

    private ArrayList<Enemy> enemies = new ArrayList<Enemy>();
    private ArrayList<Enemy> tmpEnemies = new ArrayList<Enemy>();

    private ArrayList<Animation> anims = new ArrayList<Animation>();
    private ArrayList<Animation> tmpAnims = new ArrayList<Animation>();
    //private ArrayList<Animation> animsToDraw = new ArrayList<Animation>();

    private boolean stop = false;

    private Player player;
    private Player currentPlayer;

    //private JTextField chat = new JTextField();

    private InventoryGUI inv = null;
    private StatsGUI st = null;

    private AStarNode n = null;
    private AStarNode parent = null;
    private boolean move = true;

    private Cursor walkCursor = null;

    public GUI(String ip, String playerName) {
        super(1280, 720, "Game Client Window", true);

        name = playerName;

        this.setLocation(0, 0);
        this.addMouseListener(mouse);
        //this.addMouseMotionListener(mouse);

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

        player = new Player("0001", name, GameCharacterClass.NOVICE, 0*40, 0*40);
        st = new StatsGUI(player);
        inv = new InventoryGUI(player);

        playerParent = map[0][0];

        renderX = player.getX() - 640;  // half of width
        renderY = player.getY() - 360;  // half of height

        try {
            client = new UDPClient(ip, 55555, new ServerResponseParser());
            client.send(new DataPacket(System.getProperty("user.name")));
            client.send(new DataPacket(player));
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        //chat.setLayout(null);
        //chat.setBounds(10, 720 - 60, 1280 - 30, 20);
        //this.add(chat);

        walkCursor = Toolkit.getDefaultToolkit().createCustomCursor(Resources.getImage("cursor_walk.png"), new Point(16, 16), "WALK");
        setCursor(walkCursor);


        setVisible(true);
    }

    /**
     * Parses and updates the game client, including all windows
     * with the information taken from server packets
     *
     * @author Almas Baimagambetov (ab607@uni.brighton.ac.uk)
     * @version 1.0
     *
     */
    class ServerResponseParser extends ServerPacketParser {
        @Override
        public void parseServerPacket(DataPacket packet) {
            if (packet.multipleObjectData instanceof Player[]) {
                update((Player[]) packet.multipleObjectData);
            }

            if (packet.multipleObjectData instanceof Chest[]) {
                update((Chest[]) packet.multipleObjectData);
            }

            if (packet.multipleObjectData instanceof Enemy[]) {
                update((Enemy[]) packet.multipleObjectData);
            }

            if (packet.multipleObjectData instanceof Animation[]) {
                update((Animation[]) packet.multipleObjectData);
            }
        }

        /**
         * Update info about players
         *
         * @param sPlayers
         *                 players from server
         */
        private void update(Player[] sPlayers) {
            players.clear();
            for (Player p : sPlayers) {
                if (p != null) {
                    players.add(p);
                    if (p.name.equals(player.name)) {   // find "this client's" player
                        currentPlayer = p;
                    }
                }
            }

            if (currentPlayer != null) {
                player = currentPlayer; // synch from server
                updateGameClient();

                // update other windows
                inv.repaint(currentPlayer);
                st.update(currentPlayer);
            }

            tmpPlayers = new ArrayList<Player>(players);    // for drawing game client
        }

        /**
         * Updates info about chests
         *
         * @param sChests
         *                  chests from server
         */
        private void update(Chest[] sChests) {
            chests.clear();
            for (Chest ch : sChests) {
                if (!ch.isOpened()) {   // server has already checked, remove this line at some point
                    chests.add(ch);
                }
            }

            tmpChests = new ArrayList<Chest>(chests);   // for drawing chests
        }

        /**
         * Updates info about enemies
         *
         * @param sChests
         *                  enemies from server
         */
        private void update(Enemy[] sEnemies) {
            enemies.clear();
            for (Enemy e : sEnemies) {
                enemies.add(e);
            }

            tmpEnemies = new ArrayList<Enemy>(enemies);   // for drawing enemies
        }

        private void update(Animation[] sAnimations) {
            anims.clear();
            for (Animation a : sAnimations)
                anims.add(a);

            tmpAnims = new ArrayList<Animation>(anims);
        }
    }

    public AStarNode getNext() {
        if (closed.size() == 0) return playerParent;

        if (index >= closed.size())
            index = closed.size() - 1;

        return closed.get(index++);
    }

    public void updateGameClient() {
        if (move) {
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
        }

        if (!stop) {
            try {
                String[] actions = new String[st.actions.size()];
                ArrayList<String> tmp = new ArrayList<String>(st.actions);
                tmp.toArray(actions);
                st.actions.clear();

                String[] actions2 = new String[inv.actions.size()];
                ArrayList<String> tmp2 = new ArrayList<String>(inv.actions);
                tmp2.toArray(actions2);
                inv.actions.clear();

                client.send(new DataPacket(player));
                client.send(new DataPacket(actions));
                client.send(new DataPacket(actions2));
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        repaint();
    }

    @Override
    protected void createPicture(Graphics2D g) {

        int sx = Math.max(player.getX() - 640, 0), sx1 = Math.min(player.getX() + 640, mapWidth*40);
        int sy = Math.max(player.getY() - 360, 0), sy1 = Math.min(player.getY() + 360, mapHeight*40);

        int dx = 0 + Math.max(640 - player.getX(), 0), dx1 = dx + sx1-sx;
        int dy = 0 + Math.max(360 - player.getY(), 0), dy1 = dy + sy1-sy;

        if (player != null)
            g.drawImage(Resources.getImage("map1.png"),
                    dx, dy, dx1, dy1,
                    sx, sy,
                    sx1, sy1, this);

        g.setColor(Color.YELLOW);

        for (Chest ch : tmpChests) {
            g.drawImage(Resources.getImage("chest.png"), 0 + ch.x - renderX, 0 + 10 + ch.y - renderY, this);
        }

        for (Enemy ch : tmpEnemies) {
            g.drawImage(Resources.getImage("enemy.png"), ch.getX() - renderX, ch.getY() - renderY, this);
            g.drawString(ch.name + " " + ch.getHP() + "", ch.getX() - renderX, 50 + ch.getY() - renderY);
        }

        for (Player p : tmpPlayers) {
            FontMetrics fm = g.getFontMetrics(g.getFont());
            int width = fm.stringWidth(p.name);

            g.drawImage(Resources.getImage("player1.png"),
                    p.getX() - renderX, p.getY() - renderY, p.getX() - renderX+40, p.getY() - renderY+40,
                    p.place*40, p.getRow()*40, p.place*40+40, p.getRow()*40+40, this);

            g.drawString(p.name, p.getX() - renderX + 20 - (width/2), p.getY() + 5 + 40 - renderY);
        }

        if (target != null) {
            g.drawImage(Resources.getImage("target.png"), target.getX()*40 - renderX, target.getY()*40 - renderY, this);
        }

        //System.out.println(tmpAnims.size() + "");

        for (Animation a : tmpAnims) {
            g.drawImage(Resources.getImage("ss.png"), a.getX() - renderX, a.getY() - renderY - 17, a.getX()+17 - renderX, a.getY()+17 - renderY - 17,
                    a.ssX*34, a.ssY*34, a.ssX*34+34, a.ssY*34+34, this);

            g.drawString(a.data, a.getX() - renderX + 20, a.getY() - 7 - renderY);
        }

        //chat.repaint();
    }

    class Mouse implements MouseListener, MouseMotionListener {

        private boolean isPressed = false;

        private int mouseX = 0, mouseY = 0;

        public int getX() {
            return mouseX;
        }

        public int getY() {
            return mouseY;
        }

        public boolean isPressed() {
            return isPressed;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
        }

        @Override
        public void mousePressed(MouseEvent e) {

            // corresponds to right click, "should"
            /*if (e.getButton() == 3) {
                stop = true;

                try {
                    client.send(new DataPacket("CLOSE" + "," + name));
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }

                return;
            }*/

            mouseX = e.getX();
            mouseY = e.getY();

            movePlayer(mouseX, mouseY);
            isPressed = true;
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            isPressed = false;
        }

        @Override
        public void mouseEntered(MouseEvent e) {}
        @Override
        public void mouseExited(MouseEvent e) {}

        @Override
        public void mouseDragged(MouseEvent e) {
            mouseX = e.getX();
            mouseY = e.getY();
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            mouseX = e.getX();
            mouseY = e.getY();
        }
    }

    public void movePlayer(int x, int y) {
        x = x - 0 + renderX;
        y = y - 0 + renderY;

        x /= 40; y /= 40;

        if (x < 0 || x >= mapWidth || y < 0 || y >= mapHeight)
            return;

        target = map[x][y];

        for (int i = 0; i < mapWidth; i++)
            for (int j = 0; j < mapHeight; j++)
                map[i][j].setHCost(Math.abs(x - i) + Math.abs(y - j));

        closed = logic.getPath(map, playerParent, target);
        index = 0;
    }


    public Player getPlayer() {
        return player;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }
}
