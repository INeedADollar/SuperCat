package org.pjc;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


public class MainWindow extends JFrame {

	public MainWindow() {
		setupUI();
	}

	private void setupUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
	
		setExtendedState(JFrame.MAXIMIZED_BOTH); 
		setUndecorated(true);
		
		BufferedImage icon = loadIcon("assets/cat.png");
		setIconImage(icon);
		setTitle("SuperCat");
		
		Leaderboard l = new Leaderboard(this);
		l.showDisplay();
		
		//(new Login(this)).showDisplay();
		(new MainMenu(this)).showDisplay();
		//(new Game("", new BufferedImage(1, 1, 1), this)).showDisplay();
		
	}
	
	private BufferedImage loadIcon(String path) {
		try {
			BufferedImage cat = ImageIO.read(new File(path));
			return cat;
		}
		catch(IOException e) {
			return null;
		}
	}
}
