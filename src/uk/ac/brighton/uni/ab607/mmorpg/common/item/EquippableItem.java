package uk.ac.brighton.uni.ab607.mmorpg.common.item;

import java.util.ArrayList;

import uk.ac.brighton.uni.ab607.libs.main.Out;
import uk.ac.brighton.uni.ab607.mmorpg.common.GameMath;
import uk.ac.brighton.uni.ab607.mmorpg.common.Player;
import uk.ac.brighton.uni.ab607.mmorpg.common.combat.Element;
import uk.ac.brighton.uni.ab607.mmorpg.common.object.Essence;

public abstract class EquippableItem extends GameItem {

    /**
     *
     */
    private static final long serialVersionUID = -1091033469035972887L;

    public enum ItemLevel {
        NORMAL(3, 10),  // refinement chance 100/90/80/70/60
        UNIQUE(5, 15),  // 100/85/70/55/40
        EPIC(10, 20);   // 100/80/60/40/20

        public final int bonus;
        public final int refineChanceReduction;

        private ItemLevel(int bonus, int chance) {
            this.bonus = bonus;
            this.refineChanceReduction = chance;
        }
    }

    protected static final int MAX_REFINE_LEVEL = 5;

    public final String author;
    public final ItemLevel level;

    protected Essence essence;
    protected Element element;
    protected int runesMax;
    protected int refineLevel = 0;
    protected Rune[] defaultRunes;
    protected ArrayList<Rune> runes = new ArrayList<Rune>();

    public EquippableItem(String id, String name, String description, int ssX, int ssY, String author, ItemLevel level, Element element, int runesMax, Rune... defaultRunes) {
        super(id, name, description, ssX, ssY);
        this.author = author;
        this.level = level;
        this.element = element;
        this.runesMax = runesMax;
        this.defaultRunes = defaultRunes;
    }

    public EquippableItem(String id, String name, String description, int ssX, int ssY) {
        this(id, name, description, ssX, ssY, "Almas", ItemLevel.NORMAL, Element.NEUTRAL, 1);
    }

    public Element getElement() {
        return element;
    }

    public boolean addRune(Rune rune) {
        if (runes.size() < runesMax) {
            return runes.add(rune);
        }
        Out.err("Can't add any more runes to this item");
        return false;
    }

    public boolean addEssence(Essence e) {
        if (this.essence == null) {
            this.essence = e;
            return true;
        }
        Out.err("Can't add any more essences to this item");
        return false;
    }

    public void onEquip(Player ch) {
        for (Rune r : defaultRunes)
            ch.addBonusAttribute(r.attribute, r.bonus);
        for (Rune r : runes)
            ch.addBonusAttribute(r.attribute, r.bonus);
        if (essence != null)
            ch.addBonusStat(essence.stat, essence.bonus);
    }

    public void onUnEquip(Player ch) {
        for (Rune r : runes)
            ch.addBonusAttribute(r.attribute, -r.bonus);
        for (Rune r : defaultRunes)
            ch.addBonusAttribute(r.attribute, -r.bonus);
        if (essence != null)
            ch.addBonusStat(essence.stat, -essence.bonus);
    }

    public void refine() {
        if (refineLevel >= MAX_REFINE_LEVEL) {
            Out.err("Can't refine this item any more");
            return;
        }

        if (GameMath.checkChance(100 - refineLevel * level.refineChanceReduction))
            refineLevel++;
        else if (refineLevel > 0)
            refineLevel--;
    }
}
