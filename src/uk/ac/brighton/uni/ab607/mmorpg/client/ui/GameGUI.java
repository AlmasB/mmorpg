package uk.ac.brighton.uni.ab607.mmorpg.client.ui;

import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.*;
import java.awt.image.BufferedImage;

import javax.swing.JTextField;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.almasb.common.graphics.Color;
import com.almasb.common.graphics.Drawable;
import com.almasb.common.graphics.GraphicsContext;
import com.almasb.common.graphics.Point2D;
import com.almasb.common.graphics.Rect2D;
import com.almasb.common.net.DataPacket;
import com.almasb.common.net.ServerPacketParser;
import com.almasb.common.net.UDPClient;
import com.almasb.java.io.Resources;
import com.almasb.java.ui.AWTGraphicsContext;

import uk.ac.brighton.uni.ab607.mmorpg.R;
import uk.ac.brighton.uni.ab607.mmorpg.client.ui.animation.Animation;
import uk.ac.brighton.uni.ab607.mmorpg.common.Player;
import uk.ac.brighton.uni.ab607.mmorpg.common.item.Chest;
import uk.ac.brighton.uni.ab607.mmorpg.common.object.Enemy;
import uk.ac.brighton.uni.ab607.mmorpg.common.object.GameMap;
import uk.ac.brighton.uni.ab607.mmorpg.common.object.ObjectManager;
import uk.ac.brighton.uni.ab607.mmorpg.common.request.ActionRequest;
import uk.ac.brighton.uni.ab607.mmorpg.common.request.ActionRequest.Action;
import uk.ac.brighton.uni.ab607.mmorpg.common.request.QueryRequest;
import uk.ac.brighton.uni.ab607.mmorpg.common.request.QueryRequest.Query;
import uk.ac.brighton.uni.ab607.mmorpg.common.request.ServerResponse;

/**
 * Main game window
 *
 * @author Almas Baimagambetov
 *
 */
public class GameGUI extends GUI {
    private static final long serialVersionUID = -3086068923466302200L;

    private String name = "";

    private UDPClient client = null;

    private ArrayList<Enemy> enemies = new ArrayList<Enemy>();
    private ArrayList<Enemy> tmpEnemies = new ArrayList<Enemy>();

    private int targetRuntimeID = 0;    // this is for attacking

    private boolean stop = false;

    private Player player = null;

    private JTextField chat = new JTextField();

    private InventoryGUI inv;
    private StatsGUI st;

    private Cursor walkCursor = null;

    private int selX = 0, selY = 0; // selected point
    private int renderX = 0, renderY = 0;   // render offset

    private GraphicsContext gContext = null;
    private GameMap map;

    private List<Drawable[]> gameObjects = Collections.synchronizedList(new ArrayList<Drawable[]>(5));

    private static final int INDEX_ANIMATIONS = 0,
            INDEX_CHESTS = 1,
            INDEX_ENEMIES = 2,
            INDEX_PLAYERS = 3;

