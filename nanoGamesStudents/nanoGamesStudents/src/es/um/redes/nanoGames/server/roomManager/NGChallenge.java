package es.um.redes.nanoGames.server.roomManager;

public class NGChallenge {
	public short challengeNumber;
	// TODO Change the challenge to represent accurately your game challenge
	public String challenge;
	// Status initialization
	NGChallenge() {
		challengeNumber = 0;
		challenge = null;
	}
	/*
	 * challengeNumber sera usado para ver que mensaje del juego mandamos a nuestro jugador
	 * challengeNumber = 0 : No mandaremos ningun mensaje
	 * challengeNumber = 1 : Mandaremos el mensaje del juego 
	 * challengeNumber = 2 : Mandaremos si la respuesta ha sido aceptada o denegada
	 */
	public NGChallenge(short currentChallengeNumber, String currentChallenge) {
		this.challengeNumber = currentChallengeNumber;
		challenge = currentChallenge;
	}
	
	public String getChallenge() {
		return challenge;
	}
	public int getChallengeNumber() {
		return challengeNumber;
	}

}
