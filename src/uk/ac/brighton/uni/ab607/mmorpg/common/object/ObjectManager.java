package uk.ac.brighton.uni.ab607.mmorpg.common.object;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import uk.ac.brighton.uni.ab607.mmorpg.common.Attribute;
import uk.ac.brighton.uni.ab607.mmorpg.common.Effect;
import uk.ac.brighton.uni.ab607.mmorpg.common.GameCharacter;
import uk.ac.brighton.uni.ab607.mmorpg.common.GameMath;
import uk.ac.brighton.uni.ab607.mmorpg.common.Stat;
import uk.ac.brighton.uni.ab607.mmorpg.common.StatusEffect;
import uk.ac.brighton.uni.ab607.mmorpg.common.StatusEffect.Status;
import uk.ac.brighton.uni.ab607.mmorpg.common.ai.AgentBehaviour;
import uk.ac.brighton.uni.ab607.mmorpg.common.ai.AgentBehaviour.*;
import uk.ac.brighton.uni.ab607.mmorpg.common.combat.Element;
import uk.ac.brighton.uni.ab607.mmorpg.common.item.DroppableItem;
import uk.ac.brighton.uni.ab607.mmorpg.common.item.EquippableItem.ItemLevel;
import uk.ac.brighton.uni.ab607.mmorpg.common.item.GameItem;
import uk.ac.brighton.uni.ab607.mmorpg.common.item.Rune;
import uk.ac.brighton.uni.ab607.mmorpg.common.object.Armor.ArmorType;
import uk.ac.brighton.uni.ab607.mmorpg.common.object.Enemy.EnemyType;
import uk.ac.brighton.uni.ab607.mmorpg.common.object.SkillUseResult.Target;
import uk.ac.brighton.uni.ab607.mmorpg.common.object.Weapon.WeaponType;

public class ObjectManager {

    private static HashMap<String, Weapon> defaultWeapons = new HashMap<String, Weapon>();
    private static HashMap<String, Armor> defaultArmor = new HashMap<String, Armor>();
    private static HashMap<String, Skill> defaultSkills = new HashMap<String, Skill>();
    private static HashMap<String, Enemy> defaultEnemies = new HashMap<String, Enemy>();
    private static HashMap<String, Essence> defaultEssences = new HashMap<String, Essence>();

    private ObjectManager() {}

