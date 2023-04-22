package org.pjc;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.Dimension;
import java.awt.Point;

public class Player {
	private String playerName = "";
	private BufferedImage playerCat = new BufferedImage(1, 1, 1);
	private int[] playerSize = new int[]{100, 100};
	private int[] playerPosition = new int[]{4950, 4950};
	private int playerScore = 0;
	
	public Player(JSONObject info) throws JSONException {
		setPlayerInfo(info);
	}
	
	public Player() {
		
	}
	
	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		if(playerName == "")
			return;
		
		this.playerName = playerName;
	}

	public BufferedImage getPlayerCat() {
		return playerCat;
	}

	public void setPlayerCat(BufferedImage playerCat) {
		this.playerCat = playerCat;
	}

	public int[] getPlayerSize() {
		return playerSize;
	}

	public void setPlayerSize(int[] playerSize) {
		this.playerSize = playerSize;
	}

	public int[] getPlayerPosition() {
		return playerPosition;
	}

	public void setPlayerPosition(int[] playerPosition) {
		this.playerPosition = playerPosition;
	}

	public int getPlayerScore() {
		return playerScore;
	}

	public void setPlayerScore(int playerScore) {
		this.playerScore = playerScore;
	}

	private void setPlayerInfo(JSONObject info) throws JSONException {
		try {
			this.playerName = info.getString("playerName");
			this.playerCat = bytesToBufferedImage(info.getString("playerCat").getBytes());
			
			JSONArray size = info.getJSONArray("playerSize");
			this.playerSize = new int[]{size.getInt(0), size.getInt(1)};
			
			JSONArray position = info.getJSONArray("playerPosition");
			this.playerPosition = new int[]{position.getInt(0), position.getInt(1)};
			
			this.playerScore = info.getInt("playerScore");
		}
		catch(IOException e) {
			System.out.println(e);
			e.printStackTrace();
		}
	}
	
	private BufferedImage bytesToBufferedImage(byte[] data) throws IOException {
		InputStream is = new ByteArrayInputStream(data);
        BufferedImage bi = ImageIO.read(is);
        return bi;
	}
	
	@Override
	public boolean equals(Object anotherObject) {
		if(anotherObject instanceof Player) 
			return playerName == ((Player)anotherObject).getPlayerName();
		
		return super.equals(anotherObject);
	}
}
