package ex02_RMI;


import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {
	
	/* MAIN IS THE LAUNCHER */
	public static void main (String [] args) throws Exception {
		
		Registry registry = LocateRegistry.createRegistry(1999);
		registry.bind("GUESS THE NUMBER SERVER", new GuessGameObjectImpl());
		
		System.out.println("GUESS THE NUMBER server created and listening to port 1999");
	}
	
}
