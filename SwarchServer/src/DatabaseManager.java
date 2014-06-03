import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
	
	private Connection connection = null;
	private String database;
	private Statement statement;
	
	public DatabaseManager(String database){
		this.database = database;
	}
	
	public void createNewUser(String username, String password){
		try {
			connection = DriverManager.getConnection(database);
			statement = connection.createStatement();
			statement.setQueryTimeout(30);
			
			String insertSQL = "INSERT INTO user VALUES('" + username + "', '" + password + "', 0)";
			statement.executeUpdate(insertSQL);
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
	
	public boolean loginUser(String username, String password){
		try {
			connection = DriverManager.getConnection(database);
			statement = connection.createStatement();
			statement.setQueryTimeout(30);
			
			//statement.executeUpdate("create table user(username string, password string, highscore integer)");
			
			String passwordSQL = "SELECT * FROM user WHERE username = '" + username + "'";
			ResultSet rs = statement.executeQuery(passwordSQL);
			
			// if the resultset has a next row and that row has a matching username
			if(rs.next() && rs.getString("username").equals(username)){
				if(rs.getString("password").equals(password)){
					System.out.println("Login success!  Welcome " + username);
					return true;
				}
				else{
					System.out.println("Wrong password");
					return false;
				}
			}
			// row is empty aka no such user
			else{
				createNewUser(username, password);
				return true;
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (connection != null)
					connection.close();
			} catch (SQLException e) {
				System.err.println(e);
			}
		}
		return false;
	}
	
	public void updateScore(String name, int score){
		try {
			connection = DriverManager.getConnection(database);
			statement = connection.createStatement();
			statement.setQueryTimeout(30);
			
			String scoreSQL = "SELECT highscore FROM user WHERE username = '" + name + "'";
			ResultSet rs = statement.executeQuery(scoreSQL);
			
			int highscore = 0;
			
			if(rs.next()){
				highscore = rs.getInt("highscore");
			}
			
			if(score > highscore){
				String insertSQL = "Update user SET highscore="+score+" WHERE username = '" + name + "'";
				statement.executeUpdate(insertSQL);
			}
		}catch (SQLException e) {
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
