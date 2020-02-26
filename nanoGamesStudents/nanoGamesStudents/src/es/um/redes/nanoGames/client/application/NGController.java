package es.um.redes.nanoGames.client.application;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

import es.um.redes.nanoGames.broker.BrokerClient;
import es.um.redes.nanoGames.client.comm.NGGameClient;
import es.um.redes.nanoGames.client.shell.NGCommands;
import es.um.redes.nanoGames.client.shell.NGShell;

public class NGController {
	// Number of attempts to get a token
	private static final int MAX_NUMBER_OF_ATTEMPTS = 5;
	// Different states of the client (according to the automata)
	private static final byte PRE_TOKEN = 1;
	private static final byte PRE_REGISTRATION = 2;
	private static final byte OFF_ROOM = 3;
	private static final byte IN_ROOM = 4;
	// TODO Add additional states if necessary
	// The client for the broker
	private BrokerClient brokerClient;
	// The client for the game server
	private NGGameClient ngClient;
	// The shell for user commands from the standard input
	private NGShell shell;
	// Last command provided by the user
	private byte currentCommand;
	// Nickname of the user
	private String nickname;
	// Current room of the user (if any)
	private String room; //TODO room
	// Current answer of the user (if any)
	private String answer;
	// Rules of the game
	private String rules = "";
	// Current status of the game
	private String gameStatus = "";
	// Token obtained from the broker
	private long token = 0;
	// Server hosting the games
	private String serverHostname;

	public NGController(String brokerHostname, String serverHostname) {
		try {
			brokerClient = new BrokerClient(brokerHostname);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		shell = new NGShell();
		this.serverHostname = serverHostname;
	}

	public byte getCurrentCommand() {
		return this.currentCommand;
	}

	public void setCurrentCommand(byte command) {
		currentCommand = command;
	}

	public void setCurrentCommandArguments(String[] args) {
		// According to the command we register the related parameters
		// We also check if the command is valid in the current state
		// Según el comando registramos los parámetros relacionados
		// También verificamos si el comando es válido en el estado actual
		switch (currentCommand) {
		case NGCommands.COM_NICK:
			nickname = args[0];
			break;
		case NGCommands.COM_ENTER:
			room = args[0];
			break;
		case NGCommands.COM_ANSWER:
			answer = args[0];
			break;
		default:
		}
	}

	// Process commands provided by the users when they are not in a room
	public void processCommand() {
		switch (currentCommand) {
		case NGCommands.COM_TOKEN:
			try {
				getTokenAndDeliver();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case NGCommands.COM_NICK:
			registerNickName();
			break;
		case NGCommands.COM_ROOMLIST:
			getAndShowRooms(); // IMPLEMENTADA POR NOSOTROS
			break;
		case NGCommands.COM_ENTER:
			enterTheGame();
			break;
		case NGCommands.COM_QUIT:
			ngClient.disconnect();
			brokerClient.close();
			break;
		default:
		}
	}

	private void getAndShowRooms() {
		// We obtain the rooms from the server and we display them
		// Obtenemos las habitaciones del servidor y las mostramos
		try {
			ngClient.getAndShowRooms();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO
	}
	private void registerNickName() {
		// We try to register the nick in the server (it will check for
		// duplicates)
		// Intentamos registrar el nick en el servidor (verificará si hay
		// duplicados)
		// TODO
		try {
			if (ngClient.registerNickname(nickname)) {
				System.out.println("Nick ha sido aceptado");
			} else System.out.println("Nick se encuentra duplicado\nVuelva a introducirlo por favor");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void enterTheGame() {
		// The users request to enter in the room
		// TODO
		// If success, we change the state in order to accept new commands
		try {
			if (ngClient.enterTheGame(room) == true){
				boolean salirJuego = false;
				//las reglas y luego el status cuando toque.
			do {
				// We will only accept commands related to a room
				readGameCommandFromShell();
				salirJuego = processGameCommand();
			} while (/*(currentCommand != NGCommands.COM_EXIT) && */!salirJuego);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	// Usados una vez entramos en la sala.
	private boolean processGameCommand() {
		switch (currentCommand) {
		case NGCommands.COM_RULES:
			getRules();
			break;
		case NGCommands.COM_STATUS:
			getStatus();
			break;
		case NGCommands.COM_ANSWER:
			// TODO
			sendAnswer();
			break;
		case NGCommands.COM_SOCKET_IN:
			// In this case the user did not provide a command but an incoming
			// message was received from the server
			return processGameMessage();
		case NGCommands.COM_EXIT:
			exitTheGame();
			break;
		}
		return false;
	}

	private void getStatus() {
		try {
			ngClient.getStatus();
		}catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	private void getRules() {
		try {
			ngClient.getRules();
		} catch (IOException e) {
			e.printStackTrace();
	
		}
		
	}

	private void exitTheGame() {
		// We notify the server that the user is leaving the room
		// TODO
		try {
			ngClient.exitTheGame();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void sendAnswer() {
		// In case we have to send an answer we will wait for the response to
		// display it
		// TODO
		try {
			ngClient.sendAnswer(answer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private boolean processGameMessage() {
		// This method processes the incoming message received when the shell
		// was waiting for a user command
		// Este método procesa el mensaje recibido cuando el shell
		// estaba esperando un comando de usuario
		try {
			//String mensaje = ngClient.processGameMessage();
		return ngClient.processGameMessage();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		// TODO
	}

	// Method to obtain the token from the Broker
	// Método para obtener el token del Broker
	private void getTokenAndDeliver() throws IOException {
		// There will be a max number of attempts
		// Habrá un número máximo de intentos
		int attempts = MAX_NUMBER_OF_ATTEMPTS;
		// We try to obtain a token from the broker
		// Intentamos obtener un token del broker
		// TODO
		boolean ack = false;
		while ((attempts > 0) && (!ack)) {
			try {
				this.token = brokerClient.getToken();
				ack = true;

			} catch (Exception e) {
				attempts--;
			}

		}
		// If we have a token then we will send it to the game server
		// Si tenemos un token, lo enviaremos al servidor del juego
		if (token != 0) {
			try {
				// We initialize the game client to be used to connect with the
				// name server
				// Inicializamos el cliente del juego que se utilizará para
				// conectarse con el servidor de nombres
				ngClient = new NGGameClient(serverHostname);
				// We send the token in order to verify it
				// Enviamos el token para verificarlo
				if (!ngClient.verifyToken(token)) {
					System.out.println("* The token is not valid.");
					token = 0;
				} else {
					ack = true;

				}
			} catch (IOException e) {
				System.out.println("* Check your connection, the game server is not available.");
				token = 0;
			}
		}

	}

	public void readGameCommandFromShell() {
		// We ask for a new game command to the Shell (and parameters if any)
		shell.readGameCommand(ngClient);
		setCurrentCommand(shell.getCommand());
		setCurrentCommandArguments(shell.getCommandArguments());
	}

	public void readGeneralCommandFromShell() {
		// We ask for a general command to the Shell (and parameters if any)
		shell.readGeneralCommand();
		setCurrentCommand(shell.getCommand());
		setCurrentCommandArguments(shell.getCommandArguments());
	}

	public boolean sendToken() {
		// We simulate that the Token is a command provided by the user in order
		// to reuse the existing code
		System.out.println("* Obtaining the token...");
		setCurrentCommand(NGCommands.COM_TOKEN);
		processCommand();
		if (token != 0) {
			System.out.println("* Token is " + token + " and it was validated by the server.");
		}
		return (token != 0);
	}

	public boolean shouldQuit() {
		return currentCommand == NGCommands.COM_QUIT;
	}

}
