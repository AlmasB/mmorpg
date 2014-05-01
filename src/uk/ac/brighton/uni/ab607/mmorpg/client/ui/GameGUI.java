package uk.ac.brighton.uni.ab607.mmorpg.client.ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTextField;

import uk.ac.brighton.uni.ab607.libs.io.Resources;
import uk.ac.brighton.uni.ab607.libs.net.DataPacket;
import uk.ac.brighton.uni.ab607.libs.net.ServerPacketParser;
import uk.ac.brighton.uni.ab607.libs.net.UDPClient;
import uk.ac.brighton.uni.ab607.libs.search.AStarNode;
import uk.ac.brighton.uni.ab607.mmorpg.common.Player;
import uk.ac.brighton.uni.ab607.mmorpg.common.item.Chest;
import uk.ac.brighton.uni.ab607.mmorpg.common.object.Enemy;

public class GameGUI extends GUI {
    /**
     *
     */
    private static final long serialVersionUID = -3086068923466302200L;

    private int mapWidth;
    private int mapHeight;

    private AStarNode[][] map;
    private Mouse mouse = new Mouse();

    private AStarNode target;

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

    private int targetRuntimeID = 0;    // this is for attacking

    private boolean stop = false;

    private Player player;
    private Player currentPlayer;

    private JTextField chat = new JTextField();

    private InventoryGUI inv;
    private StatsGUI st;

    private Cursor walkCursor = null;

    private int selX = 0, selY = 0; // selected point

