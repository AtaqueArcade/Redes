package es.um.redes.nanoGames.message;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class NGGameMessage extends NGMessage{
	private byte longitud;
	private String rules;
	public NGGameMessage(byte opcode, String rules) {
		super();
		NGMessage.opcode = opcode;
		this.longitud = (byte) rules.length();
		this.rules = rules;
	}
	@Override
	public byte[] toByteArray() {
		ByteBuffer mensaje = ByteBuffer.allocate(2 + longitud);
		byte[] brules = rules.getBytes(); 
		mensaje.put((byte) opcode);
		mensaje.put(longitud);
		mensaje.put(brules);
		byte[] men = mensaje.array();
		return (men);
	}
	
	public static NGGameMessage readFromIS(byte code, DataInputStream dis) {
		try {
			opcode = code;
			byte longitud = dis.readByte();
			byte[] bytesrules = new byte[longitud]; 
			dis.read(bytesrules);
			String rules = new String (bytesrules);
			return new NGGameMessage(opcode, rules);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	public String getGameMessage() {
		return rules;
	}
}

