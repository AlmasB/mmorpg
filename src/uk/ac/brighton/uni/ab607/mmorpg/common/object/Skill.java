package uk.ac.brighton.uni.ab607.mmorpg.common.object;

import uk.ac.brighton.uni.ab607.mmorpg.common.GameCharacter;

public abstract class Skill implements java.io.Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 442371944346845569L;

    //protected static int uniqueSkillID = 7000;

    public String id;   // TODO: final?
    public final String name, description;

    /**
     * Active skills need to be cast, have mana cost and cooldown
     * Whereas not active skills (passive) are always ON
     */
    public final boolean active;

    /**
     * Skill cooldowns in seconds
     */
    protected float skillCooldown, currentCooldown = 0.0f;

    protected static final int MAX_LEVEL = 10;

    protected int level = 0;

    public Skill(String name, String description, Boolean active, Float cooldown) {
        this.name = name;
        this.description = description;
        this.active = active;
        this.skillCooldown = cooldown;
    }

    public void use(GameCharacter caster, GameCharacter target) {
        useImpl(caster, target);
        putOnCooldown();
    }

    public abstract int getManaCost();

    // TODO: on skill begin/end

    /**
     * Do not use this method directly
     * It is needed to provide overridability to new skills
     *
     * use public method - {@link use()}
     *
     * @param caster
     * @param target
     */
    protected abstract void useImpl(GameCharacter caster, GameCharacter target);

    // TODO: extra check by GUI
    public boolean levelUp() {
        if (level < MAX_LEVEL) {
            level++;
            return true;
        }
        return false;
    }

    public int getLevel() {
        return level;
    }

    public float getCurrentCooldown() {
        return currentCooldown;
    }

    public void reduceCurrentCooldown(float value) {
        currentCooldown = Math.max(currentCooldown - value, 0);
    }

    public void putOnCooldown() {
        currentCooldown = skillCooldown;
    }
}
