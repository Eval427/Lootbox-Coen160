// package Project;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class StartWindow extends JFrame implements ActionListener {
	//Class Declarations
	JButton startButton, loadButton;
	boolean gameStart = false;
	PlayerStats player = null;
	
	//Constructor
	public StartWindow() {
		super("Lootbox Game");
		setSize(960, 540);
		setLocationRelativeTo(null);
		
		try {
            BufferedImage backgroundImage = ImageIO.read(new File("./src/startwindow2.png"));
            setContentPane(new ImagePanel(backgroundImage));
        } catch (IOException e) {
            e.printStackTrace();
        }
		
        JLayeredPane layeredPane = getLayeredPane();
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BorderLayout());

        startButton = new JButton("Start Game");
        startButton.addActionListener(this);
        loadButton = new JButton("Load from Save");
        loadButton.addActionListener(this);

        buttonPanel.add(startButton, BorderLayout.NORTH);
        // buttonPanel.add(new JPanel(), BorderLayout.CENTER);
        buttonPanel.add(loadButton, BorderLayout.SOUTH);

        layeredPane.add(buttonPanel, 2);

        // Center the button panel in the middle of the screen
        Dimension panelSize = buttonPanel.getPreferredSize();
        int x = (getWidth() - panelSize.width) / 2;
        int y = (getHeight() - panelSize.height) / 2;
        buttonPanel.setBounds(x, y, panelSize.width, panelSize.height);
	}

	public void actionPerformed(ActionEvent e) {
		
		if (e.getSource() == startButton) {
			gameStart = true;
		}
		
		if (e.getSource() == loadButton) {
			try {
				FileInputStream fileIn = new FileInputStream("./src/savedata.txt");
				ObjectInputStream objectIn = new ObjectInputStream(fileIn);

				player = (PlayerStats) objectIn.readObject();
				gameStart = true;
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * Shows the start window
	 */
	public void showWindow() {
		setVisible(true);
	}

	/**
	 * Hides the start window
	 */
	public void hideWindow() {
		setVisible(false);
	}

	/**
	 * Bool to detect when transition to game screen should take place
	 * @return if "Start" or "Load" is pressed
	 */
	public boolean isGameStart() {
		return gameStart;
	}

	public PlayerStats getPlayer() {
		return this.player;
	}

	//Main Program that starts Execution
	public static void main(String[] args) {
		StartWindow test = new StartWindow();
		test.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	 // ImagePanel class to display the background image
    class ImagePanel extends JPanel {
        private BufferedImage image;

        public ImagePanel(BufferedImage image) {
            this.image = image;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (image != null) {
                g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }
}


