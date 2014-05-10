package uk.ac.brighton.uni.ab607.mmorpg.common;

import java.util.ArrayList;

import uk.ac.brighton.uni.ab607.libs.main.Out;
import uk.ac.brighton.uni.ab607.mmorpg.common.item.GameItem;

/**
 * Represents a "bag" of items of a player
 *
 * @author Almas Baimagambetov (ab607@uni.brighton.ac.uk)
 * @version 1.0
 *
 */
public class Inventory implements java.io.Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 7187464078429433554L;

    /**
     * Matches number of elements that can be shown in inventory GUI
     */
    public static final int MAX_SIZE = 30;

    /**
     * Actual data structure
     */
    private ArrayList<GameItem> items = new ArrayList<GameItem>(MAX_SIZE);

    /**
     * Adds item to inventory if inventory isnt full
     *
     * @param item
     *              item to add
     * @return
     *          true if added, false otherwise
     */
    public boolean addItem(GameItem item) {
        if (isFull()) {
            Out.err("Inventory is full");
            return false;
        }
        return items.add(item);
    }

    /**
     * Retrieve item at given index
     *
     * @param index
     * @return
     *          item if index less than inventory size
     *          otherwise null
     */
    public GameItem getItem(int index) {
        return index < items.size() ? items.get(index) : null;
    }

    /**
     * Removes item from the inventory if it is in it
     *
     * @param item
     * @return
     *          true if removed, false otherwise
     */
    public boolean removeItem(GameItem item) {
        if (!items.remove(item)) {
            Out.err("This item isn't in the inventory");
            return false;
        }
        return true;
    }

    /**
     *
     * @return
     *          a new copy of items list, retaining
     *          references to original items
     */
    public ArrayList<GameItem> getItems() {
        return new ArrayList<GameItem>(items);
    }

    /**
     *
     * @return
     *          number of items in inventory
     */
    public int getSize() {
        return items.size();
    }

    /**
     *
     * @return
     *          true if number of items in inventory reached maximum
     *          false otherwise
     */
    public boolean isFull() {
        return items.size() == MAX_SIZE;
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
