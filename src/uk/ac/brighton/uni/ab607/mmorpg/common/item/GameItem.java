package uk.ac.brighton.uni.ab607.mmorpg.common.item;

public abstract class GameItem implements java.io.Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 924533834152552568L;

    public final String id, name, description;

    public final int ssX, ssY;

    public GameItem(String id, String name, String description) {
        this(id, name, description, 6, 1);
    }

    public GameItem(String id, String name, String description, int x, int y) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.ssX = x;
        this.ssY = y;
    }

    @Override
    public String toString() {
        return id + ":" + name;
    }

    // make abstract
    public String toPseudoHTML() {
        return "PSEUDO";
    }

    public String toPseudoHTMLShort() {
        return "PSEUDO";
    }
}
