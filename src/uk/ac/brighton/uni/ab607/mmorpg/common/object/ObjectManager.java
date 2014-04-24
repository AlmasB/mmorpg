package uk.ac.brighton.uni.ab607.mmorpg.common.object;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import uk.ac.brighton.uni.ab607.mmorpg.common.Attribute;
import uk.ac.brighton.uni.ab607.mmorpg.common.GameCharacter;
import uk.ac.brighton.uni.ab607.mmorpg.common.Stat;
import uk.ac.brighton.uni.ab607.mmorpg.common.ai.AgentBehaviour;
import uk.ac.brighton.uni.ab607.mmorpg.common.ai.AgentType;
import uk.ac.brighton.uni.ab607.mmorpg.common.combat.Element;
import uk.ac.brighton.uni.ab607.mmorpg.common.item.DroppableItem;
import uk.ac.brighton.uni.ab607.mmorpg.common.item.EquippableItem.ItemLevel;
import uk.ac.brighton.uni.ab607.mmorpg.common.item.GameItem;
import uk.ac.brighton.uni.ab607.mmorpg.common.item.Rune;
import uk.ac.brighton.uni.ab607.mmorpg.common.object.Armor.ArmorType;
import uk.ac.brighton.uni.ab607.mmorpg.common.object.Enemy.EnemyType;
import uk.ac.brighton.uni.ab607.mmorpg.common.object.Weapon.WeaponType;

public class ObjectManager {

    private static HashMap<String, Weapon> defaultWeapons = new HashMap<String, Weapon>();
    private static HashMap<String, Armor> defaultArmor = new HashMap<String, Armor>();
    private static HashMap<String, Skill> defaultSkills = new HashMap<String, Skill>();
    private static HashMap<String, Enemy> defaultEnemies = new HashMap<String, Enemy>();
    private static HashMap<String, Essence> defaultEssences = new HashMap<String, Essence>();

    // TODO: possibly replace all ids with static ones, i.e. class ID.ITEM_KNIFE or something similar for much easier access and code

    private static int uniqueArmorID = 5000;
    private static int uniqueWeaponID = 4000;
    private static int uniqueSkillID = 7000;
    private static int uniqueEnemyID = 2000;
    private static int uniqueEssenceID = 9000;

    private ObjectManager() {}

