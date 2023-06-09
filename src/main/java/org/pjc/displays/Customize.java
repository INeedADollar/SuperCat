package org.pjc.displays;

import javax.swing.*;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import org.pjc.widgets.CatButton;
import org.pjc.widgets.CustomizationMenu;
import org.pjc.widgets.LineEdit;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.KeyListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.Timer;

public class Customize extends Display {
	private JLabel catLabel;
	private final Map<String, BufferedImage> skinComponents;

	public Customize(JFrame parent) {
		super(parent, "assets/backgrounds/universe_background_small.png");
		this.skinComponents = new HashMap<>();

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

		CustomizationMenu customizationMenu = new CustomizationMenu((tabName, icon) -> {
			skinComponents.put(tabName, icon);

			ImageIcon newCatSkin = new ImageIcon(createCatSkin().getScaledInstance(200, 200, BufferedImage.SCALE_SMOOTH));
			catLabel.setIcon(newCatSkin);
		});
		BufferedImage defaultCat = customizationMenu.getDefaultCatSkin();
		skinComponents.put("Cats", defaultCat);

		ImageIcon catIcon = new ImageIcon(defaultCat.getScaledInstance(200, 200, BufferedImage.SCALE_SMOOTH));
		catLabel = new JLabel(catIcon);
		catLabel.setOpaque(false);;
		catPreview.add(catLabel, BorderLayout.CENTER);
		firstHalf.add(catPreview);
		
		JPanel catCustomization = new JPanel();
		catCustomization.setLayout(new BorderLayout(10, 10));
		catCustomization.setOpaque(false);
		catCustomization.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

		catCustomization.add(customizationMenu, BorderLayout.CENTER);
		firstHalf.add(catCustomization);

		add(firstHalf);
		
		JPanel login = createCustomizationMenu();
		login.setBorder(BorderFactory.createEmptyBorder(30, 30, 10, 30));
		login.setOpaque(false);
		
		add(login);
		setBackground(Color.black);
	}
	
	private JPanel createCustomizationMenu() {
		JPanel loginMenu = new JPanel();
		loginMenu.setLayout(new BoxLayout(loginMenu, BoxLayout.Y_AXIS));
		
		JLabel loginLabel = new JLabel("CUSTOMIZE", SwingConstants.CENTER);
		loginLabel.setFont(new Font("SansSerif", Font.ITALIC | Font.BOLD, 50));
		loginLabel.setForeground(new Color(163, 38, 61));
		loginMenu.add(loginLabel);
		loginMenu.add(Box.createRigidArea(new Dimension(0, 150)));
				
		LineEdit usernameField = new LineEdit();
		usernameField.setOpaque(false);
		usernameField.setPlaceholder("Username");
		usernameField.setFont(new Font("SansSerif", Font.PLAIN, 30));
		usernameField.setBorder(BorderFactory.createLineBorder(new Color(163, 38, 61), 3));
		usernameField.setMaximumSize(new Dimension(100000, 30));
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
		loginMenu.add(Box.createRigidArea(new Dimension(0, 100)));
		
		JPanel buttons = new JPanel();
		buttons.setBackground(new Color(255, 0, 0));
		buttons.setOpaque(false);
		buttons.add(Box.createRigidArea(new Dimension(5, 0)));
		
		CatButton playButton = new CatButton("Play!");
		playButton.setAlignmentX(CENTER_ALIGNMENT);
		playButton.setBorderSize(2);
		playButton.setBorderColor(new Color(163, 38, 61));
		playButton.setTextColor(new Color(163, 38, 61));
		playButton.setBorderColorOnHover(Color.black);
		playButton.setPreferredSize(new Dimension(100, 50));
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

				setVisible(false);
            	Game gameDisplay = new Game(usernameField.getText(), createCatSkin(), parent);
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
		backButton.setPreferredSize(new Dimension(100, 50));
		backButton.setFont(new Font("Arial", Font.PLAIN, 20));
		backButton.addActionListener(e -> {
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
		});
		buttons.add(backButton);
		
		loginMenu.add(buttons, BorderLayout.SOUTH);
		
		return loginMenu;
	}

	private BufferedImage createCatSkin() {
		BufferedImage cat = skinComponents.get("Cats");
		BufferedImage catSkin = new BufferedImage(cat.getWidth(), cat.getHeight(), cat.getType());

		Graphics2D g = (Graphics2D)catSkin.getGraphics();
		for(String key : skinComponents.keySet()) {
			BufferedImage component = skinComponents.get(key);

			if(component != null) {
				g.drawImage(component, 0, 0, null);
			}
		}

		return catSkin;
	}
}
