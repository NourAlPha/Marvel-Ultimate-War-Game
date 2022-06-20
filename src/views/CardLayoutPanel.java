package views;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

import engine.Game;

public class CardLayoutPanel extends JPanel {

	JFrame frame;
	CardLayout cl;
	Game game;
	
	public CardLayoutPanel(JFrame frame) {
		this.frame = frame;
		cl = new CardLayout();
		this.setLayout(cl);
		this.add(new MainMenu(this), "1");
		cl.show(this, "1");
	}
	
}
