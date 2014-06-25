package uk.ac.brighton.uni.ab607.mmorpg.common.object;

import uk.ac.brighton.uni.ab607.mmorpg.client.ui.animation.Animation;

public class SkillUseResult implements java.io.Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 606462172238372852L;

    public enum Target {
        SELF, ENEMY, AREA
    }
    
    public static SkillUseResult DEFAULT_FALSE = new SkillUseResult(Target.SELF, 0, false, null);
    public static SkillUseResult DEFAULT_TRUE = new SkillUseResult(Target.SELF, 0, true, null);
    
    public final Target target;
    public final int damage;
    public final boolean success;
    
    /**
     * Animation of the skill - can be null
     */
    public final Animation animation;
    //public final boolean buffSkill ? if yes maybe get value of buff i.e. armor + 20%
    //public final int selfDamage/targetDamage ?
    
    /**
     * Hidden ctor
     * 
     * @param target
     * @param damage
     * @param success
     */
    private SkillUseResult(Target target, int damage, boolean success, Animation animation) {
        this.target = target;
        this.damage = damage;
        this.success = success;
        this.animation = animation;
    }
    
    public SkillUseResult(Target target, int damage, Animation anim) {
        this(target, damage, true, anim);
    }
    
    public SkillUseResult(Target target, int damage) {
        this(target, damage, true, null);
    }
}
