package views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import engine.Game;
import engine.Player;
import model.abilities.DamagingAbility;
import model.abilities.HealingAbility;
import model.world.Champion;

public class TESTing extends JFrame implements ActionListener {

	JButton component1, component2, component3, component4;
	int counter;
	int randIdx;

	public int generateRand() {
		return (int) (Math.random() * Game.getAvailableChampions().size());
	}

	public TESTing() throws IOException {

		Game.loadAbilities("Abilities.csv");
		Game.loadChampions("Champions.csv");

		randIdx = generateRand();

		component1 = new JButton(Game.getAvailableChampions().get(randIdx).getName());
		component1.addActionListener(this);
		component2 = new JButton();
		component2.addActionListener(this);
		component3 = new JButton();
		component3.addActionListener(this);
		component4 = new JButton();
		component4.addActionListener(this);

		JPanel panel1 = new JPanel();
		panel1.setLayout(new BorderLayout());
		panel1.setBounds(0, 0, 400, 100);
		panel1.add(component1);

		JPanel panel2 = new JPanel();
		panel2.setLayout(new GridLayout(1, 3));
		panel2.setBounds(0, 100, 400, 300);
		panel2.add(component2);
		panel2.add(component3);
		panel2.add(component4);

		this.setLayout(null);
		this.setBounds(500, 500, 400, 400);
		this.add(panel1);
		this.add(panel2);
		this.setVisible(true);

	}

	public static void main(String[] args) throws IOException {
		new TESTing();
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == component1) {
			counter++;
			counter %= 4;
			if (counter == 0) {
				randIdx = generateRand();
				component1.setText(Game.getAvailableChampions().get(randIdx).getName());
				component2.setText("");
				component3.setText("");
				component4.setText("");
			} else if (counter == 1) {
				component2.setText(Game.getAvailableChampions().get(randIdx).getAbilities().get(counter - 1).getName());
			} else if (counter == 2) {
				component3.setText(Game.getAvailableChampions().get(randIdx).getAbilities().get(counter - 1).getName());
			} else {
				component4.setText(Game.getAvailableChampions().get(randIdx).getAbilities().get(counter - 1).getName());
			}
		}

	}

}
