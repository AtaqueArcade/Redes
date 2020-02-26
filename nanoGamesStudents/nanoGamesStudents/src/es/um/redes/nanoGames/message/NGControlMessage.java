package es.um.redes.nanoGames.message;

//import java.io.DataInputStream;
import java.nio.ByteBuffer;

public class NGControlMessage extends NGMessage {

	public NGControlMessage(byte code) {
		opcode = code;
	}

	@Override
	public byte[] toByteArray() {
		ByteBuffer mensaje = ByteBuffer.allocate(1);
		mensaje.put((byte) opcode); // Inserta un campo de 1 byte (opcode es
									// byte)
		byte[] men = mensaje.array(); // Obtiene todo el mensaje como byte[]
		return (men);
	}


}
