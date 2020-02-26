package es.um.redes.nanoGames.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

//import javax.swing.plaf.synth.SynthSeparatorUI;

//import com.sun.corba.se.impl.ior.ByteBuffer;
//import com.sun.org.apache.xerces.internal.util.SynchronizedSymbolTable;
//
import es.um.redes.nanoGames.broker.BrokerClient;
import es.um.redes.nanoGames.message.NGControlMessage;
import es.um.redes.nanoGames.message.NGEnterMessage;
import es.um.redes.nanoGames.message.NGGameMessage;
import es.um.redes.nanoGames.message.NGListMessage;
import es.um.redes.nanoGames.message.NGMessage;
import es.um.redes.nanoGames.message.NGNickMessage;
import es.um.redes.nanoGames.message.NGRulesMessage;
import es.um.redes.nanoGames.message.NGStatusMessage;
import es.um.redes.nanoGames.message.NGTokenMessage;
import es.um.redes.nanoGames.server.roomManager.NGChallenge;
import es.um.redes.nanoGames.server.roomManager.NGGunmanRoom;
//import es.um.redes.nanoGames.server.roomManager.NGRoomDescription;
import es.um.redes.nanoGames.server.roomManager.NGRoomManager;
import es.um.redes.nanoGames.server.roomManager.NGRoomStatus;

/**
 * A new thread runs for each connected client
 */
public class NGServerThread extends Thread {

	// Possible states of the connected client
	private static final byte PRE_TOKEN = 1;
	private static final byte PRE_REGISTRATION = 2;
	private static final byte OFF_ROOM = 3;
	private static final byte IN_ROOM = 4;

	// Time difference between the token provided by the client and the one
	// obtained from the broker directly
	private static final long TOKEN_THRESHOLD = 1500; // 15 seconds
	// Socket to exchange messages with the client
	private Socket socket = null;
	// Global and shared manager between the threads
	private NGServerManager serverManager = new	NGServerManager();
	// Input and Output Streams
	private DataInputStream dis;
	private DataOutputStream dos;
	// Utility class to communicate with the Broker
	BrokerClient brokerClient;
	// Current player
	NGPlayerInfo player;
	// Current RoomManager (it depends on the room the user enters)
	NGRoomManager roomManager;
	// TODO Add additional fields
	private boolean condicion;
	
	private AtomicBoolean timeout_triggered = new AtomicBoolean();
	
	private static int comprobarResultados = 0;


	public NGServerThread(NGServerManager manager, Socket socket, String brokerHostname) throws SocketException {
		// Initialization of the thread
		// TODO
		this.serverManager = manager;
		this.socket = socket;
		this.brokerClient = new BrokerClient(brokerHostname);
		this.condicion = true;
	}
	//Private class to implement a very simple timer
	private class Timeout extends TimerTask{
	@Override
		public void run(){
		timeout_triggered.set(true);
		}
	}
	
	// Main loop
	public void run() {
		try {
			// We obtain the streams from the socket
			dis = new DataInputStream(socket.getInputStream());
			dos = new DataOutputStream(socket.getOutputStream());
			// The first step is to receive and to verify the token
			receiveAndVerifyToken();
			// The second step is to receive and to verify the nick name
			receiveAndVerifyNickname();
			// While the connection is alive...
			//Usamos una variable boolean para que 
			while (this.condicion) {
				// TODO Rest of messages according to the automata
				NGMessage mensaje = NGMessage.readMessageFromSocket(dis);
				switch (mensaje.getOpcode()) {
				case NGMessage.OP_ROOM_DESCRIPTION:
					sendRoomList();
					break;
				case NGMessage.OP_ENTER_ROOM:
					receiveAndVerifyEnterRoom((NGEnterMessage) mensaje);
					break; 
				default:
					this.condicion = false;
					break;
				}
				//receiveAndVerifyEnterRoom();
				//sendRoomList();
				//processRoomMessages();
			}
		} catch (Exception e) {
			// If an error occurs with the communications the user is removed
			// from all the managers and the connection is closed
			// TODO
			//e.printStackTrace();
		}
		// TODO Close the socket
	}

	// Receive and verify Token
	// Recibir y verificar el Token
	// TODO
	private void receiveAndVerifyToken() throws IOException {
		boolean tokenVerified = false;
		while (!tokenVerified) {
			// We extract the token from the message
			// Extraemos el token del mensaje
			NGTokenMessage message = (NGTokenMessage) NGMessage.readMessageFromSocket(dis);
			long tokenMessage = message.getToken();
			// now we obtain a new token from the broker
			// Ahora obtenemos un nuevo token del broker
			long tokenBroker = brokerClient.getToken();
			// We check the token and send an answer to the client
			// Comprobamos el token y enviamos una respuesta al cliente
			if (tokenBroker - tokenMessage <= TOKEN_THRESHOLD) {
				tokenVerified = true;
				NGControlMessage mensaje = (NGControlMessage) NGMessage.makeControlMessage(NGMessage.OP_TOKENFEED);
				byte[] rawmensaje = mensaje.toByteArray();
				dos.write(rawmensaje);
				// System.out.println("Entra un cliente en
				// receiveAndVerifyToken");
			} else {
				NGControlMessage mensaje = (NGControlMessage) NGMessage.makeControlMessage(NGMessage.OP_INVALID_CODE);
				byte[] rawmensaje = mensaje.toByteArray();
				dos.write(rawmensaje);
			}
		}
	}

