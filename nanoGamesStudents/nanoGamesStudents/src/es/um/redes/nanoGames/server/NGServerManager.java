package es.um.redes.nanoGames.server;

import java.util.HashMap;
//import java.util.Set;

import com.sun.swing.internal.plaf.synth.resources.synth;

import es.um.redes.nanoGames.server.roomManager.NGChallenge;
import es.um.redes.nanoGames.server.roomManager.NGGunmanRoom;
import es.um.redes.nanoGames.server.roomManager.NGRoomManager;
import es.um.redes.nanoGames.server.roomManager.NGRoomStatus;

/**
 * This class contains the general status of the whole server (without the logic
 * related to particular games) Esta clase contiene el estado general de todo el
 * servidor (sin la lógica relacionada con juegos particulares)
 */
class NGServerManager {

	// Players registered in this server
	private HashMap<String, NGPlayerInfo> players;
	private HashMap<Integer, NGRoomManager> rooms = new HashMap<Integer, NGRoomManager>();
	//private static final int nungunman = 4;
	// Current rooms and their related RoomManagers
	// TODO Data structure to relate rooms and RoomManagers
	NGServerManager() {
		this.players = new	HashMap<String, NGPlayerInfo>();
		this.rooms = new HashMap<>();
		registerRoomManager(new NGGunmanRoom(rooms.keySet().size()));
		registerRoomManager(new NGGunmanRoom(rooms.keySet().size()));
	}

	public void registerRoomManager(NGRoomManager rm) {
		rooms.put(rooms.keySet().size(),rm);
	}
	public synchronized void GenerarSala() {
		int cont = 0;
		for(Integer numero : rooms.keySet()) {
			NGRoomManager sala = rooms.get(numero);
			if (sala.playersInRoom() == 2) {
				cont++;
			}
		}
		if(cont == rooms.size()) {
			registerRoomManager(new NGGunmanRoom(rooms.keySet().size()));
		}
	}

	// Returns the set of existing rooms
	// public synchronized getRoomList() {
	// TODO
	// }

	// Given a room it returns the description
	
	public synchronized String getRoomDescription(NGRoomManager room) { 
	 //We make use of the RoomManager to obtain an updated description of the room return
	return 	room.getDescription();
	 }
	 
	// DEVOLVEMOS EL MAPA DONDE ESTÁN NUESTRAS SALAS
	public HashMap<Integer, NGRoomManager> getRooms() {
		return rooms;
	}
	public HashMap<String, NGPlayerInfo> getPlayers(){
		return players;
	}

	// False is returned if the nickname is already registered, True otherwise
	// and the player is registered
	// False se devuelve si el apodo ya está registrado, de lo contrario es
	// verdadero y el reproductor está registrado
	public synchronized boolean addPlayer(NGPlayerInfo player) {
		//Registra un jugador
		if (players.containsKey(player.nick)) {
			return false;
		} else {
			players.put(player.nick, player);
			return true;
		}

	}

	// The player is removed from the list
	
	 public synchronized void removePlayer(NGPlayerInfo player) { //TODO 

		}
	  
	  //A player request to enter in a room. If the access is granted the RoomManager is returned 
	 public synchronized NGRoomManager enterRoom(NGPlayerInfo p, int room) { //TODO Check if the room exists if
		 if (!rooms.containsKey(room)) return null;
		 NGRoomManager sala= rooms.get(room);
	  if (sala.registerPlayer(p)) {
	  	return sala; 
	  }else 
		 return null; }
	 //* 
	// * //A player leaves the room
	 public synchronized void leaveRoom(NGPlayerInfo p, NGRoomManager room ) {
		 //TODO Check if the room exists
		 room.removePlayer(p);
	 }
	
	 //*
	// */
}
