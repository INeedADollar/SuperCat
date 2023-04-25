package org.pjc.widgets;

import org.pjc.event_handlers.SkinChangeEventHandler;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomizationMenu extends JTabbedPane {
	
	private CatButton selectedTab = null;
	private SkinChangeEventHandler eventHandler;
	private Map<String, List<Icon>> icons = new HashMap<>();

	private String tabs[] = {"Cats", "Head", "Clothing", "Paw"};
	
	public CustomizationMenu(SkinChangeEventHandler eventHandler) {
		super();

		this.eventHandler = eventHandler;

		for(int i = 0; i < 4; i++) {
			icons.put(tabs[i], new ArrayList<>());
		}

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
		addChangeListener(e -> {
			if(selectedTab != null) {
				selectedTab.setBorderColor(Color.black);
				selectedTab.setBorderColorOnHover(Color.black);
			}

			selectedTab = (CatButton)getTabComponentAt(getSelectedIndex());
			if(selectedTab != null) {
				selectedTab.setBorderColor(new Color(163, 38, 61));
				selectedTab.setBorderColorOnHover(new Color(163, 38, 61));
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
				BufferedImage icon = loadIcon(directory.listFiles()[fileIndex].getPath());
				ImageIcon smallScaledIcon = new ImageIcon(icon.getScaledInstance(50, 50, BufferedImage.SCALE_SMOOTH));
				ImageIcon scaledIcon = new ImageIcon(icon.getScaledInstance(200, 200, BufferedImage.SCALE_SMOOTH));

				List<Icon> tabIcons = icons.get(tabs[index]);
				tabIcons.add(scaledIcon);

				item = new JToggleButton(smallScaledIcon);
				item.setFocusPainted(false);
				fileIndex++;

				int finalI = i;
				item.getModel().addChangeListener(e -> {
					ButtonModel model = (ButtonModel) e.getSource();
					if (model.isSelected())
						item.setBorder(BorderFactory.createLineBorder(new Color(163, 38, 61), 3));
					else
						item.setBorder(BorderFactory.createLineBorder(new Color(122, 138, 153), 1));

					eventHandler.handleEvent(selectedTab.getText(), icons.get(selectedTab.getText()).get(finalI));
				});
			}
			else {
				item = new JToggleButton();
				item.addMouseListener(new MouseListener() {
					@Override
					public void mouseClicked(MouseEvent e) {
						e.consume();
					}

					@Override
					public void mousePressed(MouseEvent e) {
						e.consume();
					}

					@Override
					public void mouseReleased(MouseEvent e) {
						e.consume();
					}

					@Override
					public void mouseEntered(MouseEvent e) {
						e.consume();
					}

					@Override
					public void mouseExited(MouseEvent e) {

					}
				});
			}
			
			item.setOpaque(false);
			item.setContentAreaFilled(false);
			
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
		tabButton.addActionListener(e -> {
			setSelectedIndex(index);
			selectedTab = tabButton;
		});
		setTabComponentAt(index, tabButton);
	}
	
	private BufferedImage loadIcon(String path) {
		try {
			return ImageIO.read(new File(path));
		}
		catch(IOException e) {
			return null;
		}
	}
}
