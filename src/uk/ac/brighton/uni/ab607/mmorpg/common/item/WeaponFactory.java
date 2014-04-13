package uk.ac.brighton.uni.ab607.mmorpg.common.item;

import java.util.HashMap;

import uk.ac.brighton.uni.ab607.mmorpg.common.Attribute;
import uk.ac.brighton.uni.ab607.mmorpg.common.combat.Element;
import uk.ac.brighton.uni.ab607.mmorpg.common.item.Weapon.WeaponType;

public class WeaponFactory {

    private static HashMap<String, Weapon> defaultWeapons = new HashMap<String, Weapon>();

    static {
        preLoad();
    }

    private WeaponFactory() {}

    public static Weapon getWeaponById(String id) {
        return defaultWeapons.containsKey(id) ? new Weapon(defaultWeapons.get(id)) : null;
    }

    private static void preLoad() {
        add(new Weapon("Hands", "That's right, go kill everyone with your bare hands", 0, 7, WeaponType.MACE, 0));

        add(new Weapon(
                "Getsuga Tensho", "A powerful sword that is carved from the fangs of the moon itself and pierced through heaven", 4, 6,
                "Matthew", ItemLevel.EPIC, WeaponType.ONE_H_SWORD, 150, Element.NEUTRAL, 4,
                new Rune(Attribute.STRENGTH, 5), new Rune(Attribute.AGILITY, 4), new Rune(Attribute.DEXTERITY, 4), new Rune(Attribute.LUCK, 1)));

        add(new Weapon(
                "Soul Reaper", "Forged in the dephts of Aesmir, it is said the weilder can feel the weapon crave the souls of its enemies", 10, 10,
                "Sam Bowen", ItemLevel.EPIC, WeaponType.TWO_H_AXE, 170, Element.NEUTRAL, 4,
                new Rune(Attribute.STRENGTH, 7), new Rune(Attribute.VITALITY, 4), new Rune(Attribute.DEXTERITY, 2)));

        add(new Weapon(
                "The Gut Ripper", "A fierce weapon that punctures and ruptures enemies with vicious and lightning fast blows", 2, 6,
                "Tim Snow", ItemLevel.EPIC, WeaponType.DAGGER, 100, Element.NEUTRAL, 4,
                new Rune(Attribute.AGILITY, 4), new Rune(Attribute.DEXTERITY, 4), new Rune(Attribute.LUCK, 1)));

        add(new Weapon(
                "Dragon's Claw", "A mythical bow made of claws of the legendary dragon. Contains dragon's wisdom and loyal to only one master throughout his whole life. Grants dragon's and earlier owner's wisdom and knowledge to the new master",
                12, 11, "Atheryos", ItemLevel.EPIC, WeaponType.BOW, 130, Element.FIRE, 4,
                new Rune(Attribute.VITALITY, 3), new Rune(Attribute.WISDOM, 5), new Rune(Attribute.AGILITY, 3)));

        add(new Weapon(
                "Frostmourn", "The legendary sword of the Ice Dungeon's King. Can turn enemies into frozen rocks with 5% chance. Has water element", 8, 25,
                "Stefos", ItemLevel.EPIC, WeaponType.TWO_H_SWORD, 130, Element.WATER, 4,
                new Rune(Attribute.DEXTERITY, 5), new Rune(Attribute.STRENGTH, 3)));

        add(new Weapon("Iron Sword", "A standard warrior's sword with decent attack damage", 0, 5, WeaponType.ONE_H_SWORD, 15));
        add(new Weapon("Knife", "A simple knife with poor blade", 0, 6, WeaponType.DAGGER, 5));
    }

    private static void add(Weapon w) {
        defaultWeapons.put(w.id, w);
    }
}