    public static void load() {
        // ARMOR

        addArmor(new Armor(ID.Armor.HAT, "Hat", Desc.Armor.HAT, 10, 13, ArmorType.HELM, 0, 0));    // 5000, matches type enum for easy equip
        addArmor(new Armor(ID.Armor.CLOTHES, "Clothes", Desc.Armor.CLOTHES, 0, 13, ArmorType.BODY, 0, 0));  // 5001
        addArmor(new Armor(ID.Armor.SHOES, "Shoes", Desc.Armor.SHOES, 0, 14, ArmorType.SHOES, 0, 0));    // 5002
        addArmor(new Armor(ID.Armor.CHAINMAL, "Chainmail", Desc.Armor.CHAINMAL, 4, 13, ArmorType.BODY, 15, 5));

        addArmor(new Armor(ID.Armor.SOUL_BARRIER, "Soul Barrier", Desc.Armor.SOUL_BARRIER, 3, 13,
                "Stefos", ItemLevel.UNIQUE, ArmorType.BODY, 10, 50, Element.NEUTRAL, 3, new Rune(Attribute.WILLPOWER, 3)));

        addArmor(new Armor(ID.Armor.DOMOVOI, "Domovoi", Desc.Armor.DOMOVOI, 2, 13,
                "Joe", ItemLevel.UNIQUE, ArmorType.BODY, 15, 30, Element.NEUTRAL, 4, new Rune(Attribute.WILLPOWER, 3)));

        addArmor(new Armor(ID.Armor.SAPPHIRE_LEGION_PLATE_MAIL, "Sapphire Legion Plate Mail", Desc.Armor.SAPPHIRE_LEGION_PLATE_MAIL, 5, 13,
                "Oliver", ItemLevel.UNIQUE, ArmorType.BODY, 30, 10, Element.NEUTRAL, 4, new Rune(Attribute.VITALITY, 4)));

        addArmor(new Armor(ID.Armor.THANATOS_BODY_ARMOR, "Thanatos Body Armor", Desc.Armor.THANATOS_BODY_ARMOR, 6, 13,
                "Almas", ItemLevel.EPIC, ArmorType.BODY, 50, 25, Element.EARTH, 4, new Rune(Attribute.VITALITY, 5), new Rune(Attribute.PERCEPTION, 4)));

        // WEAPON

        addWeapon(new Weapon(ID.Weapon.HANDS, "Hands", Desc.Weapon.HANDS, 0, 7, WeaponType.MACE, 0));
        addWeapon(new Weapon(ID.Weapon.IRON_SWORD, "Iron Sword", Desc.Weapon.IRON_SWORD, 0, 5, WeaponType.ONE_H_SWORD, 15));
        addWeapon(new Weapon(ID.Weapon.KNIFE, "Knife", Desc.Weapon.KNIFE, 0, 6, WeaponType.DAGGER, 5));
        addWeapon(new Weapon(ID.Weapon.CLAYMORE, "Claymore", Desc.Weapon.CLAYMORE, 10, 5, WeaponType.TWO_H_SWORD, 35));
        addWeapon(new Weapon(ID.Weapon.BROADSWORD, "Broadsword", Desc.Weapon.BROADSWORD, 11, 5, WeaponType.TWO_H_SWORD, 28));

        addWeapon(new Weapon(ID.Weapon.BATTLESWORD,
                "Battlesword", Desc.Weapon.BATTLESWORD, 12, 5,
                "Almas", ItemLevel.NORMAL, WeaponType.TWO_H_SWORD, 44, Element.NEUTRAL, 2,
                new Rune(Attribute.STRENGTH, 2)));

        addWeapon(new Weapon(ID.Weapon.LONGSWORD,
                "Longsword", Desc.Weapon.LONGSWORD, 9, 5,
                "Almas", ItemLevel.NORMAL, WeaponType.TWO_H_SWORD, 33, Element.NEUTRAL, 2,
                new Rune(Attribute.DEXTERITY, 2), new Rune(Attribute.AGILITY, 1)));

        addWeapon(new Weapon(ID.Weapon.GETSUGA_TENSHO,
                "Getsuga Tensho", Desc.Weapon.GETSUGA_TENSHO, 4, 6,
                "Matthew", ItemLevel.EPIC, WeaponType.ONE_H_SWORD, 150, Element.NEUTRAL, 4,
                new Rune(Attribute.STRENGTH, 5), new Rune(Attribute.AGILITY, 4), new Rune(Attribute.DEXTERITY, 4), new Rune(Attribute.LUCK, 1)));

        addWeapon(new Weapon(ID.Weapon.SOUL_REAPER,
                "Soul Reaper", Desc.Weapon.SOUL_REAPER, 10, 10,
                "Sam Bowen", ItemLevel.EPIC, WeaponType.TWO_H_AXE, 170, Element.NEUTRAL, 4,
                new Rune(Attribute.STRENGTH, 7), new Rune(Attribute.VITALITY, 4), new Rune(Attribute.DEXTERITY, 2)));

        addWeapon(new Weapon(ID.Weapon.GUT_RIPPER,
                "The Gut Ripper", Desc.Weapon.GUT_RIPPER, 2, 6,
                "Tim Snow", ItemLevel.EPIC, WeaponType.DAGGER, 100, Element.NEUTRAL, 4,
                new Rune(Attribute.AGILITY, 4), new Rune(Attribute.DEXTERITY, 4), new Rune(Attribute.LUCK, 1)));

        addWeapon(new Weapon(ID.Weapon.DRAGON_CLAW,
                "Dragon's Claw", Desc.Weapon.DRAGON_CLAW, 12, 11,
                "Atheryos", ItemLevel.EPIC, WeaponType.BOW, 130, Element.FIRE, 4,
                new Rune(Attribute.VITALITY, 3), new Rune(Attribute.WISDOM, 5), new Rune(Attribute.AGILITY, 3)));

        addWeapon(new Weapon(ID.Weapon.FROSTMOURN, "Frostmourn", Desc.Weapon.FROSTMOURN, 8, 25,
                "Stefos", ItemLevel.EPIC, WeaponType.TWO_H_SWORD, 130, Element.WATER, 4,
                new Rune(Attribute.DEXTERITY, 5), new Rune(Attribute.STRENGTH, 3)));

        // SKILLS

        /*addSkill(new Skill(ID.Skill.HEAL, "Heal", Desc.Skill.HEAL, true, 10.0f) {
            private static final long serialVersionUID = 1379897406205500901L;

            @Override
            public int getManaCost() {
                return level * 10;
            }

            @Override
            public void useImpl(GameCharacter caster, GameCharacter target) {
                target.setHP(Math.min(target.getHP() + 30 + level*10, (int)target.getTotalStat(Stat.MAX_HP)));
            }
        });

        addSkill(new Skill(ID.Skill.MANA_BURN, "Mana Burn", Desc.Skill.MANA_BURN, true, 10.0f) {
            private static final long serialVersionUID = 7719535667188968500L;

            @Override
            public int getManaCost() {
                return 50 + level * 25;
            }

            @Override
            public void useImpl(GameCharacter caster, GameCharacter target) {
                int oldSP = target.getSP();
                target.setSP(Math.max(oldSP - 50 * level, 0));
                caster.dealMagicalDamage(target, oldSP-target.getSP(), Element.NEUTRAL);
            }
        });

        addSkill(new Skill(ID.Skill.FINAL_STRIKE, "Final Strike", Desc.Skill.FINAL_STRIKE, true, 10.0f) {
            private static final long serialVersionUID = 2091028246707933529L;

            @Override
            public int getManaCost() {
                return 100 + level * 100;
            }

            @Override
            public void useImpl(GameCharacter caster, GameCharacter target) {
                float phys = (caster.getHP() - 1) * 0.003f + 250*level;
                float mag  = caster.getSP() * 0.003f + 250*level;
                caster.setHP(1);
                caster.setSP(0);
                caster.dealMagicalDamage(target, mag, Element.NEUTRAL);
                caster.dealPhysicalDamage(target, phys, Element.NEUTRAL);
            }
        });

        // PIERCING TOUCH
        addSkill(new Skill(ID.Skill.PIERCING_TOUCH, "Piercing Touch", Desc.Skill.PIERCING_TOUCH, true, 9.0f) {
            private static final long serialVersionUID = 1513947512801417510L;

            @Override
            public int getManaCost() {
                return 25 + level * 30;
            }

            @Override
            public void useImpl(GameCharacter caster, GameCharacter target) {
                float dmg = level * 5 * (15 + target.getTotalStat(GameCharacter.ARM) / 100.0f);
                caster.dealPhysicalDamage(target, dmg);
            }
        });

        // BULLSEYE
        addSkill(new Skill(ID.Skill.BULLSEYE, "Bullseye", Desc.Skill.BULLSEYE, true, 60.0f) {
            private static final long serialVersionUID = 6923525936384357867L;

            @Override
            public int getManaCost() {
                return 5 + level * 10;
            }

            @Override
            public void useImpl(GameCharacter caster, GameCharacter target) {
                float dmg = 100 + level * 85 - target.getTotalStat(GameCharacter.DEF);
                caster.dealPureDamage(target, dmg);
            }
        });

        // CLAUDIUS
        addSkill(new Skill(ID.Skill.CLAUDIUS, "Claudius", Desc.Skill.CLAUDIUS, true, 30.0f) {
            private static final long serialVersionUID = 5612472865672733186L;

            @Override
            public int getManaCost() {
                return 25 + level * 30;
            }

            @Override
            public void useImpl(GameCharacter caster, GameCharacter target) {
                target.addBonusAttribute(Attribute.VITALITY, 2*level);
                target.addBonusAttribute(Attribute.WISDOM, 2*level);
                target.addBonusAttribute(Attribute.LUCK, 2*level);
                target.addBonusAttribute(Attribute.INTELLECT, -3*level);
            }
        });

        // Five finger death punch
        addSkill(new Skill(ID.Skill.FIVE_FINGER_DEATH_PUNCH, "Five Finger Death Punch", Desc.Skill.FIVE_FINGER_DEATH_PUNCH,
                true, 30.0f) {
            private static final long serialVersionUID = 168758926959802026L;

            @Override
            public int getManaCost() {
                return 25 + level * 30;
            }

            @Override
            public void useImpl(GameCharacter caster, GameCharacter target) {
                float dmg = 20 + level*30 - target.getTotalStat(Stat.ARM);
                caster.dealPhysicalDamage(target, dmg);
            }
        });*/

        addSkill(new Skill(ID.Skill.Warrior.BLOODLUST, "Bloodlust", Desc.Skill.Warrior.BLOODLUST, false, 0.0f) {
            /**
             *
             */
            private static final long serialVersionUID = 5844145407908548491L;

            private int value = 0;

            @Override
            public int getManaCost() {
                return 0;
            }

            @Override
            protected void useImpl(GameCharacter caster, GameCharacter target) {
                caster.addBonusStat(Stat.ATK, -value);
                // div 0 shouldn't occur
                value = (int) (10*level * caster.getTotalStat(Stat.MAX_HP) / (caster.getHP() + 1));
                caster.addBonusStat(Stat.ATK, value);
            }
        });

        addSkill(new Skill(ID.Skill.Warrior.BASH, "Bash", Desc.Skill.Warrior.BASH, true, 15.0f) {
            /**
             *
             */
            private static final long serialVersionUID = 2177640389884854474L;

            @Override
            public int getManaCost() {
                return 5 + level * 3;
            }

            @Override
            protected void useImpl(GameCharacter caster, GameCharacter target) {
                float dmg = (1 + (15 + 5*level) / 100.0f) * caster.getTotalStat(Stat.ATK);
                caster.dealPhysicalDamage(target, dmg);
                target.addStatusEffect(new StatusEffect(Status.STUNNED, 5.0f));
            }
        });

        addSkill(new Skill(ID.Skill.Warrior.MIGHTY_SWING, "Mighty Swing", Desc.Skill.Warrior.MIGHTY_SWING, true, 15.0f) {
            /**
             *
             */
            private static final long serialVersionUID = 8019137126608309704L;

            @Override
            public int getManaCost() {
                return 5 + level * 4;
            }

            @Override
            protected void useImpl(GameCharacter caster, GameCharacter target) {
                float diff = caster.getTotalAttribute(Attribute.STRENGTH) - target.getTotalAttribute(Attribute.STRENGTH);
                float dmg = (Math.max(diff, 0) + 10*level) * 5;
                caster.dealPhysicalDamage(target, dmg);
                useResult = new SkillUseResult(Target.ENEMY, (int) dmg);
            }
        });

        addSkill(new Skill(ID.Skill.Warrior.DOUBLE_EDGE, "Double Edge", Desc.Skill.Warrior.DOUBLE_EDGE, true, 0.0f) {
            /**
             *
             */
            private static final long serialVersionUID = -5670132035647752285L;

            @Override
            public int getManaCost() {
                return 0;
            }

            @Override
            protected void useImpl(GameCharacter caster, GameCharacter target) {
                float dmg = (0.1f + 0.02f * level) * caster.getHP();
                caster.setHP(Math.round(caster.getHP() - dmg));
                caster.dealPureDamage(target, 2*dmg);
            }
        });

        addSkill(new Skill(ID.Skill.Warrior.ROAR, "Roar", Desc.Skill.Warrior.ROAR, true, 5.0f) {
            /**
             *
             */
            private static final long serialVersionUID = 5098091102433780519L;

            @Override
            public int getManaCost() {
                return 2 + level*2;
            }

            @Override
            protected void useImpl(GameCharacter caster, GameCharacter target) {
                caster.addEffect(new Effect((5.0f),
                        new Rune[] {
                        new Rune(Attribute.STRENGTH, level*2),
                        new Rune(Attribute.VITALITY, level*2)
                },
                new Essence[] {}
                        ));
            }
        });

        addSkill(new Skill(ID.Skill.Warrior.LAST_STAND, "Last Stand", Desc.Skill.Warrior.LAST_STAND, true, 60.0f) {
            /**
             *
             */
            private static final long serialVersionUID = -8176078084748576113L;

            @Override
            public int getManaCost() {
                return 2 + level*5;
            }

            @Override
            protected void useImpl(GameCharacter caster, GameCharacter target) {
                caster.addEffect(new Effect((20.0f),
                        new Rune[] {
                }, new Essence[] {
                        new Essence(Stat.ATK, Math.round(caster.getBaseStat(Stat.ATK)))
                }
                        ));
            }
        });

        addSkill(new Skill(ID.Skill.Warrior.SHATTER_ARMOR, "Shatter Armor", Desc.Skill.Warrior.SHATTER_ARMOR, true, 30.0f) {
            /**
             *
             */
            private static final long serialVersionUID = -4834599835655165707L;

            @Override
            public int getManaCost() {
                return 2 + level*5;
            }

            @Override
            protected void useImpl(GameCharacter caster, GameCharacter target) {
                target.addEffect(new Effect((20.0f),
                        new Rune[] {
                }, new Essence[] {
                        new Essence(Stat.ARM, -2*level)
                }
                        ));
            }
        });

        addSkill(new Skill(ID.Skill.Warrior.ARMOR_MASTERY, "Armor Mastery", Desc.Skill.Warrior.ARMOR_MASTERY, false, 0.0f) {
            /**
             *
             */
            private static final long serialVersionUID = 8019137126608309704L;

            private int value = 0;

            @Override
            public int getManaCost() {
                return 0;
            }

            @Override
            protected void useImpl(GameCharacter caster, GameCharacter target) {
                caster.addBonusStat(Stat.ARM, -value);
                value = 2 * level;
                caster.addBonusStat(Stat.ARM, value);
            }
        });

        addSkill(new Skill(ID.Skill.Warrior.WARRIOR_HEART, "Heart of a warrior", Desc.Skill.Warrior.WARRIOR_HEART, false, 0.0f) {
            /**
             *
             */
            private static final long serialVersionUID = -9161209014480342120L;

            private int value = 0;

            @Override
            public int getManaCost() {
                return 0;
            }

            @Override
            protected void useImpl(GameCharacter caster, GameCharacter target) {
                caster.addBonusStat(Stat.MAX_HP, -value);
                value = Math.round(0.025f * level * caster.getBaseStat(Stat.MAX_HP));
                caster.addBonusStat(Stat.MAX_HP, value);
            }
        });

        // MAGE SKILL SET

        addSkill(new Skill(ID.Skill.Mage.AIR_SPEAR, "Air Spear", Desc.Skill.Mage.AIR_SPEAR, true, 9.0f) {
            /**
             *
             */
            private static final long serialVersionUID = 6306777256266732648L;

            @Override
            public int getManaCost() {
                return 5 + level * 5;
            }

            @Override
            protected void useImpl(GameCharacter caster, GameCharacter target) {
                float dmg = caster.getTotalStat(Stat.MATK) + level *20;
                caster.dealMagicalDamage(target, dmg, Element.AIR);
            }
        });

        addSkill(new Skill(ID.Skill.Mage.AMPLIFY_MAGIC, "Amplify Magic", Desc.Skill.Mage.AMPLIFY_MAGIC, true, 30.0f) {
            /**
             *
             */
            private static final long serialVersionUID = -6423702278665617928L;

            @Override
            public int getManaCost() {
                return 5 + level * 5;
            }

            @Override
            protected void useImpl(GameCharacter caster, GameCharacter target) {
                caster.addEffect(new Effect((15.0f),
                        new Rune[] {},
                        new Essence[] {
                        new Essence(Stat.MATK, 10*level)
                }
                        ));
            }
        });

        addSkill(new Skill(ID.Skill.Mage.ASTRAL_PROTECTION, "Astral Protection", Desc.Skill.Mage.ASTRAL_PROTECTION, false, 0.0f) {
            /**
             *
             */
            private static final long serialVersionUID = 8691650266711866295L;

            private int value = 0;

            @Override
            public int getManaCost() {
                return 0;
            }

            @Override
            protected void useImpl(GameCharacter caster, GameCharacter target) {
                caster.addBonusStat(Stat.MDEF, -value);
                value = level * 2;
                caster.addBonusStat(Stat.MDEF, value);
            }
        });

        addSkill(new Skill(ID.Skill.Mage.EARTH_BOULDER, "Earth Boulder", Desc.Skill.Mage.EARTH_BOULDER, true, 15.0f) {
            /**
             *
             */
            private static final long serialVersionUID = 1871962939560471153L;

            @Override
            public int getManaCost() {
                return 5 + level * 5;
            }

            @Override
            protected void useImpl(GameCharacter caster, GameCharacter target) {
                float dmg = caster.getTotalStat(Stat.MATK) + level *25;
                caster.dealMagicalDamage(target, dmg, Element.EARTH);
            }
        });

        addSkill(new Skill(ID.Skill.Mage.FIREBALL, "Fireball", Desc.Skill.Mage.FIREBALL, true, 9.0f) {
            /**
             *
             */
            private static final long serialVersionUID = -1839096679550971399L;

            @Override
            public int getManaCost() {
                return 5 + level * 5;
            }

            @Override
            protected void useImpl(GameCharacter caster, GameCharacter target) {
                float dmg = caster.getTotalStat(Stat.MATK) + level *20;
                caster.dealMagicalDamage(target, dmg, Element.FIRE);
            }
        });

        addSkill(new Skill(ID.Skill.Mage.ICE_SHARD, "Ice Shard", Desc.Skill.Mage.ICE_SHARD, true, 9.0f) {
            /**
             *
             */
            private static final long serialVersionUID = 5561489415884518543L;

            @Override
            public int getManaCost() {
                return 5 + level * 5;
            }

            @Override
            protected void useImpl(GameCharacter caster, GameCharacter target) {
                float dmg = caster.getTotalStat(Stat.MATK) + level *20;
                caster.dealMagicalDamage(target, dmg, Element.WATER);
            }
        });

        addSkill(new Skill(ID.Skill.Mage.MAGIC_MASTERY, "Magic Mastery", Desc.Skill.Mage.MAGIC_MASTERY, false, 0.0f) {
            /**
             *
             */
            private static final long serialVersionUID = 9020149732268399438L;

            private int value = 0;

            @Override
            public int getManaCost() {
                return 0;
            }

            @Override
            protected void useImpl(GameCharacter caster, GameCharacter target) {
                caster.addBonusAttribute(Attribute.INTELLECT, -value);
                caster.addBonusAttribute(Attribute.WILLPOWER, -value);
                value = level * 2;
                caster.addBonusAttribute(Attribute.INTELLECT, value);
                caster.addBonusAttribute(Attribute.WILLPOWER, value);
            }
        });

        addSkill(new Skill(ID.Skill.Mage.MAGIC_SHIELD, "Magic Shield", Desc.Skill.Mage.MAGIC_SHIELD, true, 60.0f) {
            /**
             *
             */
            private static final long serialVersionUID = 7104420977798092420L;

            @Override
            public int getManaCost() {
                return 5 + level * 5;
            }

            @Override
            protected void useImpl(GameCharacter caster, GameCharacter target) {
                caster.addEffect(new Effect((25.0f),
                        new Rune[] {},
                        new Essence[] {
                        new Essence(Stat.ARM, 5*level)
                }
                        ));
            }
        });

        addSkill(new Skill(ID.Skill.Mage.MENTAL_STRIKE, "Mental Strike", Desc.Skill.Mage.MENTAL_STRIKE, true, 20.0f) {

            /**
             *
             */
            private static final long serialVersionUID = -55046003688618764L;

            @Override
            public int getManaCost() {
                return 5 + level * 5;
            }

            @Override
            protected void useImpl(GameCharacter caster, GameCharacter target) {
                float dmg = caster.getTotalStat(Stat.MATK) * (1 + level*0.1f);
                caster.dealPureDamage(target, dmg);
            }
        });

        // SCOUT SKILL SET

        addSkill(new Skill(ID.Skill.Scout.CRITICAL_STRIKE, "Critical Strike", Desc.Skill.Scout.CRITICAL_STRIKE, true, 20.0f) {
            /**
             *
             */
            private static final long serialVersionUID = -7584376145233708322L;

            @Override
            public int getManaCost() {
                return 3 + level * 2;
            }

            @Override
            protected void useImpl(GameCharacter caster, GameCharacter target) {
                float dmg = caster.getTotalStat(Stat.ATK) + 15 + 5 * level;
                caster.addBonusStat(Stat.CRIT_CHANCE, 50 + level * 3);
                caster.dealPhysicalDamage(target, dmg);
                caster.addBonusStat(Stat.CRIT_CHANCE, -(50 + level * 3));
            }
        });

        addSkill(new Skill(ID.Skill.Scout.PINPOINT_WEAKNESS, "Pinpoint Weakness", Desc.Skill.Scout.PINPOINT_WEAKNESS, true, 15.0f) {
            /**
             *
             */
            private static final long serialVersionUID = 2458408699758838323L;

            @Override
            public int getManaCost() {
                return 3 + level * 2;
            }

            @Override
            protected void useImpl(GameCharacter caster, GameCharacter target) {
                target.addEffect(new Effect((10.0f),
                        new Rune[] {},
                        new Essence[] {
                        new Essence(Stat.ARM, -2*level)
                }
                        ));
            }
        });

        addSkill(new Skill(ID.Skill.Scout.DOUBLE_STRIKE, "Double Strike", Desc.Skill.Scout.DOUBLE_STRIKE, true, 8.0f) {
            /**
             *
             */
            private static final long serialVersionUID = 5685022402103377679L;

            @Override
            public int getManaCost() {
                return 3 + level * 2;
            }

            @Override
            protected void useImpl(GameCharacter caster, GameCharacter target) {
                caster.attack(target);
                caster.attack(target);
                if (GameMath.checkChance(level*5)) {
                    target.addStatusEffect(new StatusEffect(Status.STUNNED, 2.5f));
                }
            }
        });

        addSkill(new Skill(ID.Skill.Scout.EXPERIENCED_FIGHTER, "Experienced Fighter", Desc.Skill.Scout.EXPERIENCED_FIGHTER, false, 0.0f) {
            /**
             *
             */
            private static final long serialVersionUID = 2024648263069876L;

            private int value = 0;

            @Override
            public int getManaCost() {
                return 0;
            }

            @Override
            protected void useImpl(GameCharacter caster, GameCharacter target) {
                caster.addBonusAttribute(Attribute.AGILITY, -value);
                caster.addBonusAttribute(Attribute.DEXTERITY, -value);
                value = level * 2;
                caster.addBonusAttribute(Attribute.AGILITY, value);
                caster.addBonusAttribute(Attribute.DEXTERITY, value);
            }
        });

        addSkill(new Skill(ID.Skill.Scout.SHAMELESS, "Shameless", Desc.Skill.Scout.SHAMELESS, true, 0.0f) {
            /**
             *
             */
            private static final long serialVersionUID = 2306928037030551618L;

            @Override
            public int getManaCost() {
                return 10;
            }

            @Override
            protected void useImpl(GameCharacter caster, GameCharacter target) {
                float dmg = caster.getTotalStat(Stat.ATK);
                float casterHPFactor = caster.getHP() / caster.getTotalStat(Stat.MAX_HP);
                float targetHPFactor = target.getHP() / target.getTotalStat(Stat.MAX_HP);
                if (casterHPFactor > targetHPFactor) {
                    dmg += level * 0.1f * (casterHPFactor - targetHPFactor) * dmg;
                }

                caster.dealPhysicalDamage(target, dmg);
            }
        });

        addSkill(new Skill(ID.Skill.Scout.WEAPON_MASTERY, "Weapon Mastery", Desc.Skill.Scout.WEAPON_MASTERY, false, 0.0f) {
            /**
             *
             */
            private static final long serialVersionUID = -6762381875332894326L;

            private int value = 0;

            @Override
            public int getManaCost() {
                return 0;
            }

            @Override
            protected void useImpl(GameCharacter caster, GameCharacter target) {
                caster.addBonusStat(Stat.ATK, -value);
                value = level * 7;
                caster.addBonusStat(Stat.ATK, value);
            }
        });

        addSkill(new Skill(ID.Skill.Scout.POISON_ATTACK, "Poison Attack", Desc.Skill.Scout.POISON_ATTACK, true, 30.0f) {
            /**
             *
             */
            private static final long serialVersionUID = 6791451275759000638L;

            @Override
            public int getManaCost() {
                return 3 + level*2;
            }

            @Override
            protected void useImpl(GameCharacter caster, GameCharacter target) {
                caster.attack(target);
                if (GameMath.checkChance(level*7)) {
                    target.addStatusEffect(new StatusEffect(Status.POISONED, 10.0f));
                }
            }
        });

        addSkill(new Skill(ID.Skill.Scout.THROW_DAGGER, "Throw Dagger", Desc.Skill.Scout.THROW_DAGGER, true, 20.0f) {
            /**
             *
             */
            private static final long serialVersionUID = 1031700846462374399L;

            @Override
            public int getManaCost() {
                return 5 + level*2;
            }

            @Override
            protected void useImpl(GameCharacter caster, GameCharacter target) {
                float dmg = caster.getTotalStat(Stat.ATK) + level * 20;
                caster.dealPhysicalDamage(target, dmg);
                if (GameMath.checkChance(level*2.5f)) {
                    target.addStatusEffect(new StatusEffect(Status.POISONED, 5.0f));
                }
            }
        });

        addSkill(new Skill(ID.Skill.Scout.TRIPLE_STRIKE, "Triple Strike", Desc.Skill.Scout.TRIPLE_STRIKE, true, 40.0f) {
            /**
             *
             */
            private static final long serialVersionUID = 8295208480454374043L;

            @Override
            public int getManaCost() {
                return 3 + level*4;
            }

            @Override
            protected void useImpl(GameCharacter caster, GameCharacter target) {
                float dmg = caster.getTotalStat(Stat.ATK);
                if (target.hasStatusEffect(Status.STUNNED)) {
                    dmg += level * 15;
                }

                caster.dealPhysicalDamage(target, dmg);
                caster.dealPhysicalDamage(target, dmg);
                caster.dealPhysicalDamage(target, dmg);
            }
        });


        // ENEMIES

        addEnemy(new Enemy(ID.Enemy.MINOR_FIRE_SPIRIT, "Minor Fire Spirit", Desc.Enemy.MINOR_FIRE_SPIRIT,
                EnemyType.NORMAL, new AgentBehaviour(AgentType.SCOUT, AgentGoal.FIND_OBJECT, AgentMode.PASSIVE),
                Element.FIRE, 1, 5, Resource.Image.ENEMY1, new DroppableItem(ID.Weapon.KNIFE, 50)));

        addEnemy(new Enemy(ID.Enemy.MINOR_EARTH_SPIRIT, "Minor Earth Spirit", Desc.Enemy.MINOR_EARTH_SPIRIT,
                EnemyType.NORMAL, new AgentBehaviour(AgentType.ASSASSIN, AgentGoal.KILL_OBJECT, AgentMode.AGGRESSIVE),
                Element.EARTH, 1, 5, Resource.Image.ENEMY2, new DroppableItem(ID.Weapon.IRON_SWORD, 15)));

        addEnemy(new Enemy(ID.Enemy.MINOR_WATER_SPIRIT, "Minor Water Spirit", Desc.Enemy.MINOR_WATER_SPIRIT,
                EnemyType.NORMAL, new AgentBehaviour(AgentType.GUARD, AgentGoal.GUARD_OBJECT, AgentMode.PATROL),
                Element.WATER, 1, 5, Resource.Image.ENEMY3, new DroppableItem(ID.Armor.CHAINMAL, 25)));


        // ESSENCES

        addEssence(new Essence("Minor Fire Spirit Essence", Stat.ATK, 5));

        /*
         * Soul Slash - 7 consecutive attacks.
         * Performs 6 fast attacks of type NORMAL, each attack deals 10% more than previous.
         * Deals 850% of your base ATK.
         * Final hit is of type GHOST.
         * Deals 200% of your total ATK
         *
         *
         *  Cleanse
         *
         *
         *
         * Mind Blast
         * Drains % of target's SP based on target's level.
         * Increases cost of all skills by that % for 30s
         *
         * */
    }

