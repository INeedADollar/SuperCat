package org.pjc.displays;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.GraphicsEnvironment;
import java.awt.GraphicsDevice;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;


public abstract class Display extends JPanel {

	protected JFrame parent;
	protected Image backgroundImage;
	
	public Display(JFrame parent, String backgroundImagePath) {
		super();
		this.parent = parent;
		loadBackgroundImage(backgroundImagePath);
	}

	public void showDisplay() {
		parent.setContentPane(this);
    	parent.validate();
	}
	
	protected int[] getDisplaySize() {
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		return new int[]{gd.getDisplayMode().getWidth(), gd.getDisplayMode().getHeight()};
	}
	
	private void loadBackgroundImage(String backgroundImagePath) {
		if(backgroundImagePath == "")
			return;
		
		try {
			BufferedImage cat = ImageIO.read(new File(backgroundImagePath));
			
			int[] displaySize = getDisplaySize();
			this.backgroundImage = (Image)cat.getScaledInstance(displaySize[0], displaySize[1], Image.SCALE_SMOOTH);
			
		}
		catch(IOException e) {
			this.backgroundImage = null;
		}
	}
	
	@Override
    public void paintComponent( Graphics g ) {
		super.paintComponent(g);
		g.drawImage(backgroundImage, 0, 0, this);
	}
}
