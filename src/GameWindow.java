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
        Map<String, Integer> items = new LinkedHashMap<>();
        items.put("Coins", 0);
        items.put("Test 1", 10);
        items.put("Test 2", 20);
        for (Map.Entry<String, Integer> entry: items.entrySet()) {
            itemPanel.add(new JLabel("<html><font size = '5'><b>|</b></font></html>"));
            itemPanel.add(new JLabel(String.format("<html><font size='5'>%s: <i>%d</i></font></html>", entry.getKey(), entry.getValue())));
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

    public void actionPerformed(ActionEvent e) {
        System.out.println("yes");
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
