import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 * Implements the GUI framework for the game menu
 */
public class GameWindow extends JFrame implements ActionListener {
    // Chest selection buttons
    private JButton[] chestButtons;
    private final JLabel[] itemTrackers;
    private final PlayerStats player;
    private final ChestManager chests;

    // List of items on top

    public GameWindow() {
        super("Lootbox Simulator");
        // Initialize the player
        player = new PlayerStats();

        // Initialize the chests. ALL CHESTS SHOULD BE CREATED HERE. Any new chests are automatically added to the game
        // Note that the order of insertion will determine the button layout
        chests = new ChestManager();

        // Wooden chest
        chests.makeChest("Wooden", 0, 3, 0);
        // Golden chest
        chests.makeChest("Golden", 50, 3, 1);
        // Diamond chest
        chests.makeChest("Diamond", 150, 4, 3);
        // Emerald chest
        chests.makeChest("Emerald", 450, 5, 4);
        chests.addItemToRecent("Emerald Shard", 20, 1, "%", "~~ Emerald Shard ~~", Color.green);
        chests.addItemToRecent("Chaos Shard", 1, 1, "*", "*^\\ CHAOS /^*", Color.magenta);
        // Chaos chest
        chests.makeChest("Chaos", 1000, 7, 6);
        chests.addItemToRecent("Chaos Shard", 10, 1, "*", "*^\\ CHAOS /^*", Color.magenta);
        chests.addItemToRecent("???", 5, 1, "?", "Wait a minute... why is this here?", Color.red);

        // Get JFrame container
        Container container = getContentPane();
        container.setLayout(new BorderLayout());

        // Initialize chest selection buttons
        JPanel chestSelection = new JPanel(new GridLayout(chests.getNumChests(), 1, 0, 5));
        chestSelection.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        chestButtons = new JButton[chests.getNumChests()];
        String[] chestButtonText = chests.getButtonStrings();
        for (int i=0; i < chests.getNumChests(); i++) {
            chestButtons[i] = new JButton(chestButtonText[i]);
            chestButtons[i].addActionListener(this);
            chestButtons[i].setPreferredSize(new Dimension(125, 50));
            chestSelection.add(chestButtons[i]);
        }
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
                itemTrackers[itemIndex] = new JLabel(String.format("<html><font size='5'>%s %s: <i>%d</i></font></html>", item.getIcon(), item.getName(), player.amountOf(item)));
                itemPanel.add(new JLabel("<html><font size = '5'><b>|</b></font></html>"));
                itemPanel.add(itemTrackers[itemIndex++]);
            }
        }
        itemPanel.add(new JLabel("<html><font size = '5'><b>|</b></font></html>"));
        container.add(itemPanel, BorderLayout.NORTH);

        // Initialize chest UI
        // TODO: Actually implement this
        JPanel chestPanel = new JPanel(new FlowLayout());
        chestPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        JLabel tempText = new JLabel("<html><font size = '10'>Coming Soon(tm)");
        chestPanel.add(tempText);
        container.add(chestPanel, BorderLayout.CENTER);


        // Initialize JFrame
        setSize(960, 540);
        setPreferredSize(new Dimension(960, 540));
        setMinimumSize(new Dimension(960, 540));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
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

    public void actionPerformed(ActionEvent e) {
        String toOpen = e.getActionCommand().split("<html>")[1].split("<br/>")[0];
        if (chests.getCost(toOpen) > player.amountOf("Coins")) {
            // TODO: Update UI to account for this
            System.out.println("Can't afford that chest!");
            return;
        }

        CustomItem[] rewards = chests.openChest(toOpen);

        // TODO: Fancy animation for opening

        // Update player stats
        player.updateAmount("Coins", chests.getCost(toOpen)*-1);
        // TEMP PRINTS. DELETE LATER IN REPLACE OF UI ELEMENT
        System.out.println("\n--------------------");
        for (CustomItem reward : rewards) {
            player.updateAmount(reward.getName(), reward.getIncrement());
            updateItemDisplay(reward);
            System.out.println(reward.getRewardDisplay());
        }
        System.out.println("--------------------");

        // Update display

    }

    public static void main(String[] args) {
        // 1. initialize chests
        // 2. create game window
        GameWindow game = new GameWindow();
        game.showWindow();
    }
}