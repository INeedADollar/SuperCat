package org.pjc;

public enum ClientMessageTypes {
	CLIENT_REFUSED_CONNECTION,
	CLIENT_CONNECTED,
	CLIENT_RESTART_GAME,
	CLIENT_PLAYER_POSITION_UPDATE,
	CLIENT_PLAYER_SIZE_UPDATE,
	CLIENT_PLAYER_SCORE_UPDATE,
	CLIENT_LEADERBOARD_UPDATE,
	CLIENT_LEADERBOARD_REQUEST,
	CLIENT_PLAYER_EATEN,
	CLIENT_OBJECT_EATEN,
	CLIENT_OBJECT_SPAWNED,
	CLIENT_DISCONNECTED
}

