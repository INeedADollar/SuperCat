package org.pjc.displays;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import org.pjc.widgets.CatButton;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Timer;
import java.util.TimerTask;
import java.awt.GridBagConstraints;

public class MainMenu extends Display {

	public MainMenu(JFrame parent) {
		super(parent, "assets/backgrounds/space_background.png");
		createUI();
	}
	
	private void createUI() {
		setLayout(new GridBagLayout());
		setBorder(BorderFactory.createEmptyBorder(40, 100, 50, 100));
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(20, 0, 100, 0);
		
		JLabel title = new JLabel("SuperCat");
		title.setAlignmentY(TOP_ALIGNMENT);
		title.setOpaque(false);
		title.setForeground(new Color(163, 38, 61));
		title.setFont(new Font("SansSerif", Font.ITALIC | Font.BOLD, 100));
		add(title, gbc);
		
		CatButton playButton = new CatButton("Play");
		playButton.setAlignmentY(CENTER_ALIGNMENT);
		playButton.setBorderSize(3);
		playButton.setBorderColor(new Color(163, 38, 61));
		playButton.setBorderColorOnHover(new Color(163, 38, 61));
		playButton.setTextColor(new Color(163, 38, 61));
		playButton.setTextColorOnHover(new Color(163, 38, 61));
		playButton.setFont(new Font("Arial", Font.PLAIN, 30));
		playButton.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent e) { 
            	TimerTask task = new TimerTask() {
    				@Override
    				public void run() {
    					try {
    						FileInputStream fis = new FileInputStream("sounds/cat.mp3");
    						AdvancedPlayer player = new AdvancedPlayer(fis);
    						player.play();
    						
    					} catch (FileNotFoundException | JavaLayerException e1) {
    						System.out.println(e1);
    						e1.printStackTrace();
    					}
    				}
    			};
            	
    			(new Timer()).schedule(task, 0);
    			
            	Login loginDisplay = new Login(parent);
            	loginDisplay.showDisplay();
            }
        });

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.insets = new Insets(15, 0, 15, 0);
		add(playButton, gbc);

		CatButton leaderboardButton = new CatButton("Leaderboard");
		leaderboardButton.setAlignmentY(CENTER_ALIGNMENT);
		leaderboardButton.setBorderSize(3);
		leaderboardButton.setBorderColor(new Color(163, 38, 61));
		leaderboardButton.setBorderColorOnHover(new Color(163, 38, 61));
		leaderboardButton.setTextColor(new Color(163, 38, 61));
		leaderboardButton.setTextColorOnHover(new Color(163, 38, 61));
		leaderboardButton.setFont(new Font("Arial", Font.PLAIN, 30));
		leaderboardButton.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	TimerTask task = new TimerTask() {
    				@Override
    				public void run() {
    					try {
    						FileInputStream fis = new FileInputStream("sounds/cat.mp3");
    						AdvancedPlayer player = new AdvancedPlayer(fis);
    						player.play();
    						
    					} catch (FileNotFoundException | JavaLayerException e1) {
    						System.out.println(e1);
    						e1.printStackTrace();
    					}
    				}
    			};
            	
    			(new Timer()).schedule(task, 0);
    			
            	Leaderboard leaderboardDisplay = new Leaderboard(parent);
            	leaderboardDisplay.showDisplay();
            }
        });
		gbc.gridx = 0;
		gbc.gridy = 2;
		add(leaderboardButton, gbc);
		
		CatButton exitButton = new CatButton("Exit");
		exitButton.setAlignmentY(CENTER_ALIGNMENT);
		exitButton.setBorderSize(3);
		exitButton.setBorderColor(new Color(163, 38, 61));
		exitButton.setBorderColorOnHover(new Color(163, 38, 61));
		exitButton.setTextColor(new Color(163, 38, 61));
		exitButton.setTextColorOnHover(new Color(163, 38, 61));
		exitButton.setFont(new Font("Arial", Font.PLAIN, 30));
		exitButton.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	TimerTask task = new TimerTask() {
    				@Override
    				public void run() {
    					try {
    						FileInputStream fis = new FileInputStream("sounds/cat.mp3");
    						AdvancedPlayer player = new AdvancedPlayer(fis);
    						player.play();
    						
    					} catch (FileNotFoundException | JavaLayerException e1) {
    						System.out.println(e1);
    						e1.printStackTrace();
    					}
    				}
    			};
            	
    			(new Timer()).schedule(task, 0);
    			
            	parent.setVisible(false);
            	parent.dispose();
				System.exit(0);
            }
        });
		gbc.gridx = 0;
		gbc.gridy = 3;
		add(exitButton, gbc);
	}
}
