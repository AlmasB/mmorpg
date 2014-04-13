package uk.ac.brighton.uni.ab607.mmorpg.common.item;

import java.util.ArrayList;

import uk.ac.brighton.uni.ab607.mmorpg.common.ai.AgentGoalTarget;

public class Chest implements java.io.Serializable, AgentGoalTarget {

    /**
     *
     */
    private static final long serialVersionUID = -2554224770526414165L;

    private ArrayList<GameItem> items = new ArrayList<GameItem>();

    public final int x, y;

    public final int money;

    private boolean opened = false;

    public Chest(int x, int y, int money, GameItem... itemset) {
        this.x = x;
        this.y = y;
        this.money = money;
        for (GameItem item : itemset) {
            items.add(item);
        }
    }

    public void addItem(GameItem item) {
        items.add(item);
    }

    public void open() {
        opened = true;
    }

    public boolean isOpened() {
        return opened;
    }

    public ArrayList<GameItem> getItems() {
        return items;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }
}
