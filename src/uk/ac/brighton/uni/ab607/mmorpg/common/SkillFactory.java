package uk.ac.brighton.uni.ab607.mmorpg.common;

import java.util.HashMap;

public class SkillFactory {

    private static HashMap<String, Skill> defaultSkills = new HashMap<String, Skill>();
    // TODO: skills could kill target, check for that somewhere
    static {
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

    }

    /*public static Skill getSkillById(String id) {
        return defaultSkills.containsKey(id) ? new Armor(defaultArmor.get(id)) : null;
    }*/


    private static void add(Skill skill) {
        defaultSkills.put(skill.id, skill);
    }
}
