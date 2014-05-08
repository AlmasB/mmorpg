package uk.ac.brighton.uni.ab607.mmorpg.common;

public class StatusEffect implements java.io.Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 480489685432710301L;

    public enum Status {
        NORMAL, STUNNED, SILENCED, POISONED
    }

    private float duration;
    private Status status;

    public StatusEffect(Status status, float duration) {
        this.duration = duration;
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    public void reduceDuration(float value) {
        duration -= value;
    }

    public float getDuration() {
        return duration;
    }
}
