package uk.ac.brighton.uni.ab607.mmorpg.common.object;

import uk.ac.brighton.uni.ab607.mmorpg.common.GameCharacter;
import uk.ac.brighton.uni.ab607.mmorpg.common.GameCharacterClass;
import uk.ac.brighton.uni.ab607.mmorpg.common.GameMath;
import uk.ac.brighton.uni.ab607.mmorpg.common.ai.AgentBehaviour;
import uk.ac.brighton.uni.ab607.mmorpg.common.ai.AgentGoal;
import uk.ac.brighton.uni.ab607.mmorpg.common.ai.AgentGoalTarget;
import uk.ac.brighton.uni.ab607.mmorpg.common.ai.AgentMode;
import uk.ac.brighton.uni.ab607.mmorpg.common.ai.EnemyAgent;
import uk.ac.brighton.uni.ab607.mmorpg.common.combat.Element;
import uk.ac.brighton.uni.ab607.mmorpg.common.item.Chest;
import uk.ac.brighton.uni.ab607.mmorpg.common.item.DroppableItem;

public class Enemy extends GameCharacter implements EnemyAgent, AgentGoalTarget {

    /**
     *
     */
    private static final long serialVersionUID = -4175008430166158773L;

    public enum EnemyType {
        NORMAL, MINIBOSS, BOSS
    }

    public final EnemyType type;

    private static final int ENEMY_SIGHT = 240;

    public AgentBehaviour AI;

    public final int experience;

    private Element element;

    private DroppableItem[] drops;

    // TODO deal with mob attributes ?

    /*package-private*/ Enemy(String id, String name, String description, EnemyType type, AgentBehaviour AI, Element element, int level, int baseXP, DroppableItem... drops) {
        super(name, description, GameCharacterClass.MONSTER);
        this.id = id;
        this.type = type;
        this.AI = AI;
        this.element = element;
        this.baseLevel = level;
        this.experience = baseXP;
        this.drops = drops;
    }

    /*package-private*/ Enemy(Enemy copy) {
        this(copy.id, copy.name, copy.description, copy.type, copy.AI, copy.element, copy.baseLevel, copy.experience, copy.drops);
    }

    public Chest onDeath() {
        alive = false;
        Chest drop = new Chest(x, y, GameMath.random(this.baseLevel * 100));
        for (DroppableItem item : drops) {
            if (GameMath.checkChance(item.dropChance)) {
                drop.addItem(ObjectManager.getItemByID(item.itemID));
            }
        }
        return drop;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public Element getWeaponElement() {
        return element;
    }

    @Override
    public Element getArmorElement() {
        return element;
    }

    // TODO: add priority based rule system
    @Override
    public void proceedToGoal() {
        switch (AI.currentGoal) {
            case FIND_PLAYER:
                // search map using fuzzy logic, e.g. if player killed smth or done smth his last place becomes known
                break;
            case GUARD_CHEST:
                // do some patrolling
                break;
            case KILL_PLAYER:
                // do this only if player seen or place known
                break;
            default:
                // display error message
                break;
        }
    }

    /* EXPERIMENTAL ADDITIONS */

    //public int xSpeed, ySpeed;



    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    /* UP TO HERE */

    @Override
    public AgentMode getMode() {
        return AI.type.mode;
    }

    @Override
    public AgentGoal getGoal() {
        return AI.currentGoal;
    }

    @Override
    public void patrol(AgentGoalTarget target) {
        //Out.debug("Called in patrol");

        if (x > target.getX() + 80)
            xSpeed = -2;
        if (x < target.getX() - 80)
            xSpeed = 2;

        move();
    }

    @Override
    public void search(AgentGoalTarget target) {
        if (target != null) {
            if (canSee(target)) {
                return;
            }

            for (int i = 0; i < 2; i++) {
                if (x > target.getX())
                    xSpeed = -1;
                if (x < target.getX())
                    xSpeed = 1;

                if (y > target.getY())
                    ySpeed = -1;
                if (y < target.getY())
                    ySpeed = 1;

                move();
                xSpeed = 0;
                ySpeed = 0;
            }
        }
    }

    @Override
    public void attackAI(AgentGoalTarget target) {

        for (int i = 0; i < 3; i++) {
            if (x > target.getX())
                xSpeed = -1;
            if (x < target.getX())
                xSpeed = 1;

            if (y > target.getY())
                ySpeed = -1;
            if (y < target.getY())
                ySpeed = 1;

            if (x == target.getX() && y == target.getY())   // if reached means not the target so drop it and continue search
                AI.currentTarget = null;


            move();
            xSpeed = 0;
            ySpeed = 0;
        }
    }

    @Override
    public boolean canSee(AgentGoalTarget ch) {
        return ch.getX() >= getX() - ENEMY_SIGHT
                && ch.getX() <= getX() + ENEMY_SIGHT
                && ch.getY() >= getY() - ENEMY_SIGHT
                && ch.getY() <= getY() + ENEMY_SIGHT;
    }
}
