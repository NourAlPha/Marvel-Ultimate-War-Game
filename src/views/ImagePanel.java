package views;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class ImagePanel extends JPanel {
		private Image img;
		static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		static int height = (int) screenSize.getHeight();
		static int width = (int) screenSize.getWidth();

		public ImagePanel(BufferedImage img) {
			img = resize(img, width, height);
			img = toCompatibleImage(img);
			this.img = img;
		}
		
		public static BufferedImage resize(BufferedImage img, int newW, int newH) { 
		    Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
		    BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

		    Graphics2D g2d = dimg.createGraphics();
		    g2d.drawImage(tmp, 0, 0, null);
		    g2d.dispose();

		    return dimg;
		}
		
		public static BufferedImage toCompatibleImage(BufferedImage image)
		{
		    // obtain the current system graphical settings
		    GraphicsConfiguration gfxConfig = GraphicsEnvironment.
		        getLocalGraphicsEnvironment().getDefaultScreenDevice().
		        getDefaultConfiguration();

		    /*
		     * if image is already compatible and optimized for current system 
		     * settings, simply return it
		     */
		    if (image.getColorModel().equals(gfxConfig.getColorModel()))
		        return image;

		    
		    
		    // image is not optimized, so create a new image that is
		    BufferedImage newImage = gfxConfig.createCompatibleImage(
		            image.getWidth(), image.getHeight(), image.getTransparency());

		    // get the graphics context of the new image to draw the old image on
		    Graphics2D g2d = newImage.createGraphics();

		    // actually draw the image and dispose of context no longer needed
		    g2d.drawImage(image, 0, 0, null);
		    g2d.dispose();

		    // return the new optimized image
		    return newImage; 
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.drawImage(img, 0, 0, this);
		}
	}