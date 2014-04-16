package uk.ac.brighton.uni.ab607.mmorpg.common;

import uk.ac.brighton.uni.ab607.libs.main.Out;
import uk.ac.brighton.uni.ab607.libs.parsing.PseudoHTML;
import uk.ac.brighton.uni.ab607.mmorpg.common.ai.AgentGoalTarget;
import uk.ac.brighton.uni.ab607.mmorpg.common.combat.Element;
import uk.ac.brighton.uni.ab607.mmorpg.common.item.Armor;
import uk.ac.brighton.uni.ab607.mmorpg.common.item.ArmorFactory;
import uk.ac.brighton.uni.ab607.mmorpg.common.item.EquippableItem;
import uk.ac.brighton.uni.ab607.mmorpg.common.item.Weapon;
import uk.ac.brighton.uni.ab607.mmorpg.common.item.Weapon.WeaponType;
import uk.ac.brighton.uni.ab607.mmorpg.common.item.WeaponFactory;

public class Player extends GameCharacter implements PseudoHTML, AgentGoalTarget {

    /**
     *
     */
    private static final long serialVersionUID = 7025558302171610110L;

    /**
     * Gameplay constants
     */
    private static final int MAX_LEVEL = 100,
            MAX_ATTRIBUTE = 100,
            ATTRIBUTE_POINTS_PER_LEVEL = 3,
            SKILL_POINTS_PER_LEVEL = 1,
            EXP_NEEDED_BASE = 10;

    /**
     * By what value should experience needed for next level
     * increase per level
     */
    private static final double EXP_NEEDED_INC = 1.1;

    private static final int[] EXP_NEEDED = new int[MAX_LEVEL];

    //TODO: add exp for stat/job progression
    static {
        EXP_NEEDED[0] = EXP_NEEDED_BASE;
        for (int i = 1; i < EXP_NEEDED.length; i++) {
            EXP_NEEDED[i] = (int) (EXP_NEEDED[i-1] * EXP_NEEDED_INC);
        }
    }

    private int statLevel = 1, jobLevel = 1;

    private int gainedBaseExperience = 0,
            gainedStatExperience = 0,
            gainedJobExperience = 0,
            attributePoints = 0,
            skillPoints = 0;

    private int money = 0;
    private Inventory inventory = new Inventory(10);

    public static final int HELM = 0,
            BODY = 1,
            SHOES = 2,
            RIGHT_HAND = 3,
            LEFT_HAND = 4;

    private EquippableItem[] equip = new EquippableItem[5];

    /* EXPERIMENTAL ADDITIONS */

    private int x, y;
    public int xSpeed, ySpeed;

    public int frame = 0;
    public int place = 0;

    public int sprite = 0;  // TODO: implement

    public enum Dir {
        UP, DOWN, LEFT, RIGHT
    }

    public Dir direction = Dir.DOWN;

    private int factor = 3;

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

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    /* UP TO HERE */

    public Player(String id, String name, GameCharacterClass charClass, int x, int y) {
        super(id, name, "Player: #" + id, charClass);
        this.x = x;
        this.y = y;
        for (int i = HELM; i <= LEFT_HAND; i++) {   // helm 0, body 1, shoes 2 so we get 5000, 5001, 5002
            equip[i] = i >= RIGHT_HAND ? WeaponFactory.getWeaponById("4000") : ArmorFactory.getArmorById("500" + i);
        }
    }

    /**
     * Increase player's base experience by given value
     *
     * @param value
     *              base experience earned
     */
    public void gainBaseExperience(final int value) {
        gainedBaseExperience += value;
        if (gainedBaseExperience >= EXP_NEEDED[baseLevel-1]) {
            Out.println("Base Level UP!");
            baseLevelUp();
            gainedBaseExperience = 0;
        }
    }

    public void gainStatExperience(final int value) {
        gainedStatExperience += value;
        if (gainedStatExperience >= EXP_NEEDED[statLevel-1]) {
            Out.println("Stat Level UP!");
            statLevelUp();
            gainedStatExperience = 0;
        }
    }

    public void gainJobExperience(final int value) {
        gainedJobExperience += value;
        if (gainedJobExperience >= EXP_NEEDED[jobLevel-1]) {
            Out.println("Job Level UP!");
            jobLevelUp();
            gainedJobExperience = 0;
        }
    }

    public void baseLevelUp() {
        baseLevel++;
        calculateStats();
    }

    public void statLevelUp() {
        statLevel++;
        attributePoints += ATTRIBUTE_POINTS_PER_LEVEL;
    }

    public void jobLevelUp() {
        jobLevel++;
        skillPoints += SKILL_POINTS_PER_LEVEL;
    }

    public boolean hasAttributePoints() {
        return attributePoints > 0;
    }

    public boolean hasSkillPoints() {
        return skillPoints > 0;
    }

