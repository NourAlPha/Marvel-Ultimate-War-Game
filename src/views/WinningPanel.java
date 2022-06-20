package views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import javax.swing.JLabel;
import javax.swing.JPanel;

import engine.Player;
import model.world.Champion;

public class WinningPanel extends JPanel {
	
	int width = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
	int height = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
	
	public WinningPanel(Player player, int whichPlayer) {
		
		boolean foundIronMan = false;
		for(Champion c : player.getTeam())
			if(c.getName().equals("Ironman"))
				foundIronMan = true;
		
		boolean foundSpiderMan = false;
		for(Champion c : player.getTeam())
			if(c.getName().equals("Spiderman"))
				foundSpiderMan = true;
		
		BufferedImage img = (BufferedImage) ReadImages.hm.get("wintheme");
		JLabel label = new JLabel("<html>Congratulations <br>" + player.getName() + "<br> You Won!!</html>");

		if(whichPlayer == SelectionPanel.easteregg && !foundIronMan && foundSpiderMan) {
			label.setText("");
			img = (BufferedImage) ReadImages.hm.get("wewonmrstark");
		}
		
		label.setBounds(500, 150, width, 500);
		label.setFont(new Font("stencil", Font.BOLD, 60));
		label.setForeground(Color.white);

		ImagePanel ip = new ImagePanel(img);
		ip.setLayout(null);
		ip.add(label);
		
		
		this.setLayout(new BorderLayout());
		this.setOpaque(false);
		this.add(ip);
		this.setVisible(true);
		
	}
	
}
