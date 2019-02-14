package chatsafety;

import java.awt.List;
import java.io.BufferedInputStream;

import java.io.DataInputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

public class ChatServer5 implements Runnable{
//	private Socket socket = null;
	private ServerSocket server = null;
//	private DataInputStream strIn = null;
	private Thread thread = null;
	ChatServerThread5 client = null;
	
	private ChatServerThread5 [] clients = new ChatServerThread5[50];
	private int clientCount = 0;
	
	public ChatServer5(int port) {
		try {
			server = new ServerSocket(port);
			System.out.println("Will start server on port "+port); 
 
			start();

		} catch (IOException e) {
			System.err.println("My Server does not work " + e.getMessage());
		}
	}

	public void start() {
		if(thread == null) {
			thread = new Thread(this);
			thread.start();
		}
	}
	
	@Override
	public void run() {
		while(thread!=null) {
			try {
				System.out.println("Will wait for a client to connect"); 
				//add a thread and accept a client on it... ChatServerThread
				addThread(server.accept());	
			}
			catch (IOException e) {
				System.err.println("Yikess!!! "+e.getMessage());
			}
		}
	}
	
	public synchronized void handle(int ID, String msg){//introduced in v4
		//msg="pm_to:52341:Hi this is our secret"
		//if private send only to the recipient... example 52341
		//private encr send only to the recipient... with the encr marker
		
		//if you want to spy you can steal the encr algorithm/key and although
		//you send the encr.. you can print the decrypted for example
		
		//all other messages (non-private) just send to all
		for(int i=0; i<clientCount; i++){
			if(clients[i].getID() == ID){
				if(msg.equalsIgnoreCase("bye")){
					remove(ID);//disconnect the client.. remove from the array
				}
				else{
					clients[i].send("YOU SAID: "+msg);
				}
			}
			else{
				System.out.println("will send to "+ clients[i].getID() + " with msg= "+msg);
				clients[i].send("User: "+ID+" said: "+ msg);
			}
		}
	}
	

	public synchronized void remove(int ID) {
		int loc = findClient(ID);
		if(loc >= 0 && loc < clientCount) {
			ChatServerThread5 tempToClose = clients[loc];
			for(int i=loc+1; i<clientCount && i<clients.length-1; i++) {
				clients[i-1] = clients[i];
			}
			if (loc == clients.length-1) {
				clients[loc] = null;
			}
			clientCount--;
			System.out.println("removed "+ID+" from index location "+loc);
			try {
				tempToClose.close();
				System.out.println("Closed streams on "+tempToClose.getId() );
			} catch (IOException e) {
				System.out.println("Problem removing client "+e.getMessage());
			}
			
		}
	}
	private synchronized int findClient(int ID) { //SEQUENTIAL LINEAR SEARCH ! MIGHT BE IN TEST
		for(int i=0; i<clientCount; i++) {
			if(clients[i].getId() == ID) {
				return i; //Location of the client
			}
		}
		return -1; //CLIENT NOT IN THE ARRAY
		
	}
	
	public synchronized void addThread(Socket socket){
		if(clientCount < clients.length) {

			clients[clientCount] = new ChatServerThread5(this, socket);
		try {
			clients[clientCount].open();
			clients[clientCount].start();
			clientCount++;
			} 
		catch (IOException e) {
			System.err.println("Exception inside AddThread of ChatServer"+ e.getMessage());
		}
		}
		else {
			System.out.println("Client refused, max num of clients is "+clients.length);
		}
	}
	
	
	public static void main(String [] args){
		if(args.length != 1) {
			System.out.println("You need a port number to run your server");
		}
		else {
			int port = Integer.parseInt(args[0]); // get arg array, and start at index 0 to make it into integer
			ChatServer5 myServer = new ChatServer5(port);
		}
	}
	
	
}
