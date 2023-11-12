import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Implements the GUI framework for the game menu
 */
public class GameWindow extends JFrame implements ActionListener {
    // Chest selection buttons
    private JButton[] chestButtons;
    private final PlayerStats player;
    private final ChestManager chests;
    private final JLabel[] chestDisplay, rewardTrackers, itemTrackers;
    private final JPanel rewardDisplay, bottomPanel;
    private final JLabel openLore;
    private String toOpen;
    private final JButton upgradeButton;
    private final Upgrade[] upgrades;
    private int upgradeIndex;

    // List of items on top

    public GameWindow() {
        super("Lootbox Simulator");
        // Initialize the player
        player = new PlayerStats();

        // Initialize the chests. ALL CHESTS SHOULD BE CREATED HERE. Any new chests are automatically added to the game
        // Note that the order of insertion will determine the button layout
        chests = new ChestManager();

        // Wooden chest
        chests.makeChest("Wooden", 0, 3, 0, Color.black);
        // Golden chest
        chests.makeChest("Golden", 50, 3, 1, Color.orange);
        // Diamond chest
        chests.makeChest("Diamond", 150, 4, 3, Color.cyan);
        // Emerald chest
        chests.makeChest("Emerald", 450, 5, 4, Color.green);
        chests.addItemToRecent("Emerald Shard", 20, 1, "%", "~~ Emerald Shard ~~", Color.green);
        chests.addItemToRecent("Chaos Shard", 1, 1, "*", "*^\\ CHAOS /^*", Color.magenta);
        // Chaos chest
        chests.makeChest("Chaos", 1000, 7, 6, Color.red);
        chests.addItemToRecent("Chaos Shard", 10, 1, "*", "*^\\ CHAOS /^*", Color.magenta);
        chests.addItemToRecent("???", 5, 1, "?", "Wait a minute... why is this here?", Color.red);

        // Get JFrame container
        Container container = getContentPane();
        container.setLayout(new BorderLayout());

        // Initialize chest selection buttons
        JPanel chestSelection = new JPanel(new GridLayout(chests.getNumChests() + 2, 1, 0, 5));
        chestSelection.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        chestButtons = new JButton[chests.getNumChests()];
        String[] chestButtonText = chests.getButtonStrings();
        for (int i=0; i < chests.getNumChests(); i++) {
            chestButtons[i] = new JButton(chestButtonText[i]);
            chestButtons[i].addActionListener(this);
            chestButtons[i].setPreferredSize(new Dimension(125, 50));
            chestSelection.add(chestButtons[i]);
        }
        chestSelection.add(new JLabel());
        upgradeButton = new JButton("Upgrade");
        upgradeButton.addActionListener(this);
        chestSelection.add(upgradeButton);
        container.add(chestSelection, BorderLayout.EAST);

        // Initialize item counter
        JPanel itemPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        itemPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        // Add all custom chest items to player tracker
        player.addItems(chests.getAllItems());

        itemTrackers = new JLabel[10];
        int itemIndex = 0;
        for (CustomItem item : chests.getAllItems()) {
            if (item != null) {
                itemTrackers[itemIndex] = new JLabel(String.format("<html><font size='5'>%s %s: %d</font></html>", item.getIcon(), item.getName(), player.amountOf(item)));
                itemPanel.add(new JLabel("<html><font size = '5'><b>|</b></font></html>"));
                itemPanel.add(itemTrackers[itemIndex++]);
            }
        }
        itemPanel.add(new JLabel("<html><font size = '5'><b>|</b></font></html>"));
        container.add(itemPanel, BorderLayout.NORTH);

        // Initialize chest UI
        // chestPanel contains the two panels that contain the chest UI element and the list of rewards that drop
        JPanel chestPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        chestPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        // Displaying ascii chest
        String[] chestLabelStrings = chests.closedDisplayString(); // Array of strings to use for the JLabels
        JPanel chestImage = new JPanel(new GridLayout(40, 1, 0, 0));
        chestDisplay = new JLabel[chestLabelStrings.length];

        // Add chest ascii
        for (int i=0; i < chestLabelStrings.length; i++) {
            chestDisplay[i] = new JLabel(chestLabelStrings[i], SwingConstants.CENTER);
            chestDisplay[i].setFont(new Font("Courier", Font.PLAIN, 10));
            chestDisplay[i].setForeground(Color.black);
            chestImage.add(chestDisplay[i]);
        }

        rewardDisplay = new JPanel(new GridLayout(7, 1, 0, 5)); // Rightmost panel for list of rewards
        rewardTrackers = new JLabel[7]; // Labels to display rewards
        for (int i=0; i < rewardTrackers.length; i++) {
            rewardTrackers[i] = new JLabel("", SwingConstants.CENTER);
            rewardDisplay.add(rewardTrackers[i]);
        }
        chestPanel.add(chestImage);
        chestPanel.add(rewardDisplay);
        container.add(chestPanel, BorderLayout.CENTER);

        // Add bottom functionality
        JButton openButton = new JButton("Open");
        openButton.setPreferredSize(new Dimension(125, 50));
        openButton.addActionListener(this);
        openLore = new JLabel("", SwingConstants.CENTER);
        bottomPanel = new JPanel(new GridLayout(2, 1, 0, 0));
        bottomPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        bottomPanel.add(openLore);
        bottomPanel.add(openButton);
        container.add(bottomPanel, BorderLayout.SOUTH);

        // Initialize JFrame
        setSize(960, 540);
        setPreferredSize(new Dimension(960, 540));
        setMinimumSize(new Dimension(960, 540));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);

