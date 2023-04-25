package org.pjc.displays;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import java.net.Socket;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.pjc.entities.*;
import org.pjc.widgets.CatButton;

public class Game extends Display {
	private Socket socket;
	private BufferedReader input;
	private PrintWriter output;
	
	private JLabel loadingText;
	private Timer scheduler;
	private TimerTask timerTask;
	private GameObjectsImages objectsImages;

	private List<Player> players;
	private List<List<GameObject>> objects;
	
	private boolean isEscMenuShowed = false;
	private List<JComponent> escMenu;
	
	private boolean isDieMenuShowed = false;
	private List<JComponent> dieMenu;
	
	private boolean gameDataLoaded = false;
	private JTable leaderboard;
	
	public Game(String playerName, BufferedImage playerCat, JFrame parent) {
		super(parent, "assets/background1.png");
		this.scheduler = new Timer();
		this.loadingText = new JLabel("Connecting to server...");
		
		this.players = new ArrayList<>();
		this.objects = new ArrayList<>();
		
		for(int i = 0; i < 10; i++)
			objects.add(new ArrayList<GameObject>());
		
		setLayout(new GridBagLayout());
		setupKeyListener();
		createPlayer(playerName, playerCat);
		connectToServer();
	}
	
	private void createEscMenu() {
		this.escMenu = new ArrayList<>();
		
		loadingText.setText("SuperCat");
		loadingText.setVisible(false);
		escMenu.add(loadingText);
		
		CatButton resumeButton = new CatButton("Resume");
		resumeButton.setAlignmentX(CENTER_ALIGNMENT);
		resumeButton.setBorderSize(3);
		resumeButton.setBorderColor(new Color(163, 38, 61));
		resumeButton.setBorderColorOnHover(new Color(163, 38, 61));
		resumeButton.setTextColor(new Color(163, 38, 61));
		resumeButton.setTextColorOnHover(new Color(163, 38, 61));
		resumeButton.resize(new Dimension(300, 120));
		resumeButton.setFont(new Font("Arial", Font.PLAIN, 30));
		resumeButton.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	hideEscMenu();
            }
        });
		add(resumeButton);
		resumeButton.setVisible(false);
		escMenu.add(resumeButton);
		
		CatButton mainMenuButton = new CatButton("Exit to main menu");
		mainMenuButton.setAlignmentX(CENTER_ALIGNMENT);
		mainMenuButton.setBorderSize(3);
		mainMenuButton.setBorderColor(new Color(163, 38, 61));
		mainMenuButton.setBorderColorOnHover(new Color(163, 38, 61));
		mainMenuButton.setTextColor(new Color(163, 38, 61));
		mainMenuButton.setTextColorOnHover(new Color(163, 38, 61));
		mainMenuButton.resize(new Dimension(300, 120));
		mainMenuButton.setFont(new Font("Arial", Font.PLAIN, 30));
		mainMenuButton.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	JSONObject message = new JSONObject();
            	message.put("type", ClientMessageTypes.CLIENT_DISCONNECTED.ordinal());
            	message.put("playerName", players.get(0).getPlayerName());
            	sendMessage(message);
            	
            	MainMenu display = new MainMenu(parent);
            	display.showDisplay();
            }
        });
		add(mainMenuButton);
		mainMenuButton.setVisible(false);
		escMenu.add(mainMenuButton);
	}
	
	private void createDieMenu() {
		this.dieMenu = new ArrayList<>();
		
		loadingText.setText("You died!");
		loadingText.setVisible(false);
		dieMenu.add(loadingText);
		
		CatButton playAgainButton = new CatButton("Play again");
		playAgainButton.setAlignmentX(CENTER_ALIGNMENT);
		playAgainButton.setBorderSize(3);
		playAgainButton.setBorderColor(new Color(163, 38, 61));
		playAgainButton.setBorderColorOnHover(new Color(163, 38, 61));
		playAgainButton.setTextColor(new Color(163, 38, 61));
		playAgainButton.setTextColorOnHover(new Color(163, 38, 61));
		playAgainButton.resize(new Dimension(300, 120));
		playAgainButton.setFont(new Font("Arial", Font.PLAIN, 30));
		playAgainButton.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	hideDieMenu();
            	
            	JSONObject message = new JSONObject();
            	message.put("type", ClientMessageTypes.CLIENT_RESTART_GAME.ordinal());
            	
            	JSONArray arr = new JSONArray();
            	Random generator = new Random();
            	int[] position = new int[2];
            	int x = generator.nextInt(9900);
            	int y = generator.nextInt(9900);
            	
            	position[0] = x;
            	position[1] = y;
            	arr.put(x);
            	arr.put(y);
            	
            	Player player = players.get(0);
            	player.setPlayerPosition(position);
            	player.setPlayerScore(0);
            	player.setPlayerSize(new int[]{100, 100});
            	message.put("playerPosition", arr);
            	sendMessage(message);
            }
        });
		add(playAgainButton);
		playAgainButton.setVisible(false);
		dieMenu.add(playAgainButton);
		
		CatButton mainMenuButton = new CatButton("Exit to main menu");
		mainMenuButton.setAlignmentX(CENTER_ALIGNMENT);
		mainMenuButton.setBorderSize(3);
		mainMenuButton.setBorderColor(new Color(163, 38, 61));
		mainMenuButton.setBorderColorOnHover(new Color(163, 38, 61));
		mainMenuButton.setTextColor(new Color(163, 38, 61));
		mainMenuButton.setTextColorOnHover(new Color(163, 38, 61));
		mainMenuButton.resize(new Dimension(300, 120));
		mainMenuButton.setFont(new Font("Arial", Font.PLAIN, 30));
		mainMenuButton.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	JSONObject message = new JSONObject();
            	message.put("type", ClientMessageTypes.CLIENT_DISCONNECTED.ordinal());
            	message.put("playerName", players.get(0).getPlayerName());
            	sendMessage(message);
            	
            	MainMenu display = new MainMenu(parent);
            	display.showDisplay();
            }
        });
		add(mainMenuButton);
		mainMenuButton.setVisible(false);
		dieMenu.add(mainMenuButton);
	}
	
	private void createLeaderboard() {
		Object rowData[][] = { 
				{"Alex", "20000" }, { "2", "Mirel", "4000" },
				{ "3", "Alex", "20000" }, { "4", "Mirel", "4000" },
				{ "5", "Alex", "20000" }, { "6", "Mirel", "4000" },
				{ "7", "Alex", "20000" }, { "8", "Mirel", "4000" },
				{ "9", "Alex", "20000" }, { "10", "Mirel", "4000" },
				{ "11", "Alex", "20000" }, { "12", "Mirel", "4000" },
		};
	    Object columnNames[] = { "#", "Username", "Score" };
		    
		this.leaderboard = new JTable(rowData, columnNames) {
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
        dim.height = 400;
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
	}
	
	private void setupKeyListener() {		
		InputMap inMap = getInputMap();
		ActionMap acMap = getActionMap();
		
		inMap.put(KeyStroke.getKeyStroke("ESC"), "escMenu");
		inMap.put(KeyStroke.getKeyStroke("UP"), "moveUp");
		inMap.put(KeyStroke.getKeyStroke("DOWN"), "moveDown");
		inMap.put(KeyStroke.getKeyStroke("LEFT"), "moveLeft");
		inMap.put(KeyStroke.getKeyStroke("RIGHT"), "moveRight");
		
		acMap.put("escMenu", new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				showEscMenu();
			}
			
		});
		
		acMap.put("moveUp", new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Player player = players.get(0);
				JSONObject message = new JSONObject();
				message.put("type", ClientMessageTypes.CLIENT_PLAYER_POSITION_UPDATE.ordinal());
				message.put("playerName", player.getPlayerName());
				
				JSONArray arr = new JSONArray();
				arr.put(player.getPlayerPosition()[0]);
				arr.put(player.getPlayerPosition()[1] - 5);
				message.put("playerPosition", arr);
				
				sendMessage(message);
			}
			
		});
		
		acMap.put("moveDown", new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Player player = players.get(0);
				JSONObject message = new JSONObject();
				message.put("type", ClientMessageTypes.CLIENT_PLAYER_POSITION_UPDATE.ordinal());
				message.put("playerName", player.getPlayerName());
				
				JSONArray arr = new JSONArray();
				arr.put(player.getPlayerPosition()[0]);
				arr.put(player.getPlayerPosition()[1] + 5);
				message.put("playerPosition", arr);
				
				sendMessage(message);
			}
			
		});
		
		acMap.put("moveLeft", new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Player player = players.get(0);
				JSONObject message = new JSONObject();
				message.put("type", ClientMessageTypes.CLIENT_PLAYER_POSITION_UPDATE.ordinal());
				message.put("playerName", player.getPlayerName());
				
				JSONArray arr = new JSONArray();
				arr.put(player.getPlayerPosition()[0] - 5);
				arr.put(player.getPlayerPosition()[1]);
				message.put("playerPosition", arr);
				
				sendMessage(message);
			}
			
		});
		
		acMap.put("moveRight", new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Player player = players.get(0);
				JSONObject message = new JSONObject();
				message.put("type", ClientMessageTypes.CLIENT_PLAYER_POSITION_UPDATE.ordinal());
				message.put("playerName", player.getPlayerName());
				
				JSONArray arr = new JSONArray();
				arr.put(player.getPlayerPosition()[0] + 5);
				arr.put(player.getPlayerPosition()[1]);
				message.put("playerPosition", arr);
				
				sendMessage(message);
			}
			
		});
	}
	
	private void createPlayer(String playerName, BufferedImage playerCat) {
		Player player = new Player();
		player.setPlayerName(playerName);
		player.setPlayerCat(playerCat);
		
		Random generator = new Random();
		int[] position = new int[]{generator.nextInt(9900), generator.nextInt(9900)};
		player.setPlayerPosition(position);
		
		players.add(player);
	}
	
	private void connectToServer() {
		loadingText.setAlignmentX(CENTER_ALIGNMENT);
		loadingText.setAlignmentY(CENTER_ALIGNMENT);
		loadingText.setOpaque(false);
		loadingText.setForeground(new Color(163, 38, 61));
		loadingText.setFont(new Font("SansSerif", Font.BOLD, 100));
		loadingText.setFocusable(false);
		add(loadingText);
		
		try {
			this.socket = new Socket("0.0.0.0", 1201);
			this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			this.output = new PrintWriter(socket.getOutputStream(), true);
			
			sendPlayerData();
			
			this.timerTask = new TimerTask() {
				@Override
				public void run() {
					if(hasNewData())
						handleNewServerMessage();
				}
			};
				
			scheduler.schedule(timerTask, 10, 10);
			this.objectsImages = new GameObjectsImages();
			loadingText.setText("Loading game data...");
		}
		catch(IOException e) {
			System.out.println(e);
			e.printStackTrace();
			
			showConnectionMessage("<html>Failed to connect<br/>&#9;to the server!</html>");
		}
	}
	
	private void sendPlayerData() {
		Player player = players.get(0);
		JSONObject message = new JSONObject();
		message.put("type", ClientMessageTypes.CLIENT_CONNECTED.ordinal());
		message.put("playerName", player.getPlayerName());
		
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        ImageIO.write(player.getPlayerCat(), "png", baos);
	        byte[] bytes = baos.toByteArray();
			message.put("playerCat", new String(bytes));
		}
		catch(IOException e) {
			System.out.println(e);
			e.printStackTrace();
			
			showConnectionMessage("<html>Failed to load<br/>&#9;game data!</html>");
			return;
		}
		
		JSONArray arr = new JSONArray();
		arr.put(player.getPlayerPosition()[0]);
		arr.put(player.getPlayerPosition()[1]);
		message.put("playerPosition", arr);
		
		arr = new JSONArray();
		arr.put(player.getPlayerSize()[0]);
		arr.put(player.getPlayerSize()[1]);
		message.put("playerSize", arr);
		
		message.put("playerScore", 0);
		sendMessage(message);
	}
	
	private boolean hasNewData() {
		try {
			return input.ready();
		} catch (IOException e) {
			System.out.println(e);
			e.printStackTrace();
			
			return false;
		}
	}
	
	private void sendMessage(JSONObject message) {
		String jsonString = message.toString();
		String messageString = String.valueOf(jsonString.length()) + "\n";
		messageString += jsonString;

		output.write(messageString);	
		output.flush();
	}
	
	private void handleNewServerMessage() {
		JSONObject message = getNewMessage();
		
		try {
			int messageOrdinal = message.getInt("type");
			ClientMessageTypes messageType = ClientMessageTypes.values()[messageOrdinal];
			
			switch(messageType) {
				case CLIENT_REFUSED_CONNECTION: {
					showConnectionMessage(message.getString("reason"));
					
					break;
				}
				case CLIENT_CONNECTED: {
					loadGameData(message);
					
					break;
				}
				case CLIENT_PLAYER_POSITION_UPDATE: {
					movePlayer(message);
					
					break;
				}
				case CLIENT_PLAYER_SIZE_UPDATE: {
					resizePlayer(message);
					
					break;
				}
				case CLIENT_PLAYER_SCORE_UPDATE: {
					updateScore(message);
					
					break;
				}
				case CLIENT_LEADERBOARD_UPDATE: {
					updateLeaderboard(message);
					
					break;
				}
				case CLIENT_PLAYER_EATEN: {
					playerEaten(message);
					
					break;
				}
				case CLIENT_OBJECT_EATEN: {
					objectEaten(message);
					
					break;
				}
				case CLIENT_OBJECT_SPAWNED: {
					spawnObject(message);
					
					break;
				}
				case CLIENT_DISCONNECTED: {
					clientDisconnected(message);
					
					break;
				}
				default:
					break;
				}
		}
		catch(JSONException e) {
			System.out.println(e);
			e.printStackTrace();
		}
	}

	public JSONObject getNewMessage() {
		if(hasNewData())
			try {
				int messageSize = Integer.parseInt(input.readLine());
				int read;
				int charsRead = 0;
				
				StringBuilder response = new StringBuilder();
				while(charsRead < messageSize) {
					read = input.read();
					if(read == -1)
						continue;
					
					response.append((char)read);
					charsRead++;
				}
				
				return new JSONObject(response.toString());
			}
			catch(IOException e) {
				System.out.println(e);
				e.printStackTrace();
				
				return new JSONObject();
			}
		
		return new JSONObject();
	}
	
	private GameObject getGameObjectFromJson(JSONObject obj) {
		try {
			GameObjectTypes type = GameObjectTypes.values()[obj.getInt("objectType")];
			
			JSONArray arr = obj.getJSONArray("objectSize");
			int[] size = new int[]{arr.getInt(0), arr.getInt(1)};
			
			arr = obj.getJSONArray("objectPosition");
			int[] position = new int[]{arr.getInt(0), arr.getInt(1)};
			
			return new GameObject(type, size, position);
		}
		catch(JSONException e) {
			System.out.println(e);
			e.printStackTrace();
			
			return null;
		}
	}
	
	private Player getPlayerFromJson(JSONObject obj) {
		try {
			return new Player(obj);
		}
		catch(JSONException e) {
			System.out.println(e);
			e.printStackTrace();
			
			return null;
		}
	}
	
	private void showConnectionMessage(String text) {
		if(timerTask != null)
			timerTask.cancel();
		
		loadingText.setText(text);
		
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				MainMenu display = new MainMenu(parent);
	        	display.showDisplay();
			}	
		};
	
		scheduler.schedule(task, 3000);
	}
	
	private void loadGameData(JSONObject message) {
		try {
			System.out.println(message);
			JSONArray arr = message.getJSONArray("worldObjects");
			int index = 0;
			
			for(Object chunkList : arr) {
				if(chunkList instanceof JSONArray) {
					for(Object obj : (JSONArray)chunkList) {
						if(obj instanceof JSONObject) {
							GameObject gameObj = getGameObjectFromJson((JSONObject)obj);
							if(gameObj != null) {
								objects.get(index).add(gameObj);
							}
						}
					}
					
					System.out.println(objects.get(index).size());
					index++;
				}
			}
			
			arr = message.getJSONArray("otherPlayers");
			for(Object obj : arr) {
				if(obj instanceof JSONObject) {
					Player player = getPlayerFromJson((JSONObject)obj);
					if(player != null) {
						players.add(player);
					}
				}
			}
			
			startWorldRendering();
		}
		catch(JSONException e) {
			System.out.println(e);
			e.printStackTrace();
			
			showConnectionMessage("Game data loading failed.");
		}
	}
	
	private void startWorldRendering() {
		gameDataLoaded = true;
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				loadingText.setVisible(false);
			}			
		});

		createEscMenu();
		createDieMenu();
		repaintGame();
	}
	
	private void movePlayer(JSONObject message) {
		try {
			String playerName = message.getString("playerName");
			JSONArray arr = message.getJSONArray("playerPosition");
			
			for(Player player : players) {
				if(player.getPlayerName() == playerName) {
					player.setPlayerPosition(new int[]{arr.getInt(0), arr.getInt(1)});
					repaintGame();
					
					break;
				}
			}
		}
		catch(JSONException e) {
			System.out.println(e);
			e.printStackTrace();
		}
	}
	
	private void resizePlayer(JSONObject message) {
		try {
			String playerName = message.getString("playerName");
			JSONArray arr = message.getJSONArray("playerPosition");
			
			for(Player player : players) {
				if(player.getPlayerName() == playerName) {
					player.setPlayerSize(new int[]{arr.getInt(0), arr.getInt(1)});
					repaintGame();
					
					return;
				}
			}
		}
		catch(JSONException e) {
			System.out.println(e);
			e.printStackTrace();
		}
	}
	
	private void updateScore(JSONObject message) {
		try {
			String playerName = message.getString("playerName");
			int playerScore = message.getInt("playerScore");
			
			for(Player player : players) {
				if(player.getPlayerName() == playerName) {
					player.setPlayerScore(playerScore);
					
					return;
				}
			}
		}
		catch(JSONException e) {
			System.out.println(e);
			e.printStackTrace();
		}
	}
	
	private void updateLeaderboard(JSONObject message) {
		try {
			JSONArray arr = message.getJSONArray("playersList");
			for(Object obj : arr) {
				if(obj instanceof JSONObject) {
					JSONObject jsonObj = (JSONObject)obj;
					
				}
			}
		}
		catch(JSONException e) {
			System.out.println(e);
			e.printStackTrace();
		}
	}
	
	private void playerEaten(JSONObject message) {
		try {
			String playerName = message.getString("playerName");
			if(players.get(0).getPlayerName() == playerName) {
				showDieMenu();
				return;
			}
			
			for(Player player : players) {
				if(player.getPlayerName() == playerName) {
					player.setPlayerScore(-1);
					repaintGame();
					return;
				}
			}
		}
		catch(JSONException e) {
			System.out.println(e);
			e.printStackTrace();
		}
	}
	
	private void showEscMenu() {
		if(escMenu != null) {
			isEscMenuShowed = true;
			for(JComponent comp : escMenu) {
				comp.setVisible(true);
			}
		}
	}
	
	private void hideEscMenu() {
		if(escMenu != null) {
			isEscMenuShowed = false;
			for(JComponent comp : escMenu) {
				comp.setVisible(false);
			}
		}
	}
	
	private void showDieMenu() {
		if(dieMenu != null) {
			isDieMenuShowed = false;
			for(JComponent comp : dieMenu) {
				comp.setVisible(false);
			}
		}
	}
	
	private void hideDieMenu() {
		if(dieMenu != null) {
			isDieMenuShowed = false;
			for(JComponent comp : dieMenu) {
				comp.setVisible(false);
			}
		}
	}
	
	private void objectEaten(JSONObject message) {
		try {
			GameObject gameObj = getGameObjectFromJson(message.getJSONObject("eatenObject"));
			if(gameObj != null) {
				int chunk = gameObj.getPosition()[0] / 1000;
				objects.get(chunk).remove(gameObj);
				repaintGame();
			}
		}
		catch(JSONException e) {
			System.out.println(e);
			e.printStackTrace();
		}
	}
	
	private void spawnObject(JSONObject message) {
		try {
			GameObject gameObj = getGameObjectFromJson(message.getJSONObject("newObject"));
			if(gameObj != null) {
				int chunk = gameObj.getPosition()[0] / 1000;
				objects.get(chunk).add(gameObj);
				repaintGame();
			}
		}
		catch(JSONException e) {
			System.out.println(e);
			e.printStackTrace();
		}
	}
	
	private void clientDisconnected(JSONObject message) {
		try {
			String playerName = message.getString("playerName");

			for(Player player : players) {
				if(player.getPlayerName() == playerName) {
					players.remove(player);
					repaintGame();
					return;
				}
			}
		}
		catch(JSONException e) {
			System.out.println(e);
			e.printStackTrace();
		}
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		if(!gameDataLoaded || objectsImages == null)
			return;
		
		int[] displaySize = getDisplaySize();
		Player player = players.get(0);
		int[] chunksToRender = getChunksToRender(player, displaySize);
		
		renderCurrentChunksObjects(g, player, displaySize, chunksToRender);
		renderMainPlayer(g, player, displaySize, chunksToRender);
		renderOtherPlayers(g, player, displaySize, chunksToRender);
		System.out.println("HERE " + gameDataLoaded);
	}
	
	private int[] getChunksToRender(Player player, int[] displaySize) {
		int[] chunksToRender = new int[2];
		
		if(player.getPlayerPosition()[0] < displaySize[0] / 2) {
			chunksToRender[0] = 0;
			chunksToRender[1] = displaySize[0] / 1000;
		}
		else if(player.getPlayerPosition()[0] > 10000 - displaySize[0] / 2){
			chunksToRender[0] = (10000 - displaySize[0]) / 1000;
			chunksToRender[1] = 9;
		}
		else {
			chunksToRender[0] = (player.getPlayerPosition()[0] - displaySize[0] / 2) / 1000;
			chunksToRender[1] = (player.getPlayerPosition()[0] + displaySize[0] / 2 ) / 1000;
		}
		
		return chunksToRender;
	}
	
	private boolean isPlayerInMiddle(Player player, int[] displaySize) {
		return !(player.getPlayerPosition()[0] < displaySize[0] / 2 ||
				player.getPlayerPosition()[0] > 10000 - displaySize[0] / 2 ||
				player.getPlayerPosition()[1] < displaySize[1] / 2 ||
					player.getPlayerPosition()[1] > 10000 - displaySize[1] / 2);
	}
	
	private int[] mapPositionToDisplay(int[] positionToMap, int[] displaySize, int[] currentChunks, Player player) {
		int[] position = new int[2];
		if(positionToMap[0] < currentChunks[0] * 1000 ||
				positionToMap[0] > currentChunks[1] * 1000 ||
					positionToMap[1] < 0 || positionToMap[1] > 10000) {
			position[0] = -2000;
			position[1] = -2000;
		}
		else {
			int[] currentFrameStartingPoint = new int[2];
			
			if(isPlayerInMiddle(player, displaySize)) {
				currentFrameStartingPoint[0] = player.getPlayerPosition()[0] - displaySize[0] / 2;
				currentFrameStartingPoint[1] = player.getPlayerPosition()[1] - displaySize[1] / 2;
			}
			else {
				if(currentChunks[0] == 0) {
					currentFrameStartingPoint[0] = 0;
					
					if(positionToMap[1] > 10000 - displaySize[1] / 2)
						currentFrameStartingPoint[1] = 10000 - displaySize[1];
					else
						currentFrameStartingPoint[1] = positionToMap[1];
				}
				else if(currentChunks[1] == 9) {
					currentFrameStartingPoint[0] = 10000 - displaySize[0];
					
					if(positionToMap[1] > 10000 - displaySize[1] / 2)
						currentFrameStartingPoint[1] = 10000 - displaySize[1];
					else
						currentFrameStartingPoint[1] = positionToMap[1];
				}
				else {
					if(positionToMap[1] > 10000 - displaySize[1] / 2) {
						currentFrameStartingPoint[0] = player.getPlayerPosition()[0] - displaySize[0] / 2;
						currentFrameStartingPoint[1] = 10000 - displaySize[1];
					}
					else {
						currentFrameStartingPoint[0] = player.getPlayerPosition()[0] - displaySize[0] / 2;
						currentFrameStartingPoint[1] = 0;
					}
				}
			}
		}
		
		return position;
	}
	
	private void renderCurrentChunksObjects(Graphics g, Player player, int[] displaySize, int[] chunksToRender) {
		int[] objRenderPosition;
		
		for(int i = chunksToRender[0]; i <= chunksToRender[1]; i++) {
			List<GameObject> chunkObjects = objects.get(i);
			for(GameObject obj : chunkObjects) {
				Image objImg = objectsImages.getObjectImage(obj.getType())
						.getScaledInstance(obj.getSize()[0], obj.getSize()[1], Image.SCALE_SMOOTH);
				
				objRenderPosition = mapPositionToDisplay(obj.getPosition(), displaySize, chunksToRender, player);
				g.drawImage(objImg, objRenderPosition[0], objRenderPosition[1], obj.getSize()[0], 
						obj.getSize()[1], null);
			}
		}
	}
	
	private void renderMainPlayer(Graphics g, Player player, int[] displaySize, int[] currentChunks) {
		int[] renderPosition = new int[2];
		
		if(player.getPlayerPosition()[0] < displaySize[0] / 2 ||
				player.getPlayerPosition()[0] > 10000 - displaySize[0] / 2 ||
					player.getPlayerPosition()[1] < displaySize[1] / 2 ||
						player.getPlayerPosition()[1] > 10000 - displaySize[1] / 2) {
			renderPosition = mapPositionToDisplay(player.getPlayerPosition(), displaySize, currentChunks, player);
		}
		else {
			renderPosition[0] = (displaySize[0] - player.getPlayerSize()[0]) / 2;
			renderPosition[1] = (displaySize[1] - player.getPlayerSize()[1]) / 2;
		}
		
		Image playerCat = player.getPlayerCat().getScaledInstance(player.getPlayerSize()[0],
				player.getPlayerSize()[1], Image.SCALE_SMOOTH);
		g.drawImage(playerCat, renderPosition[0], renderPosition[1], 
				player.getPlayerSize()[0], player.getPlayerSize()[1], null);
	}
	
	private void renderOtherPlayers(Graphics g, Player player, int[] displaySize, int[] currentChunks) {
		int[] chunksToRender = getChunksToRender(player, displaySize);
		
		for(Player play : players) {
			if(play.equals(player))
				continue;
			
			if(player.getPlayerSize()[0] >= chunksToRender[0] * 1000 &&
					player.getPlayerSize()[0] <= chunksToRender[1] * 1000) {
				Image playerCat = play.getPlayerCat().getScaledInstance(play.getPlayerSize()[0],
						play.getPlayerSize()[1], Image.SCALE_SMOOTH);
				int[] positionToRender = mapPositionToDisplay(play.getPlayerPosition(), displaySize, currentChunks, player);
				g.drawImage(playerCat, positionToRender[0], positionToRender[1],
						play.getPlayerSize()[0], play.getPlayerSize()[1], null);
			}
		}
	}
	
	private void repaintGame() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				revalidate();
			}			
		});
	}
}
