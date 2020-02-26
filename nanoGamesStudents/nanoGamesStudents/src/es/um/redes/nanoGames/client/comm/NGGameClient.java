package es.um.redes.nanoGames.client.comm;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
//import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;


//import com.sun.org.apache.xerces.internal.util.SynchronizedSymbolTable;

//import es.um.redes.nanoGames.client.application.NGController;
import es.um.redes.nanoGames.message.NGControlMessage;
import es.um.redes.nanoGames.message.NGEnterMessage;
import es.um.redes.nanoGames.message.NGGameMessage;
import es.um.redes.nanoGames.message.NGListMessage;
import es.um.redes.nanoGames.message.NGMessage;
import es.um.redes.nanoGames.message.NGNickMessage;
import es.um.redes.nanoGames.message.NGRulesMessage;
import es.um.redes.nanoGames.message.NGStatusMessage;
import es.um.redes.nanoGames.message.NGTokenMessage;
//import es.um.redes.nanoGames.server.roomManager.NGRoomDescription;

//This class provides the functionality required to exchange messages between the client and the game server 
//Esta clase proporciona la funcionalidad requerida para intercambiar mensajes entre el cliente y el servidor del juego
public class NGGameClient {
	private Socket socket;
	protected DataOutputStream dos;
	protected DataInputStream dis;

	private static final int SERVER_PORT = 6969;

	public NGGameClient(String serverName) throws UnknownHostException, IOException {
		// Creation of the socket and streams
		// Creación del socket y las secuencias
		// TODO
		this.socket = new Socket(serverName, SERVER_PORT);
		this.dos = new DataOutputStream(socket.getOutputStream());
		this.dis = new DataInputStream(socket.getInputStream());
	}

	public boolean verifyToken(long token) throws IOException {
		// NOTA IMPORTANTE CAMBIARLO DESPUÉS PARA LOS MENSAJES
		// SND(token) and RCV(TOKEN_VALID) or RCV(TOKEN_INVALID)
		// TODO
		// Make message
		NGTokenMessage mensaje = (NGTokenMessage) NGMessage.makeTokenMessage(NGMessage.OP_SENDTOKEN, token);
		byte[] rawMessage = mensaje.toByteArray();
		// Send messge (dos.write())
		dos.write(rawMessage);
		// Receive response (NGMessage.readMessageFromSocket)
		// NGTokenMessage respuesta = (NGTokenMessage)
		// NGMessage.readMessageFromSocket(dis);
		// We return True if the Token is valid
		return (dis.read() == NGMessage.OP_TOKENFEED);
		/**
		 * dos.writeLong(token); byte ack = dis.readByte(); return (ack == 1);
		 */

	}

	public boolean registerNickname(String nick) throws IOException {
		// SND(nick) and RCV(NICK_OK) or RCV(NICK_DUPLICATED) <- ?????
		//Creamos el mensaje de nick 
		NGNickMessage mensaje = (NGNickMessage) NGMessage.makeNickMessage(NGMessage.OP_NICK, nick);
		// lo pasamos a bytes para trabajar con sus campos
		byte[] rawMessage = mensaje.toByteArray();
		//Mandamos el mensaje para comprobación.
		dos.write(rawMessage);
		//Devolvemos en caso de no estar ok y sino false significando que está duplicado.
		return (dis.read() == NGMessage.OP_NICK_OK);
	}
	public boolean getAndShowRooms() throws IOException{
		NGControlMessage mensaje = (NGControlMessage) NGMessage.makeControlMessage(NGMessage.OP_ROOM_DESCRIPTION);
		
		byte[] rawMessage = mensaje.toByteArray();
		
		dos.write(rawMessage);
		
		NGListMessage mensajerecibido = (NGListMessage) NGMessage.readMessageFromSocket(dis);
		
		if (mensajerecibido.getOpcode() == NGMessage.OP_ROOM_DESCRIPTION_OK) {
			String[] descriptionsrooms = mensajerecibido.getItems();
			for(int i = 0; i < descriptionsrooms.length; i++) {
				System.out.println(descriptionsrooms[i]);
			}
			return true;
		}
		return false;
	}
	// TODO
	// add additional methods for all the messages to be exchanged between
	// client and game server

	// Used by the shell in order to check if there is data available to read
	public boolean isDataAvailable() throws IOException {
		return (dis.available() != 0);
	}

	// To close the communication with the server
	public void disconnect() {
		// TODO
	}
	
	public boolean enterTheGame(String room) throws IOException {
		int roomint = Integer.parseInt(room);
		NGEnterMessage mensaje = (NGEnterMessage) NGMessage.makeEnterMessage(NGMessage.OP_ENTER_ROOM, roomint);
		byte[] rawMessage = mensaje.toByteArray();
		dos.write(rawMessage);
		if(dis.read() == NGMessage.OP_ENTER_ROOM_OK) {
			return true;
		}
		return false;
	}
	public void getRules() throws IOException{
		NGControlMessage mensaje = (NGControlMessage) NGMessage.makeControlMessage(NGMessage.OP_GAME_RULES);
		byte[] rawMessage = mensaje.toByteArray();
		dos.write(rawMessage);
	}
	public void getStatus() throws IOException{
		NGControlMessage mensaje = (NGControlMessage) NGMessage.makeControlMessage(NGMessage.OP_GAME_STATUS);
		byte[] rawMessage = mensaje.toByteArray();
		dos.write(rawMessage);
	}
	public void sendAnswer(String answer)throws IOException {
		NGGameMessage mensaje = (NGGameMessage) NGMessage.makeGameMessage(NGMessage.OP_GAME_ANSWER, answer);
		byte[] rawMessage = mensaje.toByteArray();
		dos.write(rawMessage);
		
	}
	public void exitTheGame() throws IOException {
		NGControlMessage mensaje = (NGControlMessage) NGControlMessage.makeControlMessage(NGMessage.OP_EXIT_GAME);
		byte[] rawMessage = mensaje.toByteArray();
		dos.write(rawMessage);
		/**if(dis.read() == NGMessage.OP_EXIT_GAME_OK) {
			return true;
		}
		return false;*/
	}
	public boolean processGameMessage() throws IOException {
		NGMessage mensaje = NGMessage.readMessageFromSocket(dis);
			switch (mensaje.getOpcode()) {
			case NGMessage.OP_GAME_RULES_OK:
				System.out.println(((NGRulesMessage)mensaje).getRules());
				break;
			case NGMessage.OP_GAME_STATUS_SEND:
				System.out.println(((NGStatusMessage)mensaje).getStatus());
				break;
			case NGMessage.OP_GAME_CHALLENGE:
				System.out.println(((NGGameMessage)mensaje).getGameMessage());
				break;
			case NGMessage.OP_GAME_INFO_PLAYERS:
				System.out.println(((NGGameMessage)mensaje).getGameMessage());
				break;
			case NGMessage.OP_EXIT_GAME_OK:
				System.out.println("Saliendo del juego");
				return true;
			}
		return false;
			
	}

}
