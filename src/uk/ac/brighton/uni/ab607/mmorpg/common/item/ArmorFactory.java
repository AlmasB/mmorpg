package uk.ac.brighton.uni.ab607.mmorpg.common.item;

import java.util.HashMap;

import uk.ac.brighton.uni.ab607.mmorpg.common.Attribute;
import uk.ac.brighton.uni.ab607.mmorpg.common.combat.Element;
import uk.ac.brighton.uni.ab607.mmorpg.common.item.Armor.ArmorType;

public class ArmorFactory {

    private static HashMap<String, Armor> defaultArmor = new HashMap<String, Armor>();

    static {
        add(new Armor("Hat", "Ordinary hat, already out of fashion", 10, 13, ArmorType.HELM, 0, 0));    // 5000, matches type enum for easy equip
        add(new Armor("Clothes", "Just normal clothes, don't count on any defense", 0, 13, ArmorType.BODY, 0, 0));  // 5001
        add(new Armor("Shoes", "Average size shoes", 0, 14, ArmorType.SHOES, 0, 0));    // 5002

        add(new Armor("Chainmail", "Armour consisting of small metal rings linked together in a pattern to form a mesh.", 4, 13,
                ArmorType.BODY, 15, 5));

        add(new Armor("Soul Barrier", "Protects its wearer from magic attacks", 3, 13,
                "Stefos", ItemLevel.UNIQUE, ArmorType.BODY, 10, 50, Element.NEUTRAL, 3, new Rune(Attribute.WILLPOWER, 3)));

        add(new Armor("Domovoi",
                "Generations of guardians have bled in this armour, imbuing it with spirits of protection. Spirits that awaken when the wearers need is greatest.",
                2, 13, "Joe", ItemLevel.UNIQUE, ArmorType.BODY, 15, 30, Element.NEUTRAL, 4, new Rune(Attribute.WILLPOWER, 3)));

        add(new Armor("Sapphire Legion Plate Mail",
                "Produced in the Jaded Forges of the Jewelled King, strictly for use by warriors who have proved their mastery of combat through decades of service.",
                5, 13, "Oliver", ItemLevel.UNIQUE, ArmorType.BODY, 30, 10, Element.NEUTRAL, 4, new Rune(Attribute.VITALITY, 4)));

        add(new Armor("Thanatos Body Armor",
                "A shattered piece of Thanatos' legendary armor. Grants its user great constitution", 6, 13,
                "Almas", ItemLevel.EPIC, ArmorType.BODY, 50, 25, Element.EARTH, 4, new Rune(Attribute.VITALITY, 5), new Rune(Attribute.PERCEPTION, 4)));

    }

    public static Armor getArmorById(String id) {
        return defaultArmor.containsKey(id) ? new Armor(defaultArmor.get(id)) : null;
    }


    private static void add(Armor armor) {
        defaultArmor.put(armor.id, armor);
    }
}
