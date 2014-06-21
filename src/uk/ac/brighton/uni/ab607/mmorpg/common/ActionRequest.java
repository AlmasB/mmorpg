package uk.ac.brighton.uni.ab607.mmorpg.common;

public class ActionRequest implements java.io.Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1853741539850521348L;
    
    public enum Action {
        ATTR_UP, SKILL_UP, EQUIP, UNEQUIP, REFINE, USE_ITEM, ATTACK, SKILL_USE, CHAT, MOVE
    }

    public final Action action;
    public final String playerName, data;
    private int[] values;
    
    public ActionRequest(Action action, String player, String data, int... values) {
        this.action = action;
        this.playerName = player;
        this.data = data;
        this.values = values;
    }
    
    public int[] getValues() {
        return values;
    }
}
