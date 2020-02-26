package es.um.redes.nanoGames.message;

import java.io.DataInputStream;
import java.nio.ByteBuffer;

	public class NGNickOkMessage extends NGMessage {
		
		public NGNickOkMessage (byte opcode) {
			NGMessage.opcode = opcode;
		}

		@Override
		public byte[] toByteArray() {
			ByteBuffer mensaje = ByteBuffer.allocate(1);
			mensaje.put((byte) opcode);
			byte[] men = mensaje.array(); // Obtiene todo el mensaje como byte[]
			return (men);
		}

		public static NGNickOkMessage readFromIS(byte code, DataInputStream dis) {// TODO comprobar que esto tira
			opcode = code;
			return (new NGNickOkMessage(opcode));
		}

}
