package uk.ac.brighton.uni.ab607.mmorpg.common;

/**
 * A container for commands/actions requested by the game client
 * 
 * @author Almas Baimagambetov
 * @version 1.1
 *
 */
public class ActionRequest implements java.io.Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1853741539850521348L;
    
    public enum Action {
        ATTR_UP, SKILL_UP, EQUIP, UNEQUIP, REFINE, USE_ITEM, ATTACK, SKILL_USE, CHAT, MOVE, CHANGE_CLASS, SAVE
    }

    public final Action action;
    public final String playerName, data;
    
    // add more as necessary, 2 should be sufficient
    public final int value1, value2;
    
    public ActionRequest(Action action, String player, String data, int... values) {
        this.action = action;
        this.playerName = player;
        this.data = data;
        this.value1 = values.length > 0 ? values[0] : 0;
        this.value2 = values.length > 1 ? values[1] : 0;
    }
    
    public ActionRequest(Action action, String player, int... values) {
        this(action, player, "", values);
    }
}
