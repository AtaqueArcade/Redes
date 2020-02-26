package es.um.redes.nanoGames.server;

public class NGPlayerInfo {
	public String nick; // Nickname of the user
	public byte status; // Current status of the user (according to the automata)
					    // Estado actual del usuario (según el autómata)
	public int score; // Current score of the user // Puntuación actual del usuario
	public int challenge;
	public String action;
	public int PartidasGanadas;
	public NGPlayerInfo(NGPlayerInfo p) {
		this.nick = new String(p.nick);
		this.status = p.status;
		this.score = p.score;
		this.challenge = p.challenge;
		this.action = null;
		this.PartidasGanadas = 0;
	}

	// Default constructor
	// Constructor predeterminado
	public NGPlayerInfo(String nick, byte status, int score) {
		this.nick = nick;
		this.status = status;
		this.score = score;
		this.challenge = 0;
		this.action = null;
		this.PartidasGanadas = 0;

	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public byte getStatus() {
		return status;
	}

	public void setStatus(byte status) {
		this.status = status;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}
	public int getChallenge() {
		return challenge;
	}
	public void setChallenge(int challenge) {
		this.challenge = challenge;
	}
	public String getAction() {
		return this.action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public int getPartidasGanadas() {
		return PartidasGanadas;
	}
	public void setPartidasGanadas(int partidaGananda) {
		this.PartidasGanadas = partidaGananda;
	}
	@Override
	public String toString() {
		return "Player["+getNick()+"] Life["+getScore()+"] GAMEWIN["+PartidasGanadas+"]";
	}

	// TODO Include additional fields if required
	// TODO Incluir campos adicionales si es necesario

}
