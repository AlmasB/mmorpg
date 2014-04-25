package uk.ac.brighton.uni.ab607.mmorpg.common;

public class Effect implements java.io.Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 9209936367329122501L;

    private float duration = 0.0f;

    public Effect(float duration) {
        this.duration = duration;
    }

    public void reduceDuration(float value) {
        duration -= value;
    }

    public float getDuration() {
        return duration;
    }
}
