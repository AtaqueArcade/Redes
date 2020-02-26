package es.um.redes.nanoGames.message;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class NGEnterMessage extends NGMessage{
	private int room;
	public NGEnterMessage(byte opcode, int room) {
		super();
		NGMessage.opcode = opcode;
		this.room = room;
	}
	

	@Override
	public byte[] toByteArray() {
		byte broom = (byte) room;
		ByteBuffer mensaje = ByteBuffer.allocate(2);
		mensaje.put((byte) opcode);
		mensaje.put(broom);
		byte[] men = mensaje.array();
		return (men);
	}
	
	public static NGEnterMessage readFromIS(byte code, DataInputStream dis) {// TODO comprobar que esto tira
		try {
			opcode = code;
			int room = dis.read();
			return (new NGEnterMessage(opcode, room));
		} 	catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	public int getRoomEnter() {
		return room;
	}

}