	// We obtain the nick and we request the server manager to verify if it is
	// duplicated
	// TODO
	private void receiveAndVerifyNickname() throws IOException {
		boolean nickVerified = false;
		// this loop runs until the nick provided is not duplicated
		// este ciclo se ejecuta hasta que el nick proporcionado no se duplique
		while (!nickVerified) {
			// We obtain the nick from the message
			NGNickMessage mensaje = (NGNickMessage) NGMessage.readMessageFromSocket(dis);
			String nick = mensaje.getNick();
			byte status = 0;
			this.player = new NGPlayerInfo(nick, status, 0);
			// we try to add the player in the server manager
			// tratamos de agregar al jugador en el administrador del servidor
		    // TODO REVISAR PARA COLOCAR COMO GLOBAL
			boolean resultado = serverManager.addPlayer(this.player);
			// if success we send to the client the NICK_OK message
			if (resultado == true) {
				nickVerified = true;
				NGControlMessage mensajeAceptado = (NGControlMessage) NGMessage.makeControlMessage(NGMessage.OP_NICK_OK);
				byte[] rawmensaje = mensajeAceptado.toByteArray();
				dos.write(rawmensaje);
			} else {
			// otherwise we send DUPLICATED_NICK
			nickVerified = false;
			NGControlMessage mensajeDuplicado = (NGControlMessage) NGMessage.makeControlMessage(NGMessage.OP_NICK_DUPLICATE);
			byte[] rawmensaje = mensajeDuplicado.toByteArray();
			dos.write(rawmensaje);
			}
		}
	}

	// We send to the client the room list
	//TODO
	private void sendRoomList() throws IOException {
		// The room list is obtained from the server manager
		// La lista de salas se obtiene del administrador del servidor
		// Then we build all the required data to send the message to the client
		// Luego construimos todos los datos requeridos para enviar el mensaje al cliente
			HashMap<Integer, NGRoomManager> rooms = serverManager.getRooms();
			
			String[] roomsdescription = new String[rooms.size()];

			
			int i = 0;

			for(NGRoomManager room : rooms.values()) {
				roomsdescription[i] = serverManager.getRoomDescription(room);
				i++;
			}
			NGListMessage mensajeLista = (NGListMessage) NGMessage.makeListMessage(NGMessage.OP_ROOM_DESCRIPTION_OK, roomsdescription);
			byte[] rawmensaje = mensajeLista.toByteArray(); // PROBLEMA 
			dos.write(rawmensaje);
		
		}
//	}
	private void receiveAndVerifyEnterRoom(NGEnterMessage mensaje)throws IOException{
		int room = mensaje.getRoomEnter();
		//String nick = mensaje.getNickEnter();
		//HashMap<String, NGPlayerInfo> players = serverManager.getPlayers();
		//NGPlayerInfo p = players.get(nick);
	    roomManager = serverManager.enterRoom(this.player, room);
		if(roomManager == null) {
			NGControlMessage mensajeDenegado = (NGControlMessage) NGMessage.makeControlMessage(NGMessage.OP_ENTER_ROOM_IMPOSIBLE);
			byte [] rawMenssage = mensajeDenegado.toByteArray();
			dos.write(rawMenssage);
			}
		else {
			NGControlMessage mensajeAceptado = (NGControlMessage) NGMessage.makeControlMessage(NGMessage.OP_ENTER_ROOM_OK);
			byte [] rawMessage = mensajeAceptado.toByteArray();
			dos.write(rawMessage);
			CrearSala();
			processRoomMessages();
			}
			
		}
	private void CrearSala() {
		serverManager.GenerarSala();
	}
	private boolean processNewChallenge(NGChallenge challenge) throws IOException {
		//We send the challenge to the client
		//Mandaremos el challenge a todos los jugadores de nuestro juego
		//TODO
		if(challenge.getChallengeNumber() != 2) {
		player.setChallenge(0);
		}
		NGGameMessage mensajeJuego = new NGGameMessage(NGMessage.OP_GAME_CHALLENGE, challenge.getChallenge());
		byte [] rawMessage = mensajeJuego.toByteArray();
		dos.write(rawMessage);
		//Now we set the timeout
		Timer timer = null;
		timeout_triggered.set(false);
		timer = new Timer();
		timer.schedule(new Timeout(),roomManager.getTimeout(),roomManager.getTimeout());
		boolean answerProvided = false;
		//Loop until an answer is provided or the timeout expires
		while (!timeout_triggered.get() && !answerProvided /*&& challenge.getChallengeNumber() != 2*/) {
		if (dis.available() > 0) {
		//The client sent a message
		//TODO Process the message
		//IF ANSWER Then call roomManager.answer() and proceed
		NGMessage mensajeRecibido = NGMessage.readMessageFromSocket(dis);
		switch (mensajeRecibido.getOpcode()) {
		case NGMessage.OP_GAME_ANSWER:
			timer.cancel();
			roomManager.answer(player, ((NGGameMessage)mensajeRecibido).getGameMessage());
			comprobarResultados++;
			if(comprobarResultados > 1) {
				comprobarResultados = 0;
				verifyAnswers();
			}
			answerProvided = true;
			break;
		case NGMessage.OP_EXIT_GAME:
			timer.cancel();
			serverManager.leaveRoom(player, roomManager);
			return true;
		default:
			break;
		}
		} else
		try {
		//To avoid a CPU-consuming busy wait
		Thread.sleep(50);
		} catch (InterruptedException e) {
		//Ignore
		}
		}
		if (!answerProvided && challenge.getChallengeNumber() != 2) {
		//The timeout expired
		timer.cancel();
		roomManager.noAnswer(player);
		verifyAnswers();
		comprobarResultados = 0;
		}
		return false;
		}
	
