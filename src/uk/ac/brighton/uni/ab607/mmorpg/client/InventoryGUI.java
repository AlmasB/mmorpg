package uk.ac.brighton.uni.ab607.mmorpg.client;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import uk.ac.brighton.uni.ab607.libs.io.Resources;
import uk.ac.brighton.uni.ab607.mmorpg.common.Player;
import uk.ac.brighton.uni.ab607.mmorpg.common.item.GameItem;
import uk.ac.brighton.uni.ab607.mmorpg.common.item.UsableItem;
import uk.ac.brighton.uni.ab607.mmorpg.common.object.Armor;
import uk.ac.brighton.uni.ab607.mmorpg.common.object.Weapon;

public class InventoryGUI extends JFrame {

    /**
     *
     */
    private static final long serialVersionUID = 8003620530720788503L;

    private static final String INFO_ON = "info: on", INFO_OFF = "info: off";
    private Player player;

    private JLabel itemInfoLabel = new JLabel();
    private JButton infoButton = new JButton(INFO_OFF);

    private GameItem selectedItem;

    private Mouse mouse = new Mouse();

    //make it private
    public ArrayList<String> actions = new ArrayList<String>();

    public InventoryGUI(Player p) {
        setSize(640, 304);
        setTitle("Inventory Window");
        setLayout(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        this.setLocation(640, 720);

        itemInfoLabel.setBounds(200, 0, 225, 304);
        itemInfoLabel.setFocusable(false);  // need that ?
        itemInfoLabel.setVerticalAlignment(SwingConstants.TOP);
        this.add(itemInfoLabel);

        infoButton.setBounds(346, 235, 80, 30);
        infoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        infoButton.setText(e.getActionCommand().equals(INFO_ON) ? INFO_OFF : INFO_ON);
                        if (selectedItem != null)
                            itemInfoLabel.setText(infoButton.getText().equals(INFO_ON) ? selectedItem.toPseudoHTML()
                                    : selectedItem.toPseudoHTMLShort());
                    }
                });
            }
        });
        this.add(infoButton);

        this.addMouseListener(mouse);
        this.addMouseMotionListener(mouse);

        player = p;
        setVisible(true);
    }

    private void runOnUIThread(Runnable r) {
        SwingUtilities.invokeLater(r);
    }

    public int getInvSize() {
        return player.getInventory().getCurrentSize();
    }

    // also check if items themselves have changed ie rune or essence added maybe use PseudoHTML for checking ?
    // TODO: check the exact items in inventory and perhaps player equip and then repaint if needed
    public void repaint(Player p) {
        if (!player.getInventory().toString().equals(p.getInventory().toString()) || !isSameEquip(p) || player.getMoney() != p.getMoney()) {
            player = p;
            repaint();
        }
    }

    private boolean isSameEquip(Player p) {
        for (int i = Player.HELM; i <= Player.LEFT_HAND; i++) {
            if (!player.getEquip(i).toString().equals(p.getEquip(i).toString())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Double buffer (off-screen) Image
     */
    private BufferedImage doubleBufferImage;

    /**
     * Double buffer (off-screen) Graphics
     */
    private Graphics2D doubleBufferGraphics;

    /**
     * Double buffer (off-screen) Image
     */
    private BufferedImage doubleBufferImage2;

    /**
     * Double buffer (off-screen) Graphics
     */
    private Graphics2D doubleBufferGraphics2;

    private void draw(Graphics2D g) {
        if (doubleBufferGraphics == null) {
            doubleBufferImage = (BufferedImage) createImage(203, 304);
            doubleBufferGraphics = doubleBufferImage.createGraphics();
        }

        if (doubleBufferGraphics2 == null) {
            doubleBufferImage2 = (BufferedImage) createImage(640-435, 304);
            doubleBufferGraphics2 = doubleBufferImage2.createGraphics();
        }

        drawImage(doubleBufferGraphics);
        drawImage2(doubleBufferGraphics2);

        g.drawImage(doubleBufferImage, 0, 0, this);
        g.drawImage(doubleBufferImage2, 435, 0, this);
    }

    private void drawImage(Graphics2D g) {
        g.setColor(Color.GRAY);
        g.fillRect(0, 0, 203, 304);

        drawItem(player.getEquip(Player.RIGHT_HAND), g, 45, 130);
        drawItem(player.getEquip(Player.LEFT_HAND), g, 135, 130);
        drawItem(player.getEquip(Player.BODY), g, 90, 130);
        drawItem(player.getEquip(Player.HELM), g, 90, 85);
        drawItem(player.getEquip(Player.SHOES), g, 90, 180);

        g.drawImage(Resources.getImage("inv.png"), 2, 27, this);
    }

    private void drawImage2(Graphics2D g) {
        g.setColor(Color.GRAY);
        g.fillRect(0, 0, 205, 304);

        ArrayList<GameItem> items = player.getInventory().getItems();


        int x = 0, y = 0;
        for (int i = 0; i < items.size(); i++) {
            drawItem(items.get(i), g, 2 + x*40, 29 + y*40);

            if (++x == 6) {
                x = 0;
                y++;
            }
        }

        g.drawImage(Resources.getImage("inventory2.png"), 0, 27, this);

        g.setColor(Color.YELLOW);
        g.setFont(new Font("Courier", Font.PLAIN, 20));
        g.drawString(player.getMoney() + " G", 90, 287);
    }

    @Override
    public void paint(Graphics g) {
        draw((Graphics2D) g);
    }

    private void drawItem(GameItem item, Graphics2D g, int x, int y) {
        g.drawImage(Resources.getImage("ss.png"), x, y, x + 34, y + 34,
                item.ssX*34, item.ssY*34, item.ssX*34 + 34, item.ssY*34 + 34, this);
    }

    private Rectangle rightHand = new Rectangle(45, 130, 34, 34),
            leftHand = new Rectangle(135, 130, 34, 34),
            body = new Rectangle(90, 130, 34, 34),
            helm = new Rectangle(90, 85, 34, 34),
            shoes = new Rectangle(90, 180, 34, 34);

    /**
     * Checks whether mouse clicked any of the body parts
     * of player
     *
     * @param x
     *          mouse x
     * @param y
     *          mouse y
     * @return
     *          player body part HELM to LEFT_HAND, or -1 if no body part clicked
     */
    private int getEquipPlace(int x, int y) {
        if (rightHand.contains(x, y))
            return Player.RIGHT_HAND;
        if (leftHand.contains(x, y))
            return Player.LEFT_HAND;
        if (body.contains(x, y))
            return Player.BODY;
        if (helm.contains(x, y))
            return Player.HELM;
        if (shoes.contains(x, y))
            return Player.SHOES;
        return -1;
    }

    private class Mouse implements MouseListener, MouseMotionListener {

        @Override
        public void mouseClicked(MouseEvent e) {
            int x = e.getX(), y = e.getY();

            int bodyPart = getEquipPlace(x, y);
            if (bodyPart != -1 && !player.isFree(bodyPart))
                actions.add("UNEQUIP," + player.name + "," + bodyPart);

            if (x < 440 || x > 630 || y < 30 || y > 260)
                return;

            x -= 440; y -= 30;
            x /= 40; y /= 40;

            int itemIndex = x + 5*y;  // convert to 1d array, 5 columns
            if (itemIndex < player.getInventory().getMaxSize()) {
                GameItem item = player.getInventory().getItem(itemIndex);
                if (item != null) {
                    // if weapon or armor
                    if (item instanceof Weapon || item instanceof Armor) {
                        if (e.getButton() == 3)
                            actions.add("REFINE," + player.name + "," + itemIndex);
                        else
                            actions.add("EQUIP," + player.name + "," + itemIndex);
                    }
                    else if (item instanceof UsableItem) {
                        actions.add("USE_ITEM," + player.name + "," + itemIndex);
                    }
                }
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {}
        @Override
        public void mouseEntered(MouseEvent e) {
            //e.getComponent()
            //Out.println(e.getComponent().toString());
        }
        @Override
        public void mouseExited(MouseEvent e) {}
        @Override
        public void mouseDragged(MouseEvent e) {}

        @Override
        public void mouseMoved(MouseEvent e) {
            int x = e.getX(), y = e.getY();

            int bodyPart = getEquipPlace(x, y);
            if (bodyPart != -1) {
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                selectedItem = player.getEquip(bodyPart);
                runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        if (selectedItem != null)
                            itemInfoLabel.setText(infoButton.getText().equals(INFO_ON) ? selectedItem.toPseudoHTML()
                                    : selectedItem.toPseudoHTMLShort());
                    }
                });
                return;
            }

            if (x < 440 || x > 630 || y < 30 || y > 260) {
                setCursor(Cursor.getDefaultCursor());
                return;
            }
            x -= 440; y -= 30;
            x /= 40; y /= 40;

            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            int itemIndex = x + 5*y;  // convert to 1d array, 5 columns
            if (itemIndex < player.getInventory().getMaxSize()) {
                GameItem item = player.getInventory().getItem(itemIndex);
                if (item != null && item != selectedItem) {
                    selectedItem = item;
                    runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            if (selectedItem != null)
                                itemInfoLabel.setText(infoButton.getText().equals(INFO_ON) ? selectedItem.toPseudoHTML()
                                        : selectedItem.toPseudoHTMLShort());
                        }
                    });
                }
            }
        }
    }
}
