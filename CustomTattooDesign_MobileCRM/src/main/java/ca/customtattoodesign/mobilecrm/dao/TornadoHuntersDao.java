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

import org.postgresql.util.PSQLException;

import ca.customtattoodesign.mobilecrm.beans.BasicUser;
import ca.customtattoodesign.mobilecrm.beans.DesignImage;
import ca.customtattoodesign.mobilecrm.beans.DesignRequest;
import ca.customtattoodesign.mobilecrm.beans.Job;
import ca.customtattoodesign.mobilecrm.beans.Message;
import ca.customtattoodesign.mobilecrm.beans.User;

/**
 * The {@code TornadoHuntersDao} class is a singleton data access class for the
 * database used by our team Tornado Hunters. All database interactions happen
 * within this class.
 * 
 * @author Roman Krutikov
 * @author Thomas Chapman
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
	 * @throws IllegalArgumentException when required user environment variables are
	 *                                  not set
	 * @throws SecurityException        when java is unable to access the user
	 *                                  environment variables
	 * @throws NumberFormatException    when certain environment variables that
	 *                                  should be integers are set as non-integer
	 *                                  values
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
			} catch (NumberFormatException e) {
			}

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
	 * For generating a Connection object which can be used for database commands.
	 * The connection object should be closed after use to avoid errors!
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
	 * This method checks whether the user exists within the table of users and
	 * returns the result of the operation.
	 * 
	 * @param username a String that will be checked against usernames in the
	 *                 database
	 * @param password a String that will be checked against passwords in the
	 *                 database
	 * @return {@code true} if the user exists in the database <br>
	 *         {@code false} if the user does not exist in the database
	 * @throws SQLException if the connection to the database failed, or if the SQL
	 *                      command(s) within the method failed
	 */
	public boolean isUserLoginAuthorized(String username, String password) throws SQLException {
		boolean isUserAuthenticated = false;
		String sql = "SELECT * FROM \"users\" WHERE (\"email\" = ? AND \"encrypted_password\" = ?)";

		try (Connection conn = TornadoHuntersDao.getConnection(); PreparedStatement prep = conn.prepareStatement(sql)) {

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
	 * This method checks whether the session exists within the table of sessions
	 * and returns the result of the operation.
	 * 
	 * @param username     a String that will be checked against usernames in the
	 *                     database
	 * @param sessionToken a String that will be checked against session tokens in
	 *                     the database
	 * @return {@code true} if the user + session token exists in the database <br>
	 *         {@code false} if the user + session token does not exist in the
	 *         database
	 * @throws SQLException if the connection to the database failed, or if the SQL
	 *                      command(s) within the method failed
	 */
	public boolean isSessionLoginAuthorized(String username, String sessionToken) throws SQLException {
		boolean isUserAuthenticated = false;

		String sql = "SELECT valid_token(?,?)";

		try (Connection conn = TornadoHuntersDao.getConnection(); PreparedStatement prep = conn.prepareStatement(sql)) {

			prep.setString(1, username);
			prep.setString(2, sessionToken);

			ResultSet results = prep.executeQuery();

			if (results.next()) {
				isUserAuthenticated = results.getBoolean("valid_token");
			}
		}

		return isUserAuthenticated;
	}

	/**
	 * Gets a user's ID from the database based on their login info.
	 *
	 * @param user     the User object into which the fields from the database are
	 *                 loaded into
	 * @param username a String that will be checked against usernames in the
	 *                 database
	 * @param password a String that will be checked against passwords in the
	 *                 database
	 * @return {@code true} if the user was found in the database and the id was
	 *         fetched successfully<br>
	 *         {@code false} if the user was NOT found in the database or the id was
	 *         not fetched successfully
	 * @throws SQLException if the connection to the database failed, or if the SQL
	 *                      command(s) within the method failed
	 */
	public boolean fetchSessionUserId(User user, String username, String password) throws SQLException {
		boolean fetchSuccessful = false;
		String sql = "SELECT * FROM users WHERE (email = ? AND encrypted_password = ?)";

		try (Connection conn = TornadoHuntersDao.getConnection(); PreparedStatement prep = conn.prepareStatement(sql)) {

			prep.setString(1, username);
			prep.setString(2, password);

			ResultSet results = prep.executeQuery();

			if (results.next()) {

				user.setId(results.getInt("id"));

				fetchSuccessful = true;
			}
		}
		return fetchSuccessful;
	}

	/**
	 * Gets various fields from the database to complete a User object.
	 *
	 * @param user         the User object into which the fields from the database
	 *                     are loaded into
	 * @param username     a String that will be checked against usernames in the
	 *                     database
	 * @param sessionToken a String that will be checked against session tokens in
	 *                     the database
	 * @return {@code true} if the user and session token were found in the database
	 *         and the fields were fetched successfully<br>
	 *         {@code false} if the user or session was NOT found in the database or
	 *         the fields from the database were not fetched
	 * @throws SQLException if the connection to the database failed, or if the SQL
	 *                      command(s) within the method failed
	 */
	public boolean fetchSessionUserFields(User user, String username, String sessionToken) throws SQLException {
		boolean updateSuccessful = false;
		String sql = "SELECT * FROM get_artist_info(?,?)";

		try (Connection conn = TornadoHuntersDao.getConnection(); PreparedStatement prep = conn.prepareStatement(sql)) {

			prep.setString(1, username);
			prep.setString(2, sessionToken);

			ResultSet results = prep.executeQuery();

			if (results.next()) {

				user.setFirstName(results.getString("first_name"));
				user.setLastName(results.getString("last_name"));
				user.setPaypalEmail(results.getString("paypal_email"));
				user.setRole(results.getString("role"));

				user.setOverrideJobLimit(results.getInt("override_job_limit"));
				user.setMaxJobValue(results.getInt("max_job_value"));

				user.setAverageTimeToCompletion(results.getDouble("average_time_to_completion"));
				user.setAverageTimeToIntroduction(results.getDouble("average_time_to_introduction"));

				user.setJobsTakenLast30Days(results.getInt("jobs_taken_last_30_days"));
				user.setJobsTakenLifetime(results.getInt("jobs_taken_lifetime"));
				user.setRefundsLast30Days(results.getInt("refunds_last_30_days"));
				user.setRefundsLifetime(results.getInt("refunds_lifetime"));
				user.setEarningsLifetime(results.getDouble("earnings_lifetime"));

				updateSuccessful = true;
			}
		}
		return updateSuccessful;
	}

	/**
	 * Sets a generated session token to a user in the database.
	 *
	 * @param user         the UserLogin to which the session token is being
	 *                     assigned to
	 * @param sessionToken a previously generated String token to save to the
	 *                     database
	 * @return {@code true} if the user's session was saved successfully into the
	 *         database<br>
	 *         {@code false} if the user's session was not saved successfully into
	 *         the database
	 * @throws SQLException if the connection to the database failed, or if the SQL
	 *                      command(s) within the method failed
	 */
	public boolean setUserSessionToken(int userId, String sessionToken) throws SQLException {

		boolean wasSuccessful;
		String sql = "INSERT INTO \"session_tokens\" (user_id, session_token) VALUES (?, ?)";
		try (Connection conn = TornadoHuntersDao.getConnection(); PreparedStatement prep = conn.prepareStatement(sql)) {

			prep.setInt(1, userId);
			prep.setString(2, sessionToken);

			int rowsUpdated = prep.executeUpdate();
			wasSuccessful = rowsUpdated == 1;
		}

		return wasSuccessful;
	}

	/**
	 * Gets a list of all unclaimed jobs from the database.
	 * 
	 * @return List of Jobs that have not been claimed
	 * @throws SQLException if the connection to the database failed, or if the SQL
	 *                      command(s) within the method failed
	 */
	public List<Job> fetchUnclaimedJobs() throws SQLException {

		List<Job> jobs = new ArrayList<>();
		String sql = "SELECT * from \"jobs\" WHERE \"state\"='queued'";
		try (Connection conn = TornadoHuntersDao.getConnection(); Statement prep = conn.createStatement()) {

			ResultSet results = prep.executeQuery(sql);

			while (results.next()) {

				Job tempJob = new Job();
				tempJob.setJobId(results.getInt("id"));
				tempJob.setArtistId(results.getInt("user_id"));
				tempJob.setState(results.getString("state"));
				tempJob.setTitle(results.getString("title"));
				tempJob.setCustomerName(results.getString("customer"));
				tempJob.setTattooLocation(results.getString("tattoo_position"));
				tempJob.setTattooType(results.getString("size"));
				tempJob.setTattooStyle(results.getString("style"));
				tempJob.setColor(results.getBoolean("color"));
				tempJob.setCommission(results.getDouble("commission"));
				tempJob.setDescription(results.getString("description"));
				tempJob.setMessages(TornadoHuntersDao.getInstance().fetchJobMessages(results.getInt("id")));
				
				jobs.add(tempJob);

			}
		}

		return jobs;
	}

	/**
	 * Gets a list of all artist specific Jobs from the database.
	 * 
	 * @return List of Jobs that have been assigned to an artist
	 * @throws SQLException if the connection to the database failed, or if the SQL
	 *                      command(s) within the method failed
	 */
	public List<Job> fetchArtistJobs(String username, String sessionToken) throws SQLException {

		List<Job> jobs = new ArrayList<>();
		String sql = "SELECT * FROM get_artist_jobs(?,?)";
		try (Connection conn = TornadoHuntersDao.getConnection(); PreparedStatement prep = conn.prepareStatement(sql)) {

			prep.setString(1, username);
			prep.setString(2, sessionToken);

			ResultSet results = prep.executeQuery();

			while (results.next()) {

				Job tempJob = new Job();
				tempJob.setJobId(results.getInt("id"));
				tempJob.setArtistId(results.getInt("user_id"));
				tempJob.setState(results.getString("state"));
				tempJob.setTitle(results.getString("title"));
				tempJob.setCustomerName(results.getString("customer"));
				tempJob.setTattooLocation(results.getString("tattoo_position"));
				tempJob.setTattooType(results.getString("size"));
				tempJob.setTattooStyle(results.getString("style"));
				tempJob.setColor(results.getBoolean("color"));
				tempJob.setCommission(results.getDouble("commission"));
				tempJob.setDescription(results.getString("description"));
				tempJob.setMessages(TornadoHuntersDao.getInstance().fetchJobMessages(results.getInt("id")));
				
				BasicUser tempBasicUser = this.fetchBasicArtistInfo(tempJob.getArtistId());
				if (tempBasicUser != null) {
					tempJob.setArtistFirstName(tempBasicUser.getFirstName());
					tempJob.setArtistLastName(tempBasicUser.getLastName());
				}

				jobs.add(tempJob);

			}
		}

		return jobs;
	}

	/**
	 * Claims a job for the sessionUser and returns if the claiming the job was
	 * successful.
	 * 
	 * @param sessionLogin the Session Login object containing authentication info
	 * @param jobId        ID of the job that the user is trying to claim
	 * @return {@code true} if the claim was successful<br>
	 *         {@code false} if the claim failed
	 * @throws SQLException if the connection to the database failed, or if the SQL
	 *                      command(s) within the method failed
	 */
	public boolean claimJob(int jobId, String username, String sessionToken) throws SQLException {

		boolean claimedSuccessfully = false;

		String sql = "SELECT claim_job(?,?,?)";

		try (Connection conn = TornadoHuntersDao.getConnection(); PreparedStatement prep = conn.prepareStatement(sql)) {

			prep.setString(1, username);
			prep.setString(2, sessionToken);
			prep.setInt(3, jobId);

			ResultSet results = prep.executeQuery();

			if (results.next()) {
				claimedSuccessfully = results.getBoolean("claim_job");
			}
		}

		return claimedSuccessfully;
	}

	/**
	 * Removes a session token from the database.
	 * 
	 * @param userId       the userId of the user whose session token is being
	 *                     deleted
	 * @param sessionToken the SessionToken that needs to be deleted
	 * @return {@code true} if the session token was removed successfully<br>
	 *         {@code false} if session token removal failed
	 * @throws SQLException if the connection to the database failed, or if the SQL
	 *                      command(s) within the method failed
	 */
	public boolean removeSessionToken(int userId, String sessionToken) throws SQLException {

		boolean successfullyRemoved = false;
		String sql = "DELETE FROM session_tokens WHERE user_id = ? AND session_token = ?";

		try (Connection conn = TornadoHuntersDao.getConnection(); PreparedStatement prep = conn.prepareStatement(sql)) {

			prep.setInt(1, userId);
			prep.setString(2, sessionToken);

			int valuesRemoved = prep.executeUpdate();
			successfullyRemoved = valuesRemoved > 0;
		}

		return successfullyRemoved;
	}

	/**
	 * Fetches all messages for a specified job.
	 * 
	 * @param jobId the ID of the job for which we want to fetch messages
	 * @return A list of messages that are associated with the job id
	 * @throws SQLException if the connection to the database failed, or if the SQL
	 *                      command(s) within the method failed
	 */
	public List<Message> fetchJobMessages(int jobId) throws SQLException {

		List<Message> jobMessages = new ArrayList<Message>();
		String sql = "SELECT * FROM fetch_job_messages(?)";

		try (Connection conn = TornadoHuntersDao.getConnection(); PreparedStatement prep = conn.prepareStatement(sql)) {

			prep.setInt(1, jobId);
			ResultSet results = prep.executeQuery();

			while (results.next()) {

				Message tempMessage = new Message();
				tempMessage.setMessageId(results.getInt("id"));
				tempMessage.setDesignId(results.getInt("design_id"));
				tempMessage.setDesignerId(results.getInt("designer_id"));
				tempMessage.setRead(results.getBoolean("has_been_read"));
				tempMessage.setCreatedAt(results.getTimestamp("created_at"));
				tempMessage.setBody(results.getString("body"));
				tempMessage.setCommentPicture(results.getString("comment_picture"));
				tempMessage.setJobId(results.getInt("job_id"));
				jobMessages.add(tempMessage);

			}
		}

		return jobMessages;
	}

	/**
	 * Sends a String message to a job conversation and returns if the message was
	 * sent successfully.
	 * 
	 * @param jobId         the ID of the job to which to send the message to
	 * @param stringMessage the text of the message that is being sent
	 * @param sessionToken  a sessionToken to verify if the artist or the customer
	 *                      are sending the message
	 * @return {@code true} if the String message was sent successfully<br>
	 *         {@code false} if sending the String message failed
	 * @throws SQLException if the connection to the database failed, or if the SQL
	 *                      command(s) within the method failed
	 */
	public boolean sendStringMessage(int jobId, String stringMessage, String sessionToken) throws SQLException {

		boolean successfullySent = false;
		String sql = "SELECT send_string_message (?, ?, ?)";

		try (Connection conn = TornadoHuntersDao.getConnection(); PreparedStatement prep = conn.prepareStatement(sql)) {

			prep.setInt(1, jobId);
			prep.setString(2, stringMessage);
			prep.setString(3, sessionToken);

			ResultSet results = prep.executeQuery();

			if (results.next()) {
				successfullySent = results.getBoolean("send_string_message");
			}
		}

		return successfullySent;
	}

	/**
	 * Fetches all messages for a specified job that have not yet been read by the
	 * other party.
	 * 
	 * @param jobId the ID of the job for which we want to fetch messages
	 * @return A list of unread messages that are associated with the job id
	 * @throws SQLException if the connection to the database failed, or if the SQL
	 *                      command(s) within the method failed
	 */
	public List<Message> fetchUnreadJobMessages(int jobId) throws SQLException {

		List<Message> jobMessages = new ArrayList<Message>();
		String sql = "SELECT * FROM fetch_unread_job_messages(?)";

		try (Connection conn = TornadoHuntersDao.getConnection(); PreparedStatement prep = conn.prepareStatement(sql)) {

			prep.setInt(1, jobId);
			ResultSet results = prep.executeQuery();

			while (results.next()) {

				Message tempMessage = new Message();
				tempMessage.setMessageId(results.getInt("id"));
				tempMessage.setDesignId(results.getInt("design_id"));
				tempMessage.setDesignerId(results.getInt("designer_id"));
				tempMessage.setRead(results.getBoolean("has_been_read"));
				tempMessage.setCreatedAt(results.getTimestamp("created_at"));
				tempMessage.setBody(results.getString("body"));
				tempMessage.setCommentPicture(results.getString("comment_picture"));
				tempMessage.setJobId(results.getInt("job_id"));
				jobMessages.add(tempMessage);

			}
		}

		return jobMessages;
	}

	/**
	 * Submits a design request to the database.
	 *
	 * @param designRequest DesignRequest object that holds all the info related to the job request
	 * @return Integer newJobId, the id of the job that was created from the design request
	 * @throws SQLException if the connection to the database failed, or if the SQL command(s) within the method failed
	 */
	public int submitDesignRequest(DesignRequest designRequest) throws SQLException {
		int newJobId = -1;

		String sql = "SELECT submit_design_request(?,?,?,?,?,?,?,?,?,?,?)";

		try (Connection conn = TornadoHuntersDao.getConnection(); PreparedStatement prep = conn.prepareStatement(sql)) {

			prep.setString(1, designRequest.getFirstName());
			prep.setString(2, designRequest.getLastName());
			prep.setString(3, designRequest.getEmail());
			prep.setString(4, designRequest.getIdentifyAs());
			prep.setBoolean(5, designRequest.isFirstTattoo());
			prep.setBoolean(6, designRequest.isColor());
			prep.setBoolean(7, designRequest.isCoverExistingTattoo());
			prep.setString(8, designRequest.getStyleOfTattoo());
			prep.setString(9, designRequest.getEstimateSize());
			prep.setString(10, designRequest.getDescription());
			prep.setString(11, designRequest.getPosition());

			ResultSet results = prep.executeQuery();

			if (results.next()) {
				newJobId = results.getInt("submit_design_request");
			}
		}

		return newJobId;
	}

	/**
	 * Gets the next available design id
	 *
	 * @return Integer of the next designId that can be used inserted into
	 * @throws SQLException if the connection to the database failed, or if the SQL
	 *                      command(s) within the method failed
	 */
	public int getNextAvailableDesignId() throws SQLException {
		int nextDesignId = -1;

		String sql = "SELECT nextval('next_design_unique_id')";

		try (Connection conn = TornadoHuntersDao.getConnection(); Statement stmt = conn.createStatement()) {

			ResultSet results = stmt.executeQuery(sql);

			if (results.next()) {
				nextDesignId = results.getInt("nextval");
			}
		}

		return nextDesignId;
	}

	/**
	 * Records info of a design request image that was sent to the AWS S3 storage.
	 *
	 * @param designId  the ID of the image/design that is being recorded
	 * @param jobId     the ID of the job to which the image belongs to
	 * @param imageName the file name of the image being recorded
	 * @return {@code true} if the image was recorded successfully<br>
	 *         {@code false} if the image recording failed
	 * @throws SQLException if the connection to the database failed, or if the SQL
	 *                      command(s) within the method failed
	 */
	public boolean recordDesignRequestImage(int designId, int jobId, String imageName) throws SQLException {
		boolean wasRecordedSuccessfully = false;

		String sql = "SELECT record_design_request_image(?,?,?)";

		try (Connection conn = TornadoHuntersDao.getConnection(); PreparedStatement prep = conn.prepareStatement(sql)) {

			prep.setInt(1, designId);
			prep.setInt(2, jobId);
			prep.setString(3, imageName);

			ResultSet results = prep.executeQuery();

			if (results.next()) {
				wasRecordedSuccessfully = results.getBoolean("record_design_request_image");
			}
		}

		return wasRecordedSuccessfully;
	}

	/**
	 * Executes SQL query to return the job given the jobId
	 *
	 * @param jobId unique public token given to the customer to access their jobs
	 * @return {@code job} with the same access token
	 * @throws SQLException if the connection to the database failed, or if the SQL command(s) within the method failed
	 */
	public Job fetchCustomerJob(int jobId) throws SQLException {
		Job job = null;
		String sql = "SELECT * FROM customer_access(?)";

		try(Connection conn = TornadoHuntersDao.getConnection();
				PreparedStatement prep = conn.prepareStatement(sql)) {

			prep.setInt(1, jobId);
			ResultSet results = prep.executeQuery();

			if(results.next()){
				job = new Job();
				job.setJobId(results.getInt("id"));
				job.setArtistId(results.getInt("user_id"));
				job.setState(results.getString("state"));
				job.setTitle(results.getString("title"));
				job.setCustomerName(results.getString("customer"));
				job.setTattooLocation(results.getString("tattoo_position"));
				job.setTattooType(results.getString("size"));
				job.setTattooStyle(results.getString("style"));
				job.setColor(results.getBoolean("color"));
				job.setCommission(results.getDouble("commission"));
				job.setDescription(results.getString("description"));
				job.setMessages(TornadoHuntersDao.getInstance().fetchJobMessages(results.getInt("id")));
				
				BasicUser tempBasicUser = this.fetchBasicArtistInfo(job.getArtistId());
				if (tempBasicUser != null) {
					job.setArtistFirstName(tempBasicUser.getFirstName());
					job.setArtistLastName(tempBasicUser.getLastName());
				}

			}
		}

		return job;
	}
	
	/**
	 * Returns a basicUser object based on an artistId (first name and last name)
	 *
	 * @param artistId the id of the artist
	 * @return {@code job} with the same access token
	 * @throws SQLException if the connection to the database failed, or if the SQL command(s) within the method failed
	 */
	public BasicUser fetchBasicArtistInfo(int artistId) throws SQLException{
		BasicUser basicUser = null;
		
		String sql = "SELECT first_name, last_name FROM user_profiles WHERE user_id = ?";

		try(Connection conn = TornadoHuntersDao.getConnection();
				PreparedStatement prep = conn.prepareStatement(sql)) {
			
			prep.setInt(1, artistId);
			ResultSet results = prep.executeQuery();

			if(results.next()){
				basicUser = new BasicUser();
				
				basicUser.setId(artistId);
				basicUser.setFirstName(results.getString("first_name"));
				basicUser.setLastName(results.getString("last_name"));
			}
			
		}
		
		return basicUser;
	}
	
	/**
	 * Records info of a design request image that was sent to the AWS S3 storage.
	 *
	 * @param designId  the ID of the image/design that is being recorded
	 * @param jobId     the ID of the job to which the image belongs to
	 * @param imageName the file name of the image being recorded
	 * @param sessionToken the session token of the user uploading the image
	 * @return {@code true} if the image was recorded successfully<br>
	 *         {@code false} if the image recording failed
	 * @throws SQLException if the connection to the database failed, or if the SQL
	 *                      command(s) within the method failed
	 */
	public boolean recordDesignDraft(int designId, int jobId, String imageName, String sessionToken) throws SQLException{
		boolean wasRecordedSuccessfully = false;
		
		String sql = "SELECT record_design_draft(?,?,?,?)";
		
		try (Connection conn = TornadoHuntersDao.getConnection(); PreparedStatement prep = conn.prepareStatement(sql)) {

			prep.setInt(1, designId);
			prep.setInt(2, jobId);
			prep.setString(3, imageName);
			prep.setString(4, sessionToken);
			
			try {
				ResultSet results = prep.executeQuery();

				if (results.next()) {
					wasRecordedSuccessfully = results.getBoolean("record_design_draft");
				}
			}
			catch (PSQLException e) {
				throw new SQLException(e.getMessage());
			}

		}
		
		return wasRecordedSuccessfully;
	}
	
	/**
	 * Fetches all the designs associated with a job.
	 * 
	 * @param jobId the ID of the job for which we want designs of
	 * @return List&lt;DesignImage&gt; a list of the design images for a job
	 * @throws SQLException if the connection to the database failed, or if the SQL
	 *                      command(s) within the method failed
	 */
	public List<DesignImage> fetchJobDesigns(int jobId) throws SQLException{
		ArrayList<DesignImage> jobImages = new ArrayList<DesignImage>();
		
		String sql = "SELECT id, image, created_at FROM designs WHERE job_id = ?";
		
		try (Connection conn = TornadoHuntersDao.getConnection(); PreparedStatement prep = conn.prepareStatement(sql)) {
			prep.setInt(1, jobId);
			
			ResultSet results = prep.executeQuery();
			
			while (results.next()) {
				DesignImage tempDesignImage = new DesignImage();
				
				tempDesignImage.setDesignId(results.getInt("id"));
				tempDesignImage.setImageName(results.getString("image"));
				tempDesignImage.setSubmissionDate(results.getTimestamp("created_at"));
				jobImages.add(tempDesignImage);
			}
		}
		
		return jobImages;
	}
	
	
}
