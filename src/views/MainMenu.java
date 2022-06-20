package views;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
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

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import engine.Game;

public class MainMenu extends JPanel implements ActionListener, MouseListener {

	JButton start;
	JPanel backGround;
	JProgressBar bar;
	CardLayoutPanel clp;
	Cursor handCursor, defaultCursor;
	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	int height = (int) screenSize.getHeight();
	int width = (int) screenSize.getWidth();

	public MainMenu(CardLayoutPanel clp) {

		this.clp = clp;
		defaultCursor = this.getCursor();
		handCursor = new Cursor(Cursor.HAND_CURSOR);
		
		start = new JButton("Start Game");
		start.setFont(new Font("stencil", Font.BOLD, 100));
		start.setFocusable(false);
		start.setForeground(Color.white);
		start.setBackground(Color.black);
		start.setOpaque(false);
		start.setBorder(null);
		start.setBorderPainted(false);
		start.setContentAreaFilled(false);
		start.setFocusPainted(false);
		start.setBounds(width / 2 - 350, height / 2 - 150, 700, 300);
		start.addActionListener(this);
		start.addMouseListener(this);

		BufferedImage img = (BufferedImage) ReadImages.hm.get("Cover");
		
		ImagePanel bip = new ImagePanel(img);
		bip.setLayout(null);
		bip.add(start);

		bar = new JProgressBar();
		bar.setValue(0);
		bar.setBounds(width / 2 - 515, height / 2 + 200, width * 2 / 3, 50);
		bar.setStringPainted(true);
		bar.setVisible(false);
		bip.add(bar);

		this.setBackground(Color.orange);
		this.setLayout(new GridLayout());
		this.setOpaque(false);
		this.add(bip);
		this.setVisible(true);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == start) {
			try {
				start.setEnabled(false);
				Game.loadAbilities("Abilities.csv");
				Game.loadChampions("Champions.csv");
				process();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

	}
	
	private void process() {
		
		bar.setVisible(true);
		
		Thread t = new Thread(new Runnable() {
			public void run() {
				int nCounter = 0;
				// for (int i = 0; i <= MAX; i++) {
				while (nCounter <= 100) {
					// final int currentValue = i;
					final int currentValue = nCounter;

					try {
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								bar.setValue(currentValue);
							}
						});
						java.lang.Thread.sleep(500);
					} catch (InterruptedException e) {
						System.out.println("Error: " + e.getMessage());
					}
					nCounter += Math.random() * 20 + 1;
				}
				clp.add(new SelectionPanel(clp), "2");
				clp.cl.show(clp, "2");
			}
		});
		t.start();

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if(e.getSource() == start)this.setCursor(defaultCursor);
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
		if(e.getSource() == start && start.isEnabled()) {
			this.setCursor(handCursor);
		}

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		this.setCursor(defaultCursor);
	}

}