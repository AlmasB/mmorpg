package uk.ac.brighton.uni.ab607.mmorpg.common.item;

public class DroppableItem implements java.io.Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -7613094483468439486L;
    public String itemID = "";
    public int dropChance = 0;

    public DroppableItem(String id, int chance) {
        itemID = id;
        dropChance = chance;
    }
}
