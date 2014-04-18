package uk.ac.brighton.uni.ab607.mmorpg.common;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import uk.ac.brighton.uni.ab607.libs.main.Out;

public class SkillFactory {

    private static int uniqueSkillID = 7000;
    private static HashMap<String, Skill> defaultSkills = new HashMap<String, Skill>();

    /**
     * To trigger clinit manually // TODO: create a general factory class
     */
    public static void load() {}

    // TODO: check with char's stats first like magic armor etc
    static {
        // HEAL
        add(new Skill("Heal", "Restores HP to target", true, 10.0f) {
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
        add(new Skill("Mana Burn", "Burns target's SP and deals damage based on the SP burnt", true, 10.0f) {
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
        add(new Skill("Final Strike", "Drains all HP/SP leaving 1 HP/0 SP. "
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
        add(new Skill("Piercing Touch", "Deals physical damage based on target's armor. "
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
        add(new Skill("BULLSEYE", "Deals armor ignoring damage to target."
                + "Target's defense is not ignored. "
                + "Damage is based on caster's DEX", true, 60.0f) {
            /**
             *
             */
            private static final long serialVersionUID = 6923525936384357867L;

            @Override
            public int getManaCost() {
                return 25 + level * 30;
            }

            @Override
            public void useImpl(GameCharacter caster, GameCharacter target) {
                float dmg = 100 + level * 85 - target.getTotalStat(GameCharacter.DEF);
                target.hp -= dmg;
            }
        });

        // CLAUDIUS
        add(new Skill("Claudius", "Increases VIT, WIS, LUC."
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

    /**
     *
     * @param id
     *              skill id
     * @return
     *          a complete NEW copy of a skill from database
     */
    public static Skill getSkillById(String id) {
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

    private static void add(Skill skill) {
        skill.id = ""+uniqueSkillID++;
        defaultSkills.put(skill.id, skill);
    }
}
