package es.um.redes.nanoGames.server.roomManager;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import es.um.redes.nanoGames.message.NGMessage;

public class NGRoomDescription {
	private int game;
	private int players;
	private int capacity;
	private int room;
	public NGRoomDescription(int game, int room, int players, int capacity) {
		this.game = game;
		this.players= players;
		this.capacity = capacity;
		this.room = room;
	}
	public int getGame() {
		return game;
	}
	public int getPlayers() {
		return players;
	}
	public int getCapacity() {
		return capacity;
	}
	public int getRoom() {
		return room;
	}
	@Override
	public String toString() {
		return "[room=" + room +", game=" + game + ", players=" + players + ", capacity=" + capacity
				+ "]";
	}
}
