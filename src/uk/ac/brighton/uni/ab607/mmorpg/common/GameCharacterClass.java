package uk.ac.brighton.uni.ab607.mmorpg.common;

import uk.ac.brighton.uni.ab607.mmorpg.common.object.ID;
import uk.ac.brighton.uni.ab607.mmorpg.common.object.ObjectManager;
import uk.ac.brighton.uni.ab607.mmorpg.common.object.Skill;

public enum GameCharacterClass {
    MONSTER(50, 50),
    NOVICE(10, 10, ID.Skill.MIGHTY_SWING, ID.Skill.DOUBLE_EDGE, ID.Skill.LAST_STAND, ID.Skill.ROAR, ID.Skill.SHATTER_ARMOR, ID.Skill.WARRIOR_HEART, ID.Skill.ARMOR_MASTERY, ID.Skill.BLOODLUST, ID.Skill.BASH),
    WARRIOR(100, 20),
    SCOUT(75, 55),
    MAGE(55, 100, ID.Skill.HEAL);

    public final int hp;
    public final int sp;
    public final Skill[] skills;

    private GameCharacterClass(int hp, int sp, String... IDs) {
        this.hp = hp;
        this.sp = sp;
        this.skills = new Skill[IDs.length];
        for (int i = 0; i < skills.length; i++)
            skills[i] = ObjectManager.getSkillByID(IDs[i]);
    }

    @Override
    public String toString() {
        return this.name();
    }
}
