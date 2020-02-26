package es.um.redes.nanoGames.server.roomManager;

public class NGRoomStatus {
	public short statusNumber;
	// TODO Change the status to represent accurately your game status
	public String status;

	// Status initialization
	NGRoomStatus() {
		statusNumber = 0;
		status = null;
	}
	/*
	 * Nuestros estatus en la sala van a ser los siguientes
	 * statusNumber = 1 : Cuando hay un solo jugador y falta otro para comenzar el juego
	 * statusNumber = 2 : Cunado hay dos jugadores y el juego comienza // tratar challenge
	 * statusNumber = 3 : Cuando un jugador queda sin vidas los dos salen de la sala y se quedan en el servidor
	 */

	public NGRoomStatus(short currentStatus, String message) {
		statusNumber = currentStatus;
		this.status = message;
	}
	
	public short getStatusNumber() {
		return this.statusNumber;
	}
}
