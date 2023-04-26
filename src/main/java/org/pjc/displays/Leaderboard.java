package org.pjc.displays;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import org.pjc.widgets.CatButton;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Timer;
import java.util.TimerTask;
import java.awt.Component;

public class Leaderboard extends Display {

	public Leaderboard(JFrame parent) {
		super(parent, "assets/backgrounds/universe_background_small.png");
		createUI();
	}
	
	private void createUI() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(BorderFactory.createEmptyBorder(70, 70, 90,70));
		setOpaque(false);
		
		JLabel leaderboardLabel = new JLabel("Leaderboard");
		leaderboardLabel.setAlignmentX(CENTER_ALIGNMENT);
		leaderboardLabel.setFont(new Font("SansSerif", Font.ITALIC | Font.BOLD, 50));
		leaderboardLabel.setForeground(new Color(163, 38, 61));
		add(leaderboardLabel);
		add(Box.createRigidArea(new Dimension(0, 100)));
		
		Object rowData[][] = { 
				{ "1", "Alex", "20000" }, { "2", "Mirel", "4000" },
				{ "3", "Alex", "20000" }, { "4", "Mirel", "4000" },
				{ "5", "Alex", "20000" }, { "6", "Mirel", "4000" },
				{ "7", "Alex", "20000" }, { "8", "Mirel", "4000" },
				{ "9", "Alex", "20000" }, { "10", "Mirel", "4000" },
				{ "11", "Alex", "20000" }, { "12", "Mirel", "4000" },
		};
	    Object columnNames[] = { "#", "Username", "Score" };
		    
		JTable leaderboard = new JTable(rowData, columnNames) {
			@Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
	            JComponent cellComponent = (JComponent)super.prepareRenderer (
	                    renderer, row, column);
	            
	            cellComponent.setOpaque(false);
            	cellComponent.setBackground(new Color(0, 0, 0, 0));
            	cellComponent.setForeground(Color.white);
	            
	            cellComponent.setFont(new Font("SansSerif", Font.BOLD, 20));
	            return cellComponent;
			}
			
			@Override
		    public void changeSelection( int row, int col, boolean toggle, boolean expand ) {
		        
		    }
		};
		
		leaderboard.setOpaque(false);
		leaderboard.setRowMargin(10);
		leaderboard.setRowHeight(50);
		leaderboard.setShowGrid(false);
		leaderboard.setDefaultEditor(Object.class, null);
		leaderboard.getColumnModel().setColumnMargin(0);

		JTableHeader header = leaderboard.getTableHeader();
		header.setFont(new Font("Arial", Font.PLAIN, 30));
		header.setReorderingAllowed(false);
		header.setResizingAllowed(false);
		
		leaderboard.getColumnModel().getColumn(0).setMaxWidth(100);
	
		DefaultTableCellRenderer firstColumnRenderer = new DefaultTableCellRenderer();
		firstColumnRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		leaderboard.setDefaultRenderer(Object.class, firstColumnRenderer);
		
		DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
			public Component getTableCellRendererComponent(JTable tblData,
	                Object value, boolean isSelected, boolean hasFocus,
	                int row, int column) {
	            Component cellComponent = super.getTableCellRendererComponent(
	                    tblData, value, isSelected, hasFocus, row, column);
	            
	            cellComponent.setFont(new Font("Arial", Font.PLAIN, 30));
	            cellComponent.setBackground(new Color(163, 38, 61));
            	cellComponent.setForeground(Color.white);
            	
	            return cellComponent;
			}
		};
		
		headerRenderer.setBorder(null);
        headerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		leaderboard.getTableHeader().setDefaultRenderer(headerRenderer);
		
		JScrollPane scrollPane = new JScrollPane(leaderboard);
		scrollPane.setOpaque(false);
		scrollPane.setAlignmentX(CENTER_ALIGNMENT);
		
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		Dimension dim = leaderboard.getPreferredScrollableViewportSize();
        dim.height = 200;
        leaderboard.setPreferredScrollableViewportSize(dim);
        leaderboard.setMinimumSize(dim);

        Dimension dimHeader = leaderboard.getTableHeader().getPreferredSize();
        dim.height += dimHeader.height;
        scrollPane.setMinimumSize(dim);
        
		scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        scrollPane.getViewport().setOpaque(false);
		add(scrollPane, BorderLayout.CENTER);
		add(Box.createRigidArea(new Dimension(0, 100)));
		
		JPanel buttons = new JPanel();
		buttons.setOpaque(false);
		buttons.setLayout(new BorderLayout());
		buttons.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 70));
		
		CatButton backButton = new CatButton("Back");
		backButton.setBorder(BorderFactory.createEmptyBorder(500, 10, 10, 10));
		backButton.setAlignmentX(RIGHT_ALIGNMENT);
		backButton.setBorderSize(2);
		backButton.setBorderColor(new Color(163, 38, 61));
		backButton.setTextColor(new Color(163, 38, 61));
		backButton.setBorderColorOnHover(Color.black);
		backButton.setPreferredSize(new Dimension(100, 50));
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
    			
            	setVisible(false);
            	MainMenu mainMenuDisplay = new MainMenu(parent);
            	mainMenuDisplay.showDisplay();
            }
        });
		
		buttons.add(backButton, BorderLayout.EAST);
		add(buttons);
	}
}
