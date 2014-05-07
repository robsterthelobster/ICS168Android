import java.io.IOException;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.example.game.network.*;

import java.sql.DriverManager;
import java.sql.ResultSet;
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

		// try
		// {
		// // create a database connection
		// connection = DriverManager.getConnection("jdbc:sqlite:sample.db");
		// statement = connection.createStatement();
		// statement.setQueryTimeout(30); // set timeout to 30 sec.
		//
		// statement.executeUpdate("drop table if exists person");
		// statement.executeUpdate("create table person (username string, password string)");
		//
		// System.out.println("database initiated");
		// }
		// catch (SQLException e)
		// {
		// System.err.println(e.getMessage());
		// }
		// finally
		// {
		// if (connection != null)
		// {
		// try
		// {
		// connection.close();
		// System.out.println("closed");
		// }
		// catch (SQLException e)
		// {
		// // connection close failed.
		// System.err.print(e);
		// }
		// }
		// }
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

		if (o instanceof LoginPacket) {
			try {
				connection = DriverManager
						.getConnection("jdbc:sqlite:sample.db");
				statement = connection.createStatement();
				statement.setQueryTimeout(30);

				LoginPacket packet = (LoginPacket) o;

				// Username checking
				// String usernameSQL =
				// "SELECT username FROM person WHERE username =" +
				// packet.username; // NEED TO CHANGE (SECURITY REASONS)
				// System.out.println("usernameSQL: " + usernameSQL);

				boolean inDB = false;
				ResultSet rs = statement
						.executeQuery("SELECT username FROM person");
				while (rs.next()) {

					if (rs.getString("username").equals(packet.username)) {
						System.out.println(rs.getString("username"));
						inDB = true;
					}
				}

				// if username exists in database
				if (inDB) {
					// check its password
					String passwordSQL = "SELECT password FROM person WHERE username = '" 
							+ packet.username + "'"; // NEED TO CHANGE (SECURITY REASONS)
						
					rs = statement.executeQuery(passwordSQL);
					
					while(rs.next()){
						if(rs.getString("password").equals(packet.password)){
							// LOGIN SUCCESS
							System.out.println("Login success!  Welcome "
									+ packet.username);
							// need to change to specific
							StartPacket p = new StartPacket();
							p.start = true;
							server.sendToTCP(c.getID(), p);;
							break;
						}
					}
				}

				// else username isn't in the database, so must insert into table
				else {
					String insertSQL = "INSERT INTO person VALUES('"
							+ packet.username + "', '" + packet.password + "')";

					System.out.println(insertSQL);
					statement.executeUpdate(insertSQL);
					
					StartPacket p = new StartPacket();
					p.start = true;
					server.sendToTCP(c.getID(), p);;
				}

			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					if (connection != null)
						connection.close();
				} catch (SQLException e) {
					System.err.println(e);
				}
			}
		}
	}

	public void disconnected(Connection c) {
		System.out.println("client disconnected");
	}
}
