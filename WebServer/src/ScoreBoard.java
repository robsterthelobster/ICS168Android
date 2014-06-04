import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class ScoreBoard {

	public static void main(String[] args) throws Exception {
		HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
		server.createContext("/scores", new MyHandler());
		server.setExecutor(null); // creates a default executor
		server.start();
		System.out.println("Running");
	}

	static class MyHandler implements HttpHandler {
		public void handle(HttpExchange t) throws IOException {
			String dbName = "jdbc:sqlite:D:/Robin/GitHub/ICS168Android/SwarchServer/Swarch.db";
			Connection connection = null;
			Statement statement;

			// System.out.printf("%-12s%s\n","Player","Highscore");

			String score = String.format("%-12s%-12s%s\n", "Rank", "Player", "Highscore") + "\n";
			
			int rank = 1;

			try {
				connection = DriverManager.getConnection(dbName);
				statement = connection.createStatement();
				statement.setQueryTimeout(30);

				String scoreSQL = "SELECT * FROM user ORDER BY highscore DESC LIMIT 20";
				ResultSet rs = statement.executeQuery(scoreSQL);

				while (rs.next()) {
					score += String.format("%-12s%-12s%s\n", ""+rank, rs.getString("username"),
							rs.getInt("highscore"));
					rank++;

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
			// String response = "This is the response";
			t.sendResponseHeaders(200, score.length());
			OutputStream os = t.getResponseBody();
			os.write(score.getBytes());
			os.close();
		}
	}

}