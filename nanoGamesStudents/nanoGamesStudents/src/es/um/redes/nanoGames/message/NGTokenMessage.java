package es.um.redes.nanoGames.message;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class NGTokenMessage extends NGMessage {
	private static long token;

	public NGTokenMessage(byte opcode, long token) {
		super();
		NGMessage.opcode = opcode;
		NGTokenMessage.token = token;
	}

	public byte[] toByteArray() {
		ByteBuffer mensaje = ByteBuffer.allocate(9);
		mensaje.put((byte) opcode); // Inserta un campo de 1 byte (opcode es
									// byte)
		mensaje.putLong(token); // Inserta un campo de 8 bytes (token es long)
		byte[] men = mensaje.array(); // Obtiene todo el mensaje como byte[]
		return (men);
	}

	public static NGTokenMessage readFromIS(byte code, DataInputStream dis) {
		try {
			opcode = code;
			token = dis.readLong();
			return (new NGTokenMessage(opcode, token));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	public long getToken() {
		return token;
	}

}
