package uk.ac.brighton.uni.ab607.mmorpg.common;

import static uk.ac.brighton.uni.ab607.libs.parsing.PseudoHTML.*;

import java.util.ArrayList;
import java.util.Iterator;

import uk.ac.brighton.uni.ab607.mmorpg.common.combat.Element;
import uk.ac.brighton.uni.ab607.mmorpg.common.object.Skill;

public abstract class GameCharacter implements java.io.Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -4840633591092062960L;

    /**
     * id - object ID in database
     * name - in game name
     * description - info about object
     */
    public String id, name, description;

    /**
     * ID of the object in 1 instance of the game
     */
    private int runtimeID = 0;

    // TODO: add statuses
    // TODO: private int x, y;
    public abstract int getX();
    public abstract int getY();

    /**
     * How attributes modify stats
     */
    private static final float MODIFIER_VERY_LOW = 0.1f,
            MODIFIER_LOW = 0.2f,
            MODIFIER_MEDIUM = 0.3f,
            MODIFIER_HIGH = 0.4f,
            MODIFIER_VERY_HIGH = 0.5f,
            MODIFIER_LEVEL = 0.25f;

    /**
     * These allow convenient access to {@link #attributes} and {@link #stats} arrays
     * to retrieve particular attribute value
     * These essentially represent indexes
     *
     * Note: the order is based on the order of attributes in
     * {@code Attribute} and {@code Stat} enums, so they have to match
     */
    public static final int STR = 0,
            VIT = 1,
            DEX = 2,
            AGI = 3,
            INT = 4,
            WIS = 5,
            WIL = 6,
            PER = 7,
            LUC = 8,
            // STATS
            MAX_HP = 0,
            MAX_SP = 1,
            ATK = 2,
            MATK = 3,
            DEF = 4,    // flat damage reduction
            MDEF = 5,
            ARM = 6,    // % damage reduction
            MARM = 7,
            ASPD = 8,   // attack speed
            MSPD = 9,
            CRIT_CHANCE = 10,
            MCRIT_CHANCE = 11,
            CRIT_DMG = 12,
            MCRIT_DMG = 13;

    protected int[] attributes = new int[9];    // we have 9 attributes
    protected int[] bAttributes = new int[9];   // on top of native attributes, bonuses can be given we items
    protected float[] stats = new float[14];        // 14 stats
    protected float[] bStats = new float[14];       // bonus stats given by item

    protected Skill[] skills = new Skill[10];   // from 1 to 0 on keyboard, TODO: maybe from 1 to 9 makes more sense

    protected ArrayList<Effect> effects = new ArrayList<Effect>();

    // TODO: add hp/sp regen maybe as stat?
    protected int baseLevel = 1,
            hp = 0, sp = 0; // these are current hp/sp

    public int atkTime = 0;

    /**
     * Signifies whether character is alive
     * Typically hp <= 0 means character is NOT alive
     */
    protected boolean alive = true;

    protected GameCharacterClass charClass;

    public GameCharacter(String name, String description, GameCharacterClass charClass) {
        //this.id = id;
        this.name = name;
        this.description = description;
        this.charClass = charClass;
        this.skills = charClass.skills;

        for (int i = STR; i <= LUC; i++)    // set all attributes to 1, that's the minimum
            attributes[i] = 1;

        calculateStats();
        setHP((int)(stats[MAX_HP] + bStats[MAX_HP]));   // set current hp/sp to max
        setSP((int)(stats[MAX_SP] + bStats[MAX_SP]));
    }

    public int getBaseAttribute(int attr) {
        return attributes[attr];
    }

    public float getBaseStat(Stat stat) {
        return stats[stat.ordinal()];
    }

    /**
     *
     * @param attr
     *              one of the constants for attr, STR = 0, LUC = 8
     * @return
     *          total value for attr, including bonuses
     */
    public int getTotalAttribute(int attr) {
        return attributes[attr] + bAttributes[attr];
    }

    public int getTotalAttribute(Attribute attr) {
        return attributes[attr.ordinal()] + bAttributes[attr.ordinal()];
    }

    /**
     *
     * @param stat
     *              one of the constants for stat, MAX_HP = 0, MCRIT = 11
     * @return
     *          total value for stat, including bonuses
     */
    public float getTotalStat(int stat) {
        return stats[stat] + bStats[stat];
    }

    public float getTotalStat(Stat stat) {
        return stats[stat.ordinal()] + bStats[stat.ordinal()];
    }

    //TODO find a way to track changes and auto update
    //(optional) also only recalculate those stats attributes of which changed, not all
    /**
     * Character stats are directly affected by his attributes
     * Therefore any change in attributes must be followed by
     * call to this method
     */
    public final void calculateStats() {
        int strength    = attributes[STR] + bAttributes[STR];   // calculate totals first
        int vitality    = attributes[VIT] + bAttributes[VIT];
        int dexterity   = attributes[DEX] + bAttributes[DEX];
        int agility     = attributes[AGI] + bAttributes[AGI];
        int intellect   = attributes[INT] + bAttributes[INT];
        int wisdom      = attributes[WIS] + bAttributes[WIS];
        int willpower   = attributes[WIL] + bAttributes[WIL];
        int perception  = attributes[PER] + bAttributes[PER];
        int luck        = attributes[LUC] + bAttributes[LUC];

        // None of these formulae are finalised yet and need to be checked for game balance
        // only calculate "native" base stats

        stats[MAX_HP] = (vitality*MODIFIER_VERY_HIGH + strength*MODIFIER_MEDIUM + MODIFIER_LEVEL*baseLevel + (vitality/10))
                * charClass.hp;

        stats[MAX_SP] = (wisdom*MODIFIER_VERY_HIGH + intellect*MODIFIER_MEDIUM + willpower*MODIFIER_VERY_LOW + MODIFIER_LEVEL*baseLevel + (wisdom/10))
                * charClass.sp;

        stats[ATK] = strength*MODIFIER_VERY_HIGH + dexterity*MODIFIER_MEDIUM + perception*MODIFIER_LOW + luck*MODIFIER_VERY_LOW
                + baseLevel + (strength/10)*( (strength/10)+1);

        stats[MATK] = intellect*MODIFIER_VERY_HIGH + wisdom*MODIFIER_HIGH + willpower*MODIFIER_HIGH + dexterity*MODIFIER_MEDIUM
                + perception*MODIFIER_LOW + luck*MODIFIER_VERY_LOW + baseLevel + (intellect/10)*( (intellect/10)+1);

        stats[DEF] = vitality*MODIFIER_MEDIUM + perception*MODIFIER_LOW + strength*MODIFIER_VERY_LOW
                + MODIFIER_LEVEL*baseLevel + (vitality/20)*(charClass.hp/10);

        stats[MDEF] = willpower*MODIFIER_HIGH + wisdom*MODIFIER_MEDIUM + perception*MODIFIER_LOW + intellect*MODIFIER_VERY_LOW
                + MODIFIER_LEVEL*baseLevel + (willpower/20)*(intellect/10);

        stats[ASPD] = agility*MODIFIER_VERY_HIGH + dexterity*MODIFIER_LOW;

        stats[MSPD] = dexterity*MODIFIER_MEDIUM + willpower*MODIFIER_VERY_LOW + wisdom*MODIFIER_VERY_LOW
                + intellect*MODIFIER_VERY_LOW + perception*MODIFIER_VERY_LOW + luck*MODIFIER_VERY_LOW;

        stats[CRIT_CHANCE] = luck*MODIFIER_VERY_HIGH + dexterity*MODIFIER_VERY_LOW + perception*MODIFIER_VERY_LOW
                + wisdom*MODIFIER_VERY_LOW;

        stats[MCRIT_CHANCE] = luck*MODIFIER_HIGH + willpower*MODIFIER_LOW + perception*MODIFIER_VERY_LOW;

        stats[CRIT_DMG]  = 2 + luck*0.01f;
        stats[MCRIT_DMG] = 2 + luck*0.01f;
    }

    /**
     * Apply bonus attr that comes from item for example
     *
     * @param attr
     *              attr
     * @param bonus
     *              value
     */
    public void addBonusAttribute(Attribute attr, int bonus) {
        bAttributes[attr.ordinal()] += bonus;
    }

    /**
     * Apply bonus stat that comes from item for example
     *
     * @param stat
     *              stat
     * @param bonus
     *              value
     */
    public void addBonusStat(Stat stat, int bonus) {
        bStats[stat.ordinal()] += bonus;
    }

    public void setHP(int hp) {
        this.hp = hp;
    }

    public void setSP(int sp) {
        this.sp = sp;
    }

    /**
     *
     * @return
     *          current HP
     */
    public int getHP() {
        return hp;
    }

    /**
     *
     * @return
     *          current SP
     */
    public int getSP() {
        return sp;
    }

    /**
     *
     * @return
     *          a skill array for this character
     */
    public Skill[] getSkills() {
        return skills;
    }

    public void setRuntimeID(int id) {
        runtimeID = id;
    }

    public int getRuntimeID() {
        return runtimeID;
    }

    public boolean isAlive() {
        return alive;
    }

    public abstract Element getWeaponElement();
    public abstract Element getArmorElement();

    public void addEffect(Effect e) {
        // we should do synchronized
        synchronized (effects) {
            effects.add(e);
        }
    }

    public void updateEffects() {
        for (Iterator<Effect> it = effects.iterator(); it.hasNext(); ) {
            Effect e = it.next();
            e.reduceDuration(0.05f);
            if (e.getDuration() <= 0)
                it.remove();
        }

        //System.out.println(effects.size() + "");
    }

    /**
     * Performs basic attack with equipped weapon
     *
     * @param target
     *               target being attacked
     * @return
     *          damage dealt
     */
    public int attack(GameCharacter target) {
        return dealPhysicalDamage(target, this.getTotalStat(ATK) + 1.25f * GameMath.random(baseLevel), this.getWeaponElement());
    }

    /**
     * Deals physical damage to target. The damage is reduced by armor and defense
     * The damage is affected by attacker's weapon element and by target's armor element
     *
     * @param target
     * @param baseDamage
     * @param element
     * @return
     */
    public int dealPhysicalDamage(GameCharacter target, float baseDamage, Element element) {
        if (GameMath.checkChance(getTotalStat(CRIT_CHANCE))) {
            baseDamage *= getTotalStat(CRIT_DMG);
        }

        float elementalDamageModifier = element.getDamageModifierAgainst(target.getArmorElement());
        float damageAfterReduction = (100 - target.getTotalStat(ARM)) * baseDamage / 100.0f - target.getTotalStat(DEF);

        int totalDamage = Math.max(Math.round(elementalDamageModifier * damageAfterReduction), 0);
        target.hp -= totalDamage;

        return totalDamage;
    }

    public int dealPhysicalDamage(GameCharacter target, float baseDamage) {
        return dealPhysicalDamage(target, baseDamage, Element.NEUTRAL);
    }

    public int dealMagicalDamage(GameCharacter target, float baseDamage, Element element) {
        if (GameMath.checkChance(getTotalStat(MCRIT_CHANCE))) {
            baseDamage *= getTotalStat(MCRIT_DMG);
        }

        float elementalDamageModifier = element.getDamageModifierAgainst(target.getArmorElement());
        float damageAfterReduction = (100 - target.getTotalStat(MARM)) * baseDamage / 100.0f - target.getTotalStat(MDEF);

        int totalDamage = Math.max(Math.round(elementalDamageModifier * damageAfterReduction), 0);
        target.hp -= totalDamage;

        return totalDamage;
    }

    public int dealMagicalDamage(GameCharacter target, float baseDamage) {
        return dealMagicalDamage(target, baseDamage, Element.NEUTRAL);
    }

    public void dealPureDamage(GameCharacter target, float dmg) {
        target.hp -= dmg;
    }

    /*public int dealDamage(GameCharacter target) {
        float totalDamage = 0.0f, totalPhysicalDamage = 0.0f, totalMagicalDamage = 0.0f;   // there's no magical dmg yet
        float elementalDamageModifier = getWeaponElement().getDamageModifierAgainst(target.getArmorElement());  // using right hand's element

        float basePhysicalDamage = getTotalStat(ATK) + 1.25f * GameMath.random(baseLevel);

        if (GameMath.checkChance(getTotalStat(CRIT_CHANCE))) {
            basePhysicalDamage *= getTotalStat(CRIT_DMG);
        }

        float physicalDamageAfterReduction = (100 - target.getTotalStat(ARM)) * basePhysicalDamage / 100.0f - target.getTotalStat(DEF);

        totalPhysicalDamage = elementalDamageModifier * physicalDamageAfterReduction;

        // TODO: calculate magical damage when added

        totalDamage = totalPhysicalDamage + totalMagicalDamage;
        totalDamage = Math.max(totalDamage, 0);

        target.hp -= Math.round(totalDamage);

        return Math.round(totalDamage);
    }*/

    /**
     * TODO: implement return value, which is mainly damage
     *
     *
     * @param skillCode
     * @param target
     * @return
     */
    public int useSkill(int skillCode, GameCharacter target) {
        if (skillCode >= skills.length)
            return -1;

        Skill sk = skills[skillCode];
        if (sk != null && sk.active && sk.getLevel() > 0 && sk.getCurrentCooldown() == 0) {
            if (this.sp >= sk.getManaCost()) {
                this.sp -= sk.getManaCost();
                sk.use(this, target);
            }
        }

        return 0;
    }

    /**
     * Convenient tags for pseudoHTML
     */
    protected static final String FBR = FONT_END + BR;
    protected static final String BFBR = B_END + FBR;

    public String attributesToPseudoHTML() {
        return HTML_START
                + "STR: " + B + BLUE + attributes[STR] + FONT_END + "+" + GREEN + bAttributes[STR] + BFBR
                + "VIT: " + B + BLUE + attributes[VIT] + FONT_END + "+" + GREEN + bAttributes[VIT] + BFBR
                + "DEX: " + B + BLUE + attributes[DEX] + FONT_END + "+" + GREEN + bAttributes[DEX] + BFBR
                + "AGI: " + B + BLUE + attributes[AGI] + FONT_END + "+" + GREEN + bAttributes[AGI] + BFBR
                + "INT: " + B + BLUE + attributes[INT] + FONT_END + "+" + GREEN + bAttributes[INT] + BFBR
                + "WIS: " + B + BLUE + attributes[WIS] + FONT_END + "+" + GREEN + bAttributes[WIS] + BFBR
                + "WIL: " + B + BLUE + attributes[WIL] + FONT_END + "+" + GREEN + bAttributes[WIL] + BFBR
                + "PER: " + B + BLUE + attributes[PER] + FONT_END + "+" + GREEN + bAttributes[PER] + BFBR
                + "LUC: " + B + BLUE + attributes[LUC] + FONT_END + "+" + GREEN + bAttributes[LUC] + FONT_END;
    }

    //TODO: maybe move stats here ?

    @Override
    public String toString() {
        return id + "," + name;
    }
}
