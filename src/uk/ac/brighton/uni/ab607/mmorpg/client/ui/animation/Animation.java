package uk.ac.brighton.uni.ab607.mmorpg.client.ui.animation;

import uk.ac.brighton.uni.ab607.mmorpg.client.ui.GraphicsContext;

/**
 * Any visible image/text/object that may or may not
 * change over the duration
 * 
 * @author Almas Baimagambetov
 * @version 1.1
 */
public abstract class Animation implements java.io.Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -1505513151259620352L;
    
    /**
     * Top left position of the animation frame
     */
    protected int x, y;
    
    /**
     * Current duration is amount of time units passed since
     * animation started
     * 
     * Max duration - at which animation ends
     */
    protected float currentDuration = 0.0f, maxDuration;
    
    public Animation(int x, int y, float maxDuration) {
        this.x = x;
        this.y = y;
        this.maxDuration = maxDuration;
    }
    
    public void update(float tick) {
        currentDuration += tick;
        updateImpl(currentDuration / maxDuration);
    }
    
    protected abstract void updateImpl(float completed);
    public abstract void draw(GraphicsContext gContext);
    
    public boolean hasFinished() {
        return currentDuration >= maxDuration;
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    public void setMaxDuration(float maxDuration) {
        this.maxDuration = maxDuration;
    }
}
