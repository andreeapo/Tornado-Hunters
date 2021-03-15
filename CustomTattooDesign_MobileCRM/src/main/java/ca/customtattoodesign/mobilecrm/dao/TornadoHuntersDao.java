package ca.customtattoodesign.mobilecrm.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Set;

import ca.customtattoodesign.mobilecrm.beans.LoginUser;

public class TornadoHuntersDao {

	
	private static String templateURL = "jdbc:postgresql://%s:%d/%s";
	
	private static String host = "ec2-18-204-101-137.compute-1.amazonaws.com";
	private static String database = "dfqkli9qkim5un";
	private static String user = "";
	private static int port = 5432;
	private static String password = "";
	
	private static String URL = String.format(templateURL, host, port, database);
	
	private static TornadoHuntersDao daoObject = null;
	
	public static TornadoHuntersDao getInstance() throws ClassNotFoundException, NullPointerException, SecurityException{
		if (daoObject == null) {
			daoObject = new TornadoHuntersDao();
			
			// "Load" environment variables, this line may or may not help them load better
			System.getenv();
			
			String envUser = System.getenv("capDBusername");
			String envPassword = System.getenv("capDBpassword");
			
			if (envUser == null) {
				throw new NullPointerException("Database username not set in user environment variables...");
			}
			if (envPassword == null) {
				throw new NullPointerException("Database password not set in user environment variables...");
			}
			
			user = envUser;
			password = envPassword;

		}
		return daoObject;
	}
	
	private TornadoHuntersDao() throws ClassNotFoundException {
		Class.forName("org.postgresql.Driver");
	}
	
	private Connection getConnection() throws SQLException {
		Properties props = new Properties();
		props.setProperty("user", user);
		props.setProperty("password", password);
		props.setProperty("ssl", "false");
		Connection conn = DriverManager.getConnection(URL, props);
		return conn;
	}
	
	public void testQuery() throws SQLException {
		
		Connection conn = this.getConnection();
		PreparedStatement prep = conn.prepareStatement("SELECT * FROM \"Test\"");
		ResultSet results = prep.executeQuery();
		
		while (results.next()) {
			System.out.println("Name = " + results.getString("name"));
		}
		
		if (conn != null) {
			conn.close();
		}
		
	}
	
	public boolean isUserAuthenticated(LoginUser user) throws SQLException {
		boolean isUserAuthenticated = false;
		
		Connection conn = this.getConnection();
		PreparedStatement prep = conn.prepareStatement("SELECT * FROM \"Login\" WHERE (USERNAME = ? AND PASSWORD = ?)");
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

}