    public void increaseAttr(int attr) {
        if (attributes[attr] < MAX_ATTRIBUTE) {
            attributes[attr]++;
            attributePoints--;
            calculateStats();
        }
    }

    public int getMoney() {
        return money;
    }

    public void incMoney(int value) {
        money += value;
    }

    public void equipWeapon(Weapon w) {
        if (w.type.ordinal() >= WeaponType.TWO_H_SWORD.ordinal()) {
            unEquipItem(RIGHT_HAND);
            unEquipItem(LEFT_HAND);
            equip[RIGHT_HAND] = w;
            equip[LEFT_HAND] = w;
        }
        else if (w.type == WeaponType.SHIELD) {
            unEquipItem(LEFT_HAND);
            equip[LEFT_HAND] = w;
        }
        else {  // normal 1H weapon
            if (isFree(RIGHT_HAND)) {
                unEquipItem(RIGHT_HAND);
                equip[RIGHT_HAND] = w;
            }
            else {
                unEquipItem(LEFT_HAND);
                equip[LEFT_HAND] = w;
            }
        }

        inventory.removeItem(w);    // remove item from inventory
        w.onEquip(this);            // put it on
        calculateStats();
    }

    public void equipArmor(Armor a) {
        unEquipItem(a.type.ordinal());  // just because place number made to match ArmorType enum
        equip[a.type.ordinal()] = a;
        inventory.removeItem(a);
        a.onEquip(this);
        calculateStats();
    }

    public void unEquipItem(int itemPlace) {
        if (isFree(itemPlace) || inventory.isFull())
            return; // no item at this place

        equip[itemPlace].onUnEquip(this);   // take item off
        inventory.addItem(equip[itemPlace]);    // put it in inventory
        equip[itemPlace] = itemPlace >= RIGHT_HAND ? WeaponFactory.getWeaponById("4000") : ArmorFactory.getArmorById("500" + itemPlace);    // replace with default
        calculateStats();
    }

    public boolean isFree(int itemPlace) {
        return equip[itemPlace].id.equals("4000") || equip[itemPlace].id.equals("5000")
                || equip[itemPlace].id.equals("5001") || equip[itemPlace].id.equals("5002");
    }

    public EquippableItem getEquip(int place) {
        return equip[place];
    }

    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public Element getWeaponElement() {
        return getEquip(RIGHT_HAND).getElement();
    }

    @Override
    public Element getArmorElement() {
        return getEquip(BODY).getElement();
    }

    @Override
    public int dealDamage(GameCharacter target) {
        int dmg = super.dealDamage(target);
        /*if (target.hp <= 0 && target instanceof Enemy) {
            Enemy mob = (Enemy) target;
            gainBaseExperience(mob.experience);
            gainStatExperience(mob.experience);
            gainJobExperience(mob.experience);
            // TODO: possibly move onDeath of mob here to add drops straight to player inventory
        }*/
        return dmg;
    }

    /*
    @Override
    public boolean equals(Object o) {
        return o != null && (o instanceof Player && ((Player) o).name.equals(this.name) || name.equals(o));
    }*/

    @Override
    public String toPseudoHTML() {
        return "pseudo";
    }

    public String statsToPseudoHTML() {
        return HTML_START
                + B + this.name + B_END + BR
                + "Class: " + BLUE + charClass.toString() + FBR
                + "Base/Stat/Job <b>" + BLUE + baseLevel + FONT_END + "/" + PURPLE + statLevel + FONT_END + "/" + MAGENTA + jobLevel + "</b>" + FBR
                + "HP: <b>" + RED + hp + "/" + (int)getTotalStat(MAX_HP) + FONT_END + "</b> SP: <b>" + DARK_BLUE + sp + "/" + (int)getTotalStat(MAX_SP) + "</b>" + FBR
                + "ATK: " + BLUE + stats[ATK] + "+" + bStats[ATK] + FONT_END + " MATK: " + BLUE + stats[MATK] + "+" + bStats[MATK] + FBR
                + "DEF: " + BLUE + stats[DEF] + "+" + bStats[DEF] + FONT_END + " MDEF: " + BLUE + stats[MDEF] + "+" + bStats[MDEF] + FBR
                + "ARM: " + BLUE + (stats[ARM] + bStats[ARM]) + "%" + FONT_END + " MARM: " + BLUE + (stats[MARM] + bStats[MARM]) + "%" + FBR
                + "ASPD: " + BLUE + getTotalStat(ASPD) + "%" + FONT_END + " MSPD: " + BLUE + getTotalStat(MSPD) + "%" + FBR
                + "CRIT: " + BLUE + getTotalStat(CRIT) + "%" + FONT_END + " MCRIT: " + BLUE + getTotalStat(MCRIT) + "%" + FONT_END;
    }
}
