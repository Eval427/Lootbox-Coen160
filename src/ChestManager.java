import java.util.Arrays;
import java.util.Objects;
import java.awt.Color;
import java.util.Random;

// Stores information about a chest's value, its items, and has functionality to generate accurate rewards
class Chest {
    private int cost, slots, numItems;
    private CustomItem[] items, table;
    private String name;
    private Color color;

    /**
     * Creates a new chest
     * @param name Display name of chest to user
     * @param cost Cost to open chest
     * @param slots Number of rewards given from each open
     */
    public Chest(String name, int cost, int slots, int value, Color color) {
        this.name = name;
        this.cost = cost;
        this.slots = slots;
        this.items = new CustomItem[10];
        this.numItems = 0;
        this.color = color;
    }

    public void addItem(CustomItem item) {
        // I really, really doubt there will be more than 10 items. No need to reallocate the array
        items[numItems++] = item;
    }

    public void generateTable() {
        int tableLen = 0;

        // Calculate length for table array
        for (CustomItem c : items) {
            if (c != null) {
                tableLen += c.getChance();
            }
        }

        // Use # insertions equal to chance to create drop chances
        table = new CustomItem[tableLen];
        int i;
        int index = 0;
        for (CustomItem c : items) {
            if (c != null) {
                for (i = 0; i < c.getChance(); i++) {
                    table[index++] = c;
                }
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

    public Color getColor() { return this.color; }
}

// Manages an array of chests. This should be interacted with everywhere else
public class ChestManager {
    private Chest[] chests;
    private Chest mostRecent;
    private int numChests, numItems;
    private CustomItem[] allItems;

    /**
     * Manages the array of chests
     */
    public ChestManager() {
        chests = new Chest[10];
        numChests = 0;
        mostRecent = null;
        allItems = new CustomItem[10];
        numItems = 0;
    }

    /**
     * Adds a new chest to the game
     * @param name Chest name
     * @param cost Chest cost
     * @param slots Number of rewards at one time
     * @param value Multiplier for rare items
     */
    public void makeChest(String name, int cost, int slots, int value, Color color) {
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

        Chest newChest = new Chest(name, cost, slots, value, color);
        chests[numChests++] = newChest;
        mostRecent = newChest;

        // Add default items and save to global item array
        CustomItem sCoins = new CustomItem("Coins", 20, 10, "@", "Some Coins!", Color.black);
        newChest.addItem(sCoins);
        addItemToList(sCoins);
        //Medium amount of coins
        CustomItem mCoins = new CustomItem("Coins", 15, 25, "@", "-!Extra Coins!-", Color.black);
        newChest.addItem(mCoins);
        addItemToList(mCoins);
        // Large amount of coins - Chest cost dependent
        CustomItem lCoins = new CustomItem("Coins", 10*value, 50, "@", "!!! Tons of Coins !!!", Color.blue);
        newChest.addItem(lCoins);
        addItemToList(lCoins);
        // Weird shard - Chest cost dependent
        CustomItem wShard = new CustomItem("Weird Shard", 3*value, 1, "%", "%%% A Weird Shard %%%", Color.cyan);
        newChest.addItem(wShard);
        addItemToList(wShard);
        // Golden shard - Chest cost dependent
        CustomItem gShard = new CustomItem("Golden Shard", 2*value, 1, "$", "~$ Golden Shard $~", Color.orange);
        newChest.addItem(gShard);
        addItemToList(gShard);
    }

    private void addItemToList(CustomItem item) {
        boolean add = true;
        for (int i=0; i < numItems; i++) {
            if (allItems[i].getName() == item.getName()) {
                add = false;
                break;
            }
        }
        if (add) allItems[numItems++] = item;
    }

    /**
     * Returns array of button strings for creation of the UI
     * @return Generated label array
     */
    public String[] getButtonStrings() {
        String[] result = new String[numChests];
        int i = 0;

        for (Chest c : chests) {
            if (c != null) {
                result[i++] = String.format("<html>%s<br/>%s</html>", c.getName(), c.getCost() > 0 ? (c.getCost() + " Coins") : "Free");
            }
        }

        return result;
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

        CustomItem newItem = new CustomItem(name, chance, increment, icon, rewardDisplay, color);
        c.addItem(newItem);
        addItemToList(newItem);
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

        CustomItem newItem = new CustomItem(name, chance, increment, icon, rewardDisplay, color);
        mostRecent.addItem(newItem);
        addItemToList(newItem);
    }

    /**
     * Meant to be called by ActionListener to open a chest's contents
     * @param buttonString String generated by .getActionCommand()
     * @return Reward text array
     */
    public CustomItem[] openChest(String buttonString) {
        Chest c = getChestByName(buttonString.split("\n")[0]);
        assert c != null;

        c.generateTable();
        return c.createRewards();
    }

    public int getNumChests() { return this.numChests; }

    public CustomItem[] getAllItems() { return this.allItems; }

    public int getCost(String chestName) {
        Chest c = getChestByName(chestName);
        assert c != null;
        return c.getCost();
    }

    public Color getColor(String chestName) {
        Chest c = getChestByName(chestName);
        assert c!= null;
        return c.getColor();
    }

    public CustomItem getItemByIcon(String icon) {
        for (CustomItem i : allItems) {
            if (Objects.equals(i.getIcon(), icon)) {
                return i;
            }
        }
        return null;
    }

    /**
     * The massive ascii chest that will be used
     * @return ascii art
     */
    public String[] closedDisplayString() {
        return new String[]{
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓\n",
                "▓▓░░▓▓▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▓▓░░▓▓\n",
                "▓▓░░▓▓▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▓▓░░▓▓\n",
                "▓▓░░▓▓▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▓▓░░▓▓\n",
                "▓▓░░▓▓▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▓▓░░▓▓\n",
                "▓▓░░▓▓▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▓▓░░▓▓\n",
                "▓▓░░▓▓▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▓▓░░▓▓\n",
                "▓▓░░▓▓▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▓▓░░▓▓\n",
                "▓▓░░▓▓▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▓▓░░▓▓\n",
                "▓▓░░▓▓▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▓▓░░▓▓\n",
                "▓▓░░▓▓▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▓▓░░▓▓\n",
                "▓▓░░▓▓▒▒▒▒▒▒▒▒▒▒▓▓▓▓▒▒▒▒▒▒▒▒▒▒▒▒▓▓░░▓▓\n",
                "▓▓░░▓▓▓▓▓▓▓▓▓▓▓▓░░░░▓▓▓▓▓▓▓▓▓▓▓▓▓▓░░▓▓\n",
                "▓▓░░▒▒▒▒▒▒░░▒▒▓▓░░░░▓▓▒▒░░▒▒▒▒▒▒▒▒░░▓▓\n",
                "▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓░░░░▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓\n",
                "▓▓▒▒▓▓▓▓▓▓▓▓▓▓▓▓▒▒▒▒▓▓▓▓▓▓▓▓▓▓▓▓▓▓▒▒▓▓\n",
                "▓▓▒▒▓▓▓▓▓▓▓▓▓▓▓▓▒▒▒▒▓▓▓▓▓▓▓▓▓▓▓▓▓▓▒▒▓▓\n",
                "▓▓░░▓▓▒▒▒▒▒▒▒▒▓▓▓▓▓▓▓▓▒▒▒▒▒▒▒▒▒▒▓▓░░▓▓\n",
                "▓▓░░▓▓▒▒▒▒▒▒▒▒▒▒▓▓▓▓▒▒▒▒▒▒▒▒▒▒▒▒▓▓░░▓▓\n",
                "▓▓░░▓▓▒▒▒▒▒▒▒▒▒▒▒▓▓▒▒▒▒▒▒▒▒▒▒▒▒▒▓▓░░▓▓\n",
                "▓▓░░▓▓▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▓▓░░▓▓\n",
                "▓▓░░▓▓▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▓▓░░▓▓\n",
                "▓▓░░▓▓▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▓▓░░▓▓\n",
                "▓▓░░▓▓▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▓▓░░▓▓\n",
                "▓▓░░▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓░░▓▓\n",
                "▓▓░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░▓▓\n",
                "▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓\n"
        };
    }

    public String[] openedDisplayString() {
        return new String[] {
                "",
                "",
                "",
                "",
                "▓▓▓▓            .\n",
                "▒▒▒▒▒▒▒▒▒▒▒▒▒▒░░░░▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒░░\n",
                "▓▓▒▒▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▒▒▓▓\n",
                "▓▓▒▒▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▒▒▓▓\n",
                "▓▓▒▒▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▒▒▓▓\n",
                "▓▓▒▒▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▒▒▓▓\n",
                "▓▓▒▒▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▒▒▓▓\n",
                "▓▓▒▒▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▒▒▓▓\n",
                "▓▓▒▒▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▒▒▓▓\n",
                "▓▓▒▒▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▒▒▓▓\n",
                "▓▓▒▒▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▒▒▓▓\n",
                "▓▓▓▓▓▓▓▓▓▓░░░░░░░░▓▓▓▓░░░░░░▓▓▓▓▓▓▓▓\n",
                "▓▓▓▓▓▓▓▓▓▓░░░░░░░░▓▓▒▒░░░░░░▓▓▓▓▓▓▓▓\n",
                "▓▓░░▓▓▓▓░░░░░░░░░░░░░░░░░░░░▓▓▓▓░░▓▓\n",
                "▓▓░░▓▓░░░░▒▒░░░░░░░░▒▒▒▒░░░░░░▓▓░░▓▓\n",
                "▓▓░░▓▓░░▒▒▒▒▒▒░░▒▒▒▒▒▒▒▒▒▒▒▒▒▒▓▓░░▓▓\n",
                "▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓\n",
                "▓▓░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░▓▓\n",
                "▓▓░░▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓░░▓▓\n",
                "▓▓░░▓▓▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▓▓░░▓▓\n",
                "▓▓░░▓▓▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▓▓░░▓▓\n",
                "▓▓░░▓▓▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▓▓░░▓▓\n",
                "▓▓░░▓▓▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▓▓░░▓▓\n",
                "▓▓░░▓▓▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▓▓░░▓▓\n",
                "▓▓░░▓▓▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▓▓░░▓▓\n",
                "▓▓░░▓▓▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▓▓░░▓▓\n",
                "▓▓░░▓▓▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▓▓░░▓▓\n",
                "▓▓░░▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓░░▓▓\n",
                "▓▓░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░▓▓\n",
                "▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓"};
    }
}