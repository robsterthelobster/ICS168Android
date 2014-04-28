import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
	public static void main(String[] args) {
		int serverPort = 8888;
		Socket socket = null;
		ObjectOutputStream toServer = null;
		ObjectInputStream fromServer = null;
		try {
			if(args.length != 1) {
				System.out.println("Need 1 argument");
				System.exit(1);
			}
			Scanner reader = new Scanner(System.in);
			System.out.println("Enter the first number");
			//get user input for number
			int number = reader.nextInt(); // = Integer.parseInt(args[0]);
			InetAddress serverHost = InetAddress.getByName("localhost"); 
			System.out.println("Connecting to server on port " + serverPort); 
			socket = new Socket(serverHost,serverPort); 
			System.out.println("Just connected to " + socket.getRemoteSocketAddress()); 
			toServer = new ObjectOutputStream(
					new BufferedOutputStream(socket.getOutputStream()));
			Message msgToSend = new Message(number);
			toServer.writeObject(msgToSend);
			toServer.flush();
			
			// This will block until the corresponding ObjectOutputStream 
			// in the server has written an object and flushed the header
			fromServer = new ObjectInputStream(
					new BufferedInputStream(socket.getInputStream()));
			Message msgFromReply = (Message)fromServer.readObject();
			System.out.println(number + " * " + number + " = " + msgFromReply.number);
		}
		catch(IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		catch(ClassNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		}
		finally {
			if(socket != null) {
				try {
					socket.close();
				}
				catch(IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}