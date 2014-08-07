package uk.ac.brighton.uni.ab607.mmorpg.common.object;

import java.util.Arrays;

import uk.ac.brighton.uni.ab607.mmorpg.common.Player;
import uk.ac.brighton.uni.ab607.mmorpg.common.Stat;
import uk.ac.brighton.uni.ab607.mmorpg.common.combat.Element;
import uk.ac.brighton.uni.ab607.mmorpg.common.item.EquippableItem;
import uk.ac.brighton.uni.ab607.mmorpg.common.item.Rune;

import static com.almasb.common.parsing.PseudoHTML.*;

public class Armor extends EquippableItem {

    /**
     *
     */
    private static final long serialVersionUID = 826666654020818850L;

    public enum ArmorType {
        HELM, BODY, SHOES
    }

    public final ArmorType type;

    private int armor, marmor;

    /*package-private*/ Armor(String id, String name, String description, int ssX, int ssY, String author, ItemLevel level, ArmorType type,
            int armor, int marmor, Element element, int runesMax, Rune... defaultRunes) {
        super(id, name, description, ssX, ssY, author, level, element, runesMax, defaultRunes);
        this.type = type;
        this.armor = armor;
        this.marmor = marmor;
    }

    /*package-private*/ Armor(Armor copy) {
        this(copy.id, copy.name, copy.description, copy.ssX, copy.ssY, copy.author, copy.level, copy.type,
                copy.armor, copy.marmor, copy.element, copy.runesMax, copy.defaultRunes);
    }

    /*package-private*/ Armor(String id, String name, String description, int ssX, int ssY, ArmorType type, int armor, int marmor) {
        super(id, name, description, ssX, ssY);
        this.type = type;
        this.armor = armor;
        this.marmor = marmor;
    }

    @Override
    public void onEquip(Player ch) {
        super.onEquip(ch);
        ch.addBonusStat(Stat.ARM, getArmorRating());
        ch.addBonusStat(Stat.MARM, getMArmorRating());
    }

    @Override
    public void onUnEquip(Player ch) {
        super.onUnEquip(ch);
        ch.addBonusStat(Stat.ARM, -getArmorRating());
        ch.addBonusStat(Stat.MARM, -getMArmorRating());
    }

    public int getArmorRating() {
        return armor + refineLevel * (refineLevel > 2 ? level.bonus + 1 : level.bonus);
    }

    public int getMArmorRating() {
        return marmor + refineLevel * (refineLevel > 2 ? level.bonus + 1 : level.bonus);
    }

    @Override
    public String toString() {
        return (refineLevel == 0 ? "" : "+" + refineLevel + " ") + name + "(" + runesMax + ")" + "\n"
                + "ID: " + id + "\n"
                + description + "\n"
                + "Author: " + author + "\n"
                + "Armor: " + armor + " + " + refineLevel * (refineLevel > 2 ? level.bonus + 1 : level.bonus) + "\n"
                + "MArmor: " + marmor + " + " + refineLevel * (refineLevel > 2 ? level.bonus + 1 : level.bonus) + "\n"
                + "Element: " + element + "\n"
                + Arrays.toString(defaultRunes) + "\n"
                + "Runes installed: " + runes.toString() + "\n"
                + "Essence installed: " + (essence == null ? "NONE" : essence);
    }

    @Override
    public String toPseudoHTML() {
        return HTML_START
                + "<center>" + BLUE + (refineLevel == 0 ? "" : "+" + refineLevel + " ") + name + FONT_END + "(" + runesMax + ")" + "</center>"
                + description + BR
                + "Author: " + RED + author + FONT_END + BR
                + "Armor: " + BLUE + armor + " + " + refineLevel * (refineLevel > 2 ? level.bonus + 1 : level.bonus) + "%" + FONT_END + BR
                + "MArmor: " + BLUE + marmor + " + " + refineLevel * (refineLevel > 2 ? level.bonus + 1 : level.bonus) + "%" + FONT_END + BR
                + "Element: " + BLUE + element + FONT_END + BR
                + GREEN + Arrays.toString(defaultRunes) + FONT_END + BR
                + "Runes: " + BR
                + GREEN + runes.toString() + FONT_END + BR
                + "Essence: " + BR
                + (essence == null ? "NONE" : essence.toStringHTML());
    }

    @Override
    public String toPseudoHTMLShort() {
        return HTML_START
                + "<center>" + BLUE + (refineLevel == 0 ? "" : "+" + refineLevel + " ") + name + FONT_END + " (" + runesMax + ")" + "</center>"
                + "Armor: " + BLUE + armor + " + " + refineLevel * (refineLevel > 2 ? level.bonus + 1 : level.bonus) + "%" + FONT_END + BR
                + "MArmor: " + BLUE + marmor + " + " + refineLevel * (refineLevel > 2 ? level.bonus + 1 : level.bonus) + "%" + FONT_END + BR
                + "Element: " + BLUE + element + FONT_END + BR
                + GREEN + Arrays.toString(defaultRunes) + FONT_END + BR
                + "Runes: " + BR
                + GREEN + runes.toString() + FONT_END + BR
                + "Essence: " + BR
                + (essence == null ? "NONE" : essence.toStringHTML());
    }
}
