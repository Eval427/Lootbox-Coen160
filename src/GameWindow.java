import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 * Implements the GUI framework for the game menu
 */
public class GameWindow extends JFrame implements ActionListener {
    // Chest selection buttons
    private int numChests;
    private JButton[] chests;
    private final JLabel[] itemTrackers;
    private final Map<String, Integer> items;

    // List of items on top

    public GameWindow(int numChests) {
        super("Lootbox Simulator");
        this.numChests = numChests;

        // Get JFrame container
        Container container = getContentPane();
        container.setLayout(new BorderLayout());

        // Initialize chest selection buttons
        JPanel chestSelection = new JPanel(new GridLayout(numChests, 1, 0, 5));
        chestSelection.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        chests = new JButton[this.numChests];
        for (int i=0; i < this.numChests; i++) {
            // TODO: Change to "ChestName - ChestPrice"
            chests[i] = new JButton("Chest " + (i+1));
            chests[i].addActionListener(this);
            chests[i].setPreferredSize(new Dimension(125, 50));
            chestSelection.add(chests[i]);
        }
        container.add(chestSelection, BorderLayout.EAST);

        // Initialize item counter
        // TODO: Make this automatically gather all item types from chests
        JPanel itemPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        itemPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        items = new LinkedHashMap<>();
        items.put("Coins", 0);
        items.put("Test 1", 10);
        items.put("Test 2", 20);

        // TODO: Automatically adjust array size to match number of custom items
        itemTrackers = new JLabel[10];
        int itemIndex = 0;
        for (Map.Entry<String, Integer> entry: items.entrySet()) {
            itemTrackers[itemIndex] = new JLabel(String.format("<html><font size='5'>%s: <i>%d</i></font></html>", entry.getKey(), entry.getValue()));
            itemPanel.add(new JLabel("<html><font size = '5'><b>|</b></font></html>"));
            itemPanel.add(itemTrackers[itemIndex++]);
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
     * @param increment Amount to increase/decrease item amount
     */
    public void updateItemAmount(String item, int increment) {
        JLabel toUpdate = null;

        // Fetch correct JLabel
        for (JLabel label: itemTrackers) {
            if (label.getText().contains(item)) {
                toUpdate = label;
                break;
            }
        }

        if (toUpdate == null) {
            System.out.println("IGNORING updateItemAmount() call. Invalid item " + item);
        } else {
            toUpdate.setText(String.format("<html><font size='5'>%s: <i>%d</i></font></html>", item, (items.get(item) + increment)));
            items.put(item, items.get(item) + increment);
        }
    }

    public void actionPerformed(ActionEvent e) {
        updateItemAmount("Coins", 10);
        System.out.println(e.getActionCommand());
    }

    /**
     * Automatically updates the item count text on the top of the UI
     * Call after any chest opening
     */
    public void updateItems() {
        // TODO: implement this lol
        System.out.println("Implement me");
    }

    public static void main(String[] args) {
        // 1. initialize chests
        // 2. create game window
        GameWindow game = new GameWindow(10);
        game.showWindow();
    }
}