    private static void addArmor(Armor armor) {
        defaultArmor.put(armor.id, armor);
    }

    private static void addWeapon(Weapon weapon) {
        defaultWeapons.put(weapon.id, weapon);
    }

    private static void addSkill(Skill skill) {
        defaultSkills.put(skill.id, skill);
    }

    private static void addEnemy(Enemy enemy) {
        defaultEnemies.put(enemy.id, enemy);
    }

    private static void addEssence(Essence e) {
        defaultEssences.put(e.id, e);
    }

    public static Skill getSkillByID(String id) {
        if (defaultSkills.containsKey(id)) {
            Skill sk = defaultSkills.get(id);
            Constructor<? extends Skill> c;
            try {
                c = sk.getClass().getDeclaredConstructor(String.class, String.class, String.class, Boolean.class, Float.class);
                return c.newInstance(sk.id, sk.name, sk.description, sk.active, sk.skillCooldown);
            }
            catch (NoSuchMethodException | SecurityException | InstantiationException
                    | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public static Armor getArmorByID(String id) {
        return defaultArmor.containsKey(id) ? new Armor(defaultArmor.get(id)) : null;
    }

    public static Weapon getWeaponByID(String id) {
        return defaultWeapons.containsKey(id) ? new Weapon(defaultWeapons.get(id)) : null;
    }

    public static Enemy getEnemyByID(String id) {
        return defaultEnemies.containsKey(id) ? new Enemy(defaultEnemies.get(id)) : null;
    }

    public static GameItem getItemByID(String id) {
        if (id.startsWith("5"))
            return getArmorByID(id);
        if (id.startsWith("4"))
            return getWeaponByID(id);

        return null;
    }

    public static Essence getEssenceByID(String id) {
        return defaultEssences.containsKey(id) ? new Essence(defaultEssences.get(id)) : null;
    }
}