    public static void load() {
        // ARMOR

        addArmor(new Armor("Hat", "Ordinary hat, already out of fashion", 10, 13, ArmorType.HELM, 0, 0));    // 5000, matches type enum for easy equip
        addArmor(new Armor("Clothes", "Just normal clothes, don't count on any defense", 0, 13, ArmorType.BODY, 0, 0));  // 5001
        addArmor(new Armor("Shoes", "Average size shoes", 0, 14, ArmorType.SHOES, 0, 0));    // 5002

        addArmor(new Armor("Chainmail", "Armour consisting of small metal rings linked together in a pattern to form a mesh.", 4, 13,
                ArmorType.BODY, 15, 5));

        addArmor(new Armor("Soul Barrier", "Protects its wearer from magic attacks", 3, 13,
                "Stefos", ItemLevel.UNIQUE, ArmorType.BODY, 10, 50, Element.NEUTRAL, 3, new Rune(Attribute.WILLPOWER, 3)));

        addArmor(new Armor("Domovoi",
                "Generations of guardians have bled in this armour, imbuing it with spirits of protection. Spirits that awaken when the wearers need is greatest.",
                2, 13, "Joe", ItemLevel.UNIQUE, ArmorType.BODY, 15, 30, Element.NEUTRAL, 4, new Rune(Attribute.WILLPOWER, 3)));

        addArmor(new Armor("Sapphire Legion Plate Mail",
                "Produced in the Jaded Forges of the Jewelled King, strictly for use by warriors who have proved their mastery of combat through decades of service.",
                5, 13, "Oliver", ItemLevel.UNIQUE, ArmorType.BODY, 30, 10, Element.NEUTRAL, 4, new Rune(Attribute.VITALITY, 4)));

        addArmor(new Armor("Thanatos Body Armor",
                "A shattered piece of Thanatos' legendary armor. Grants its user great constitution", 6, 13,
                "Almas", ItemLevel.EPIC, ArmorType.BODY, 50, 25, Element.EARTH, 4, new Rune(Attribute.VITALITY, 5), new Rune(Attribute.PERCEPTION, 4)));

        // WEAPON

        addWeapon(new Weapon("Hands", "That's right, go kill everyone with your bare hands", 0, 7, WeaponType.MACE, 0));

        addWeapon(new Weapon(
                "Getsuga Tensho", "A powerful sword that is carved from the fangs of the moon itself and pierced through heaven", 4, 6,
                "Matthew", ItemLevel.EPIC, WeaponType.ONE_H_SWORD, 150, Element.NEUTRAL, 4,
                new Rune(Attribute.STRENGTH, 5), new Rune(Attribute.AGILITY, 4), new Rune(Attribute.DEXTERITY, 4), new Rune(Attribute.LUCK, 1)));

        addWeapon(new Weapon(
                "Soul Reaper", "Forged in the dephts of Aesmir, it is said the weilder can feel the weapon crave the souls of its enemies", 10, 10,
                "Sam Bowen", ItemLevel.EPIC, WeaponType.TWO_H_AXE, 170, Element.NEUTRAL, 4,
                new Rune(Attribute.STRENGTH, 7), new Rune(Attribute.VITALITY, 4), new Rune(Attribute.DEXTERITY, 2)));

        addWeapon(new Weapon(
                "The Gut Ripper", "A fierce weapon that punctures and ruptures enemies with vicious and lightning fast blows", 2, 6,
                "Tim Snow", ItemLevel.EPIC, WeaponType.DAGGER, 100, Element.NEUTRAL, 4,
                new Rune(Attribute.AGILITY, 4), new Rune(Attribute.DEXTERITY, 4), new Rune(Attribute.LUCK, 1)));

        addWeapon(new Weapon(
                "Dragon's Claw", "A mythical bow made of claws of the legendary dragon. Contains dragon's wisdom and loyal to only one master throughout his whole life. Grants dragon's and earlier owner's wisdom and knowledge to the new master",
                12, 11, "Atheryos", ItemLevel.EPIC, WeaponType.BOW, 130, Element.FIRE, 4,
                new Rune(Attribute.VITALITY, 3), new Rune(Attribute.WISDOM, 5), new Rune(Attribute.AGILITY, 3)));

        addWeapon(new Weapon(
                "Frostmourn", "The legendary sword of the Ice Dungeon's King. Can turn enemies into frozen rocks with 5% chance. Has water element", 8, 25,
                "Stefos", ItemLevel.EPIC, WeaponType.TWO_H_SWORD, 130, Element.WATER, 4,
                new Rune(Attribute.DEXTERITY, 5), new Rune(Attribute.STRENGTH, 3)));

        addWeapon(new Weapon("Iron Sword", "A standard warrior's sword with decent attack damage", 0, 5, WeaponType.ONE_H_SWORD, 15));
        addWeapon(new Weapon("Knife", "A simple knife with poor blade", 0, 6, WeaponType.DAGGER, 5));

        // SKILLS

        // HEAL
        addSkill(new Skill("Heal", "Restores HP to target", true, 10.0f) {
            /**
             *
             */
            private static final long serialVersionUID = 1379897406205500901L;

            @Override
            public int getManaCost() {
                return level * 10;
            }

            @Override
            public void useImpl(GameCharacter caster, GameCharacter target) {
                target.hp = Math.min(target.hp + 30 + level*10, (int)target.getTotalStat(Stat.MAX_HP));
            }
        });

        // MANA BURN
        addSkill(new Skill("Mana Burn", "Burns target's SP and deals damage based on the SP burnt", true, 10.0f) {
            /**
             *
             */
            private static final long serialVersionUID = 7719535667188968500L;

            @Override
            public int getManaCost() {
                return 50 + level * 25;
            }

            @Override
            public void useImpl(GameCharacter caster, GameCharacter target) {
                int oldSP = target.sp;
                target.sp = Math.max(oldSP - 50 * level, 0);
                target.hp -= (oldSP - target.sp);
            }
        });

        // FINAL STRIKE
        addSkill(new Skill("Final Strike", "Drains all HP/SP leaving 1 HP/0 SP. "
                + "For each HP/SP drained the skill damage increases by 0.3%", true, 10.0f) {
            /**
             *
             */
            private static final long serialVersionUID = 2091028246707933529L;

            @Override
            public int getManaCost() {
                return 100 + level * 100;
            }

            @Override
            public void useImpl(GameCharacter caster, GameCharacter target) {
                int total = caster.hp - 1 + caster.sp;
                caster.hp = 1;
                caster.sp = 0;
                target.hp -= 500 * level + total * 0.003f;
            }
        });

        // PIERCING TOUCH
        addSkill(new Skill("Piercing Touch", "Deals physical damage based on target's armor. "
                + "The more armor target has the greater the damage", true, 9.0f) {
            /**
             *
             */
            private static final long serialVersionUID = 1513947512801417510L;

            @Override
            public int getManaCost() {
                return 25 + level * 30;
            }

            @Override
            public void useImpl(GameCharacter caster, GameCharacter target) {
                float dmg = level * 5 * (15 + target.getTotalStat(GameCharacter.ARM) / 100.0f);
                target.hp -= dmg;
            }
        });

        // BULLSEYE
        addSkill(new Skill("Bullseye", "Deals armor ignoring damage to target."
                + "Target's defense is not ignored. "
                + "Damage is based on caster's DEX", true, 60.0f) {
            /**
             *
             */
            private static final long serialVersionUID = 6923525936384357867L;

            @Override
            public int getManaCost() {
                return 5 + level * 10;
            }

            @Override
            public void useImpl(GameCharacter caster, GameCharacter target) {
                float dmg = 100 + level * 85 - target.getTotalStat(GameCharacter.DEF);
                target.hp -= dmg;
            }
        });

        // CLAUDIUS
        addSkill(new Skill("Claudius", "Increases VIT, WIS, LUC."
                + "Decreases INT.", true, 30.0f) {
            /**
             *
             */
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
        addSkill(new Skill("Five Finger Death Punch", "Deals devastating damage to unarmoured targets",
                true, 30.0f) {

            /**
             *
             */
            private static final long serialVersionUID = 168758926959802026L;

            @Override
            public int getManaCost() {
                return 25 + level * 30;
            }

            @Override
            public void useImpl(GameCharacter caster, GameCharacter target) {
                float dmg = 20 + level*30 - target.getTotalStat(Stat.ARM);
                target.hp -= dmg;
            }
        });


        // ENEMIES
        // TODO: different AI assignment

        addEnemy(new Enemy("Minor Fire Spirit", "Minor Fire Spirit DESC", EnemyType.NORMAL, new AgentBehaviour(AgentType.SCOUT, null),
                Element.FIRE, 1, 5, new DroppableItem("4007", 50)));

        addEnemy(new Enemy("Minor Earth Spirit", "Minor Earth Spirit DESC", EnemyType.NORMAL, new AgentBehaviour(AgentType.SCOUT, null),
                Element.EARTH, 1, 5, new DroppableItem("4006", 15)));


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
        // TODO: check with char's stats first like magic armor etc
    }

    private static void addArmor(Armor armor) {
        armor.id = ""+uniqueArmorID++;
        defaultArmor.put(armor.id, armor);
    }

    private static void addWeapon(Weapon weapon) {
        weapon.id = ""+uniqueWeaponID++;
        defaultWeapons.put(weapon.id, weapon);
    }

    private static void addSkill(Skill skill) {
        skill.id = ""+uniqueSkillID++;
        defaultSkills.put(skill.id, skill);
    }

    private static void addEnemy(Enemy enemy) {
        enemy.id = ""+uniqueEnemyID++;
        defaultEnemies.put(enemy.id, enemy);
    }

    private static void addEssence(Essence e) {
        e.id = ""+uniqueEssenceID++;
        defaultEssences.put(e.id, e);
    }

    public static Skill getSkillByID(String id) {
        if (defaultSkills.containsKey(id)) {
            Skill sk = defaultSkills.get(id);
            Constructor<? extends Skill> c;
            try {
                c = sk.getClass().getDeclaredConstructor(String.class, String.class, Boolean.class, Float.class);
                Skill newSkill = c.newInstance(sk.name, sk.description, sk.active, sk.skillCooldown);
                newSkill.id = sk.id;
                return newSkill;
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
