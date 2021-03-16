package ca.customtattoodesign.mobilecrm.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import ca.customtattoodesign.mobilecrm.beans.LoginUser;

/**
 * The {@code TornadoHuntersDao} class is a singleton data access class for 
 * 		the database used by our team Tornado Hunters. All database interactions
 * 		happen within this class.
 * 
 * @author Roman Krutikov
 *
 */
public class TornadoHuntersDao {
	
	private static String templateURL = "jdbc:postgresql://%s:%d/%s";
	
	private static String host = "";
	private static String database = "";
	private static String user = "";
	private static int port = -1;
	private static String password = "";
	
	private static String URL = "";
	
	private static TornadoHuntersDao daoObject = null;
	
	/**
	 * For returning an instance of the database access object.
	 * 
	 * @return The singleton instance of the TornadoHuntersDao object
	 * 
	 * @throws ClassNotFoundException when the database driver is not found
	 * @throws NullPointerException when required user environment variables are not set
	 * @throws SecurityException when java is unable to access the user environment variables
	 * @throws NumberFormatException when certain environment variables that should be integers are
	 * 		set as non-integer values
	 */
	public static TornadoHuntersDao getInstance() throws ClassNotFoundException, 
			NullPointerException, SecurityException, NumberFormatException{
		if (daoObject == null) {
			daoObject = new TornadoHuntersDao();
			
			String envUser = System.getenv("capDBusername");
			String envPassword = System.getenv("capDBpassword");
			String envHost = System.getenv("capDBhost");
			String envDatabase = System.getenv("capDBdatabase");
			String envPort = System.getenv("capDBport");
			
			if (envUser == null) {
				throw new NullPointerException("Database 'username' not set in user environment variables...");
			}
			if (envPassword == null) {
				throw new NullPointerException("Database 'password' not set in user environment variables...");
			}
			if (envHost == null) {
				throw new NullPointerException("Database 'host' not set in user environment variables...");
			}
			if (envDatabase == null) {
				throw new NullPointerException("Database 'database' not set in user environment variables...");
			}
			
			int possiblePort = -1;
			if (envPort == null) {
				throw new NullPointerException("Database 'port' not set in user environment variables...");
			}
			try {
				possiblePort = Integer.parseInt(envPort);
			}
			catch (NumberFormatException e) {}
			
			user = envUser;
			password = envPassword;
			host = envHost;
			database = envDatabase;
			port = possiblePort;
			
			URL = String.format(templateURL, host, port, database);
			
			if (port == -1) {
				throw new NumberFormatException("Database 'port' in environment variables is not a valid number...");
			}

		}
		return daoObject;
	}
	
	/**
	 * Sets the database driver.
	 * 
	 * @throws ClassNotFoundException when the database driver is not found
	 */
	private TornadoHuntersDao() throws ClassNotFoundException {
		Class.forName("org.postgresql.Driver");
	}
	
	/**
	 * For generating a Connection object which can be used for database commands. The connection
	 * 		object should be closed after use to avoid errors!
	 * 
	 * @return a Connection object which can be used for database commands
	 * @throws SQLException if the connection to the database failed
	 */
	private Connection getConnection() throws SQLException {
		Properties properties = new Properties();
		properties.setProperty("user", user);
		properties.setProperty("password", password);
		properties.setProperty("ssl", "false");
		Connection conn = DriverManager.getConnection(URL, properties);
		return conn;
	}
	
	/**
	 * This method checks whether the user exists within the database of users and returns
	 * 		the result of the operation.
	 * 
	 * @param user a LoginUser object which will have some of its parameters checked against the database
	 * @return {@code true} if the user exists in the database <br>
			   {@code false} if the user does not exist in the database
	 * @throws SQLException if the connection to the database failed, or if the SQL command(s) within the method failed
	 */
	public boolean isUserAuthorized(LoginUser user) throws SQLException {
		boolean isUserAuthenticated = false;
		
		Connection conn = this.getConnection();
		PreparedStatement prep = conn.prepareStatement("SELECT * FROM \"users\" WHERE (\"email\" = ? AND \"encrypted_password\" = ?)");
		prep.setString(1, user.getUsername());
		prep.setString(2, user.getPassword());
		
		ResultSet results = prep.executeQuery();
		
		if (results.next()) {
			isUserAuthenticated = true;
		}
		
		if (conn != null) {
			conn.close();
		}
		
		return isUserAuthenticated;
	}
	
	/**
	 * Sets a generated session token to a user in the database.
	 *  
	 * @param user the LoginUser to which the session token is being assigned to
	 * @param sessionToken a previously generated String token to save to the database
	 * @return {@code true} if the user's session was saved successfully into the database<br>
			   {@code false} if the user's session was not saved successfully into the database
	 * @throws SQLException if the connection to the database failed, or if the SQL command(s) within the method failed
	 */
	public boolean setUserSessionToken(LoginUser user, String sessionToken) throws SQLException {
		Connection conn = this.getConnection();
		PreparedStatement prep = conn.prepareStatement("UPDATE \"users\" SET \"session_token\" = ? WHERE (\"email\" = ? AND \"encrypted_password\" = ?)");
		prep.setString(1, sessionToken);
		prep.setString(2, user.getUsername());
		prep.setString(3, user.getPassword());
		
		int rowsUpdated = prep.executeUpdate();
		boolean wasSuccessful = rowsUpdated==1;
		
		return wasSuccessful;
	}

}
