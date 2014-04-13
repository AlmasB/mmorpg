package uk.ac.brighton.uni.ab607.mmorpg.common;

public abstract class ActiveSkill extends Skill {

    /**
     *
     */
    private static final long serialVersionUID = 3482667800827843763L;

    public ActiveSkill(String name, String description) {
        super(""+uniqueSkillID++, name, description);
    }

    public abstract int getManaCost(int level);
    public abstract void use(GameCharacter caster, GameCharacter target);
}
