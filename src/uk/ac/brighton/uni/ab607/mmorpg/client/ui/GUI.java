package uk.ac.brighton.uni.ab607.mmorpg.client.ui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JFrame;

/**
 * Abstract GUI to be subclassed by any game related windows
 *
 * @author Almas Baimagambetov (ab607@uni.brighton.ac.uk)
 * @version 1.0
 *
 */
public abstract class GUI extends JFrame {
    /**
     *
     */
    private static final long serialVersionUID = -2472437923536886979L;

    private final int W, H;

    private ArrayList<String> actionRequests = new ArrayList<String>();

    public GUI(int w, int h, String title) {
        super(title);
        W = w; H = h;
        this.setSize(w, h);
        this.setResizable(false);
        this.setLayout(null);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public void addActionRequest(String action) {
        actionRequests.add(action);
    }

    public String[] clearPendingActionRequests() {
        String[] res = new String[actionRequests.size()];
        actionRequests.toArray(res);
        actionRequests.clear();
        return res;
    }

    /**
     * Called by repaint()
     *
     * @param g
     *            Graphics context to use
     */
    @Override
    public void update(Graphics g) {
        showPicture((Graphics2D) g);
    }

    /**
     * Called when the window is shown for the first time or after being
     * 'damaged'
     *
     * @param g
     *            Graphics context to use
     */
    @Override
    public void paint(Graphics g) {
        showPicture((Graphics2D) g);
    }

    /**
     * Double buffer (off-screen) Image
     */
    protected BufferedImage doubleBufferImage;

    /**
     * Double buffer (off-screen) Graphics
     */
    protected Graphics2D doubleBufferGraphics;

    /**
     * Calls draw to buffer and then shows previous image
     *
     * @param g
     */
    protected void showPicture(Graphics2D g) {
        if (doubleBufferGraphics == null) {
            doubleBufferImage = (BufferedImage) createImage(W, H);
            doubleBufferGraphics = doubleBufferImage.createGraphics();
        }

        createPicture(doubleBufferGraphics);
        g.drawImage(doubleBufferImage, 0, 0, this);
    }

    protected abstract void createPicture(Graphics2D g);
}
