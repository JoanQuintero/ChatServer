package chatsafety;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class ChatClientThread extends Thread{

	private Socket socket = null;
	private ChatClient5 client = null;
	private DataInputStream strIn = null;
	private Boolean done = false;
	
	public ChatClientThread(ChatClient5 _client, Socket _socket) {
		client = _client;
		socket = _socket;
		open();
		start();
	}
	

	
	public void open() {
		try {
			strIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
		}catch (IOException e) {
			System.out.println("Inside ChatClientThread problem opening");
		}
	}
	
	public void run() {
		//handle IO
		done = false;
		while (!done) {
			
		
		try {
			client.handle(strIn.readUTF());
		}
		catch (IOException e) {
			close();
			client.stop();
			System.out.println("Err in ChatClientThread, "
					+ "couldn't handle input off of strIn");
			done = true;
		}
	}
	}
	
	public void close() {
		try {
			if(strIn != null) {
				strIn.close();
			}
			if(socket !=null) {
				socket.close();
			}
		}catch (IOException e) {
			System.out.println("Problems at closing");
		}
	}
}
