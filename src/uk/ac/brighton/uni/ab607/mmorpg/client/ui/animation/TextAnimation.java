package uk.ac.brighton.uni.ab607.mmorpg.client.ui.animation;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import uk.ac.brighton.uni.ab607.mmorpg.client.ui.GraphicsContext;

public class TextAnimation extends Animation {
    /**
     * 
     */
    private static final long serialVersionUID = 6951995575141406171L;
    
    private String text;
    private Font font;
    private Color color;
    
    /*public enum TextAnimationType {
        DAMAGE, SKILL, CHAT
    }*/
    
    public TextAnimation(int x, int y, float duration, String text, Font font, Color color) {
        super(x, y, duration);
        this.text = text;
        this.font = font;
        this.color = color;
    }
    
    public TextAnimation(int x, int y, float duration, String text) {
        this(x, y, duration, text, AnimationUtils.DEFAULT_FONT, Color.WHITE);
    }
    
    public TextAnimation(int x, int y, float duration, String text, Color color) {
        this(x, y, duration, text, AnimationUtils.DEFAULT_FONT, color);
    }

    @Override
    protected void updateImpl(float completed) {
    }
    
    @Override
    public void draw(GraphicsContext gContext) {
        Graphics2D g = gContext.getGraphics();
        g.setFont(font);
        g.setColor(color);
        g.drawString(text, x - gContext.getRenderX(), y - gContext.getRenderY());
    }
    
    /*@Override
    public BufferedImage nextFrame() {
        BufferedImage img = new BufferedImage(10, 10, BufferedImage.TYPE_4BYTE_ABGR_PRE);
        Graphics2D g = img.createGraphics();
        Font font = new Font("Arial", Font.PLAIN, 20);
        FontMetrics fm = g.getFontMetrics(font);
        
        img = new BufferedImage(fm.stringWidth(text), fm.getHeight(), BufferedImage.TYPE_4BYTE_ABGR_PRE);
        g = img.createGraphics();
        
        g.setFont(font);
        g.drawString(text, 0, 15);
        g.dispose();
        
        return img;
    }*/
}
