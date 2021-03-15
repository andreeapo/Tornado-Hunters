package ca.customtattoodesign.mobilecrm.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Set;

import ca.customtattoodesign.mobilecrm.beans.LoginUser;

public class TornadoHuntersDao {
	
	private static String templateURL = "jdbc:postgresql://%s:%d/%s";
	
	private static String host = "";
	private static String database = "";
	private static String user = "";
	private static int port = -1;
	private static String password = "";
	
	private static String URL = "";
	
	private static TornadoHuntersDao daoObject = null;
	
	public static TornadoHuntersDao getInstance() throws ClassNotFoundException, NullPointerException, SecurityException, NumberFormatException{
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
	
	private TornadoHuntersDao() throws ClassNotFoundException {
		Class.forName("org.postgresql.Driver");
	}
	
	private Connection getConnection() throws SQLException {
		Properties properties = new Properties();
		properties.setProperty("user", user);
		properties.setProperty("password", password);
		properties.setProperty("ssl", "false");
		Connection conn = DriverManager.getConnection(URL, properties);
		return conn;
	}
	
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
