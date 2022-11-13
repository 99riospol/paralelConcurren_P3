package ex01_CS;

import java.io.*;
import java.net.*;
import java.util.Random;

import ex01_CS.Request.Type;

public class Server extends Thread  {
	
	private static ServerSocket serverSocket;
	
	/* MAIN IS THE LAUNCHER */
	public static void main (String [] args) throws IOException {
		serverSocket = new ServerSocket(6666);
		
		for (int i=1;i<=10;i++) {
			(new Server()).start();
		}
		System.out.println("GUESS THE NUMBER server created and listening to port 6666");
	}
	
	private Socket connection;
	private BufferedReader inputChannel;
	private PrintWriter outputChannel;
	
	public void run() {
		try {
			innerRun();
		}
		catch(IOException ioex) {}
	}

	private void innerRun() throws IOException {
		Request request;
		
		int number=0;
		final String EQUAL = "EQUAL";
		final String HIGHER = "HIGHER";
		final String LOWER = "LOWER";
		final String GOODBYE = "GOODBYE";
		int wins=0;
		int attempts=0;
		
		while (true) {
			// accept a new connection
			acceptConnection();
			request = this.receiveRequest();
			number = resetClient(request);
			if (request.type!=Type.TERMINATE) {
				request = this.receiveRequest();
				
				while (request.type!=Type.TERMINATE) {
					if(request.type==Type.RESET) {
						number = resetClient(request);
					}
					if(request.type==Type.CHECK) {
						if(request.value==number) {
							this.sendReply(EQUAL);
							wins++;
						}else if(number>request.value){
							this.sendReply(HIGHER);
						}else {
							this.sendReply(LOWER);
						}
						attempts+=1;
					}
					request = this.receiveRequest();
				}
			}
				
			sendReply(GOODBYE+" Numbers guessed: "+wins+" Attempts: "+attempts+"\n");
			disconnect();
			
		}
		
	}
	
	//UTILITY METHODS.
	
	private int resetClient(Request request) throws IOException{
		final String RESET = "OK RESET";
		final int INTERVAL_MIN = 1;
		final int INTERVAL_MAX = 999;
		int number = 0;
		Random r = new Random();
		if (request.type==Type.RESET) {
			number = r.nextInt((INTERVAL_MAX+1 - INTERVAL_MIN) + INTERVAL_MIN);
			sendReply(RESET);
			return number;
		}else {
			return -1;
		}
	}

	private void acceptConnection () throws IOException {
	     this.connection = serverSocket.accept();
	     this.inputChannel = new BufferedReader(
	                             new InputStreamReader(
	                                 this.connection.getInputStream()));
	     this.outputChannel = new PrintWriter(
	                              this.connection.getOutputStream(), true);
	}
	
	private Request receiveRequest () throws IOException {
        return new Request(this.inputChannel.readLine());
    }
	
	private void sendReply(String reply) throws IOException {
		this.outputChannel.println(reply);
	}
	
	private void disconnect() throws IOException {
		this.connection.close();
		this.inputChannel.close();
		this.outputChannel.close();
	}

}



// utility class. Makes requests out of strings
class Request {
	
	public enum Type {CHECK, RESET, TERMINATE, UNKNOWN};
	
	public int value;
	public Type type;
	public String message;
	
	// make a request object out of a message...
	public Request (String message) {
		this.message = message;
		String [] elements =  message.split(" ");
		if (elements[0].equalsIgnoreCase("check")) {
			try {
				this.value = Integer.parseInt(elements[1]);
				this.type = Type.CHECK;
				return;
			}
			catch (Exception ex) {
				this.type = Type.UNKNOWN;
				return;
			}
		}
		if (elements[0].equalsIgnoreCase("reset")) {
			this.type=Type.RESET;
			return;
		}
		if (elements[0].equalsIgnoreCase("terminate")) {
			this.type = Type.TERMINATE;
			return;
		}
		this.type = Type.UNKNOWN;
	}
	
}
