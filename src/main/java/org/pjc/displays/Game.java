package org.pjc.displays;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import java.io.*;

import java.net.Socket;

import java.util.*;
import java.util.List;
import java.util.Timer;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.pjc.entities.*;
import org.pjc.utills.GameThread;
import org.pjc.widgets.CatButton;

public class Game extends Display {
	private Socket socket;
	private BufferedReader input;
	private PrintWriter output;
	
	private JLabel loadingText;
	private Timer scheduler;
	private TimerTask timerTask;
	private GameObjectsImages objectsImages;
	private  BufferedImage world;

	private List<Player> players;
	private List<GameObject> objects;
	
	private boolean isEscMenuShowed = false;
	private JPanel escMenu;
	
	private boolean isDieMenuShowed = false;
	private List<JComponent> dieMenu;
	
	private boolean gameDataLoaded = false;
	private JTable leaderboard;

	private boolean isMirrored = false;
	
	public Game(String playerName, BufferedImage playerCat, JFrame parent) {
		super(parent, "assets/backgrounds/universe_background_small.png");
		this.scheduler = new Timer();
		this.loadingText = new JLabel("Connecting to server...", SwingConstants.CENTER);

		this.players = new ArrayList<>();
		this.objects = new ArrayList<>();

		setLayout(new BorderLayout());
		add(loadingText, BorderLayout.CENTER);
		//loadingText.setVisible(true);

		createPlayer(playerName, playerCat);
		loadWorldImage();
	}

	@Override
	public void showDisplay() {
		super.showDisplay();

		System.out.println("HERE SHOW");
		setupKeyListener();

		GameThread thread = new GameThread(this::connectToServer);
		thread.addListener(t -> {
			if(socket == null) {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
				MainMenu mainMenu = new MainMenu(parent);
				mainMenu.showDisplay();
				System.out.println("EHE");
			}
		});
		thread.start();
	}

