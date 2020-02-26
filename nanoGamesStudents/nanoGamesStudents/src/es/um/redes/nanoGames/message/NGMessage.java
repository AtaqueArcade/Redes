package es.um.redes.nanoGames.message;

import java.io.DataInputStream;
import java.io.IOException;

//import es.um.redes.nanoGames.server.roomManager.NGRoomDescription;

public abstract class NGMessage {
	protected static byte opcode;

	public static final byte OP_INVALID_CODE = 0;
	public static final byte OP_SENDTOKEN = 1;
	public static final byte OP_TOKENFEED = 2;
	public static final byte OP_NICK = 3;
	public static final byte OP_NICK_OK = 4;
	public static final byte OP_NICK_DUPLICATE = 5;
	public static final byte OP_ROOM_DESCRIPTION = 6;
	public static final byte OP_ROOM_DESCRIPTION_OK= 7;
	public static final byte OP_ENTER_ROOM = 8;
	public static final byte OP_ENTER_ROOM_OK = 9;
	public static final byte OP_ENTER_ROOM_IMPOSIBLE = 10;
	public static final byte OP_GAME_RULES = 11;
	public static final byte OP_GAME_RULES_OK = 12;
	public static final byte OP_GAME_RULES_IMP = 13;
	public static final byte OP_EXIT_GAME = 14;
	public static final byte OP_EXIT_GAME_OK = 15;
	public static final byte OP_EXIT_GAME_IMP = 16;
	public static final byte OP_GAME_STATUS = 17;
	public static final byte OP_GAME_STATUS_SEND = 18;
	public static final byte OP_GAME_CHALLENGE = 19;
	public static final byte OP_GAME_ANSWER = 20;
	public static final byte OP_GAME_INFO_PLAYERS = 21;
	/*
	 * public static final byte OP_GETINFO = 3; public static final byte
	 * OP_SENDINFO = 4; public static final byte OP_CONNECT = 5; public static
	 * final byte OP_FEEDBACK = 6; public static final byte OP_GAMESTART = 7;
	 * public static final byte OP_GAMEACTION = 8; public static final byte
	 * OP_GAMERESPONSE = 9; public static final byte OP_GAMEINACTIVE = 10;
	 */



	// Returns the opcode of the message
	// Devuelve el código de operación del mensaje
	public byte getOpcode() {
		return opcode;
	}

	// Method to be implemented specifically by each subclass of NGMessage
	// Método que se implementará específicamente por cada subclase de
	// NGMessage
	public abstract byte[] toByteArray();

	// Reads the opcode of the incoming message and uses the subclass to parse
	// the rest of the message
	// Lee el código de operación del mensaje entrante y usa la subclase para
	// analizar el resto del mensaje
	public static NGMessage readMessageFromSocket(DataInputStream dis) throws IOException {
		// We use the operation to differentiate among all the subclasses
		// Usamos la operación para diferenciar entre todas las subclases
		opcode = dis.readByte();
		switch (opcode) {
		// TODO additional messages
		case (OP_SENDTOKEN): {
			return NGTokenMessage.readFromIS(opcode, dis);
		}
		case (OP_TOKENFEED): {
			return makeTokenMessage(opcode, dis.readLong());
			}
		case (OP_NICK): {
			return NGNickMessage.readFromIS(opcode, dis);
		}
		case (OP_NICK_OK):{//TODO check;
			return NGNickOkMessage.readFromIS(opcode, dis);
		}
		case (OP_NICK_DUPLICATE):{//TODO check
			//String nick = dis.read();
			return makeControlMessage(opcode);
		}
		case (OP_ROOM_DESCRIPTION):{
			return makeControlMessage(opcode);
		}
		case(OP_ROOM_DESCRIPTION_OK):{
			return NGListMessage.readFromIS(opcode, dis);
		}
		case(OP_ENTER_ROOM):{
			return NGEnterMessage.readFromIS(opcode, dis);
		}
		case(OP_GAME_RULES):{
			return NGMessage.makeControlMessage(opcode);
		}
		case(OP_GAME_RULES_OK):{
			return NGRulesMessage.readFromIS(opcode, dis);
		}
		case(OP_GAME_STATUS):{
			return NGMessage.makeControlMessage(opcode);
		}
		case(OP_GAME_STATUS_SEND):{
			return NGStatusMessage.readFromIS(opcode, dis);
		}
		case(OP_GAME_CHALLENGE):{
			return NGGameMessage.readFromIS(opcode, dis);
		}
		case(OP_GAME_ANSWER):{
			return NGGameMessage.readFromIS(opcode, dis);
		}
		case(OP_GAME_INFO_PLAYERS):{
			return NGGameMessage.readFromIS(opcode, dis);
		}
		case(OP_EXIT_GAME):{
			return NGMessage.makeControlMessage(opcode);
		}
		case (OP_EXIT_GAME_OK):{
			return NGMessage.makeControlMessage(opcode);
		}
		default:
			System.err.println("Unknown message type received:");
		}
		return null;
	}

	// The following method is just an example
	// El siguiente método es solo un ejemplo
	public static NGMessage makeTokenMessage(byte opcode, long token) {
		return new NGTokenMessage(opcode, token);
	}

	public static NGMessage makeListMessage(byte opcode, String[] items) {
		return new NGListMessage(opcode, items);
	}

	public static NGMessage makeControlMessage(byte opcode) {
		return new NGControlMessage(opcode);
	}

	public static NGMessage makeNickMessage(byte opcode, String nick) {
		return new NGNickMessage(opcode, nick);
	}
	public static NGMessage makeNickOkMessage(byte opcode){
		return new NGNickOkMessage(opcode);
	}
	public static NGMessage makeEnterMessage(byte opcode, int room) {
		return new NGEnterMessage(opcode, room);
	}
	public static NGMessage makeRulesMessage(byte opcode, String rules) {
		return new NGRulesMessage(opcode, rules);
	}
	/**public static NGMessage makeRoomDescription(byte opcode, int game, int capacity) {
		return new NGRoomDescription(opcode, game, capacity);
	}*/

	public static NGStatusMessage makeStatusMessage(byte opStatus, String status) {
		return new NGStatusMessage(opStatus, status);
	}
	public static NGGameMessage makeGameMessage(byte opcode, String gameMessage) {
		return new NGGameMessage(opcode, gameMessage);
	}

}
