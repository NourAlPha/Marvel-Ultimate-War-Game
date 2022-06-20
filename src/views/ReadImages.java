package views;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;

import views.MainFrame.StartFrame;

public class ReadImages {

	public static HashMap<String, Image> hm = new HashMap<>();

	String[] images = { "Cover", "galaxy22", "galaxy3", "wewonmrstark", "wintheme", "Deadpool", "Dr Strange", "Electro",
			"Hela", "Hulk", "Iceman", "Ironman", "Spiderman", "Thor", "Venom", "Yellow Jacket", "shield", "shield2",
			"Quicksilver", "Ghost Rider", "Loki", "Captain America" };

	int width = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
	int height = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();

	public ReadImages() {

		int cnt = 0;

		for (String image : images) {
			try {
				if (cnt < 5) {
					BufferedImage img = ImageIO.read(new File(image + ".jpg"));
					img = ImagePanel.resize(img, width, height);
					img = ImagePanel.toCompatibleImage(img);
					hm.put(image, img);
				} else {
					BufferedImage img = ImageIO.read(new File(image + ".png"));
					img = ImagePanel.resize(img, 90, 140);
					img = ImagePanel.toCompatibleImage(img);
					hm.put(image, img);
				}
			} catch (IOException e) {
				e.getStackTrace();
			}
			cnt++;
			if (cnt == 1) {
				StartFrame s = new StartFrame();
				s.start();
			}
		}

	}

}
