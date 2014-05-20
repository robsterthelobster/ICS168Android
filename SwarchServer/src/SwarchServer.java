import java.io.IOException;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.example.game.network.*;

public class SwarchServer extends Listener {

	static Server server;
	private static DatabaseManager dbm;

	public static void main(String[] args) throws IOException, ClassNotFoundException {
		
		String dbName = "jdbc:sqlite:Swarch.db";

		dbm = new DatabaseManager(dbName);
		
		server = new Server();
		// register packets under here
		Network.register(server);
		server.bind(Network.PORT);
		server.start();

		server.addListener(new SwarchServer());

		System.out.println("Server started");
	}

	public void connected(Connection c) {
		System.out.println("Connection received.");
	}

	public void received(Connection c, Object o) {

		System.out.println("packet recieved");

		if (o instanceof LoginPacket) {
			LoginPacket packet = (LoginPacket) o;
			System.out.println("username: " + packet.username);
			System.out.println("password: " + packet.password);
			
			// send true to start
			// false to prompt wrong credentials
			StartPacket p = new StartPacket();
			// if login successful
			if(dbm.loginUser(packet.username, packet.password))
				p.start = true;
			else
				p.start = false;
			server.sendToTCP(c.getID(), p);
		}

	}

	public void disconnected(Connection c) {
		System.out.println("client disconnected");
	}
}