	private void sendRules() throws IOException{
			String rules = roomManager.getRules();
			NGRulesMessage mensaje = (NGRulesMessage) NGMessage.makeRulesMessage(NGMessage.OP_GAME_RULES_OK, rules);
			byte [] rawMessage = mensaje.toByteArray();
			dos.write(rawMessage);
		
	}
	private void sendStatus() throws IOException{
		NGRoomStatus status = roomManager.checkStatus(player);
		NGStatusMessage mensaje = (NGStatusMessage) NGMessage.makeStatusMessage(NGMessage.OP_GAME_STATUS_SEND, status.status);
		byte [] rawMessage = mensaje.toByteArray();	
		dos.write(rawMessage);
		}
	// Method to process messages received when the player is in the room
	// Método para procesar los mensajes recibidos cuando el jugador está en la habitación
	// TODO
	//PARA MOSTRAR LA SITUACION DE CADA JUGADOR EN LA SALA
	private void sendChallenge(NGChallenge challenge) throws IOException{
		player.setChallenge(0);
		NGGameMessage mensaje = (NGGameMessage) NGMessage.makeGameMessage(NGMessage.OP_GAME_CHALLENGE, challenge.getChallenge());
		byte [] rawMessage = mensaje.toByteArray();	
		dos.write(rawMessage);
	}
	private void sendInfoGame() throws IOException{
		String playersdescription = roomManager.getdescriptionPlayers();
		NGGameMessage mensajeLista = (NGGameMessage) NGMessage.makeGameMessage(NGMessage.OP_GAME_INFO_PLAYERS, playersdescription);
		byte[] rawmensaje = mensajeLista.toByteArray(); // PROBLEMA 
		dos.write(rawmensaje);
		
	}
	private void verifyAnswers() throws IOException{
		roomManager.setResultado();
	}
	private void sendExitGame() throws IOException{
		NGControlMessage mensajeExit = (NGControlMessage) NGMessage.makeControlMessage(NGMessage.OP_EXIT_GAME_OK);
		byte[] rawmessage = mensajeExit.toByteArray();
		dos.write(rawmessage);
	}
	private void processRoomMessages() throws IOException {
		// First we send the rules and the initial status
		// Now we check for incoming messages, status updates and new challenges
		sendRules();
		sendStatus();
		boolean salirJuego = false;
		while (!salirJuego) {
			if (dis.available() > 0) {
				NGMessage mensaje = NGMessage.readMessageFromSocket(dis);
				switch (mensaje.getOpcode()) {
				case NGMessage.OP_GAME_RULES:
					sendRules();
					break;
				case NGMessage.OP_GAME_STATUS:
					sendStatus();
					break;
				case NGMessage.OP_EXIT_GAME:
					salirJuego = true;
					serverManager.leaveRoom(player, roomManager);
					//sendExitGame();
					break;
				default:
					break;
				}
			}else if((roomManager.checkStatus(player).getStatusNumber()!= 2) && (roomManager.checkStatus(player).getStatusNumber() != 0) && (roomManager.checkStatus(player).getStatusNumber() != 10)){
					sendStatus();
					player.setStatus((byte)2);
			}else if(roomManager.checkStatus(player).getStatusNumber() == 10) {
					sendStatus();
					roomManager.endGame(player);
					salirJuego = true;
			}else if(roomManager.checkChallenge(player) != null) {
				sendInfoGame();
				salirJuego = processNewChallenge(roomManager.checkChallenge(player));
			}
		}
		sendExitGame();
		//serverManager.leaveRoom(player, roomManager);
	}

}
