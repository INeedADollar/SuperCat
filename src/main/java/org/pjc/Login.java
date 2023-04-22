package org.pjc;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.awt.BorderLayout;
import java.awt.event.KeyListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;

public class Login extends Display {

	public Login(JFrame parent) {
		super(parent, "assets/background.png");
		createUI();
	}
	
	private void createUI() {
		setLayout(new GridLayout());
		
		JPanel firstHalf = new JPanel();
		firstHalf.setLayout(new BoxLayout(firstHalf, BoxLayout.Y_AXIS));
		firstHalf.setOpaque(false);;
		
		JPanel catPreview = new JPanel();
		catPreview.setLayout(new BorderLayout());
		catPreview.setOpaque(false);;
		ImageIcon catImage = loadCatModel("assets/cats/cat.png");
		if(catImage == null) 
			throw new RuntimeException("Cat model could not be loaded.");
		
		JLabel catLabel = new JLabel(catImage);
		catLabel.setOpaque(false);;
		catPreview.add(catLabel, BorderLayout.CENTER);
		firstHalf.add(catPreview);
		
		JPanel catCustomization = new JPanel();
		catCustomization.setLayout(new BorderLayout(10, 10));
		catCustomization.setOpaque(false);;
		catCustomization.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		catCustomization.add(new CustomizationMenu(), BorderLayout.CENTER);
		firstHalf.add(catCustomization);
		
		add(firstHalf);
		
		JPanel login = createLoginMenu();
		login.setBorder(BorderFactory.createEmptyBorder(30, 30, 10, 30));
		login.setOpaque(false);;
		
		add(login);
		setBackground(Color.black);
	}
	
	private ImageIcon loadCatModel(String path) {
		try {
			BufferedImage cat = ImageIO.read(new File(path));
			return new ImageIcon(cat.getScaledInstance(200, 200, BufferedImage.SCALE_SMOOTH));
		}
		catch(IOException e) {
			return null;
		}
	}
	
	private JPanel createLoginMenu() {
		JPanel loginMenu = new JPanel();
		loginMenu.setLayout(new BoxLayout(loginMenu, BoxLayout.Y_AXIS));
		
		JLabel loginLabel = new JLabel("LOGIN", SwingConstants.CENTER);
		loginLabel.setFont(new Font("SansSerif", Font.ITALIC | Font.BOLD, 50));
		loginLabel.setForeground(new Color(163, 38, 61));
		loginMenu.add(loginLabel);
		loginMenu.add(Box.createRigidArea(new Dimension(0, 150)));
				
		LineEdit usernameField = new LineEdit();
		usernameField.setOpaque(false);
		usernameField.setPlaceholder("Username");
		usernameField.setFont(new Font("SansSerif", Font.PLAIN, 30));
		usernameField.setBorder(BorderFactory.createLineBorder(new Color(163, 38, 61), 3));
		usernameField.addKeyListener(new KeyListener() {
		    public void keyTyped(KeyEvent e) { 
		        if (usernameField.getText().length() >= 30 )
		            e.consume(); 
		    }

			@Override
			public void keyPressed(KeyEvent e) {
				
			}

			@Override
			public void keyReleased(KeyEvent e) {
				
			}  
		});
				
		Color disabledTextColor = usernameField.getDisabledTextColor();
		usernameField.setCaretColor(disabledTextColor);
		usernameField.setForeground(disabledTextColor);
		
		loginMenu.add(usernameField);
		loginMenu.add(Box.createRigidArea(new Dimension(0, 500)));
		
		JPanel buttons = new JPanel();
		buttons.setOpaque(false);
		buttons.add(Box.createRigidArea(new Dimension(5, 0)));
		
		CatButton playButton = new CatButton("Play!");
		playButton.setAlignmentX(CENTER_ALIGNMENT);
		playButton.setBorderSize(2);
		playButton.setBorderColor(new Color(163, 38, 61));
		playButton.setTextColor(new Color(163, 38, 61));
		playButton.setBorderColorOnHover(Color.black);
		playButton.resize(new Dimension(100, 50));
		playButton.setFont(new Font("Arial", Font.PLAIN, 20));
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
    			
            	Game gameDisplay = new Game(usernameField.getText(), new BufferedImage(1, 1, 1), parent);
            	gameDisplay.showDisplay();
            }
        });
		buttons.add(playButton);
		
		buttons.add(Box.createRigidArea(new Dimension(10, 0)));
		
		CatButton backButton = new CatButton("Back");
		backButton.setAlignmentX(CENTER_ALIGNMENT);
		backButton.setBorderSize(2);
		backButton.setBorderColor(new Color(163, 38, 61));
		backButton.setTextColor(new Color(163, 38, 61));
		backButton.setBorderColorOnHover(Color.black);
		backButton.resize(new Dimension(100, 50));
		backButton.setFont(new Font("Arial", Font.PLAIN, 20));
		backButton.addActionListener( new ActionListener() {
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
    			
            	MainMenu mainMenuDisplay = new MainMenu(parent);
            	mainMenuDisplay.showDisplay();
            }
        });
		buttons.add(backButton);
		
		loginMenu.add(buttons);		
		
		return loginMenu;
	}
}
