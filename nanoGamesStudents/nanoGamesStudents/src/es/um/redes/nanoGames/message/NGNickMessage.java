package es.um.redes.nanoGames.message;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
//import java.nio.charset.StandardCharsets;

public class NGNickMessage extends NGMessage {
	private byte longitud;
	private String nick;

	public NGNickMessage(byte opcode, String nick) {
		super();
		NGMessage.opcode = opcode;
		this.longitud = (byte) nick.length();
		this.nick = nick;
	}

	@Override
	public byte[] toByteArray() {
		ByteBuffer mensaje = ByteBuffer.allocate(2 + longitud);
		byte[] bnick = nick.getBytes(); 
		mensaje.put((byte) opcode);
		mensaje.put(longitud);
		mensaje.put(bnick);
		byte[] men = mensaje.array();
		return (men);
	}

	public static NGNickMessage readFromIS(byte code, DataInputStream dis) {// TODO comprobar que esto tira
		try {
			opcode = code;
			byte longitud = dis.readByte();
			byte[] bytesnick = new byte[longitud]; 
			dis.read(bytesnick);
			String nick = new String (bytesnick);
			return (new NGNickMessage(opcode, nick));
		} 	catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getNick() {
		return nick;
	}
}
