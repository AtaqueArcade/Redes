package es.um.redes.nanoGames.message;

import java.io.DataInputStream;
import java.nio.ByteBuffer;

public class NGNickDuplicateMessage extends NGMessage{
//No usada
	public NGNickDuplicateMessage (byte opcode) {
		NGMessage.opcode = opcode;
	}

	@Override
	public byte[] toByteArray() {
		ByteBuffer mensaje = ByteBuffer.allocate(1);
		mensaje.put((byte) opcode);
		byte[] men = mensaje.array(); // Obtiene todo el mensaje como byte[]
		return (men);
	}

	public static NGNickDuplicateMessage readFromIS(byte code, DataInputStream dis) {
		opcode = code;
		return (new NGNickDuplicateMessage(opcode));
	}

}
