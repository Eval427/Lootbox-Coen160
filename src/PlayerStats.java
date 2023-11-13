// package Project;

import java.util.HashMap;
import java.util.Map;

public class PlayerStats {
    private Map<CustomItem, Integer> items;

    /**
     * Object to store a given player's item amounts
     */
    public PlayerStats() {
        items = new HashMap<>();
    }

    /**
     * Returns the amount of a given item a user has
     * @param item Item name
     * @return Number of given item
     */
    public int amountOf(CustomItem item) {
        for (CustomItem i : items.keySet()) {
            if (i != null) {
                if (i.getName() == item.getName()) {
                    return items.get(i);
                }
            }
        }
        return -1;
    }

    public int amountOf(String item) {
        for (CustomItem i : items.keySet()) {
            if (i != null) {
                if (i.getName() == item) {
                    return items.get(i);
                }
            }
        }
        return -1;
    }

    /**
     * Increments/decrements a given item
     * @param item Item name
     * @param increment Change in item amount
     */
    public void updateAmount(String item, int increment) {
        for (CustomItem i : items.keySet()) {
            if (i != null) {
                if (i.getName() == item) {
                    items.put(i, items.get(i) + increment);
                }
            }
        }
    }

    /**
     * Adds items to the player data
     * @param newItems Array of custom items
     */
    public void addItems(CustomItem[] newItems) {
        for (CustomItem i : newItems) {
            items.put(i, 0);
        }
    }
}
