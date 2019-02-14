package chatsafety;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

public class ChatClient5 implements Runnable {

	private Socket socket = null;
	private BufferedReader console = null;		
	private DataOutputStream strOut = null;
	private ChatClientThread client = null;
	private Thread thread = null;
	private String line = "";
	private boolean done = false;
	private static final String ENC_MARKER = "enc_xyz";
	
	
	public ChatClient5(String serverName, int serverPort) {
		
		//step 1 connect to server using Socket
		try {
			socket = new Socket(serverName, serverPort);
			
			start();//step 2 open streams
			communicate();//step 3 communicate
			
			
			//stop(); //step 4 close streams and sockets
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	
	}
	public void start() throws IOException{
		//step 2 open streams
		console = new BufferedReader(new InputStreamReader(System.in));
		
		//sending data out and getting it on the DataOutput
		strOut = new DataOutputStream(socket.getOutputStream()); 
		
		if (thread == null) {
			client = new ChatClientThread(this, socket);
			thread = new Thread(this);
			thread.start();
		}
	}
	
	@Override
	public void run() {
		while ( (thread != null) ) {
			String line = "";
			try {
				communicate();
			}catch(IOException e) {
				System.out.println("Chat Client IO problem running thread"
						+ "to read line and send it");
			}
		}
		
	}
	public void communicate() throws IOException{
		//step 3 communicate
//		String line = "";
		do {
			line = console.readLine(); //Read the line!
			// msg="pm_to: 52341: Hi this is our secret" // simple private message example
			//encrypt the message, mark it, store it in the line variable and send it
			
			
			strOut.writeUTF(line); //Write it out!
			strOut.flush(); // sends it out right away!
			
		}while(!line.equalsIgnoreCase("bye"));
	}
	public void handle(String msg){
		if (msg.contains(ENC_MARKER)) {
			//we know we need to decrypt
		}
//		if(msg.equalsIgnoreCase("bye")){
//			line="bye";
//			stop();
//		}
		else{
			System.out.println(msg);
		}
		
	}

		
	public void stop() {
		done=true;
		if(thread!=null) {
			thread=null;
		}
		try{
			if(console !=null){
				console.close();
			}
			if(strOut !=null){
				strOut.close();
			}
			if(socket !=null){
				socket.close();
			}
			client.close();
		}
		catch(IOException e){
			System.out.println("problem inside stop of "
					+ "ChatClient " + e.getMessage());
		}
	}
	
	public static void main(String [] args) {
//		ChatClient myClient = new ChatClient("localhost", 8080); <-- A way to connect
		if(args.length != 2) {
			System.out.println("You need a hostname and a port number to connect your client to a server");
		}
		else {
			String serverName = args[0];
			int port = Integer.parseInt(args[1]); // get arg array, and start at index 0 to make it into integer
			ChatClient5 client = new ChatClient5(serverName, port);
		}
		
	}
	private class OneTimePad {

		private String plainMessage = "";
		private String encrMessage = "";
		private String keyMessage = "";
		

		public OneTimePad(){
			
		}
		public OneTimePad(String msg){
			plainMessage = msg;
			keyMessage = getKey();
			encrMessage = encrypt();
		}
		
		protected String getKey(){
			String key = "";
			for(int i=0; i<plainMessage.length(); i++){
				char randomChar = Character.toChars( 7 + (int)(Math.random() * 50))[0];
				key += randomChar;
			}
			return key;
		}
		
		protected String encrypt(){
			String encryptedMessage = "";
			for(int i=0; i<plainMessage.length(); i++){
				encryptedMessage += 
						Character.toChars((keyMessage.charAt(i) + plainMessage.charAt(i)))[0];
				//System.out.println("heheeh encrypted message is "+encryptedMessage);
			}
			return encryptedMessage;
		}
		protected String decrypt(){
			String decryptedMessage = "";
			for(int i=0; i<encrMessage.length(); i++){
				decryptedMessage += 
						Character.toChars((encrMessage.charAt(i)  -  keyMessage.charAt(i)))[0];
				//System.out.println("heheeh decrypted message is "+decryptedMessage);
			}
			return decryptedMessage;
		}
		
		
		
//		public static void main(String [] args){
//			OneTimePad otp = new OneTimePad("abcdefghijklmnopqrstuvwxyz");
//			System.out.println("THE ENC MESSAGE IS "+ otp.encrMessage);
//			System.out.println("THE KEY  IS "+ otp.keyMessage);
//			System.out.println("THE DEC MESSAGE IS "+ otp.plainMessage);
//			
//			
//			
//		}
		
		
		
		
		
		
		
		
		
	}

	
	
}
