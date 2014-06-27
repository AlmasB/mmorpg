package uk.ac.brighton.uni.ab607.mmorpg.client.ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import uk.ac.brighton.uni.ab607.libs.io.Resources;
import uk.ac.brighton.uni.ab607.mmorpg.common.Player;
import uk.ac.brighton.uni.ab607.mmorpg.common.item.GameItem;
import uk.ac.brighton.uni.ab607.mmorpg.common.item.UsableItem;
import uk.ac.brighton.uni.ab607.mmorpg.common.object.Armor;
import uk.ac.brighton.uni.ab607.mmorpg.common.object.Weapon;
import uk.ac.brighton.uni.ab607.mmorpg.common.request.ActionRequest;
import uk.ac.brighton.uni.ab607.mmorpg.common.request.ActionRequest.Action;

public class InventoryGUI extends GUI {
    /**
     *
     */
    private static final long serialVersionUID = 4657910668634504112L;

    private static final String INFO_ON = "info: on", INFO_OFF = "info: off";
    private Player player;

    private JLabel itemInfoLabel = new JLabel();
    private JButton infoButton = new JButton(INFO_OFF);

    private GameItem selectedItem;

    private Mouse mouse = new Mouse();

    public InventoryGUI() {
        super(640, 304, "Inventory Window");
        this.setUndecorated(true);
        this.setAlwaysOnTop(true);
        this.setFocusableWindowState(false);
        this.setLocation(640, 44);

        itemInfoLabel.setBounds(200, 0, 225, 304);
        //itemInfoLabel.setFocusable(false);
        itemInfoLabel.setVerticalAlignment(SwingConstants.TOP);
        this.add(itemInfoLabel);

        infoButton.setBounds(346, 235, 80, 30);
        infoButton.addActionListener(event -> {
            runOnUIThread(() -> {
                infoButton.setText(event.getActionCommand().equals(INFO_ON) ? INFO_OFF : INFO_ON);
                if (selectedItem != null)
                    itemInfoLabel.setText(infoButton.getText().equals(INFO_ON) ? selectedItem.toPseudoHTML()
                            : selectedItem.toPseudoHTMLShort());
            });
        });
        
        this.add(infoButton);

        this.addMouseListener(mouse);
        this.addMouseMotionListener(mouse);

    }

    public void update(Player p) {
        if (player == null || !player.getInventory().toString().equals(p.getInventory().toString())
                || !isSameEquip(p) || player.getMoney() != p.getMoney()) {
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

    @Override
    protected void createPicture(Graphics2D g) {
        g.setColor(Color.GRAY);
        g.fillRect(0, 0, 203, 304);

        if (player != null) {
            drawItem(player.getEquip(Player.RIGHT_HAND), g, 45, 130);
            drawItem(player.getEquip(Player.LEFT_HAND),  g, 135, 130);
            drawItem(player.getEquip(Player.BODY),       g, 90, 130);
            drawItem(player.getEquip(Player.HELM),       g, 90, 85);
            drawItem(player.getEquip(Player.SHOES),      g, 90, 180);
        }

        g.drawImage(Resources.getImage("inv.png"), 2, 27, this);
    }

    private void createPicture2(Graphics2D g) {
        g.setColor(Color.GRAY);
        g.fillRect(0, 0, 205, 304);

        if (player != null) {
            ArrayList<GameItem> items = player.getInventory().getItems();

            int x = 0, y = 0;
            for (int i = 0; i < items.size(); i++) {
                drawItem(items.get(i), g, 2 + x*40, 29 + y*40);

                if (++x == 5) {
                    x = 0;
                    y++;
                }
            }

            g.drawImage(Resources.getImage("inventory2.png"), 0, 27, this);

            g.setColor(Color.YELLOW);
            g.setFont(new Font("Courier", Font.PLAIN, 20));
            g.drawString(player.getMoney() + " G", 90, 287);
        }
    }

    /**
     * Double buffer (off-screen) Image
     */
    private BufferedImage doubleBufferImage2;

    /**
     * Double buffer (off-screen) Graphics
     */
    private Graphics2D doubleBufferGraphics2;

    @Override
    protected void showPicture(Graphics2D g) {
        if (doubleBufferGraphics == null) {
            doubleBufferImage = (BufferedImage) createImage(203, 304);
            doubleBufferGraphics = doubleBufferImage.createGraphics();
        }

        if (doubleBufferGraphics2 == null) {
            doubleBufferImage2 = (BufferedImage) createImage(640-435, 304);
            doubleBufferGraphics2 = doubleBufferImage2.createGraphics();
        }

        createPicture(doubleBufferGraphics);
        createPicture2(doubleBufferGraphics2);

        g.drawImage(doubleBufferImage, 0, 0, this);
        g.drawImage(doubleBufferImage2, 435, 0, this);
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

    private void runOnUIThread(Runnable r) {
        SwingUtilities.invokeLater(r);
    }

    private class Mouse implements MouseListener, MouseMotionListener {
        @Override
        public void mouseClicked(MouseEvent e) {
            int x = e.getX(), y = e.getY();

            int bodyPart = getEquipPlace(x, y);
            if (bodyPart != -1 && !player.isFree(bodyPart))
                addActionRequest(new ActionRequest(Action.UNEQUIP, player.name, bodyPart));

            if (x < 440 || x > 630 || y < 30 || y > 260)
                return;

            x -= 440; y -= 30;
            x /= 40; y /= 40;

            int itemIndex = x + 5*y;  // convert to 1d array, 5 columns
            if (itemIndex < player.getInventory().getSize()) {
                GameItem item = player.getInventory().getItem(itemIndex);
                if (item != null) {
                    // if weapon or armor
                    if (item instanceof Weapon || item instanceof Armor) {
                        if (e.getButton() == 3)
                            addActionRequest(new ActionRequest(Action.REFINE, player.name, itemIndex));
                        else
                            addActionRequest(new ActionRequest(Action.EQUIP, player.name, itemIndex));
                    }
                    else if (item instanceof UsableItem) {
                        addActionRequest(new ActionRequest(Action.USE_ITEM, player.name, itemIndex));
                    }
                }
            }
        }
        
        @Override
        public void mouseMoved(MouseEvent e) {
            int x = e.getX(), y = e.getY();

            int bodyPart = getEquipPlace(x, y);
            if (bodyPart != -1) {
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                selectedItem = player.getEquip(bodyPart);
                runOnUIThread(() -> {
                    if (selectedItem != null)
                        itemInfoLabel.setText(infoButton.getText().equals(INFO_ON) ? selectedItem.toPseudoHTML()
                                : selectedItem.toPseudoHTMLShort());
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
            if (itemIndex < player.getInventory().getSize()) {
                GameItem item = player.getInventory().getItem(itemIndex);
                if (item != null && item != selectedItem) {
                    selectedItem = item;
                    runOnUIThread(() -> {
                        if (selectedItem != null)
                            itemInfoLabel.setText(infoButton.getText().equals(INFO_ON) ? selectedItem.toPseudoHTML()
                                    : selectedItem.toPseudoHTMLShort());
                    });
                }
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {}
        @Override
        public void mouseReleased(MouseEvent e) {}
        @Override
        public void mouseEntered(MouseEvent e) {}
        @Override
        public void mouseExited(MouseEvent e) {}
        @Override
        public void mouseDragged(MouseEvent e) {}
    }
}
