package es.um.redes.nanoGames.server.roomManager;

import java.util.HashMap;


import es.um.redes.nanoGames.server.NGPlayerInfo;

public class NGGunmanRoom extends NGRoomManager{
	private final static int capacity = 2;
	private HashMap<String, NGPlayerInfo> setPlayers;
	private NGPlayerInfo player;
	private NGRoomStatus roomStatus;
	private NGChallenge challenge;
	boolean registrado = false;
	
	private int id;
	
	public NGGunmanRoom(int room ) {
		this.rules = "Bienvenidos al juego del Gunman (El pistolero) reglas: \n"+
	                 "Protegerse: No pierdes vidas\n"+
	                 "Disparar: Quitas vidas al jugardor";
		this.id = room; // NUMERO DE HABITACION
		this.setPlayers = new HashMap<String,NGPlayerInfo>();
		gameTimeout = 1000*60; // un milisegundo * segundos de contestacion;
	}
	
	@Override
	public synchronized boolean registerPlayer(NGPlayerInfo p) {
		if(setPlayers.size()< capacity) {
			p.setScore(2);
			p.setStatus((byte)0);
			setPlayers.put(p.getNick(), p);
		if(setPlayers.size()== capacity) {
				//this.roomStatus = new NGRoomStatus((short)1, "El juego acaba de comezar");
				for(String player : setPlayers.keySet()) {
					NGPlayerInfo jugador = setPlayers.get(player);
					jugador.setStatus((byte) 1);
					jugador.setChallenge(1);
					setPlayers.replace(player, jugador);
					roomStatus = checkStatus(jugador);
					challenge = checkChallenge(jugador);
				}
				//challenge = checkChallenge(p);
			}else roomStatus = checkStatus(p);//*/
			return true;
			}
		return false;
	}
	public synchronized int getNumberRoom() {
		return this.id;
	}
	public synchronized HashMap<String, NGPlayerInfo> getSetPlayers(){
		return setPlayers;
	}

	@Override
	public synchronized String getRules() {
		return (rules);
	}

	@Override
	public synchronized NGRoomStatus checkStatus(NGPlayerInfo p) {
		// TODO Auto-generated method stub
		switch (p.status) {
		case 0:
			return new NGRoomStatus((short)0,"Hay en la sala: "+setPlayers.size()+" jugadores. La partida no puede empezar");
		case 1:
			return new NGRoomStatus((short)1,"El juego ha comenzado");
		case 2:
			return new NGRoomStatus((short)2, "En partida");
		case 3:
			return new NGRoomStatus((short)3, "Has ganado este turno");
		case 4:
			return new NGRoomStatus((short)4, "Has perdido este turno");
		case 5:
			return new NGRoomStatus((short)5, "Ambos jugadores pierden vidas este turno");
		case 6:
			return new NGRoomStatus((short)6, "Ambos jugadores empatan este turno");
		case 7:
			return new NGRoomStatus((short)7, "Has ganado la partida");
		case 8:
			return new NGRoomStatus((short)8, "Has perdido la partida");
		case 9:
			return new NGRoomStatus((short)9, "Ambos jugadores pierden la partida");
		case 10:
			return new NGRoomStatus((short)10, "El otro jugador abandono la partida, procedemos a echarte de la sala, gracias por jugar");
		default:
			break;
		}
		return null;
	}

	@Override
	public synchronized NGChallenge checkChallenge(NGPlayerInfo p) {
		switch (p.getChallenge()){
		case 1:
			return new NGChallenge((short)1, "Elige una de las opciones:\na Disparar \na Protegerse \nNo puedes pedir ni rules ni status antes de contestar");
		default:
			break;
		}
		return null;
	}

	@Override
	public synchronized void noAnswer(NGPlayerInfo p) {
		ActionPlayer(p, "NOACCION");
		p.setAction("NOACCION");
	}

	@Override
	public synchronized void answer(NGPlayerInfo p, String answer) {
		//System.out.println(answer);
		//p.setAction(answer);
		switch (answer) {
		case "Disparar":
			ActionPlayer(p, answer);
			setPlayers.replace(p.getNick(), p);
			break;
		case "Protegerse":
			ActionPlayer(p, answer);
			setPlayers.replace(p.getNick(), p);
			break;
		default:
			ActionPlayer(p, "NOACCION");
			setPlayers.replace(p.getNick(), p);
			break;
		}
	}

	@Override
	public synchronized void removePlayer(NGPlayerInfo p) {
		setPlayers.remove(p.getNick());
		if(setPlayers.size() > 0) {
		for(String player : setPlayers.keySet()) {
			NGPlayerInfo jugador = setPlayers.get(player);
			jugador.setStatus((byte)10);
			setPlayers.replace(player, jugador);
		}
		}
		}
	@Override
	public void endGame(NGPlayerInfo p) {
		setPlayers.remove(p.getNick());	
	}

	@Override
	public synchronized NGRoomManager duplicate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public synchronized String getRegistrationName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public synchronized String getDescription() {
		NGRoomDescription room = new NGRoomDescription(1, id, setPlayers.size(), NGGunmanRoom.capacity);
		return room.toString();
	}

