// package Project;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class StartWindow extends JFrame implements ActionListener {
	//Class Declarations
	JTextField firstTF, middleTF, lastTF, fullTF;
	JButton startButton, loadButton;
	
	//Constructor
	public StartWindow() {
		super("Lootbox Game");
		setSize(600, 300);
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
		
		setVisible(true);
	}

		public void actionPerformed(ActionEvent e) {
			
			if (e.getSource() == startButton) {
				System.out.println("start pressed");
			}
			
			if (e.getSource() == loadButton) {
				System.out.println("load pressed");
			}
	}
	//Main Program that starts Execution
	public static void main(String args[]) {
		StartWindow test = new StartWindow();
		test.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}


