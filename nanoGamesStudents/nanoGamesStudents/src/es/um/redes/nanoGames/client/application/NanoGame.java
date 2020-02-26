package es.um.redes.nanoGames.client.application;

public class NanoGame {

	public static void main(String[] args) {

		// Check the two required arguments
		// Verifica los dos argumentos requeridos
		if (args.length != 2) {
			System.out.println("Usage: java NanoGame <broker_hostname> <server_hostname>");
			return;
		}

		// Create controller object that will accept and process user commands
		// Crea un objeto controlador que aceptará y procesará los comandos
		// del usuario
		NGController controller = new NGController(args[0], args[1]);

		// Begin conversation with broker by getting the token
		// Comienza la conversación con el intermediario obteniendo el token
		if (controller.sendToken()) {
			// Begin accepting commands from user using shell
			// Empieza a aceptar comandos del usuario que usa shell
			do {
				controller.readGeneralCommandFromShell();
				controller.processCommand();
			} while (controller.shouldQuit() == false);
		} else
			System.out.println("ERROR: broker not available.");
		System.out.println("Bye.");
	}
}
