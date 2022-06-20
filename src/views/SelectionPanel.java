package views;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;

import engine.Game;
import engine.Player;
import model.world.Champion;

public class SelectionPanel extends JPanel implements ActionListener, MouseListener {

	public static int noOfFrames;
	ArrayList<JButton> buttons;
	JButton confirm, reset, exit;
	JTextField textField;
	HashMap<JButton, Champion> buttonToChampion;
	Player player1;
	ArrayList<Champion> chosenChampions;
	CardLayoutPanel clp;
	Cursor handCursor, defaultCursor;
	public static int easteregg = -1;
	
	public SelectionPanel(CardLayoutPanel clp) {

		defaultCursor = this.getCursor();
		handCursor = new Cursor(Cursor.HAND_CURSOR);
		
		int width = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		int height = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
		
		this.clp = clp;
		
		buttonToChampion = new HashMap<>();
		chosenChampions = new ArrayList<>();
		noOfFrames++;

		buttons = new ArrayList<>();

		BufferedImage img = (BufferedImage) ReadImages.hm.get("galaxy22");
		
		ImagePanel ip = new ImagePanel(img);
		ip.setBounds(0, 0, width, height);
		
		String name = "Please Enter the Player " + noOfFrames + " name : ";

		JLabel label = new JLabel();
		label.setText(name);
		label.setForeground(Color.white);
		label.setFont(new Font("Impact", Font.BOLD, 20));

		textField = new JTextField();
		textField.addActionListener(this);
		textField.setPreferredSize(new Dimension(350, 50));
		textField.setFont(new Font("MV Boli", Font.PLAIN, 20));
		textField.setOpaque(false);
		textField.setForeground(Color.white);
		

		JPanel textFieldPanel = new JPanel();
		textFieldPanel.setBackground(Color.gray);
		textFieldPanel.add(label);
		textFieldPanel.add(textField);
		textFieldPanel.setBounds(0, 0, width, 60);
		textFieldPanel.setOpaque(false);

		JLabel info1 = new JLabel();
		info1.setText("<html>Please select your team.<br><html>");
		info1.setForeground(Color.white);
		info1.setFont(new Font("Impact", Font.ITALIC, 25));

		JLabel info2 = new JLabel();
		info2.setText(
				"<html>Your team consists of 3 champions in which " + "the first champion selected will be your leader "
						+ "who has a special ability to be used once.</html>");
		info2.setForeground(Color.white);
		info2.setFont(new Font("Impact", Font.ITALIC, 25));

		JPanel infoPanel = new JPanel();
		infoPanel.setBackground(Color.gray);
		infoPanel.add(info1);
		infoPanel.add(info2);
		infoPanel.setBounds(0, 60, width, 100);
		infoPanel.setOpaque(false);

		JPanel championsList = new JPanel();
		championsList.setBackground(Color.gray);
		championsList.setLayout(new GridLayout(4, 1));
		championsList.setBounds(0, 160, width, height - 275);
		for (Champion c : Game.getAvailableChampions()) {
			JButton button = new JButton();
			button.addActionListener(this);
			button.addMouseListener(this);
			button.setBorder(null);
			button.setFont(new Font("spark pro", Font.PLAIN, 15));
			button.setBorderPainted(false);
			button.setContentAreaFilled(false);
			button.setFocusPainted(false);
			button.setOpaque(false);
			button.setFocusable(false);
			BufferedImage img1 = (BufferedImage) ReadImages.hm.get(c.getName());
			ImageIcon icon = new ImageIcon(img1);
			button.setIcon(icon);
			button.setForeground(Color.white);
			button.setBackground(Color.black);
			String s = "<html>Champion name : " + c.getName() + "<br>";
			s += "MaxHP : " + c.getMaxHP() + "<br>";
			s += "Mana : " + c.getMana() + "<br>";
			s += "MaxActions : " + c.getMaxActionPointsPerTurn() + "<br>";
			s += "Speed : " + c.getSpeed() + "<br>";
			s += "Attack Range : " + c.getAttackRange() + "<br>";
			s += "Attack Damage : " + c.getAttackDamage() + "<br>";
			s += "</html>";
			button.setText(s);
			buttonToChampion.put(button, c);
			buttons.add(button);
			championsList.add(button);
		}
		championsList.setOpaque(false);

		confirm = new JButton("Confirm");
		confirm.setFont(new Font("stencil", Font.BOLD, 30));
		confirm.addActionListener(this);
		confirm.setFocusable(false);
		confirm.setForeground(Color.white);
		confirm.setBackground(Color.black);
		confirm.setPreferredSize(new Dimension(200, 100));
		confirm.setBorder(null);
		confirm.setBorderPainted(false);
		confirm.setContentAreaFilled(false);
		confirm.setFocusPainted(false);
		confirm.setOpaque(false);
		confirm.setFocusable(false);
		confirm.addMouseListener(this);

		reset = new JButton("Reset");
		reset.setFont(new Font("stencil", Font.BOLD, 30));
		reset.addActionListener(this);
		reset.setFocusable(false);
		reset.setForeground(Color.white);
		reset.setBackground(Color.black);
		reset.setPreferredSize(new Dimension(200, 100));
		reset.setBorder(null);
		reset.setBorderPainted(false);
		reset.setContentAreaFilled(false);
		reset.setFocusPainted(false);
		reset.setOpaque(false);
		reset.setFocusable(false);
		reset.addMouseListener(this);

		exit = new JButton("Exit");
		exit.setFont(new Font("stencil", Font.BOLD, 30));
		exit.addActionListener(this);
		exit.setFocusable(false);
		exit.setForeground(Color.white);
		exit.setBackground(Color.black);
		exit.setPreferredSize(new Dimension(200, 100));
		exit.setBorder(null);
		exit.setBorderPainted(false);
		exit.setContentAreaFilled(false);
		exit.setFocusPainted(false);
		exit.setOpaque(false);
		exit.setFocusable(false);
		exit.addMouseListener(this);
		

		JPanel confirmReset = new JPanel();
		confirmReset.setBackground(Color.gray);
		confirmReset.add(exit);
		confirmReset.add(reset);
		confirmReset.add(confirm);
		confirmReset.setBounds(0, height - 115, width, 115);
		confirmReset.setOpaque(false);
		

		ImageIcon image = new ImageIcon("icon.png");
		
		this.setLayout(null);
		this.add(textFieldPanel);
		this.add(infoPanel);
		this.add(championsList);
		this.add(confirmReset);
		this.add(ip);
		this.setVisible(true);

		textFieldPanel.setFocusable(true);
		textFieldPanel.requestFocus();
		textField.setFocusable(true);
		textField.requestFocus();
	}

