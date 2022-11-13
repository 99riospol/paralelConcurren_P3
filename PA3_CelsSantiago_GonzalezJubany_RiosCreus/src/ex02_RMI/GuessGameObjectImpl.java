package ex02_RMI;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.*;
import java.util.*;

public class GuessGameObjectImpl extends UnicastRemoteObject implements GuessGameObject{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	
	final int INTERVAL_MIN = 1;
	final int INTERVAL_MAX = 999;
	final String EQUAL = "EQUAL";
	final String HIGHER = "HIGHER";
	final String LOWER = "LOWER";
	
	private Random r = new Random();
	
	Map <Integer,ClientRep> marker = new TreeMap<Integer,ClientRep>();
	
	protected GuessGameObjectImpl() throws RemoteException {
		this.id = 0;
	}

	// launcher
	public static void main (String [] args)  {
		try {
			Registry registry = LocateRegistry.createRegistry(1999);
			registry.bind("GUESS", new GuessGameObjectImpl());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Guess service bound and running");
	}

	@Override
	public int startGame() throws RemoteException {
		// TODO Auto-generated method stub
		
		//int newId;
		ClientRep client = new ClientRep();
		client.theNumber = r.nextInt((INTERVAL_MAX+1 - INTERVAL_MIN) + INTERVAL_MIN);

		id = marker.size()+1;
		marker.put(this.id, client);
		return this.id;
		
		
	}

	@Override
	public String check(int id, int number) throws RemoteException {
		// TODO Auto-generated method stub
		ClientRep client = marker.get(id);
		int value = client.theNumber;
		
		client.attempts+=1;
		
		if(value==number) {
			client.justGuessed = true;
			client.guessed+=1;
			return EQUAL;
		}else if(value>number){
			return HIGHER;
		}else {
			return LOWER;
		}
		
	}

	@Override
	public String reset(int id) throws RemoteException {
		// TODO Auto-generated method stub
		ClientRep client = marker.get(id);
		int alea = r.nextInt((INTERVAL_MAX+1 - INTERVAL_MIN) + INTERVAL_MIN);
		if (client != null) {
			client.theNumber= alea;
		}else {
			client = new ClientRep();
			client.theNumber=alea;
			marker.put(id, client);
		}
		System.out.println(marker.get(id).theNumber);
		return "OK RESET";
	}

	@Override
	public String terminate(int id) throws RemoteException {
		// TODO Auto-generated method stub
		ClientRep client = marker.remove(id);
		return "GOODBYE Numbers guessed: "+client.guessed+" Attempts: "+client.attempts;
	}
	
}

// utility class to represent clients (stores all relevant info regarding a client)
class ClientRep {
	boolean justGuessed = false;
	int theNumber;
	int attempts = 0;
	int guessed = 0;
}
