import java.io.IOException;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.example.game.network.LoginPacket;
import com.example.game.network.Network;

import java.sql.DriverManager;
//import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SwarchServer extends Listener {

	static Server server;

	static java.sql.Connection connection = null;
	static Statement statement;

	public static void main(String[] args) throws IOException,
			ClassNotFoundException {

		server = new Server();
		// register packets under here
		Network.register(server);
		server.bind(Network.PORT);
		server.start();

		server.addListener(new SwarchServer());

		System.out.println("Server started");

		try {
			// create a database connection
			connection = DriverManager.getConnection("jdbc:sqlite:sample.db");
			statement = connection.createStatement();
			statement.setQueryTimeout(30); // set timeout to 30 sec.

			statement.executeUpdate("drop table if exists person");
			statement.executeUpdate("create table person (username string, password string)");
			
			System.out.println("database initiated");
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		} finally {
			try {
				if (connection != null)
					connection.close();
				System.out.println("closed");
			} catch (SQLException e) {
				// connection close failed.
				System.err.println(e);
			}
		}
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
		}
		
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:sample.db");
			statement = connection.createStatement();
			statement.setQueryTimeout(30);
			
			if (o instanceof LoginPacket) {
				LoginPacket packet = (LoginPacket) o;
//				System.out.println("username: " + packet.username);
//				System.out.println("password: " + packet.password);
				 String sql = "insert into person values('" + packet.username +
				 "', '" + packet.password + "')";
				 System.out.println(sql);
				 statement.executeUpdate(sql);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (connection != null)
					connection.close();
			} catch (SQLException e) {
				// connection close failed.
				System.err.println(e);
			}
		}
	}

	public void disconnected(Connection c) {
		System.out.println("client disconnected");
	}
}
