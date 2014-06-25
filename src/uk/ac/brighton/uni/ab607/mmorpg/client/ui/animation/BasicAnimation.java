package uk.ac.brighton.uni.ab607.mmorpg.client.ui.animation;

import java.awt.Graphics2D;

import uk.ac.brighton.uni.ab607.mmorpg.client.ui.GraphicsContext;

/**
 * This mainly uses java.awt to create basic shape based
 * animations
 * 
 * @author Almas Baimagambetov
 *
 */
public class BasicAnimation extends Animation {
    /**
     * 
     */
    private static final long serialVersionUID = -7369869688084003215L;
    
    private float radius = 1.0f;

    public BasicAnimation(int x, int y, float maxDuration) {
        super(x, y, maxDuration);
    }

    @Override
    protected void updateImpl(float completed) {
        radius = 1.0f + completed;
    }

    @Override
    public void draw(GraphicsContext gContext) {
        Graphics2D g = gContext.getGraphics();
        // atm only circle for skill target, more later
        g.setColor(AnimationUtils.COLOR_GOLD);
        g.drawOval((int)(x - gContext.getRenderX() - radius*10), (int)(y - gContext.getRenderY() - radius*10),
                30 + (int)(20*radius), 30 + (int)(20*radius));
    }
}
