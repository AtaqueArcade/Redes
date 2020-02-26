package es.um.redes.nanoGames.message;

import java.io.DataInputStream;
import java.nio.ByteBuffer;

/*
Include here the specification of this particular message
*/
//TODO

public class NGListMessage extends NGMessage {
	//private byte opcode;
	private String[] items;
	
	// Constructor
	public NGListMessage(byte code, String[] items) {
		NGMessage.opcode = code;
		this.items = items;
	}

	@Override
	public byte[] toByteArray() {
		// TODO Transform the internal representation into a byte array ready to
		// be trasmitted
		byte longitudarray = (byte)items.length; // longitud de mi array 
		int lon = 0;
		for (int i = 0; i < items.length; i++) {
			lon = lon + items[i].length();
		}
		byte longitudcadenas = (byte) lon;
		ByteBuffer mensaje = ByteBuffer.allocate(2 + longitudarray + longitudcadenas);
		mensaje.put((byte)opcode);
		mensaje.put(longitudarray);
		
		for(int i = 0; i< items.length; i++) {
			mensaje.put((byte) items[i].length());
			byte[] description = items[i].getBytes();
			mensaje.put(description);
		}
		byte[] men = mensaje.array();
		return men;
	}

	public static NGListMessage readFromIS(byte code, DataInputStream dis) {
	// TODO Decode the message received from the Input Stream
	try {
		opcode = code;
		byte longitudarray = dis.readByte();
		String[] descriptions = new String[longitudarray];
		for(int i = 0; i <longitudarray; i++) {
			byte longitud = dis.readByte();
			byte[] bytesdescription= new byte[longitud];
			dis.read(bytesdescription);
			String description = new String(bytesdescription);
			descriptions[i] = description;
		}
		return(new NGListMessage(code, descriptions));
	} catch (Exception e) {
		// TODO: handle exception
	}
	return null;
	}

	// TODO Replace this method according to your specific message
	public String[] getItems() {
		return items;
	}
}
