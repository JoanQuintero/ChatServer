package chatsafety;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ChatServerThread5 extends Thread{

	private ChatServer5 server = null;
	private Socket socket = null;
	private DataInputStream strIn = null;
	private DataOutputStream strOut = null;
	private int ID = -1;
	
	
	public ChatServerThread5(ChatServer5 _server, Socket _socket) {
		super();
		server = _server;
		socket = _socket;
		ID = socket.getPort(); //becomes client's ID
		System.out.println("Info: Server= "+server+" Socket= "+socket+ " ID="+ID);
		
	}

	protected int getID() {
		return ID;
	}
	public void run() {
		try {
			while(ID != -1){
//				getInput();
				// msg="pm_to: 52341: Hi this is our secret"
				
				server.handle(ID, strIn.readUTF()); // get input and send out to clients using the server's handle method
//				close();
			}
		}catch(IOException e) {
			System.out.println("Exception running ChatServerThread "+e.getMessage());
		}
	}
	
	public void getInput() {
		boolean done = false; 
		try {
			
		
			do {
				String line = strIn.readUTF();
				System.out.println("user: " + socket.getPort() +" said: " + line);
				if (line.equalsIgnoreCase("bye")) {
					done = true;
				}
				
				//else if(line.contentEquals(clients[i].getID())) {	
				//}
				
			}while(!done);

		
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void open() throws IOException{ 
		strIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
		strOut = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
		
		System.out.println("Opened Input & Output Streams Successfully");
	}
	
	public void send(String msg) {
		try {
			strOut.writeUTF(msg);
			strOut.flush();
	} catch (IOException e) {
			e.printStackTrace();
			server.remove(ID);
			ID = -1;
	}
		
	}

	public void close() throws IOException{
		if(strIn!=null) {
			strIn.close();
			
		}
		if(strOut != null) {
			strOut.close();
		}
		if(socket!=null) {
			socket.close();
		}
	}
	
}

