package ca.customtattoodesign.mobilecrm.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import ca.customtattoodesign.mobilecrm.beans.UserLogin;
import ca.customtattoodesign.mobilecrm.beans.Job;
import ca.customtattoodesign.mobilecrm.beans.SessionUser;

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
	
	private static TornadoHuntersDao daoObject;
	
	/**
	 * For returning an instance of the database access object.
	 * 
	 * @return The singleton instance of the TornadoHuntersDao object
	 * 
	 * @throws IllegalArgumentException when required user environment variables are not set
	 * @throws SecurityException when java is unable to access the user environment variables
	 * @throws NumberFormatException when certain environment variables that should be integers are
	 * 		set as non-integer values
	 */
	public static TornadoHuntersDao getInstance() throws IllegalArgumentException, SecurityException {
		if (daoObject == null) {
			daoObject = new TornadoHuntersDao();
			
			String envUser = System.getenv("capDBusername");
			String envPassword = System.getenv("capDBpassword");
			String envHost = System.getenv("capDBhost");
			String envDatabase = System.getenv("capDBdatabase");
			String envPort = System.getenv("capDBport");
			
			if (envUser == null) {
				throw new IllegalArgumentException("Database 'username' not set in user environment variables...");
			}
			if (envPassword == null) {
				throw new IllegalArgumentException("Database 'password' not set in user environment variables...");
			}
			if (envHost == null) {
				throw new IllegalArgumentException("Database 'host' not set in user environment variables...");
			}
			if (envDatabase == null) {
				throw new IllegalArgumentException("Database 'database' not set in user environment variables...");
			}
			if (envPort == null) {
				throw new IllegalArgumentException("Database 'port' not set in user environment variables...");
			}
			
			int possiblePort = -1;
			try {
				possiblePort = Integer.parseInt(envPort);
			}
			catch (NumberFormatException e) {}
			
			user = envUser.trim();
			password = envPassword.trim();
			host = envHost.trim();
			database = envDatabase.trim();
			port = possiblePort;
			
			URL = String.format(templateURL, host, port, database);
			
			if (port == -1) {
				throw new NumberFormatException("Database 'port' in environment variables is not a valid number...");
			}

		}
		return daoObject;
	}

	/**
	 * For generating a Connection object which can be used for database commands. The connection
	 * 		object should be closed after use to avoid errors!
	 * 
	 * @return a Connection object which can be used for database commands
	 * @throws SQLException if the connection to the database failed
	 */
	private static Connection getConnection() throws SQLException {
		Properties properties = new Properties();
		properties.setProperty("user", user);
		properties.setProperty("password", password);
		properties.setProperty("ssl", "false");
		return DriverManager.getConnection(URL, properties);
	}
	
	/**
	 * This method checks whether the user exists within the database of users and returns
	 * 		the result of the operation.
	 * 
	 * @param username a String that will be checked against usernames in the database
	 * @param password a String that will be checked against passwords in the database
	 * @return {@code true} if the user exists in the database <br>
			   {@code false} if the user does not exist in the database
	 * @throws SQLException if the connection to the database failed, or if the SQL command(s) within the method failed
	 */
	public boolean isUserAuthorized(String username, String password) throws SQLException {
		boolean isUserAuthenticated = false;
		String sql = "SELECT * FROM \"users\" WHERE (\"email\" = ? AND \"encrypted_password\" = ?)";
		
		try (Connection conn = TornadoHuntersDao.getConnection(); 
				PreparedStatement prep = conn.prepareStatement(sql)) {
			
			prep.setString(1, username);
			prep.setString(2, password);
			
			ResultSet results = prep.executeQuery();
			
			if (results.next()) {
				isUserAuthenticated = true;
			}		
		}
		
		return isUserAuthenticated;
	}
	
	/**
	 * Gets various fields from the database to complete a SessionUser object.
	 *  
	 * @param sessionUser the SessionUser object into which the fields from the database are loaded into
	 * @param username a String that will be checked against usernames in the database
	 * @param password a String that will be checked against passwords in the database
	 * @return {@code true} if the user was found in the database and the fields were fetched successfully<br>
			   {@code false} if the user was NOT found in the database or the fields from the database were not fetched
	 * @throws SQLException if the connection to the database failed, or if the SQL command(s) within the method failed
	 */
	public boolean fetchSessionUserFields(SessionUser sessionUser, String username, String password) throws SQLException {
		boolean updateSuccessful = false;
		String sql = "SELECT * FROM users INNER JOIN user_profiles ON users.id = user_profiles.user_id "
				+ "WHERE (email = ? AND encrypted_password = ?)";
		
		try (Connection conn = TornadoHuntersDao.getConnection(); 
				PreparedStatement prep = conn.prepareStatement(sql)){
			
			prep.setString(1, username);
			prep.setString(2, password);
			
			ResultSet results = prep.executeQuery();
		    
			if (results.next()) {
				
				sessionUser.setFirstName(results.getString("first_name"));
				sessionUser.setLastName(results.getString("last_name"));
				sessionUser.setPaypalEmail(results.getString("paypal_email"));
				sessionUser.setRole(results.getString("role"));
				
				sessionUser.setOverrideJobLimit(results.getInt("override_job_limit"));
				sessionUser.setMaxJobValue(results.getInt("max_job_value"));
				
				sessionUser.setAverageTimeToCompletion(results.getDouble("average_time_to_completion"));
				sessionUser.setAverageTimeToIntroduction(results.getDouble("average_time_to_introduction"));
				
				updateSuccessful = true;
			}
		}
		return updateSuccessful;
	}
	
	/**
	 * Sets a generated session token to a user in the database.
	 *  
	 * @param user the UserLogin to which the session token is being assigned to
	 * @param sessionToken a previously generated String token to save to the database
	 * @return {@code true} if the user's session was saved successfully into the database<br>
			   {@code false} if the user's session was not saved successfully into the database
	 * @throws SQLException if the connection to the database failed, or if the SQL command(s) within the method failed
	 */
	public boolean setUserSessionToken(UserLogin user, String sessionToken) throws SQLException {
		
		boolean wasSuccessful;
		String sql = "UPDATE \"users\" SET \"session_token\" = ? WHERE (\"email\" = ? AND \"encrypted_password\" = ?)";
		try (Connection conn = TornadoHuntersDao.getConnection(); 
				PreparedStatement prep = conn.prepareStatement(sql)) {
			
			prep.setString(1, sessionToken);
			prep.setString(2, user.getUsername());
			prep.setString(3, user.getPassword());
			
			int rowsUpdated = prep.executeUpdate();
			wasSuccessful = rowsUpdated==1;
		}
		
		return wasSuccessful;
	}
	
	
	/**
	 * Gets a list of all unclaimed Jobs from the database/
	 * 
	 * @return List of Jobs that have not been claimed
	 * @throws SQLException if the connection to the database failed, or if the SQL command(s) within the method failed
	 */
	public List<Job> fetchUnclaimedJobs() throws SQLException {
		
		List<Job> jobs = new ArrayList<Job>();
		String sql = "SELECT * from \"jobs\" WHERE \"state\"='queued'";
		try (Connection conn = TornadoHuntersDao.getConnection(); 
				Statement prep = conn.createStatement()) {
			
			ResultSet results = prep.executeQuery(sql);
			
			while (results.next()) {
				
				Job tempJob = new Job();
				tempJob.setJobId(results.getInt("id"));
				tempJob.setStatus(results.getString("state"));
				tempJob.setTitle(results.getString("title"));
				tempJob.setCustomerName(results.getString("customer"));
				tempJob.setTattooLocation(results.getString("tattoo_position"));
				tempJob.setTattooType(results.getString("size"));
				tempJob.setTattooStyle(results.getString("style"));
				tempJob.setColor(results.getBoolean("color"));
				tempJob.setCommission(results.getDouble("commission"));
				tempJob.setDescription(results.getString("description"));
				jobs.add(tempJob);
				
			}
		}
		
		return jobs;
	}

}
