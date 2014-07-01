package uk.ac.brighton.uni.ab607.mmorpg.common;

import uk.ac.brighton.uni.ab607.mmorpg.common.object.ID;

/**
 * A game character will have one of these classes
 * Profession/job in a nutshell
 * 
 * @author Almas Baimagambetov
 *
 */
public enum GameCharacterClass {
    MONSTER  (50, 50),
    NOVICE   (10, 10),
    
    WARRIOR  (40, 20, ID.Skill.Warrior.MIGHTY_SWING, ID.Skill.Warrior.ROAR, ID.Skill.Warrior.WARRIOR_HEART, ID.Skill.Warrior.ARMOR_MASTERY),
    CRUSADER (60, 35, ID.Skill.Crusader.DIVINE_ARMOR, ID.Skill.Crusader.FAITH, ID.Skill.Crusader.HOLY_LIGHT, ID.Skill.Crusader.LAST_STAND, ID.Skill.Crusader.PRECISION_STRIKE),
    PALADIN  (90, 50),
    GLADIATOR(65, 30, ID.Skill.Gladiator.BASH, ID.Skill.Gladiator.BLOODLUST, ID.Skill.Gladiator.DOUBLE_EDGE, ID.Skill.Gladiator.ENDURANCE, ID.Skill.Gladiator.SHATTER_ARMOR),
    KNIGHT   (100, 35),
    
    SCOUT    (30, 35, ID.Skill.Scout.CRITICAL_STRIKE, ID.Skill.Scout.DOUBLE_STRIKE, ID.Skill.Scout.EXPERIENCED_FIGHTER, ID.Skill.Scout.PINPOINT_WEAKNESS, ID.Skill.Scout.POISON_ATTACK, ID.Skill.Scout.SHAMELESS, ID.Skill.Scout.THROW_DAGGER, ID.Skill.Scout.TRIPLE_STRIKE, ID.Skill.Scout.WEAPON_MASTERY),
    ROGUE    (50, 40),
    ASSASSIN (75, 45),
    RANGER   (40, 40),
    HUNTER   (50, 55),
    
    MAGE     (25, 45, ID.Skill.Mage.AIR_SPEAR, ID.Skill.Mage.AMPLIFY_MAGIC, ID.Skill.Mage.ASTRAL_PROTECTION, ID.Skill.Mage.EARTH_BOULDER, ID.Skill.Mage.FIREBALL, ID.Skill.Mage.ICE_SHARD, ID.Skill.Mage.MAGIC_MASTERY, ID.Skill.Mage.MAGIC_SHIELD, ID.Skill.Mage.MENTAL_STRIKE),
    WIZARD   (35, 60),
    ARCHMAGE (45, 75),
    ENCHANTER(30, 65),
    SAGE     (40, 90);
    
    public final int hp;
    public final int sp;
    public final String[] skillIDs;

    private GameCharacterClass(int hp, int sp, String... IDs) {
        this.hp = hp;
        this.sp = sp;
        this.skillIDs = IDs;
    }
}
