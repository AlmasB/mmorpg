package uk.ac.brighton.uni.ab607.mmorpg.common;

import java.util.HashMap;

import uk.ac.brighton.uni.ab607.mmorpg.common.combat.Element;
import uk.ac.brighton.uni.ab607.mmorpg.common.item.Armor;
import uk.ac.brighton.uni.ab607.mmorpg.common.item.ItemLevel;
import uk.ac.brighton.uni.ab607.mmorpg.common.item.Rune;
import uk.ac.brighton.uni.ab607.mmorpg.common.item.Armor.ArmorType;

public class SkillFactory {

    private static HashMap<String, Skill> defaultSkills = new HashMap<String, Skill>();

    static {
        //add();

    }

    /*public static Skill getSkillById(String id) {
        return defaultSkills.containsKey(id) ? new Armor(defaultArmor.get(id)) : null;
    }*/


    private static void add(Skill skill) {
        defaultSkills.put(skill.id, skill);
    }
}
