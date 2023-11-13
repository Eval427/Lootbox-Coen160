// package Project;

import java.io.IOException;
import java.util.Objects;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
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
    private static Clip backgroundMusic;

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
            rewardTrackers[i].setFont(new Font("Courier", Font.PLAIN, 15));
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
        upgrades[0] = new Upgrade("Upgrade?!", cost) {
            @Override
            public void upgradeAction() {
                chestSelection.setBackground(Color.darkGray);
                openLore.setText("Huh? What just happened?");
            }
        };

        // Upgrade 2: Dark Mode
        cost = new HashMap<>();
        cost.put("@", 100);
        upgrades[1] = new Upgrade("Dark Mode", cost) {
            @Override
            public void upgradeAction() {
            	openLore.setForeground(Color.white);
            	openLore.setBackground(Color.black);
            	openLore.setOpaque(true);
            	openLore.setText("Lookin' classy B-)");
            }
        };
        
        // Upgrade 3: Change Font
        cost = new HashMap<>();
        cost.put("@", 100);
        upgrades[2] = new Upgrade("Change Font", cost) {
            @Override
            public void upgradeAction() {
            	Font newFont = new Font("Helvetica", Font.BOLD, 14); // You can adjust the font name, style, and size
            	openLore.setFont(newFont);
            	openLore.setText("That's a neat font!");
            }
        };
        
        // Upgrade 4: Background color
        cost = new HashMap<>();
        cost.put("@", 100);
        upgrades[3] = new Upgrade("Change Background", cost) {
            @Override
            public void upgradeAction() {
            	rewardDisplay.setBackground(Color.lightGray);
            	openLore.setText("Looks like you'll need Weird Shards (%) for this next upgrade");
            }
        };
        
        // Upgrade 5: Upgrade Wooden Chest
        cost = new HashMap<>();
        cost.put("%", 2);
        upgrades[4] = new Upgrade("Wooden Upgrade", cost) {
            @Override
            public void upgradeAction() {
            	ImageIcon newIcon = new ImageIcon("./src/woodenchest.png");
            	chestButtons[0].setIcon(newIcon);
            	openLore.setText("So dramatic for a plain wooden chest...");
            }
        };
        
        // Upgrade 6: Upgrade Golden Chest
        cost = new HashMap<>();
        cost.put("%", 2);
        upgrades[5] = new Upgrade("Golden Upgrade", cost) {
            @Override
            public void upgradeAction() {
            	ImageIcon newIcon = new ImageIcon("./src/goldenchest.png");
            	chestButtons[1].setIcon(newIcon);
            	openLore.setText("Now that's a bit better!");
            }
        };
        
        // Upgrade 7: Upgrade Diamond Chest
        cost = new HashMap<>();
        cost.put("%", 2);
        upgrades[6] = new Upgrade("Diamond Upgrade", cost) {
            @Override
            public void upgradeAction() {
            	ImageIcon newIcon = new ImageIcon("./src/diamondchest.png");
            	chestButtons[2].setIcon(newIcon);
            	openLore.setText("Diamonds... Shiny!");
            }
        };
        
        // Upgrade 8: Upgrade Emerald Chest
        cost = new HashMap<>();
        cost.put("%", 2);
        upgrades[7] = new Upgrade("Emerald Upgrade", cost) {
            @Override
            public void upgradeAction() {
            	ImageIcon newIcon = new ImageIcon("./src/emeraldchest.png");
            	chestButtons[3].setIcon(newIcon);
            	openLore.setText("Follow the Yellow Brick Road!");
            }
        };
        
        // Upgrade 9: Upgrade Chaos Chest
        cost = new HashMap<>();
        cost.put("%", 2);
        upgrades[8] = new Upgrade("Chaos Upgrade", cost) {
            @Override
            public void upgradeAction() {
            	ImageIcon newIcon = new ImageIcon("./src/chaoschest.png");
            	chestButtons[4].setIcon(newIcon);
            	openLore.setText("Chaos! Chaos! Chaos!");
            }
        };
        
        // // Upgrade 10: Upgrade  Chest
        cost = new HashMap<>();
        cost.put("%", 2);
        upgrades[9] = new Upgrade("Chaos Upgrade", cost) {
            @Override
            public void upgradeAction() {
            	ImageIcon newIcon = new ImageIcon("./src/chaoschest.png");
            	chestButtons[4].setIcon(newIcon);
            	openLore.setText("Looks like you'll need Golden Shards ($)");
            }
        };
        
        // Upgrade 11: Change Music
        cost = new HashMap<>();
        cost.put("%", 2);
        cost.put("$", 2);
        upgrades[10] = new Upgrade("Change Music", cost) {
            @Override
            public void upgradeAction() {
            	changeBackgroundMusic("./src/dreamsbkmusic.wav");
            	openLore.setText("The atmosphere is changing...");
            }
        };
        
        // Downgrade 1: Shrink Chest Buttons (Not working 100% yet)
        cost = new HashMap<>();
        cost.put("%", 2);
        cost.put("$", 2);
        upgrades[11] = new Upgrade("Upgrade?", cost) {
            @Override
            public void upgradeAction() {
            	for(int i=0; i<5; i++) {
            		chestButtons[i].setPreferredSize(new Dimension(50, 25));
            		chestButtons[i].setSize(new Dimension(50, 25));
            	}
            	openLore.setText("That doesn't seem to be an upgrade...");
            	
            }
        };
        
        // Downgrade 2: Remove Diamond Chest
        cost = new HashMap<>();
        cost.put("%", 2);
        cost.put("$", 2);
        upgrades[12] = new Upgrade("Remove chest", cost) {
            @Override
            public void upgradeAction() {
            	chestButtons[2].setVisible(false);
            	openLore.setText("What happened to the Diamond Chest?!");
            }
        };
        
        // Downgrade 3: Change to Comic Sans
        cost = new HashMap<>();
        cost.put("%", 2);
        cost.put("$", 2);
        upgrades[13] = new Upgrade("Font Changer", cost) {
            @Override
            public void upgradeAction() {
            	Font newFont = new Font("Comic Sans MS", Font.PLAIN, 14);
            	for(int i=0; i<10; i++)
            		itemTrackers[i].setFont(newFont);
            	openLore.setText("Not so bad honestly...");
            }
        };
        
        // Downgrade 4: Neon Colors
        cost = new HashMap<>();
        cost.put("%", 2);
        cost.put("$", 2);
        upgrades[14] = new Upgrade("Neon Colors", cost) {
            @Override
            public void upgradeAction() {
            	Color neonGreen = new Color(100, 255, 0);
            	Color neonBlue = new Color(0, 255, 255);
            	Color neonPink = new Color(255, 0, 255);
            	chestSelection.setBackground(neonGreen);
            	openLore.setBackground(neonBlue);
            	rewardDisplay.setBackground(neonPink);
            	openLore.setText("These colors hurt my eyes.");
            }
        };

        // TODO: Some more downgrade ideas
        // Add troll face in place of Chest ASCII
        // Play iPhone alarm sound instead of music
        // Move some buttons around randomly
        // Crash the game.
        
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
            	if(upgradeIndex < 2) openLore.setForeground(Color.black);
            	else openLore.setForeground(Color.white);
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

        // Phrases for lore
        String[] phrases = {
        		"That's pretty good... I think!",
        		"Fortune favors the brave.",
        		"Lucky day to be you!",
        		"Glory awaits!",
        		"You're soon gonna be as rich as Bezos!",
        		"Swimming in cash (and shards)",
        		"A bounty of riches!",
        		"What secrets lie within?",
        		"Share your finds on Facebook!",
        		"Do you feel like Indiana Jones yet?",
        		"Treasure behold! ARRRR",
        		"Acquired the goods.",
        		"Soon to rival the wealth of empires!",
        		"It's like cookies, but coins...",
        };
        
        // Update lore with random phrase
        Random random = new Random();
        String phrase = phrases[random.nextInt(phrases.length)];
        openLore.setText(phrase);
    }
    
    /*
     * 	Start background music with file
     */
    static void startBackgroundMusic(String fileName) {
        try {
        	File audioFile = new File(fileName);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile);
            backgroundMusic = AudioSystem.getClip();
            backgroundMusic.open(audioInputStream);
            
            // Listener for playback
            backgroundMusic.addLineListener(new LineListener() {
		    @Override
		    public void update(LineEvent event) {
		    	if (event.getType() == LineEvent.Type.STOP) {
		    		// Loop when music stops running
		            if (!backgroundMusic.isRunning()) {
		            	backgroundMusic.setFramePosition(0);
		                backgroundMusic.start();
		            }
		    	}
		      }
            });
            
            backgroundMusic.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     *  Stops background music
     */
    private static void stopBackgroundMusic() {
        if (backgroundMusic != null && backgroundMusic.isRunning()) {
            backgroundMusic.stop();
            backgroundMusic.close();
        }
    }

    /*
     *  Changes background music (stop and start new)
     */
    private static void changeBackgroundMusic(String newFileName) {
        stopBackgroundMusic();
        startBackgroundMusic(newFileName);
    }

    public static void main(String[] args) {
        // 1. initialize chests
        // 2. create game window
        GameWindow game = new GameWindow();
        game.showWindow();
    }
}