	@Override
	public synchronized int playersInRoom() {
		// TODO Auto-generated method stub
		return setPlayers.size();
	}
	@Override
	public synchronized String getdescriptionPlayers(){
		String[] players = new String[2];
		int i = 0;
		for(String player: setPlayers.keySet()) {
			NGPlayerInfo jugador = setPlayers.get(player);
			players[i] = jugador.toString();
			i++;
		}
		return(""+players[0]+"\n"+players[1]);
	}
	public synchronized void ActionPlayer(NGPlayerInfo p, String answer) {
		/**if((answer == "Disparar")&&(p.g) {
			p.setAction(null);
		}else*/
		p.setAction(answer);
	}
	
	@Override
	public synchronized void setResultado() {
		NGPlayerInfo[] resultados = new NGPlayerInfo[2];
		int i = 0;
		for(String player : setPlayers.keySet()) {
			NGPlayerInfo jugador = setPlayers.get(player);
			resultados[i] = jugador;
			i++;
		}
		switch(resultados[0].getAction()) {
		case "Disparar":
				switch (resultados[1].getAction() ) {
				case "Disparar":
					resultados[0].setScore(resultados[0].getScore() - 1);
					resultados[1].setScore(resultados[1].getScore() - 1);
						switch (resultados[0].getScore()) {
						case 0:
								switch (resultados[1].getScore()) {
								case 0:
									resultados[0].setStatus((byte)9);
									resultados[1].setStatus((byte)9);
									resultados[0].setChallenge(1);
									resultados[1].setChallenge(1);
									reset();
									break;
								default:
									resultados[0].setStatus((byte)8);
									resultados[1].setStatus((byte)7);
									resultados[1].setPartidasGanadas(resultados[1].getPartidasGanadas() + 1);
									reset();
									resultados[0].setChallenge(1);
									resultados[1].setChallenge(1);
									break;
									}
						break;
						default:
								switch (resultados[1].getScore()) {
								case 0:
								resultados[0].setStatus((byte)7);
								resultados[1].setStatus((byte)8);
								resultados[0].setPartidasGanadas(resultados[0].getPartidasGanadas() + 1);
								reset();
								resultados[0].setChallenge(1);
								resultados[1].setChallenge(1);
								break;
								default:
								resultados[0].setStatus((byte)5);
								resultados[1].setStatus((byte)5);
								resultados[0].setChallenge(1);
								resultados[1].setChallenge(1);
								break;
								}
							break;
						}
					break;
				case "Protegerse":
					resultados[0].setStatus((byte)6);
					resultados[1].setStatus((byte)6);
					resultados[0].setChallenge(1);
					resultados[1].setChallenge(1);
					break;
				case "NOACCION":
					resultados[1].setScore(resultados[1].getScore() - 1);
					switch (resultados[1].getScore()) {
					case 0:
						resultados[0].setStatus((byte)7);
						resultados[1].setStatus((byte)8);
						resultados[0].setPartidasGanadas(resultados[0].getPartidasGanadas() + 1);
						reset();
						resultados[0].setChallenge(1);
						resultados[1].setChallenge(1);
						break;
					default:
						resultados[0].setStatus((byte)3);
						resultados[1].setStatus((byte)4);
						resultados[0].setChallenge(1);
						resultados[1].setChallenge(1);
						break;
					}
					break;
				default:
					break;
				}
			break;
		case "Protegerse":
				switch (resultados[1].getAction()) {
				case "Disparar":
					resultados[0].setStatus((byte)6);
					resultados[1].setStatus((byte)6);
					resultados[0].setChallenge(1);
					resultados[1].setChallenge(1);
					break;
				case "Protegerse":
					resultados[0].setStatus((byte)6);
					resultados[1].setStatus((byte)6);
					resultados[0].setChallenge(1);
					resultados[1].setChallenge(1);
					break;
				case "NOACCION":
					resultados[0].setStatus((byte)6);
					resultados[1].setStatus((byte)6);
					resultados[0].setChallenge(1);
					resultados[1].setChallenge(1);
					break;
				default:
					break;
				}
		break;
		case "NOACCION":
			switch (resultados[1].getAction()) {
			case "Disparar":
				resultados[0].setScore(resultados[0].getScore() - 1);
				switch (resultados[0].getScore()) {
				case 0:
				resultados[0].setStatus((byte)8);
				resultados[1].setStatus((byte)7);
				resultados[1].setPartidasGanadas(resultados[1].getPartidasGanadas() + 1);
				reset();
				resultados[0].setChallenge(1);
				resultados[1].setChallenge(1);
				break;
				default:
				resultados[0].setStatus((byte)4);
				resultados[1].setStatus((byte)3);
				resultados[0].setChallenge(1);
				resultados[1].setChallenge(1);
				break;
				}
				break;
			case "Protegerse":
				resultados[0].setStatus((byte)6);
				resultados[1].setStatus((byte)6);
				resultados[0].setChallenge(1);
				resultados[1].setChallenge(1);
			case "NOACCION":
				resultados[0].setStatus((byte)6);
				resultados[1].setStatus((byte)6);
				resultados[0].setChallenge(1);
				resultados[1].setChallenge(1);
				break;
			default:
				break;
			}
			break;
			default:
				break;
		}		
		setPlayers.replace(resultados[0].getNick(), resultados[0]);
		setPlayers.replace(resultados[1].getNick(), resultados[1]);
	}
	public void reset() {
		for(String player : setPlayers.keySet()){
			NGPlayerInfo jugador = setPlayers.get(player);
			jugador.setScore(2);
			setPlayers.replace(player, jugador);
		}
	}
}
