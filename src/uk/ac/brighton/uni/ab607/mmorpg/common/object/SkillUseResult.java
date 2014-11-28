package uk.ac.brighton.uni.ab607.mmorpg.common.object;

public class SkillUseResult implements java.io.Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 606462172238372852L;

    public enum Target {
        SELF, ENEMY, AREA
    }

    public static SkillUseResult DEFAULT_FALSE = new SkillUseResult(Target.SELF, 0, false);
    public static SkillUseResult DEFAULT_TRUE = new SkillUseResult(Target.SELF, 0, true);

    public final Target target;
    public final int damage;
    public final boolean success;

    //public final boolean buffSkill ? if yes maybe get value of buff i.e. armor + 20%
    //public final int selfDamage/targetDamage ?

    /**
     * Hidden ctor
     *
     * @param target
     * @param damage
     * @param success
     */
    private SkillUseResult(Target target, int damage, boolean success) {
        this.target = target;
        this.damage = damage;
        this.success = success;
    }

    public SkillUseResult(Target target, int damage) {
        this(target, damage, true);
    }
}