	private void createEscMenu() {
		this.escMenu = new JPanel();
		escMenu.setBackground(new Color(0, 0, 0, 128));
		escMenu.setLayout(new GridBagLayout());
		escMenu.setVisible(false);
		add(escMenu);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(20, 0, 100, 0);

		JLabel label = new JLabel("SuperCat");
		label.setFont(new Font("SansSerif", Font.ITALIC | Font.BOLD, 100));
		label.setForeground(new Color(163, 38, 61));
		escMenu.add(label, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.insets = new Insets(15, 0, 15, 0);

		CatButton resumeButton = new CatButton("Resume");
		resumeButton.setAlignmentX(CENTER_ALIGNMENT);
		resumeButton.setBorderSize(3);
		resumeButton.setBorderColor(new Color(163, 38, 61));
		resumeButton.setBorderColorOnHover(new Color(163, 38, 61));
		resumeButton.setTextColor(new Color(163, 38, 61));
		resumeButton.setTextColorOnHover(new Color(163, 38, 61));
		resumeButton.setPreferredSize(new Dimension(300, 100));
		resumeButton.setFont(new Font("Arial", Font.PLAIN, 30));
		resumeButton.addActionListener(e -> {
			hideEscMenu();
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
		});
		escMenu.add(resumeButton, gbc);

		gbc.gridx = 0;
		gbc.gridy = 2;

		CatButton mainMenuButton = new CatButton("Exit to main menu");
		mainMenuButton.setAlignmentX(CENTER_ALIGNMENT);
		mainMenuButton.setBorderSize(3);
		mainMenuButton.setBorderColor(new Color(163, 38, 61));
		mainMenuButton.setBorderColorOnHover(new Color(163, 38, 61));
		mainMenuButton.setTextColor(new Color(163, 38, 61));
		mainMenuButton.setTextColorOnHover(new Color(163, 38, 61));
		mainMenuButton.setPreferredSize(new Dimension(300, 100));
		mainMenuButton.setFont(new Font("Arial", Font.PLAIN, 30));
		mainMenuButton.addActionListener(e -> {
			JSONObject message = new JSONObject();
			message.put("type", ClientMessageTypes.CLIENT_DISCONNECTED.ordinal());
			message.put("playerName", players.get(0).getPlayerName());
			sendMessage(message);

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

			MainMenu display = new MainMenu(parent);
			display.showDisplay();
		});
		escMenu.add(mainMenuButton, gbc);
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
		playAgainButton.setPreferredSize(new Dimension(300, 120));
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
		
		inMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "escMenu");
		inMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "moveUp");
		inMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "moveDown");
		inMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "moveLeft");
		inMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "moveRight");
		
		acMap.put("escMenu", new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(!isEscMenuShowed) {
					showEscMenu();
				}
				else {
					hideEscMenu();
				}
			}
			
		});
		
		acMap.put("moveUp", new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Player player = players.get(0);
				int[] playerPosition = player.getPlayerPosition();
				int newY = playerPosition[1] - 10;

				if(newY >= 0) {
					player.setPlayerPosition(new int[]{player.getPlayerPosition()[0], newY});

					JSONObject message = new JSONObject();
					message.put("type", ClientMessageTypes.CLIENT_PLAYER_POSITION_UPDATE.ordinal());
					message.put("playerName", player.getPlayerName());

					JSONArray arr = new JSONArray();
					arr.put(player.getPlayerPosition()[0]);
					arr.put(newY);
					message.put("playerPosition", arr);

					sendMessage(message);
					repaint();
				}
			}
			
		});
		
		acMap.put("moveDown", new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Player player = players.get(0);
				int[] playerPosition = player.getPlayerPosition();
				int[] playerSize = player.getPlayerSize();
				int newY = playerPosition[1] + 10;

				if(newY + playerSize[1] <= 10000) {
					player.setPlayerPosition(new int[]{player.getPlayerPosition()[0], newY});

					JSONObject message = new JSONObject();
					message.put("type", ClientMessageTypes.CLIENT_PLAYER_POSITION_UPDATE.ordinal());
					message.put("playerName", player.getPlayerName());

					JSONArray arr = new JSONArray();
					arr.put(player.getPlayerPosition()[0]);
					arr.put(newY);
					message.put("playerPosition", arr);

					sendMessage(message);
					repaint();
				}
			}
			
		});
		
		acMap.put("moveLeft", new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Player player = players.get(0);
				int[] playerPosition = player.getPlayerPosition();
				int newX = playerPosition[0] - 10;

				if(newX >= 0) {
					player.setPlayerPosition(new int[]{newX, player.getPlayerPosition()[1]});

					JSONObject message = new JSONObject();
					message.put("type", ClientMessageTypes.CLIENT_PLAYER_POSITION_UPDATE.ordinal());
					message.put("playerName", player.getPlayerName());

					JSONArray arr = new JSONArray();
					arr.put(newX);
					arr.put(player.getPlayerPosition()[1]);
					message.put("playerPosition", arr);

					isMirrored = true;
					sendMessage(message);
					repaint();
				}
			}
			
		});
		
		acMap.put("moveRight", new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Player player = players.get(0);
				int[] playerPosition = player.getPlayerPosition();
				int[] playerSize = player.getPlayerSize();
				int newX = playerPosition[0] + 10;

				if(newX + playerSize[0] <= 10000) {
					player.setPlayerPosition(new int[]{newX, player.getPlayerPosition()[1]});

					JSONObject message = new JSONObject();
					message.put("type", ClientMessageTypes.CLIENT_PLAYER_POSITION_UPDATE.ordinal());
					message.put("playerName", player.getPlayerName());

					JSONArray arr = new JSONArray();
					arr.put(newX);
					arr.put(player.getPlayerPosition()[1]);
					message.put("playerPosition", arr);

					sendMessage(message);
					isMirrored = false;
					repaint();
				}
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

		this.objectsImages = new GameObjectsImages();

		try {
			this.socket = new Socket("0.0.0.0", 1201);
			this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			this.output = new PrintWriter(socket.getOutputStream(), true);
			
			sendPlayerData();
			loadingText.setText("Loading game data...");

			this.timerTask = new TimerTask() {
				@Override
				public void run() {
					if(hasNewData())
						handleNewServerMessage();
				}
			};
				
			scheduler.schedule(timerTask, 10, 10);
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
			message.put("playerCat", Base64.getEncoder().encodeToString(baos.toByteArray()));
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
	}
	
	private void loadGameData(JSONObject message) {
		try {
			System.out.println(message);
			Graphics g = world.getGraphics();
			JSONArray arr = message.getJSONArray("worldObjects");
			
			for(Object obj : arr) {
                if(obj instanceof JSONObject) {
                    GameObject gameObj = getGameObjectFromJson((JSONObject)obj);
                    if(gameObj != null) {
						Image objImg = objectsImages.getObjectImage(gameObj.getType());

						g.drawImage(objImg, gameObj.getPosition()[0], gameObj.getPosition()[1], gameObj.getSize()[0],
								gameObj.getSize()[1], null);
                    }
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

			gameDataLoaded = true;
			loadingText.setVisible(false);
			requestFocus();

			createEscMenu();
			//createDieMenu();
			repaintGame();
		}
		catch(JSONException e) {
			System.out.println(e);
			e.printStackTrace();
			
			showConnectionMessage("Game data loading failed.");
		}
	}
	
	private void loadWorldImage() {
		try {
			world = ImageIO.read(new File("assets/backgrounds/universe_background.png"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static BufferedImage toBufferedImage(Image img)
	{
		if (img instanceof BufferedImage)
		{
			return (BufferedImage) img;
		}

		BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

		Graphics2D bGr = bimage.createGraphics();
		bGr.drawImage(img, 0, 0, null);
		bGr.dispose();

		return bimage;
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
		System.out.println("HER");
		if(escMenu != null && !isEscMenuShowed) {
			System.out.println("HER");
			isEscMenuShowed = true;
			escMenu.setVisible(true);
			validate();
		}
	}
	
	private void hideEscMenu() {
		if(escMenu != null && isEscMenuShowed) {
			isEscMenuShowed = false;
			escMenu.setVisible(false);
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
				objects.remove(gameObj);
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
				objects.add(gameObj);
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

	private BufferedImage getGameBackground(int[] displaySize) {
		int[] playerPosition = players.get(0).getPlayerPosition();
		int[] playerSize = players.get(0).getPlayerSize();

		int x = playerPosition[0] - ((displaySize[0] - playerSize[0]) / 2);
		int y = playerPosition[1] - ((displaySize[1] - playerSize[1]) / 2);

		if(x < 0){
			x = 0;
		}

		if(x + displaySize[0] > 10000) {
			x = 10000 - displaySize[0];
		}

		if(y < 0) {
			y = 0;
		}

		if(y + displaySize[1] > 10000) {
			y = 10000 - displaySize[1];
		}

		return world.getSubimage(x, y, displaySize[0], displaySize[1]);
	}

	@Override
	public void paintComponent(Graphics g) {
		if(!gameDataLoaded || objectsImages == null) {
			super.paintComponent(g);
			return;
		}

		int[] displaySize = getDisplaySize();
		BufferedImage imageToDraw = getGameBackground(displaySize);
		g.drawImage(imageToDraw, 0, 0, displaySize[0], displaySize[1], null);
		renderPlayers(g);

		System.out.println("RENDER");
	}

	private void renderAllWorld() {
		Graphics2D g = world.createGraphics();
		renderGameObjects(g);
		renderPlayers(g);

		File outputfile = new File("image.jpg");
		System.out.println(outputfile.getAbsoluteFile().getPath());
		try {
			ImageIO.write(world, "jpg", outputfile);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void renderGameObjects(Graphics g) {
		for (GameObject obj : objects) {
            Image objImg = objectsImages.getObjectImage(obj.getType())
                    .getScaledInstance(obj.getSize()[0], obj.getSize()[1], Image.SCALE_SMOOTH);

            g.drawImage(objImg, obj.getPosition()[0], obj.getPosition()[1], obj.getSize()[0],
                    obj.getSize()[1], null);
		}

		System.out.println("GATA");
	}
	
	private void renderPlayers(Graphics g) {
		Player currentPlayer = players.get(0);
		for(Player player : players) {
			Image playerCat = player.getPlayerCat().getScaledInstance(player.getPlayerSize()[0],
					player.getPlayerSize()[1], Image.SCALE_SMOOTH);

			int[] playerPos = player.getPlayerPosition();
			int[] playerSize = player.getPlayerSize();
			if(player == currentPlayer) {
				System.out.println("MAța");
				int[] displaySize = getDisplaySize();
				playerPos = new int[]{(displaySize[0] - playerSize[0]) / 4, (displaySize[1] - playerSize[1]) / 4};
			}

			g.setColor(new Color(255, 0, 0));
			g.drawString(player.getPlayerName(), playerPos[0] + 30, playerPos[1] - 10);

			Image catImage = playerCat;
			if(isMirrored) {
				BufferedImage bi = new BufferedImage(playerCat.getWidth(null), playerCat.getHeight(null), BufferedImage.TYPE_INT_ARGB);

				Graphics2D g2d = bi.createGraphics();
				g2d.translate(bi.getWidth(), 0);
				g2d.scale(-1, 1);
				g2d.drawImage(playerCat, 0, 0, null);

				g2d.dispose();
				catImage = bi;
			}

			System.out.println(playerPos[0]);
			g.drawImage(catImage, playerPos[0], playerPos[1], null);
		}
	}
	
	private void repaintGame() {
		revalidate();
		repaint();
	}
}
