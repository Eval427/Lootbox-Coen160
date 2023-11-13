// package Project;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PlayerStats implements Serializable {
    private Map<CustomItem, Integer> items;
    private int numUpgrades;

    /**
     * Object to store a given player's item amounts
     */
    public PlayerStats() {
        items = new HashMap<>();
        numUpgrades = 0;
    }

    /**
     * Returns the amount of a given item a user has
     * @param item Item name
     * @return Number of given item
     */
    public int amountOf(CustomItem item) {
        for (CustomItem i : items.keySet()) {
            if (i != null) {
                if (Objects.equals(i.getName(), item.getName())) {
                    return items.get(i);
                }
            }
        }
        return -1;
    }

    public int amountOf(String item) {
        for (CustomItem i : items.keySet()) {
            if (i != null) {
                if (Objects.equals(i.getName(), item)) {
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
                if (Objects.equals(i.getName(), item)) {
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

    /**
     * Used for recounting number of player upgrades gotten in game. Important for save data
     */
    public void incrementUpgradeCounter() {
        this.numUpgrades++;
    }

    /**
     * Returns number of upgrades gotten in a save
     * @return integer of upgrade number
     */
    public int upgradeNumber() {
        return this.numUpgrades;
    }
}