	public SelectionPanel(CardLayoutPanel clp, Player player1) {
		this(clp);
		this.player1 = player1;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == exit) {
			clp.frame.dispose();
		} else if (e.getSource() == reset) {
			for (Champion c : chosenChampions)
				Game.getAvailableChampions().add(c);
			chosenChampions.clear();
			for (JButton button : buttons)
				button.setEnabled(true);
			textField.setText("");
		} else if (e.getSource() == confirm || e.getSource() == textField) {
			if (chosenChampions.size() < 3) {
				// TODO
				return;
			}
			if (textField.getText().equals("")) {
				// TODO
				return;
			}
			Player player = new Player(textField.getText());
			for (Champion c : chosenChampions)
				player.getTeam().add(c);
			player.setLeader(chosenChampions.get(0));

			if (noOfFrames == 2) {
				boolean ironMan = false, spiderMan = false;
				for(Champion c : player1.getTeam()) {
					if(c.getName().equals("Ironman"))ironMan = true;
					else if(c.getName().equals("Spiderman"))spiderMan = true;
				}
				if(ironMan && spiderMan)easteregg = 0;
				ironMan = spiderMan = false;
				for(Champion c : player.getTeam()) {
					if(c.getName().equals("Ironman"))ironMan = true;
					else if(c.getName().equals("Spiderman"))spiderMan = true;
				}
				if(ironMan && spiderMan)easteregg = 1;
				clp.game = new Game(player1, player);
				clp.add(new GameplayPanel(clp, clp.game), "4");
				clp.cl.show(clp, "4");
				clp.remove(0);
				clp.frame.revalidate();
				
			} else {
				clp.add(new SelectionPanel(clp, player), "3");
				clp.cl.show(clp, "3");
				clp.remove(0);
			}

		} else {
			for (JButton button : buttons) {
				if (e.getSource() == button) {
					chosenChampions.add(buttonToChampion.get(button));
					Game.getAvailableChampions().remove(buttonToChampion.get(button));
					button.setEnabled(false);
					break;
				}
			}
			if (chosenChampions.size() == 3)
				for (JButton button : buttons)
					button.setEnabled(false);
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if(chosenChampions.size() == 3)
			this.setCursor(defaultCursor);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		if(e.getSource() instanceof JButton && ((JButton) e.getSource()).isEnabled())
			this.setCursor(handCursor);
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		this.setCursor(defaultCursor);
	}
	
}
