package org.pjc;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.awt.event.ActionEvent;

public class CustomizationMenu extends JTabbedPane {
	
	private CatButton selectedTab = null;
	private String tabs[] = {"Cats", "Head", "Clothing", "Paw"};
	
	public CustomizationMenu() {
		super();
		createCustomizationMenu();
	}
	
	private void createCustomizationMenu() {
		setOpaque(false);
		setTabPlacement(JTabbedPane.BOTTOM);
		setUI(new BasicTabbedPaneUI() {
            @Override
            protected void paintTab(Graphics g, int tabPlacement, Rectangle[] rects, int tabIndex, Rectangle iconRect, Rectangle textRect){
            	
            }
            
            @Override
            protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
            	
            }
            
            @Override
            protected Insets getContentBorderInsets(int tabPlacement) {
                return new Insets(0, 0, 0, 0);
            }
        });
		addChangeListener(new ChangeListener() {
	        public void stateChanged(ChangeEvent e) {
	        	if(selectedTab != null) {
	        		selectedTab.setBorderColor(Color.black);
		            selectedTab.setBorderColorOnHover(Color.black);
	        	}
	        	
	            selectedTab = (CatButton)getTabComponentAt(getSelectedIndex());
	            if(selectedTab != null) {
		            selectedTab.setBorderColor(new Color(163, 38, 61));
		            selectedTab.setBorderColorOnHover(new Color(163, 38, 61));
	            }
	        }
	    });
		
		for(int i = 0; i < 4; i++)
			addTab(i);
		
		selectedTab = (CatButton)getTabComponentAt(0);
		selectedTab.setBorderColor(new Color(163, 38, 61));
        selectedTab.setBorderColorOnHover(new Color(163, 38, 61));
	}

	private void addTab(int index) {
		if(index < 0 || index > tabs.length)
			return;
		
		JPanel tabPanel = new JPanel();
		tabPanel.setOpaque(false);
		tabPanel.setLayout(new GridLayout(5, 10, 10, 10));
		tabPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		File directory;
		if(index == 0) 
			directory = new File("assets/cats");
		else if(index == 1)
			directory = new File("assets/head objects");
		else if(index == 2)
			directory = new File("assets/clothing objects");
		else
			directory = new File("assets/paw objects");
		
		int fileIndex = 0;
		File[] files = directory.listFiles();
		ButtonGroup buttonGroup = new ButtonGroup();
		
		for(int i = 0; i < 30; i++) {
			JToggleButton item;
			if(fileIndex < files.length) {
				ImageIcon icon = loadImageIcon(directory.listFiles()[fileIndex].getPath());
				item = new JToggleButton(icon);
				fileIndex++;
			}
			else {
				item = new JToggleButton();
			}
			
			item.setOpaque(false);
			item.setContentAreaFilled(false);
			item.getModel().addChangeListener(new ChangeListener() {
			    @Override
			    public void stateChanged(ChangeEvent e) {
			        ButtonModel model = (ButtonModel) e.getSource();
			        if (model.isSelected()) 
			        	item.setBorder(BorderFactory.createLineBorder(new Color(163, 38, 61), 3));
			        else 
			        	item.setBorder(BorderFactory.createLineBorder(new Color(122, 138, 153), 1));
			    }
			});
			
			item.setBackground(new Color(0, 0, 0, 0));
			tabPanel.add(item);
			buttonGroup.add(item);
		}
		
		addTab(tabs[index], tabPanel);
		
		CatButton tabButton = new CatButton(tabs[index]);
		tabButton.setTextColor(new Color(163, 38, 61));
		tabButton.setTextColorOnHover(new Color(163, 38, 61));
		tabButton.setPreferredSize(new Dimension(70, 35));
		tabButton.setFont(new Font("Arial", Font.PLAIN, 14));
		tabButton.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	setSelectedIndex(index);
            }
        });
		setTabComponentAt(index, tabButton);
	}
	
	private ImageIcon loadImageIcon(String path) {
		try {
			BufferedImage cat = ImageIO.read(new File(path));
			return new ImageIcon(cat.getScaledInstance(50, 50, BufferedImage.SCALE_SMOOTH));
		}
		catch(IOException e) {
			return null;
		}
	}
}
