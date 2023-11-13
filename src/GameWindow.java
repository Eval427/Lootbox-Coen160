// package Project;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Random;

import static java.lang.System.exit;

/**
 * Implements the GUI framework for the game menu
 */
public class GameWindow extends JFrame implements ActionListener {
    // Chest selection buttons
    private final JButton[] chestButtons;
    private final PlayerStats player;
    private final ChestManager chests;
    private final JLabel[] chestDisplay, rewardTrackers, itemTrackers;
    private final JPanel rewardDisplay;
    private final JLabel openLore;
    private String toOpen;
    private final JButton upgradeButton;
    private final Upgrade[] upgrades;
    private int upgradeIndex;
    private static Clip backgroundMusic;
    private final String[] endDialogue = {
            "Huh?",
            "The game...",
            "Where did all of those buttons go?",
            "I guess it's for the best. I think the upgrade system was broken anyways",
            "Cheers. You destroyed our hard work.",
            "Goodbye",
            "...",
            "for now..."
    };
    private int endLog = 0;
    private boolean endGame = false;

    // List of items on top

    public GameWindow(PlayerStats oldPlayer, boolean cheatMode) {
        super("Lootbox Simulator");
        // Initialize the player
        if (oldPlayer == null) {
            player = new PlayerStats();
        } else {
            player = oldPlayer;
        }

        // Initialize the chests. ALL CHESTS SHOULD BE CREATED HERE. Any new chests are automatically added to the game
        // Note that the order of insertion will determine the button layout
        chests = new ChestManager();

        // Wooden chest
        chests.makeChest("Wooden", 0, 3, 0, Color.black);
        // Golden chest
        chests.makeChest("Golden", 100, 3, 1, Color.orange);
        // Diamond chest
        chests.makeChest("Diamond", 250, 4, 3, Color.cyan);
        // Emerald chest
        chests.makeChest("Emerald", 600, 5, 4, Color.green);
        chests.addItemToRecent("Emerald Shard", 20, 1, "%", "~~ Emerald Shard ~~", Color.green);
        chests.addItemToRecent("Chaos Shard", 1, 1, "*", "*^\\ CHAOS /^*", Color.magenta);
        // Chaos chest
        chests.makeChest("Chaos", 1500, 7, 6, Color.red);
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
            chestButtons[i].putClientProperty("name", chestButtonText[i].split("<html>")[1].split("<br/>")[0]);
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
        if (oldPlayer == null) {
            player.addItems(chests.getAllItems());
        }

        itemTrackers = new JLabel[10];
        int itemIndex = 0;
        for (CustomItem item : chests.getAllItems()) {
            if (item != null) {
                itemTrackers[itemIndex] = new JLabel(String.format("<html><font size='4'>%s %s: %d</font></html>", item.getIcon(), item.getName(), player.amountOf(item)));
                itemPanel.add(new JLabel("<html><font size = '4'><b>|</b></font></html>"));
                itemPanel.add(itemTrackers[itemIndex++]);
            }
        }
        itemPanel.add(new JLabel("<html><font size = '4'><b>|</b></font></html>"));

        //Save button
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(this);
        itemPanel.add(saveButton);

        container.add(itemPanel, BorderLayout.NORTH);

        // Initialize itemTrackers from save
        if (oldPlayer != null) {
            for (CustomItem item : chests.getAllItems()) {
                if (item != null) updateItemDisplay(item);
            }
        }

        // Add items from cheatMode
        if (cheatMode) {
            for (CustomItem item : chests.getAllItems()) {
                if (item != null && item.getName().equals("Coins")) player.updateAmount(item.getName(), 100000);
                if (item != null && !item.getName().equals("Coins")) player.updateAmount(item.getName(), 500);
                if (item != null) updateItemDisplay(item);
            }
        }

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
        JPanel bottomPanel = new JPanel(new GridLayout(2, 1, 0, 0));
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
        cost.put("%", 1);
        upgrades[4] = new Upgrade("Wooden Upgrade", cost) {
            @Override
            public void upgradeAction() {
            	ImageIcon newIcon = new ImageIcon("./src/woodenchest.png");
            	chestButtons[0].setIcon(newIcon);
                chestButtons[0].setText("");
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
                chestButtons[1].setText("");
            	openLore.setText("Now that's a bit better!");
            }
        };
        
        // Upgrade 7: Upgrade Diamond Chest
        cost = new HashMap<>();
        cost.put("%", 3);
        upgrades[6] = new Upgrade("Diamond Upgrade", cost) {
            @Override
            public void upgradeAction() {
            	ImageIcon newIcon = new ImageIcon("./src/diamondchest.png");
            	chestButtons[2].setIcon(newIcon);
                chestButtons[2].setText("");
            	openLore.setText("Diamonds... Shiny!");
            }
        };
        
        // Upgrade 8: Upgrade Emerald Chest
        cost = new HashMap<>();
        cost.put("%", 4);
        upgrades[7] = new Upgrade("Emerald Upgrade", cost) {
            @Override
            public void upgradeAction() {
            	ImageIcon newIcon = new ImageIcon("./src/emeraldchest.png");
            	chestButtons[3].setIcon(newIcon);
                chestButtons[3].setText("");
            	openLore.setText("Follow the Yellow Brick Road!");
            }
        };
        
        // Upgrade 9: Upgrade Chaos Chest
        cost = new HashMap<>();
        cost.put("%", 5);
        upgrades[8] = new Upgrade("Chaos Upgrade", cost) {
            @Override
            public void upgradeAction() {
            	ImageIcon newIcon = new ImageIcon("./src/chaoschest.png");
            	chestButtons[4].setIcon(newIcon);
                chestButtons[4].setText("");
            	openLore.setText("Chaos! Chaos! Chaos!");
            }
        };
        
        // // Upgrade 10: Upgrade  Chest
        cost = new HashMap<>();
        cost.put("%", 8);
        upgrades[9] = new Upgrade("UI Upgrade", cost) {
            @Override
            public void upgradeAction() {
            	chestPanel.setBackground(Color.black);
            	openLore.setText("Looks like you'll need Golden Shards ($)");
            }
        };
        
        // Upgrade 11: Change Music
        cost = new HashMap<>();
        cost.put("%", 5);
        cost.put("$", 2);
        upgrades[10] = new Upgrade("Change Music", cost) {
            @Override
            public void upgradeAction() {
            	changeBackgroundMusic("./src/dreamsbkmusic.wav");
            	openLore.setText("The atmosphere is changing...");
            }
        };
        
        // Downgrade 1: Remove image for Wooden Chest
        cost = new HashMap<>();
        cost.put("%", 6);
        cost.put("$", 4);
        upgrades[11] = new Upgrade("Upgrade?", cost) {
            @Override
            public void upgradeAction() {
            	chestButtons[0].setIcon(null);
            	openLore.setText("That doesn't seem to be an upgrade...");
            	
            }
        };
        
        // Downgrade 2: Remove Diamond Chest
        cost = new HashMap<>();
        cost.put("%", 7);
        cost.put("$", 6);
        upgrades[12] = new Upgrade("Remove chest", cost) {
            @Override
            public void upgradeAction() {
            	chestButtons[2].setVisible(false);
            	openLore.setText("What happened to the Diamond Chest?!");
            }
        };
        
        // Downgrade 3: Change to Comic Sans
        cost = new HashMap<>();
        cost.put("%", 10);
        cost.put("$", 10);
        upgrades[13] = new Upgrade("Font Change", cost) {
            @Override
            public void upgradeAction() {
            	Font newFont = new Font("Comic Sans MS", Font.PLAIN, 14);
            	for(int i=0; i<10; i++)
            		if(itemTrackers[i] != null)
            			itemTrackers[i].setFont(newFont);
            	openLore.setText("Not so bad honestly...");
            }
        };
        
        // Downgrade 4: Neon Colors
        cost = new HashMap<>();
        cost.put("%", 10);
        cost.put("*", 1);
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
        
        // Downgrade 5: Replace Chest Image
        cost = new HashMap<>();
        cost.put("%", 12);
        cost.put("$", 10);
        upgrades[15] = new Upgrade("Chest Image", cost) {
            @Override
            public void upgradeAction() {
            	ImageIcon newIcon = new ImageIcon("./src/404.jpg");
            	for (JLabel label : chestDisplay)
            		label.setIcon(newIcon);
            	openLore.setText("What's happening?");
            }
        };
        
        // Downgrade 6: Change Reward Font
        cost = new HashMap<>();
        cost.put("%", 13);
        cost.put("$", 12);
        upgrades[16] = new Upgrade("Reward Font", cost) {
            @Override
            public void upgradeAction() {
            	for(JLabel label : rewardTrackers)
            		label.setFont(new Font("Jokerman", Font.BOLD, 20));
            	openLore.setText("The Joker has arrived.");
            }
        };
        
        // Downgrade 7: Delete Save Button
        cost = new HashMap<>();
        cost.put("$", 10);
        cost.put("*", 2);
        upgrades[17] = new Upgrade("Point of No Return", cost) {
            @Override
            public void upgradeAction() {
            	saveButton.setVisible(false);
            	openLore.setText("At death's door...");
            }
        };
        
        // Downgrade 8: Delete Chest Buttons
        cost = new HashMap<>();
        cost.put("?", 5);
        upgrades[18] = new Upgrade("Chests no more", cost) {
            @Override
            public void upgradeAction() {
            	chestButtons[0].setVisible(false);
            	chestButtons[1].setVisible(false);
            	chestButtons[3].setVisible(false);
            	chestButtons[4].setVisible(false);
            	openButton.setText("No more chests.");
            	openLore.setText("You have reached Valhalla. You... win?");
            }
        };
        
        // Endgame
        cost = new HashMap<>();
        upgrades[19] = new Upgrade("Nothing left.", cost) {
            @Override
            public void upgradeAction() {
                container.remove(chestSelection);
                container.remove(itemPanel);
                container.remove(chestSelection);
                openButton.setText("Continue");
                openLore.setText(endDialogue[endLog++]);
                openLore.setForeground(Color.black);
                endGame = true;
                stopBackgroundMusic();
            }
        };

        // Do upgrades from save data
        if (player.upgradeNumber() > 0) {
            for (int i=0; i < player.upgradeNumber(); i++) {
                upgrades[i].upgradeAction();
            }
            upgradeIndex = player.upgradeNumber();
        }
        
        upgradeButton.setText(upgrades[upgradeIndex].getUpgradeString());
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
            toUpdate.setText(String.format("<html><font size='4'>%s %s: <i>%d</i></font></html>", item.getIcon(), item.getName(), player.amountOf(item)));
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
        // Game end events
        if (e.getActionCommand().equals("Continue") && endGame) {
            if (endLog == endDialogue.length) exit(420);
            openLore.setText(endDialogue[endLog++]);
            return;
        }

        // On save
        if (e.getActionCommand().equals("Save")) {
            try {
                FileOutputStream fileOut = new FileOutputStream("./src/savedata.txt");
                ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);

                objectOut.writeObject(player);
                openLore.setText("Game progress saved!");
                openLore.setForeground(Color.green);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return;
        }

        // Clear reward text
        for (JLabel label : rewardTrackers) {
            label.setText("");
        }

        // Close chest
        String[] chestLabelStrings = chests.closedDisplayString();
        for (int i = 0; i < chestLabelStrings.length; i++) {
            chestDisplay[i].setText(chestLabelStrings[i]);
        }

        if (!e.getActionCommand().equals("Open") && !endGame) {
            // Upgrade
            if (commandIsUpgrade(e.getActionCommand())) { // Ensure button press is upgrade
                if (canAffordUpgrade(upgrades[upgradeIndex])) { // Ensure user can afford upgrade
                    for (String i : upgrades[upgradeIndex].getCost().keySet()) { // Decrement player items
                        player.updateAmount(chests.getItemByIcon(i).getName(), upgrades[upgradeIndex].getCost().get(i)*-1);
                        updateItemDisplay(chests.getItemByIcon(i));
                        player.incrementUpgradeCounter();
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
            JButton source = (JButton) e.getSource();
            toOpen = (String) source.getClientProperty("name");
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

        if (chests.getCost(toOpen) > player.amountOf("Coins")) {
            openLore.setText(String.format("You can't afford a %s Chest!", toOpen));
            openLore.setForeground(Color.red);
            return;
        }

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
        GameWindow game = new GameWindow(null, false);
        game.showWindow();
    }
}