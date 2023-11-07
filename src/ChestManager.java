import java.util.Objects;
import java.awt.Color;
import java.util.Random;

// Stores basic information about every item in a chest
class CustomItem {
    int chance, increment;
    String name, icon, rewardDisplay;
    Color color;

    public CustomItem(String name, int chance, int increment, String icon, String rewardDisplay, Color color) {
        this.name = name;
        this.chance = chance;
        this.increment = increment;
        this.icon = icon;
        this.rewardDisplay = rewardDisplay;
        this.color = color;
    }

    public String getName() { return this.name; };

    public int getChance() { return this.chance; }

    public int getIncrement() { return this.increment; }

    public String getRewardDisplay() { return this.rewardDisplay; }

    public String getIcon() { return this.icon; }
}

// Stores information about a chest's value, its items, and has functionality to generate accurate rewards
class Chest {
    int cost, slots, numItems;
    CustomItem[] items, table;
    String name;

    /**
     * Creates a new chest
     * @param name Display name of chest to user
     * @param cost Cost to open chest
     * @param slots Number of rewards given from each open
     */
    public Chest(String name, int cost, int slots) {
        this.name = name;
        this.cost = cost;
        this.slots = slots;
        this.items = new CustomItem[10];

        // Default items
        //Small amount of coins
        addItem("Coins", 20, 10, "@", "Some Coins!", Color.black);
        //Medium amount of coins
        addItem("Coins", 15, 25, "@", "-_Extra Coins_-", Color.black);
        // Large amount of coins - Chest cost dependent
        addItem("Coins", 10*cost, 50, "@", "!!! Tons of Coins !!!", Color.orange);
    }

    public void addItem(String name, int chance, int increment, String icon, String display, Color color) {
        items[numItems++] = new CustomItem(name, chance, increment, icon, display, color);
    }

    public void generateTable() {
        int tableLen = 0;

        // Calculate length for table array
        for (CustomItem c : items) {
            tableLen += c.getChance();
        }

        // Use # insertions equal to chance to create drop chances
        table = new CustomItem[tableLen];
        int i;
        int index = 0;
        for (CustomItem c : items) {
            for (i=0; i < c.getChance(); i++) {
                table[index++] = c;
            }
        }
    }

    public CustomItem[] createRewards() {
        CustomItem[] results = new CustomItem[slots];
        Random rand = new Random();
        for (int i=0; i < slots; i++) {
            results[i] = table[rand.nextInt(table.length)];
        }

        return results;
    }

    public String getName() { return this.name; }

    public int getCost() { return this.cost; }

    public int getSlots() { return this.slots; }
}

// Manages an array of chests. This should be interacted with everywhere else
public class ChestManager {
    Chest[] chests;
    Chest mostRecent;
    int numChests;

    /**
     * Manages the array of chests
     */
    public ChestManager() {
        chests = new Chest[10];
        numChests = 0;
        mostRecent = null;
    }

    /**
     * Adds a new chest to the game
     * @param name Chest name
     * @param cost Chest cost
     * @param slots Number of rewards at one time
     */
    public void makeChest(String name, int cost, int slots) {
        // Create larger array if array becomes full
        if (numChests == chests.length) {
            Chest[] temp = new Chest[chests.length*2];
            numChests = 0;
            for (Chest c : chests) {
                temp[numChests] = c;
                numChests++;
            }
            chests = temp;
        }

        Chest newChest = new Chest(name, cost, slots);
        chests[numChests++] = newChest;
        mostRecent = newChest;
    }

    /**
     * Creates the label for any UI button for a given chest
     * @param chestName Chest name
     * @return Generated label
     */
    public String getButtonString(String chestName) {
        Chest c = getChestByName(chestName);

        assert c != null;
        return String.format("%s\n%d Coins", c.getName(), c.getCost());
    }

    /**
     * Returns chest object based on name
     * @param name Chest name
     * @return Chest object
     */
    private Chest getChestByName(String name) {
        for (Chest c : chests) {
            if (Objects.equals(c.getName(), name)) {
                return c;
            }
        }
        return null;
    }

    /**
     * Creates a custom item. Chests contain default items such as coins and basic items that are automatically balanced
     * @param name Item name. Duplicates will be added to the same item pool
     * @param chance Item drop chance
     * @param increment How much this item increments its given item pool
     * @param icon Display icon for UI
     * @param rewardDisplay Display text for when item is pulled from chest
     * @param color Display color for reward text
     */
    public void addItemToChest(String chest, String name, int chance, int increment, String icon, String rewardDisplay, Color color) {
        Chest c = getChestByName(chest);
        assert c != null;
        c.addItem(name, chance, increment, icon, rewardDisplay, color);
    }

    /**
     * Creates a custom item in most recently added chest (to remove boilerplate for item additions). Chests contain default items such as coins and basic items that are automatically balanced
     * @param name Item name. Duplicates will be added to the same item pool
     * @param chance Item drop chance
     * @param increment How much this item increments its given item pool
     * @param icon Display icon for UI
     * @param rewardDisplay Display text for when item is pulled from chest
     * @param color Display color for reward text
     */
    public void addItemToRecent(String name, int chance, int increment, String icon, String rewardDisplay, Color color) {
        assert mostRecent != null;
        mostRecent.addItem(name, chance, increment, icon, rewardDisplay, color);
    }

    /**
     * Meant to be called by ActionListener to open a chest's contents
     * @param buttonString String generated by .getActionCommand()
     * @return Reward text array
     */
    public String[] openChest(String buttonString) {
        Chest c = getChestByName(buttonString.split("\n")[0]);
        assert c != null;
        String[] rewards = new String[c.slots];

        c.generateTable();
        int i = 0;
        for (CustomItem item : c.createRewards()) {
            rewards[i++] = item.getRewardDisplay();
        }

        return rewards;
    }
}