package uk.ac.brighton.uni.ab607.mmorpg.common;

public abstract class ActiveSkill extends Skill {

    /**
     *
     */
    private static final long serialVersionUID = 3482667800827843763L;

    // TODO: cooldown ?
    // TODO: reduce mana within use() ?
    // TODO: onEffectEnd()  overridable

    public ActiveSkill(String name, String description) {
        super(""+uniqueSkillID++, name, description);
    }

    public abstract int getManaCost();
    public abstract void use(GameCharacter caster, GameCharacter target);
}
