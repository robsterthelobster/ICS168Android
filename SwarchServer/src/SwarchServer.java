import java.io.IOException;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import java.sql.DriverManager;
//import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SwarchServer extends Listener {

	static Server server;
	static final int port = 8080;

	public static void main(String[] args) throws IOException,
			ClassNotFoundException {

		server = new Server();
		// register packets under here
		server.getKryo().register(Packet.class);
		server.bind(port);
		server.start();

		server.addListener(new SwarchServer());
		
		System.out.println("Server started");

		java.sql.Connection connection = null;
		try {
			// create a database connection
			connection = DriverManager.getConnection("jdbc:sqlite:sample.db");
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30); // set timeout to 30 sec.

			//statement.executeUpdate("drop table if exists person");
			//statement.executeUpdate("create table person (id integer, name string)");
			//statement.executeUpdate("insert into person values(1, 'leo')");
			//statement.executeUpdate("insert into person values(2, 'yui')");
			//ResultSet rs = statement.executeQuery("select * from person");
			//while (rs.next()) {
				// read the result set
				//System.out.println("name = " + rs.getString("name"));
				//System.out.println("id = " + rs.getInt("id"));
			//}
		} catch (SQLException e) {
			// if the error message is "out of memory",
			// it probably means no database file is found
			System.err.println(e.getMessage());
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

	public void connected(Connection c) {
		System.out.println("Connection received.");
	}

	public void received(Connection c, Object o) {
	}

	public void disconnected(Connection c) {
		System.out.println("client disconnected");
	}
}
