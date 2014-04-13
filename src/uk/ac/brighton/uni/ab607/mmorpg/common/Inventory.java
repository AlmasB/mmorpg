package uk.ac.brighton.uni.ab607.mmorpg.common;

import java.util.ArrayList;

import uk.ac.brighton.uni.ab607.libs.main.Out;
import uk.ac.brighton.uni.ab607.mmorpg.common.item.GameItem;

// make it abstract and use droppable items to extend for monsters ?
public class Inventory implements java.io.Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 7187464078429433554L;

    private static final int MAX_SIZE = 30;

    private int size;

    private ArrayList<GameItem> items;

    private boolean hasChanged = false;

    public Inventory(int size) {
        this.size = Math.min(size, MAX_SIZE);
        items = new ArrayList<GameItem>(this.size);
    }

    public boolean addItem(GameItem item) {
        if (items.size() < size) {
            return items.add(item);
        }
        Out.err("Inventory is full");
        return false;
    }

    public void setChanged() {
        hasChanged = true;
    }

    public boolean hasChanged() {
        if (hasChanged) {
            hasChanged = false;
            return true;
        }
        return false;
    }

    // TODO: clean
    public GameItem getItem(int index) {
        if (index < items.size())
            return items.get(index);
        else
            return null;
    }

    public GameItem removeItem(GameItem item) {
        if (!items.remove(item))
            Out.err("This item isn't in the inventory");
        return item;
    }

    public ArrayList<GameItem> getItems() {
        return new ArrayList<GameItem>(items);
    }

    public int getMaxSize() {
        return size;
    }

    public int getCurrentSize() {
        return items.size();
    }

    public boolean isFull() {
        return items.size() >= size;
    }

    @Override
    public String toString() {
        return items.toString();
        /*String res = "[";
        for (GameItem item : items)
            res += item.name + ",";
        res += "]";
        return res;*/
    }
}
