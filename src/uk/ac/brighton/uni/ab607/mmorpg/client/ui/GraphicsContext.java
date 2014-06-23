package uk.ac.brighton.uni.ab607.mmorpg.client.ui;

import java.awt.Graphics2D;

/**
 * Convenient wrapper for Graphics2D
 * 
 * @author Almas Baimagambetov
 *
 */
public class GraphicsContext {

    private int renderX = 0, renderY = 0;
    private Graphics2D g;
    
    public GraphicsContext(Graphics2D g) {
        this.g = g;
    }
    
    public void setRenderOffset(int x, int y) {
        renderX = x;
        renderY = y;
    }
    
    public Graphics2D getGraphics() {
        return g;
    }
    
    public int getRenderX() {
        return renderX;
    }
    
    public int getRenderY() {
        return renderY;
    }
}
