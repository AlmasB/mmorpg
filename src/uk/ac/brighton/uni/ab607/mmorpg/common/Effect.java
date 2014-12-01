package uk.ac.brighton.uni.ab607.mmorpg.common;

import uk.ac.brighton.uni.ab607.mmorpg.common.object.Essence;

/**
 * Buff that lasts for a period of time, can be negative
 * i.e. atk decrease
 * 
 * @author Almas Baimagambetov
 *
 */
public class Effect implements java.io.Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 9209936367329122501L;

    private Rune[] runes;   // attribute bonuses
    private Essence[] essences; // stat bonuses

    private float duration;
    
    /**
     * ID of the skill that created this effect
     */
    public final String sourceID;

    public Effect(float duration, String sourceID, Rune[] runes, Essence[] essences) {
        this.duration = duration;
        this.runes = runes;
        this.essences = essences;
        this.sourceID = sourceID;
    }

    public void reduceDuration(float value) {
        duration -= value;
    }

    public float getDuration() {
        return duration;
    }

    public void onBegin(GameCharacter ch) {
        for (Rune r : runes)
            ch.addBonusAttribute(r.attribute, r.bonus);
        for (Essence e : essences)
            ch.addBonusStat(e.stat, e.bonus);
    }

    public void onEnd(GameCharacter ch) {
        for (Rune r : runes)
            ch.addBonusAttribute(r.attribute, -r.bonus);
        for (Essence e : essences)
            ch.addBonusStat(e.stat, -e.bonus);
    }
}
