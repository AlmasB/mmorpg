package uk.ac.brighton.uni.ab607.mmorpg.common;

import static uk.ac.brighton.uni.ab607.libs.parsing.PseudoHTML.*;

import java.util.ArrayList;
import java.util.Iterator;

import uk.ac.brighton.uni.ab607.mmorpg.common.StatusEffect.Status;
import uk.ac.brighton.uni.ab607.mmorpg.common.ai.AgentGoalTarget;
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
    public static final int STR = 0,    // ATTRIBUTES
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
            MCRIT_DMG = 13,
            HP_REGEN = 14,
            SP_REGEN = 15;

    protected int[] attributes = new int[9];    // we have 9 attributes
    protected int[] bAttributes = new int[9];   // on top of native attributes, bonuses can be given we items
    protected float[] stats = new float[16];        // 16 stats
    protected float[] bStats = new float[16];       // bonus stats given by item

    protected Skill[] skills;

    private ArrayList<StatusEffect> statuses = new ArrayList<StatusEffect>();
    private ArrayList<Effect> effects = new ArrayList<Effect>();

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
     *              one of the constants for stat, MAX_HP = 0, SP_REGEN = 15
     * @return
     *          total value for stat, including bonuses
     */
    public float getTotalStat(int stat) {
        return stats[stat] + bStats[stat];
    }

    public float getTotalStat(Stat stat) {
        return stats[stat.ordinal()] + bStats[stat.ordinal()];
    }

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

        stats[HP_REGEN] = 1 + vitality * MODIFIER_VERY_LOW;
        stats[SP_REGEN] = 2 + wisdom * MODIFIER_VERY_LOW;
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
     * @param status
     * @return
     *          true if character is under "@param status" status effect
     *          false otherwise
     */
    public boolean hasStatusEffect(Status status) {
        synchronized (statuses) {
            for (StatusEffect e : statuses) {
                if (e.getStatus() == status) {
                    return true;
                }
            }
        }

        return false;
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
            e.onBegin(this);
            effects.add(e);
            calculateStats();
        }
    }

    public void addStatusEffect(StatusEffect e) {
        synchronized (statuses) {
            statuses.add(e);
        }
    }

    protected void updateEffects() {
        for (Iterator<Effect> it = effects.iterator(); it.hasNext(); ) {
            Effect e = it.next();
            e.reduceDuration(0.05f);
            if (e.getDuration() <= 0) {
                e.onEnd(this);
                it.remove();
                calculateStats();
            }
        }
    }

    private void updateStatusEffects() {
        for (Iterator<StatusEffect> it = statuses.iterator(); it.hasNext(); ) {
            StatusEffect e = it.next();
            e.reduceDuration(0.05f);
            if (e.getDuration() <= 0) {
                it.remove();
            }
        }
    }

    protected float regenTick = 0.0f;

    public void update() {
        // HP/SP regen
        regenTick += 0.05f;

        if (regenTick >= 2.0f) {    // 2 secs
            hp = Math.min((int)getTotalStat(MAX_HP), (int)(hp + getTotalStat(HP_REGEN)));
            sp = Math.min((int)getTotalStat(MAX_SP), (int)(sp + getTotalStat(SP_REGEN)));
            regenTick = 0.0f;
        }

        // skill cooldowns

        for (Skill sk : skills) {
            if (sk.active) {
                if (sk.getCurrentCooldown() > 0) {
                    sk.reduceCurrentCooldown(0.05f);
                }
            }
            else {  // reapply passive skills
                if (sk.getLevel() > 0)
                    sk.use(this, null);
            }
        }

        // check buffs
        updateEffects();
        updateStatusEffects();

        calculateStats();
    }

    /**
     * Change this characters game class to @param cl
     *
     * @param cl
     *          game character class to change to
     */
    public void changeClass(GameCharacterClass cl) {
        this.charClass = cl;
        this.skills = cl.skills;
        calculateStats();
    }

    /**
     * Performs basic attack with equipped weapon
     * Damage is physical and element depends on weapon element
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

    /**
     * Deals physical damage of type NEUTRAL to target.
     * The damage is reduced by target's armor and DEF
     *
     * @param target
     * @param baseDamage
     *
     * @return
     *          damage dealt
     */
    public int dealPhysicalDamage(GameCharacter target, float baseDamage) {
        return dealPhysicalDamage(target, baseDamage, Element.NEUTRAL);
    }

    /**
     * Deal magical damage of type param element to target. The damage is reduced by target's
     * magical armor and MDEF
     *
     * @param target
     * @param baseDamage
     *
     * @return
     *          damage dealt
     */
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

    /**
     * Deal magical damage of type NEUTRAL to target. The damage is reduced by target's
     * magical armor and MDEF
     *
     * @param target
     * @param baseDamage
     *
     * @return
     *          damage dealt
     */
    public int dealMagicalDamage(GameCharacter target, float baseDamage) {
        return dealMagicalDamage(target, baseDamage, Element.NEUTRAL);
    }

    /**
     * Deals the exact amount of damage to target as specified by
     * param dmg
     *
     * @param target
     * @param dmg
     */
    public void dealPureDamage(GameCharacter target, float dmg) {
        target.hp -= dmg;
    }

    /**
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

    @Override
    public String toString() {
        return id + "," + name;
    }

    public boolean canSee(AgentGoalTarget ch) {
        return ch.getX() >= getX() - 240
                && ch.getX() <= getX() + 240
                && ch.getY() >= getY() - 240
                && ch.getY() <= getY() + 240;
    }

    // For Drawing/Moving screen stuff
    protected int x, y;

    public int xSpeed, ySpeed;

    public int frame = 0;
    public int place = 0;
    public int sprite = 0;
    private int factor = 3;

    public enum Dir {
        UP, DOWN, LEFT, RIGHT
    }

    public Dir direction = Dir.DOWN;

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void move() {
        x += xSpeed;
        y += ySpeed;

        if (xSpeed > 0)
            direction = Dir.RIGHT;
        if (xSpeed < 0)
            direction = Dir.LEFT;
        if (ySpeed > 0)
            direction = Dir.DOWN;
        if (ySpeed < 0)
            direction = Dir.UP;

        frame++;

        if (frame == 4 * factor)
            frame = 0;

        if (frame /factor == 0 || frame/factor == 2)
            place = 0;
        if (frame/factor == 1)
            place = 1;
        if (frame/factor == 3)
            place = 2;
    }

    public int getRow() {
        return direction.ordinal();
    }
}
