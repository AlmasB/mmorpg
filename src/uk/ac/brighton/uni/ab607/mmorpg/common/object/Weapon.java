package uk.ac.brighton.uni.ab607.mmorpg.common.object;

import java.util.Arrays;

import com.almasb.common.parsing.PseudoHTML;
import uk.ac.brighton.uni.ab607.mmorpg.common.Player;
import uk.ac.brighton.uni.ab607.mmorpg.common.Stat;
import uk.ac.brighton.uni.ab607.mmorpg.common.combat.Element;
import uk.ac.brighton.uni.ab607.mmorpg.common.item.EquippableItem;
import uk.ac.brighton.uni.ab607.mmorpg.common.item.Rune;

public class Weapon extends EquippableItem implements PseudoHTML {

    /**
     *
     */
    private static final long serialVersionUID = 2639185454495196264L;

    public enum WeaponType {
        ONE_H_SWORD(2, 0.85f), ONE_H_AXE(2, 0.95f), DAGGER(1, 1.25f), SPEAR(3, 0.85f), MACE(2, 1.0f), ROD(5, 0.9f), SHIELD(0, 0.9f),   // 1H, shield only left-hand
        TWO_H_SWORD(2, 0.7f), TWO_H_AXE(2, 0.65f), KATAR(1, 0.85f), BOW(5, 0.75f);    // 2H

        public final int range;
        public final float aspdFactor;

        private WeaponType(int range, float aspdFactor) {
            this.range = range;
            this.aspdFactor = aspdFactor;
        }
    }

    public final WeaponType type;
    public final int range;
    public final int pureDamage;

    /*package-private*/ Weapon(String id, String name, String description, int ssX, int ssY, String author,
            ItemLevel level, WeaponType type, int pureDamage, Element element, int runesMax, Rune... defaultRunes) {
        super(id, name, description, ssX, ssY, author, level, element, runesMax, defaultRunes);
        this.type = type;
        this.range = type.range;
        this.pureDamage = pureDamage;
    }

    /*package-private*/ Weapon(Weapon copy) {
        this(copy.id, copy.name, copy.description, copy.ssX, copy.ssY, copy.author, copy.level, copy.type, copy.pureDamage, copy.element, copy.runesMax, copy.defaultRunes);
    }

    /*package-private*/ Weapon(String id, String name, String description, int ssX, int ssY, WeaponType type, int pureDamage) {
        super(id, name, description, ssX, ssY);
        this.type = type;
        this.range = type.range;
        this.pureDamage = pureDamage;
    }

    @Override
    public void onEquip(Player ch) {
        super.onEquip(ch);
        ch.addBonusStat(Stat.ATK, getDamage());
    }

    @Override
    public void onUnEquip(Player ch) {
        super.onUnEquip(ch);
        ch.addBonusStat(Stat.ATK, -getDamage());
    }

    public int getDamage() {
        return pureDamage + refineLevel * (refineLevel > 2 ? level.bonus + 5 : level.bonus);
    }

    @Override
    public String toString() {
        return "ID: " + id + "\n"
                + (refineLevel == 0 ? "" : "+" + refineLevel + " ") + name + "(" + runesMax + ")" + "\n"
                + description + "\n"
                + "Author: " + author + "\n"
                + "Stats:" + "\n"
                + "Type: " + type + "\n"
                + "Damage: " + pureDamage + " + " + refineLevel * level.bonus + "\n"
                + "Element: " + element + "\n"
                + Arrays.toString(defaultRunes) + "\n"
                + "Runes installed: " + runes.toString() + "\n"
                + "Essence installed: " + (essence == null ? "NONE" : essence);
    }

    public String toStringShort() {
        return "ID: " + id + "\n"
                + (refineLevel == 0 ? "" : "+" + refineLevel + " ") + name + "(" + runesMax + ")" + "\n"
                //+ description + "\n"
                + "Author: " + author + "\n"
                + "Stats:" + "\n"
                + "Type: " + type + "\n"
                + "Damage: " + pureDamage + " + " + refineLevel * level.bonus + "\n"
                + "Element: " + element + "\n"
                + Arrays.toString(defaultRunes) + "\n"
                + runes.toString() + "\n"
                + (essence == null ? "" : essence.name);
    }

    @Override
    public String toPseudoHTML() {
        return HTML_START
                + "<center>" + BLUE + (refineLevel == 0 ? "" : "+" + refineLevel + " ") + name + FONT_END + " (" + runesMax + ")" + "</center>"
                + description + BR
                + "Author: " + RED + author + FONT_END + BR
                + "Type: " + BLUE + type + FONT_END + BR
                + "Damage: " + BLUE + pureDamage + " + " + refineLevel * (refineLevel > 2 ? level.bonus + 5 : level.bonus) + FONT_END + BR
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
                + "Type: " + BLUE + type + FONT_END + BR
                + "Damage: " + BLUE + pureDamage + " + " + refineLevel * (refineLevel > 2 ? level.bonus + 5 : level.bonus) + FONT_END + BR
                + "Element: " + BLUE + element + FONT_END + BR
                + GREEN + Arrays.toString(defaultRunes) + FONT_END + BR
                + "Runes: " + BR
                + GREEN + runes.toString() + FONT_END + BR
                + "Essence: " + BR
                + (essence == null ? "NONE" : essence.toStringHTML());
    }
}
