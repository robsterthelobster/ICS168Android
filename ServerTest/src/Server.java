import java.io.*;
import java.net.*;

public class Server {
	public static void main(String[] args) {
	  
		int serverPort = 8888;
		ServerSocket serverSocket = null;
		ObjectOutputStream toClient = null;
		ObjectInputStream fromClient = null;
		try {
			serverSocket = new ServerSocket(serverPort);
			while(true) {
				Socket socket = serverSocket.accept();
				System.out.println("Just connected to " + 
					socket.getRemoteSocketAddress());
				toClient = new ObjectOutputStream(
					new BufferedOutputStream(socket.getOutputStream()));
				fromClient = new ObjectInputStream(
					new BufferedInputStream(socket.getInputStream()));
				Message msgRequest = (Message) fromClient.readObject();
				int number = msgRequest.number;
				toClient.writeObject(new Message(number*number));
				toClient.flush();
			}
		}
		catch(IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		catch(ClassNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}