    public GameGUI(String ip, String playerName) throws IOException {
        super(1280, 720, "Main Window");
        this.setLocation(0, 0);
        this.setDefaultCloseOperation(HIDE_ON_CLOSE);

        name = playerName;

        this.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {}

            @Override
            public void componentMoved(ComponentEvent e) {
                inv.setLocation(GameGUI.this.getX()+640, GameGUI.this.getY()+22);
                st.setLocation(GameGUI.this.getX(), GameGUI.this.getY()+22);
            }

            @Override
            public void componentShown(ComponentEvent e) {
            }
            @Override
            public void componentHidden(ComponentEvent e) {}
        });

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                inv.setVisible(false);
                st.setVisible(false);

                // development version
                System.exit(0);
                /*try {
                    client.send(new DataPacket(new QueryRequest(Query.LOGOFF, name)));
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                }*/
            }
        });

        this.addKeyListener(new Keyboard());
        this.addMouseListener(new Mouse());

        chat.setLayout(null);
        chat.setBounds(5, 720 - 53, 1280 - 25, 20);
        chat.addActionListener(e -> {
            String chatText = e.getActionCommand();
            if (!chatText.isEmpty()) {
                addActionRequest(new ActionRequest(Action.CHAT, player.name, map.name + "," + chatText));
                chat.setText("");
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

        walkCursor = Toolkit.getDefaultToolkit().createCustomCursor(Resources.getImage(R.drawable.cursor_walk), new Point(16, 16), "WALK");
        setCursor(walkCursor);

        client = new UDPClient(ip, 55555, new ServerResponseParser());
        client.send(new DataPacket(new QueryRequest(Query.LOGIN, name)));

        // placeholders
        gameObjects.add(new Drawable[]{ });
        gameObjects.add(new Drawable[]{ });
        gameObjects.add(new Drawable[]{ });
        gameObjects.add(new Drawable[]{ });
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
            if (packet.objectData instanceof ServerResponse) {
                ServerResponse res = (ServerResponse) packet.objectData;

                map = ObjectManager.getMapByName(res.data);

                selX = res.value1;
                selY = res.value2;
            }

            if (packet.objectData instanceof Player) {
                player = (Player) packet.objectData;
                // login complete, all set, we can now show GUI
                // and start drawing
                inv = new InventoryGUI(player);
                st = new StatsGUI(player);

                setVisible(true);
                requestFocusInWindow();
            }

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
            for (Player p : sPlayers) {
                if (p.name.equals(name)) {
                    player = p;
                    break;
                }
            }

            gameObjects.set(INDEX_PLAYERS, sPlayers);

            // update main window
            updateGameClient();

            // update other windows
            inv.update(player);
            st.update(player);
        }

        /**
         * Updates info about chests
         *
         * @param sChests
         *                  chests from server
         */
        private void update(Chest[] sChests) {
            gameObjects.set(INDEX_CHESTS, sChests);
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

            gameObjects.set(INDEX_ENEMIES, sEnemies);

            tmpEnemies = new ArrayList<Enemy>(enemies);
        }

        private void update(Animation[] sAnimations) {
            gameObjects.set(INDEX_ANIMATIONS, sAnimations);
        }
    }

    public void updateGameClient() {
        renderX = player.getX() - 640;  // half of width
        renderY = player.getY() - 360;  // half of height

        if (gContext != null)
            gContext.setRenderXY(renderX, renderY);

        int moveToX = (selX/40)*40;
        int moveToY = (selY/40)*40;

        if (moveToX != player.getX() || moveToY != player.getY()) {
            addActionRequest(new ActionRequest(Action.MOVE, player.name, map.name, moveToX, moveToY));
        }

        if ((targetRuntimeID = checkRuntimeID()) != 0) {
            addActionRequest(new ActionRequest(Action.ATTACK, player.name, map.name, targetRuntimeID));
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
        if (gContext == null)
            gContext = new AWTGraphicsContext(g, 1280, 720);

        // draw background / clear screen
        gContext.setColor(Color.GRAY);
        gContext.fillRect(0, 0, 1280, 690);

        // draw map
        int sx = Math.max(player.getX() - 640, 0), sx1 = Math.min(player.getX() + 640, map.width*40);
        int sy = Math.max(player.getY() - 360, 0), sy1 = Math.min(player.getY() + 360, map.height*40);

        int dx = 0 + Math.max(640 - player.getX(), 0), dx1 = dx + sx1-sx;
        int dy = 0 + Math.max(360 - player.getY(), 0), dy1 = dy + sy1-sy;

        gContext.drawImage(map.spriteID, dx, dy, dx1, dy1, sx, sy, sx1, sy1);

        synchronized(gameObjects) {
            Rect2D playerVision = new Rect2D(player.getX() - 640, player.getY() - 360, 1280, 720);
            for (Drawable[] objects : gameObjects) {
                for (Drawable obj : objects) {
                    if (playerVision.contains(new Point2D(obj.getX(), obj.getY())))
                        obj.draw(gContext);
                }
            }
        }

        // debug full grid drawing
        /*for (int i = 0; i < map.height; i++) {
            for (int j = 0; j < map.width; j++) {
                gContext.setColor(Color.YELLOW);
                gContext.drawRect(j*40 - renderX, i*40 - renderY, 40, 40);
            }
        }*/
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
            if (input >= '1' && input <= '9') {
                choosingTarget = true;
                setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
            }


            // once skill is clicked cursor changes
            // choose target and send action to server if target is valid

        }
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_UP:
                    chat.requestFocusInWindow();
                    break;
                case KeyEvent.VK_I:
                    inv.setVisible(!inv.isVisible());
                    break;
                case KeyEvent.VK_S:
                    st.setVisible(!st.isVisible());
                    break;
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
                    Rect2D r = new Rect2D(enemy.getX(), enemy.getY(), 40, 40);
                    if (r.contains(new Point2D(mouseX + renderX, mouseY + renderY))) {
                        targetRuntimeID = enemy.getRuntimeID();
                        return;
                    }
                }

                // if no enemy in that selection
                // drop target, then move player to that cell
                targetRuntimeID = 0;

                selX = mouseX + renderX;
                selY = mouseY + renderY;
                isPressed = true;
            }
            else {
                choosingTarget = false;
                setCursor(walkCursor);
                for (Enemy enemy : tmpEnemies) {
                    Rect2D r = new Rect2D(enemy.getX(), enemy.getY(), 40, 40);
                    if (r.contains(new Point2D(mouseX + renderX, mouseY + renderY))) {
                        addActionRequest(new ActionRequest(Action.SKILL_USE, player.name,
                                map.name, Integer.parseInt(input+"")-1, enemy.getRuntimeID()));
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

    /**
     * This check is needed to see if target runtimeID
     * still exists in the world
     *
     * @return
     *          target's runtimeID or 0 if invalid
     */
    private int checkRuntimeID() {
        if (targetRuntimeID == 0) return 0;

        for (Enemy e : tmpEnemies) {
            if (e.getRuntimeID() == targetRuntimeID)
                return targetRuntimeID;
        }
        return 0;
    }
}
