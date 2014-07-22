package uk.ac.brighton.uni.ab607.mmorpg.client.ui.animation;

import com.almasb.common.graphics.Color;
import com.almasb.common.graphics.GraphicsContext;

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
    public void draw(GraphicsContext g) {
        // atm only circle for skill target, more later
        g.setColor(Color.GOLD);
        //g.drawOval((int)(x - g.getRenderX() - radius*8), (int)(y - g.getRenderY() - radius*8),
        //      40 + (int)(16*radius), 40 + (int)(16*radius));
    }
}