    public GameGUI(String ip, String playerName) {
        super(1280, 720, "Main Window");

        name = playerName;

        inv = new InventoryGUI();
        st = new StatsGUI(name);

        this.setLocation(0, 0);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        this.addKeyListener(new Keyboard());
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

        try {
            client = new UDPClient(ip, 55555, new ServerResponseParser());
            client.send(new DataPacket("CREATE_PLAYER," + name));
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        chat.setLayout(null);
        chat.setBounds(5, 720 - 53, 1280 - 25, 20);
        chat.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String chatText = e.getActionCommand();
                if (!chatText.isEmpty()) {
                    addActionRequest("CHAT," + player.name + ",0,0," + chatText);
                    chat.setText("");
                }
            }
        });
        chat.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    GameGUI.this.requestFocusInWindow();
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {}
        });
        this.add(chat);

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

        boolean once = true;

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
                    if (p.name.equals(name)) {   // find "this client's" player
                        currentPlayer = p;
                    }
                }
            }

            if (currentPlayer != null) {
                player = currentPlayer; // synch from server

                // TODO: consider something better
                if (once) {
                    selX = player.getX();
                    selY = player.getY();
                    once = false;
                }


                updateGameClient();

                // update other windows
                inv.update(currentPlayer);
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

    public void updateGameClient() {
        renderX = player.getX() - 640;  // half of width
        renderY = player.getY() - 360;  // half of height

        if (selX /40 != player.getX()/40 || selY/40 != player.getY()/40) {
            target = map[selX/40][selY/40];
            addActionRequest("MOVE," + player.name + "," + (selX) + "," + (selY));
        }
        else {
            target = null;
        }

        checkRuntimeID();

        if (targetRuntimeID != 0) {
            addActionRequest("ATTACK," + player.name + "," + targetRuntimeID);
        }

        if (!stop) {
            try {
                client.send(new DataPacket(inv.clearPendingActionRequests()));
                client.send(new DataPacket(st.clearPendingActionRequests()));
                client.send(new DataPacket(this.clearPendingActionRequests()));  // main ui actions
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        repaint();
    }

    @Override
    protected void createPicture(Graphics2D g) {
        g.setColor(Color.GRAY);
        g.fillRect(0, 0, 1280, 690);

        if (player != null) {
            int sx = Math.max(player.getX() - 640, 0), sx1 = Math.min(player.getX() + 640, mapWidth*40);
            int sy = Math.max(player.getY() - 360, 0), sy1 = Math.min(player.getY() + 360, mapHeight*40);

            int dx = 0 + Math.max(640 - player.getX(), 0), dx1 = dx + sx1-sx;
            int dy = 0 + Math.max(360 - player.getY(), 0), dy1 = dy + sy1-sy;
            g.drawImage(Resources.getImage("map1.png"),
                    dx, dy, dx1, dy1,
                    sx, sy, sx1, sy1, this);
        }

        g.setColor(Color.YELLOW);

        for (Chest ch : tmpChests) {
            g.drawImage(Resources.getImage("chest.png"), 0 + ch.x - renderX, 0 + 10 + ch.y - renderY, this);
        }

        for (Enemy e : tmpEnemies) {
            g.drawImage(Resources.getImage("enemy3.png"),
                    e.getX() - renderX, e.getY() - renderY, e.getX() - renderX+40, e.getY() - renderY+40,
                    e.place*40, e.getRow()*40, e.place*40+40, e.getRow()*40+40, this);

            g.drawString(e.name + " " + e.getHP() + "", e.getX() - renderX, 50 + e.getY() - renderY);
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

        for (Animation a : tmpAnims) {
            g.drawImage(Resources.getImage("ss.png"), a.getX() - renderX, a.getY() - renderY - 17, a.getX()+17 - renderX, a.getY()+17 - renderY - 17,
                    a.ssX*34, a.ssY*34, a.ssX*34+34, a.ssY*34+34, this);

            g.drawString(a.data, a.getX() - renderX + 20, a.getY() - 7 - renderY);
        }
    }

    @Override
    protected void showPicture(Graphics2D g) {
        if (doubleBufferGraphics == null) {
            doubleBufferImage = (BufferedImage) createImage(1280, 690);
            doubleBufferGraphics = doubleBufferImage.createGraphics();
        }

        createPicture(doubleBufferGraphics);
        g.drawImage(doubleBufferImage, 0, 0, this);
    }

    @Override
    public void paint(Graphics g) {
        showPicture((Graphics2D) g);
        chat.repaint();
    }

    private boolean choosingTarget = false; // if player is choosing target for skill or smth
    private char input = ' ';

    class Keyboard implements KeyListener {
        @Override
        public void keyTyped(KeyEvent e) {
            input = e.getKeyChar();
            if (input >= '1' && input <= '9')
                choosingTarget = true;


            // once skill is clicked cursor changes
            // choose target and send action to server if target is valid

        }
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_UP) {
                chat.requestFocusInWindow();
            }
        }
        @Override
        public void keyReleased(KeyEvent e) {}
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

            if (!choosingTarget) {  // if not choosing skill target

                for (Enemy enemy : tmpEnemies) {
                    Rectangle r = new Rectangle(enemy.getX(), enemy.getY(), 40, 40);
                    if (r.contains(new Point(mouseX + renderX, mouseY + renderY))) {
                        targetRuntimeID = enemy.getRuntimeID();
                        return;
                    }
                }

                targetRuntimeID = 0;

                // if no enemy in that selection then move player to that cell

                selX = mouseX + renderX;
                selY = mouseY + renderY;
                isPressed = true;
            }
            else {
                choosingTarget = false;
                for (Enemy enemy : tmpEnemies) {
                    Rectangle r = new Rectangle(enemy.getX(), enemy.getY(), 40, 40);
                    if (r.contains(new Point(mouseX + renderX, mouseY + renderY))) {
                        addActionRequest("SKILL_USE," + player.name + "," + input + "," + enemy.getRuntimeID());
                        return;
                    }
                }
            }
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

    public Player getPlayer() {
        return player;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    private void checkRuntimeID() {
        for (Enemy e : tmpEnemies) {
            if (e.getRuntimeID() == targetRuntimeID)
                return;
        }
        targetRuntimeID = 0;
    }
}
