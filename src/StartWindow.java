// package Project;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class StartWindow extends JFrame implements ActionListener {
	//Class Declarations
	JTextField firstTF, middleTF, lastTF, fullTF;
	JButton startButton, loadButton;
	boolean gameStart = false;
	
	//Constructor
	public StartWindow() {
		super("Lootbox Game");
		setSize(960, 540);
		setLocationRelativeTo(null);
		Container container = getContentPane();
		container.setLayout(new BorderLayout());
		
		JPanel titlePanel = new JPanel();
		JPanel startPanel = new JPanel();
		JPanel loadPanel = new JPanel();
		
		titlePanel.add(new JLabel("LOOTBOX GAME"));
		
		startButton = new JButton("Start Game");
		startButton.addActionListener(this);
		startPanel.add(startButton);
		
		loadButton = new JButton("Load from Save");
		loadButton.addActionListener(this);
		loadPanel.add(loadButton);
		
		container.add(titlePanel, BorderLayout.NORTH);
		container.add(startPanel, BorderLayout.CENTER);
		container.add(loadPanel, BorderLayout.SOUTH);
	}

		public void actionPerformed(ActionEvent e) {
			
			if (e.getSource() == startButton) {
				gameStart = true;
			}
			
			if (e.getSource() == loadButton) {
				System.out.println("load pressed");
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

	//Main Program that starts Execution
	public static void main(String[] args) {
		StartWindow test = new StartWindow();
		test.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}


