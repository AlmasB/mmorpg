package uk.ac.brighton.uni.ab607.mmorpg.common;

import uk.ac.brighton.uni.ab607.mmorpg.common.object.ID;
import uk.ac.brighton.uni.ab607.mmorpg.common.object.ObjectManager;
import uk.ac.brighton.uni.ab607.mmorpg.common.object.Skill;

/**
 * A game character will have one of these classes
 * Profession/job in a nutshell
 * 
 * @author Almas Baimagambetov
 *
 */
public enum GameCharacterClass {
    MONSTER(50, 50),
    NOVICE(10, 10),
    WARRIOR(100, 20, ID.Skill.Warrior.MIGHTY_SWING, ID.Skill.Warrior.DOUBLE_EDGE, ID.Skill.Warrior.LAST_STAND, ID.Skill.Warrior.ROAR, ID.Skill.Warrior.SHATTER_ARMOR, ID.Skill.Warrior.WARRIOR_HEART, ID.Skill.Warrior.ARMOR_MASTERY, ID.Skill.Warrior.BLOODLUST, ID.Skill.Warrior.BASH),
    SCOUT(75, 55, ID.Skill.Scout.CRITICAL_STRIKE, ID.Skill.Scout.DOUBLE_STRIKE, ID.Skill.Scout.EXPERIENCED_FIGHTER, ID.Skill.Scout.PINPOINT_WEAKNESS, ID.Skill.Scout.POISON_ATTACK, ID.Skill.Scout.SHAMELESS, ID.Skill.Scout.THROW_DAGGER, ID.Skill.Scout.TRIPLE_STRIKE, ID.Skill.Scout.WEAPON_MASTERY),
    MAGE(55, 100, ID.Skill.Mage.AIR_SPEAR, ID.Skill.Mage.AMPLIFY_MAGIC, ID.Skill.Mage.ASTRAL_PROTECTION, ID.Skill.Mage.EARTH_BOULDER, ID.Skill.Mage.FIREBALL, ID.Skill.Mage.ICE_SHARD, ID.Skill.Mage.MAGIC_MASTERY, ID.Skill.Mage.MAGIC_SHIELD, ID.Skill.Mage.MENTAL_STRIKE);

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
}
