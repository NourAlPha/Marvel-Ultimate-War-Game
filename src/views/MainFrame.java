package views;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class MainFrame extends JFrame {

	public MainFrame() {
		
		ImageIcon image = new ImageIcon("icon.png");

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		this.setUndecorated(true);
		this.setIconImage(image.getImage());
		this.add(new CardLayoutPanel(this));
		this.setVisible(true);
		
		try {
			AudioInputStream audio = AudioSystem.getAudioInputStream(new File("theme.wav"));
			Clip clip = AudioSystem.getClip();
			clip.open(audio);
			clip.start();
			clip.loop(Clip.LOOP_CONTINUOUSLY);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedAudioFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

	public static void main(String[] args) {
		System.setProperty("sun.java2d.noddraw", Boolean.TRUE.toString());
		Read r = new Read();
		r.start();
	}
	
	public static class StartFrame extends Thread{
		private void starting() {
			new MainFrame();
		}
		
		public void run() {
			starting();
		}
	}
	
	public static class Read extends Thread{
		private void read() {
			new ReadImages();
		}
		
		public void run() {
			read();
		}
	}

}
