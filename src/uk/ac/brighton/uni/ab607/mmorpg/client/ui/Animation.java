package uk.ac.brighton.uni.ab607.mmorpg.client.ui;

public class Animation implements java.io.Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 877998634257114809L;
    
    public enum AnimationType {
        TEXT, IMAGE
    }
    
    private int x, y;
    public float duration;
    public final int ssX, ssY;

    public String data = "";
    
    //private AnimationType type;

    public Animation(int x, int y, float duration, int ssX, int ssY, String data) {
        this.x = x;
        this.y = y;
        this.duration = duration;
        this.ssX = ssX;
        this.ssY = ssY;
        this.data = data;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    /*public float getDuration() {
        return duration;
    }*/
}
