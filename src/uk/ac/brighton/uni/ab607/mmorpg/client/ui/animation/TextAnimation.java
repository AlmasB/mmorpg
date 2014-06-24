package uk.ac.brighton.uni.ab607.mmorpg.client.ui.animation;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;

import uk.ac.brighton.uni.ab607.mmorpg.client.ui.GraphicsContext;

public class TextAnimation extends Animation {
    /**
     * 
     */
    private static final long serialVersionUID = 6951995575141406171L;
    
    public enum TextAnimationType {
        DAMAGE_PLAYER(new Font("Lucida Grande", Font.PLAIN, 15), new Color(0x73, 0x6A, 0xFF), 0.5f),
        DAMAGE_ENEMY(AnimationUtils.DEFAULT_FONT, Color.WHITE, 0.5f),
        SKILL(AnimationUtils.DEFAULT_FONT, Color.BLUE, 0.5f),
        CHAT(AnimationUtils.DEFAULT_FONT, Color.WHITE, 2.0f),
        FADE(AnimationUtils.DEFAULT_FONT, Color.YELLOW, 1.5f),
        NONE(AnimationUtils.DEFAULT_FONT, Color.WHITE, 1.0f);
        
        public final Font font;
        public final Color color;
        public final float duration;
        
        private TextAnimationType(Font font, Color color, float duration){
            this.font = font;
            this.color = color;
            this.duration = duration;
        }
    }
    
    private String text;
    private TextAnimationType type;
    private float alpha = 1.0f; // fully visible
    
    public TextAnimation(int x, int y, String text, TextAnimationType type) {
        super(x, y, type.duration);
        this.text = text;
        this.type = type;
    }

    @Override
    protected void updateImpl(float completed) {
        switch (type) {
            case CHAT:
                break;
            case FADE:
                alpha = 1.0f - completed; // FALLTHRU
            case DAMAGE_PLAYER: // FALLTHRU
            case DAMAGE_ENEMY:
                y -= 1;
                break;
            case NONE:
                break;
            case SKILL:
                break;
            default:
                break;
        }
    }
    
    @Override
    public void draw(GraphicsContext gContext) {
        Graphics2D g = gContext.getGraphics();
        
        Composite tmp = g.getComposite();
        Composite c = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
        
        g.setFont(type.font);
        g.setColor(type.color);
        g.setComposite(c);
        g.drawString(text, x - gContext.getRenderX(), y - gContext.getRenderY());
        g.setComposite(tmp);
    }
}
