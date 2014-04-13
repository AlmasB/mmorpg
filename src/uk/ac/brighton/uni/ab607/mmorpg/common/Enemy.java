package uk.ac.brighton.uni.ab607.mmorpg.common;

import java.util.HashMap;

import uk.ac.brighton.uni.ab607.libs.main.Out;
import uk.ac.brighton.uni.ab607.mmorpg.common.Player.Dir;
import uk.ac.brighton.uni.ab607.mmorpg.common.ai.AgentBehaviour;
import uk.ac.brighton.uni.ab607.mmorpg.common.ai.AgentGoal;
import uk.ac.brighton.uni.ab607.mmorpg.common.ai.AgentGoalTarget;
import uk.ac.brighton.uni.ab607.mmorpg.common.ai.AgentMode;
import uk.ac.brighton.uni.ab607.mmorpg.common.ai.AgentType;
import uk.ac.brighton.uni.ab607.mmorpg.common.ai.EnemyAgent;
import uk.ac.brighton.uni.ab607.mmorpg.common.combat.Element;
import uk.ac.brighton.uni.ab607.mmorpg.common.item.Chest;
import uk.ac.brighton.uni.ab607.mmorpg.common.item.GameItem;

public class Enemy extends GameCharacter implements EnemyAgent, AgentGoalTarget {

    /**
     *
     */
    private static final long serialVersionUID = -4175008430166158773L;

    public enum EnemyType {
        NORMAL, MINIBOSS, BOSS
    }

    private static int uniqueEnemyID = 2000;

    public final EnemyType type;
    //public final AgentType aiType;

    private static final int ENEMY_SIGHT = 240;

    public AgentBehaviour AI;

    public final int experience;

    private Element element;

    private int x, y;

    private HashMap<GameItem, Integer> drops = new HashMap<GameItem, Integer>();

    // TODO where do we spawn mobs
    // TODO deal with mob attributes ?
    public Enemy(String name, String description, EnemyType type, AgentBehaviour AI, Element element, int level, int x, int y) {
        super(""+uniqueEnemyID++, name, description, GameCharacterClass.MONSTER);
        this.type = type;
        this.AI = AI;
        this.baseLevel = level;
        this.element = element;
        this.x = x;
        this.y = y;
        this.hp = 5000;
        this.experience = 15;
        //AI = new AgentBehaviour(aiType, null);
    }

    public Chest onDeath() {
        Chest drop = new Chest(x, y, GameMath.random(this.baseLevel * 100));
        for (GameItem item : drops.keySet()) {
            if (GameMath.checkChance(drops.get(item))) {
                drop.addItem(item);
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

    // TODO: add some kind of search algorithm, target is @Nullable
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
        else {
            // TODO: fuzzy logic should be here
            Out.debug("NULL");

            xSpeed = -5;
            ySpeed = -5;
            move();
        }
    }

    // TODO: something cleaner
    @Override
    public void attack(AgentGoalTarget target) {
        for (int i = 0; i < 5; i++) {
            if (x > target.getX())
                xSpeed = -1;
            if (x < target.getX())
                xSpeed = 1;

            if (y > target.getY())
                ySpeed = -1;
            if (y < target.getY())
                ySpeed = 1;


            if (xSpeed == 0 && ySpeed == 0) // if reached just drop the target
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
