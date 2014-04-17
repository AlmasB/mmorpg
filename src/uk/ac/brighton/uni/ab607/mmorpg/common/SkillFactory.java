package uk.ac.brighton.uni.ab607.mmorpg.common;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class SkillFactory {

    private static HashMap<String, Skill> defaultSkills = new HashMap<String, Skill>();

    // TODO: check with char's stats first like magic armor etc
    static {
        // HEAL
        add(new ActiveSkill("Heal", "Restores HP to target") {
            /**
             *
             */
            private static final long serialVersionUID = 1379897406205500901L;

            @Override
            public int getManaCost() {
                return 15 + level * 10;
            }

            @Override
            public void use(GameCharacter caster, GameCharacter target) {
                target.hp += 30 + level*10;
                if (target.hp > target.getTotalStat(GameCharacter.MAX_HP)) {
                    target.hp = (int)target.getTotalStat(GameCharacter.MAX_HP);
                }
            }
        });

        // MANA BURN
        add(new ActiveSkill("Mana Burn", "Burns target's SP and deals damage based on the SP burnt") {
            /**
             *
             */
            private static final long serialVersionUID = 7719535667188968500L;

            @Override
            public int getManaCost() {
                return 50 + level * 25;
            }

            @Override
            public void use(GameCharacter caster, GameCharacter target) {
                int oldSP = target.sp;
                target.sp = Math.max(oldSP - 50 * level, 0);
                target.hp -= (oldSP - target.sp);
            }
        });

        // FINAL STRIKE
        add(new ActiveSkill("Final Strike", "Drains all HP/SP leaving 1 HP/0 SP. "
                + "For each HP/SP drained the skill damage increases by 0.3%") {
            /**
             *
             */
            private static final long serialVersionUID = 2091028246707933529L;

            @Override
            public int getManaCost() {
                return 100 + level * 100;
            }

            @Override
            public void use(GameCharacter caster, GameCharacter target) {
                int total = caster.hp - 1 + caster.sp;
                caster.hp = 1;
                caster.sp = 0;
                target.hp -= 500 * level + total * 0.003f;
            }
        });

        // PIERCING TOUCH
        add(new ActiveSkill("Piercing Touch", "Deals physical damage based on target's armor. "
                + "The more armor target has the greater the damage") {
            /**
             *
             */
            private static final long serialVersionUID = 1513947512801417510L;

            @Override
            public int getManaCost() {
                return 25 + level * 30;
            }

            @Override
            public void use(GameCharacter caster, GameCharacter target) {
                float dmg = level * 5 * (15 + target.getTotalStat(GameCharacter.ARM) / 100.0f);
                target.hp -= dmg;
            }
        });

        // BULLSEYE
        add(new ActiveSkill("BULLSEYE", "Deals armor ignoring damage to target."
                + "Target's defense is not ignored. "
                + "Damage is based on caster's DEX") {
            /**
             *
             */
            private static final long serialVersionUID = 6923525936384357867L;

            @Override
            public int getManaCost() {
                return 25 + level * 30;
            }

            @Override
            public void use(GameCharacter caster, GameCharacter target) {
                float dmg = 100 + level * 85 - target.getTotalStat(GameCharacter.DEF);
                target.hp -= dmg;
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
                c = sk.getClass().getDeclaredConstructor(String.class, String.class);
                sk = c.newInstance(sk.name, sk.description);
                return sk;
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
        defaultSkills.put(skill.id, skill);
    }
}