        // Initialize upgrades
        upgrades = new Upgrade[20];
        upgradeIndex = 0;

        // Upgrade 1: Background color
        HashMap<String, Integer> cost = new HashMap<>();
        cost.put("@", 100);
        upgrades[0] = new Upgrade("Background", cost) {
            @Override
            public void upgradeAction() {
                chestSelection.setBackground(Color.blue);
            }
        };

        cost = new HashMap<>();
        cost.put("%", 1);
        upgrades[1] = new Upgrade("Add me!", cost) {
            @Override
            public void upgradeAction() {
                System.out.println("I don't do anything yet...");
            }
        };
        // Add more upgrades here...

        upgradeButton.setText(upgrades[0].getUpgradeString());
    }

    /**
     * Shows the game screen
     */
    public void showWindow() {
        setVisible(true);
    }

    /**
     * Hides the game screen
     */
    public void hideWindow() {
        setVisible(false);
    }

    /**
     * Increments the number of a given item
     * @param item Item which should be updated
     */
    public void updateItemDisplay(CustomItem item) {
        JLabel toUpdate = null;

        // Fetch correct JLabel
        for (JLabel label: itemTrackers) {
            if (label.getText().contains(item.getName())) {
                toUpdate = label;
                break;
            }
        }

        if (toUpdate == null) {
            System.out.println("IGNORING updateItemAmount() call. Invalid item " + item);
        } else {
            toUpdate.setText(String.format("<html><font size='5'>%s %s: <i>%d</i></font></html>", item.getIcon(), item.getName(), player.amountOf(item)));
        }
    }

    private boolean commandIsUpgrade(String command) {
        for (Upgrade u : upgrades) {
            if (u != null) {
                if (command.contains(u.getName())) return true;
            }
        }
        return false;
    }

    private boolean canAffordUpgrade(Upgrade u) {
        boolean canAfford = true;
        for (String item : u.getCost().keySet()) {
            if (u.getCost().get(item) > player.amountOf(chests.getItemByIcon(item))) {
                canAfford = false;
            }
        }
        return canAfford;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Clear reward text
        for (JLabel label : rewardTrackers) {
            label.setText("");
        }

        // Close chest
        String[] chestLabelStrings = chests.closedDisplayString();
        for (int i = 0; i < chestLabelStrings.length; i++) {
            chestDisplay[i].setText(chestLabelStrings[i]);
        }

        if (e.getActionCommand() != "Open") {
            // Upgrade
            if (commandIsUpgrade(e.getActionCommand())) { // Ensure button press is upgrade
                if (canAffordUpgrade(upgrades[upgradeIndex])) { // Ensure user can afford upgrade
                    for (String i : upgrades[upgradeIndex].getCost().keySet()) { // Decrement player items
                        player.updateAmount(chests.getItemByIcon(i).getName(), upgrades[upgradeIndex].getCost().get(i)*-1);
                    }
                    // Enact the upgrade
                    upgrades[upgradeIndex++].upgradeAction();
                    upgradeButton.setText(upgrades[upgradeIndex].getUpgradeString());
                    openLore.setText("Huh, now that looks weird...");
                    openLore.setForeground(Color.blue);
                } else { // Display player is too poor
                    openLore.setText("You can't afford that upgrade yet!");
                    openLore.setForeground(Color.red);
                }
                return;
            }

            // Chest selection
            toOpen = e.getActionCommand().split("<html>")[1].split("<br/>")[0];
            for (JLabel label : chestDisplay) {
                label.setForeground(chests.getColor(toOpen));
            }
            
            // Handle case of not affording chest
            if (chests.getCost(toOpen) > player.amountOf("Coins")) {
                openLore.setForeground(Color.red);
                openLore.setText(String.format("You can't afford a %s Chest!", toOpen));
            } else {
                openLore.setForeground(Color.black);
                openLore.setText(String.format("Taking a chance on a %s Chest?", toOpen));
            }
            return;
        }

        if (chests.getCost(toOpen) > player.amountOf("Coins")) return;

        // Generate rewards
        CustomItem[] rewards = chests.openChest(toOpen);

        // Decrement player coins
        player.updateAmount("Coins", chests.getCost(toOpen) * -1);

        // Chest opening
        chestLabelStrings = chests.openedDisplayString();
        for (int i=0; i < chestLabelStrings.length; i++) {
            chestDisplay[i].setText(chestLabelStrings[i]);
        }

        // Display rewards
        int startIndex = (int) Math.floor((double) (7 - rewards.length) / 2);
        for (CustomItem reward : rewards) {
            player.updateAmount(reward.getName(), reward.getIncrement());
            updateItemDisplay(reward);
            rewardTrackers[startIndex].setText(reward.getRewardDisplay());
            rewardTrackers[startIndex++].setForeground(reward.getColor());
        }

        // Update lore
        openLore.setText("That's pretty good... I think!");
    }

    public static void main(String[] args) {
        // 1. initialize chests
        // 2. create game window
        GameWindow game = new GameWindow();
        game.showWindow();
    }
}