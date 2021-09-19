package ca.customtattoodesign.mobilecrm.dao;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import ca.customtattoodesign.mobilecrm.beans.UserLogin;
import ca.customtattoodesign.mobilecrm.beans.Job;
import ca.customtattoodesign.mobilecrm.beans.Message;
import ca.customtattoodesign.mobilecrm.beans.SessionUser;

@RunWith(SpringRunner.class)
@SpringBootTest
class TornadoHuntersDaoTest {

	private static int capTestId;
	private static int capTestId2;
	private static String capTestUser;
	private static String capTestUser2;
	private static String capTestPassword;
	private static String capTestPassword2;
	private static String capTestSessionToken;
	private static String capTestSessionToken2;
	private static int capTestJobId;
	private static int capTestJobId2;
	private static int capTestJobExpectedSize;
	private static int capTestJobExpectedSize2;
	private static int capTestMessagesExpectedSize;
	private static int capTestMessagesExpectedSize2;
	
	@BeforeAll
	public static void fetchEnvironmentVariables() {
		capTestId = Integer.parseInt(System.getenv("capTestId"));
		capTestId2 = Integer.parseInt(System.getenv("capTestId2"));
		capTestUser = System.getenv("capTestUser");
		capTestUser2 = System.getenv("capTestUser2");
		capTestPassword = System.getenv("capTestPassword");
		capTestPassword2 = System.getenv("capTestPassword2");
		capTestSessionToken = System.getenv("capTestSessionToken");
		capTestSessionToken2 = System.getenv("capTestSessionToken2");
		capTestJobId = Integer.parseInt(System.getenv("capTestJobId"));
		capTestJobId2 = Integer.parseInt(System.getenv("capTestJobId2"));
		capTestJobExpectedSize = Integer.parseInt(System.getenv("capTestJobExpectedSize"));
		capTestJobExpectedSize2 = Integer.parseInt(System.getenv("capTestJobExpectedSize2"));
		capTestMessagesExpectedSize = Integer.parseInt(System.getenv("capTestMessagesExpectedSize"));
		capTestMessagesExpectedSize2 = Integer.parseInt(System.getenv("capTestMessagesExpectedSize2"));
	}
	
	@AfterAll
	public static void afterAll() {
		
	}
	
	@BeforeEach
	public void setSessionToken() throws SQLException{
		TornadoHuntersDao.getInstance().setUserSessionToken(capTestId, capTestSessionToken);
		TornadoHuntersDao.getInstance().setUserSessionToken(capTestId2, capTestSessionToken2);
	}
	
	@AfterEach
	public void removeSessionToken() throws SQLException{
		TornadoHuntersDao.getInstance().removeSessionToken(capTestId, capTestSessionToken);
		TornadoHuntersDao.getInstance().removeSessionToken(capTestId2, capTestSessionToken2);
	}
	
	@Test
	public void testIsUserLoginAuthorizedRegular() throws SQLException {
		String username = capTestUser;
		String password = capTestPassword;
		UserLogin user = UserLogin.builder().username(username).password(password).build();
		
		boolean isValid = TornadoHuntersDao.getInstance().isUserLoginAuthorized(user.getUsername(), user.getPassword());
		
		assertTrue("User was not validated correctly...", isValid);
	}
	
	@Test
	public void testIsUserLoginAuthorizedBoundaryIn() throws SQLException {
		String username = capTestUser2;
		String password = capTestPassword2;
		UserLogin user = UserLogin.builder().username(username).password(password).build();
		
		boolean isValid = TornadoHuntersDao.getInstance().isUserLoginAuthorized(user.getUsername(), user.getPassword());

		assertTrue("User was not validated correctly...", isValid);
	}
	
	@Test
	public void testIsUserLoginAuthorizedBoundaryOut() throws SQLException {
		String username = capTestUser;
		String password = capTestPassword + "0";
		UserLogin user = UserLogin.builder().username(username).password(password).build();
		
		boolean isValid = TornadoHuntersDao.getInstance().isUserLoginAuthorized(user.getUsername(), user.getPassword());

		assertFalse("User was not validated correctly...", isValid);
	}
	
	@Test
	public void testIsUserLoginAuthorizedException() throws SQLException {
		String username = null;
		String password = capTestPassword + "0";
		UserLogin user = UserLogin.builder().username(username).password(password).build();
		
		boolean isValid = TornadoHuntersDao.getInstance().isUserLoginAuthorized(user.getUsername(), user.getPassword());
	
		assertFalse("User was not validated correctly...", isValid);
	}
	
	@Test
	public void testIsSessionLoginAuthorizedRegular() throws SQLException {
		
		String username = capTestUser;
		String sessionToken = capTestSessionToken;
		
		boolean isSessionAuthorized = TornadoHuntersDao.getInstance()
				.isSessionLoginAuthorized(username, sessionToken);
		
		assertTrue("Session token was not authorized when it should have been...", isSessionAuthorized);
	}
	
	@Test
	public void testIsSessionLoginAuthorizedBoundaryIn() throws SQLException {
		String username = capTestUser2;
		String sessionToken = capTestSessionToken2;
		
		boolean isSessionAuthorized = TornadoHuntersDao.getInstance()
				.isSessionLoginAuthorized(username, sessionToken);
		
		assertTrue("Session token was not authorized when it should have been...", isSessionAuthorized);
	}
	
	@Test
	public void testIsSessionLoginAuthorizedBoundaryOut() throws SQLException {
		String username = capTestUser2;
		String sessionToken = capTestSessionToken2 + "0";
		
		boolean isSessionAuthorized = TornadoHuntersDao.getInstance()
				.isSessionLoginAuthorized(username, sessionToken);
		
		assertFalse("Session token was authorized when it shouldn't have been...", isSessionAuthorized);
	}
	
	@Test
	public void testIsSessionLoginAuthorizedBoundaryException() throws SQLException {
		String username = capTestUser2;
		String sessionToken = null;
		
		boolean isSessionAuthorized = TornadoHuntersDao.getInstance()
				.isSessionLoginAuthorized(username, sessionToken);
		
		assertFalse("Session token was authorized when it shouldn't have been...", isSessionAuthorized);
	}
	
	@Test
	public void testFetchSessionUserFieldsViaLoginRegular() throws SQLException {
		String username = capTestUser;
		String password = capTestPassword;
		
		SessionUser sessionUser = new SessionUser();
		boolean wasSuccessful = TornadoHuntersDao.getInstance().fetchSessionUserId(sessionUser, username, password);
		boolean idExists = sessionUser.getId() != 0;
		
		assertTrue("SessionUser fields fetching failed...", wasSuccessful && idExists);
	}
	
	@Test
	public void testFetchSessionUserFieldsViaLoginBoundaryIn() throws SQLException {
		String username = capTestUser2;
		String password = capTestPassword2;
		
		SessionUser sessionUser = new SessionUser();
		boolean wasSuccessful = TornadoHuntersDao.getInstance().fetchSessionUserId(sessionUser, username, password);
		boolean idExists = sessionUser.getId() != 0;
		
		assertTrue("SessionUser fields fetching failed...", wasSuccessful && idExists);
	}
	
	@Test
	public void testFetchSessionUserFieldsViaLoginBoundaryOut() throws SQLException {
		String username = capTestUser2;
		String password = capTestPassword2 + "0";
		
		SessionUser sessionUser = new SessionUser();
		boolean wasSuccessful = TornadoHuntersDao.getInstance().fetchSessionUserId(sessionUser, username, password);
		boolean idExists = sessionUser.getId() != 0;
		
		assertFalse("SessionUser fields fetching failed...", wasSuccessful && idExists);
	}
	
	@Test
	public void testFetchSessionUserFieldsViaLoginException() throws SQLException {
		String username = capTestUser2;
		String password = null;
		
		SessionUser sessionUser = new SessionUser();
		boolean wasSuccessful = TornadoHuntersDao.getInstance().fetchSessionUserId(sessionUser, username, password);
		boolean idExists = sessionUser.getId() != 0;
		
		assertFalse("SessionUser fields fetching failed...", wasSuccessful && idExists);
	}
	
	@Test
	public void testFetchSessionUserFieldsRegular() throws SQLException {
		String username = capTestUser;
		String sessionToken = capTestSessionToken;
		
		SessionUser sessionUser = new SessionUser();
		boolean wasSuccessful = TornadoHuntersDao.getInstance().fetchSessionUserFields(sessionUser, username, sessionToken);
		boolean fieldExists = sessionUser.getFirstName() != null && !"".equals(sessionUser.getFirstName());
		
		assertTrue("SessionUser fields fetching failed...", wasSuccessful && fieldExists);
	}
	
	@Test
	public void testFetchSessionUserFieldsBoundaryIn() throws SQLException {
		String username = capTestUser2;
		String sessionToken = capTestSessionToken2;
		
		SessionUser sessionUser = new SessionUser();
		boolean wasSuccessful = TornadoHuntersDao.getInstance().fetchSessionUserFields(sessionUser, username, sessionToken);
		boolean fieldExists = sessionUser.getFirstName() != null && !"".equals(sessionUser.getFirstName());
		
		assertTrue("SessionUser fields fetching failed...", wasSuccessful && fieldExists);
	}
	
	@Test
	public void testFetchSessionUserFieldsBoundaryOut() throws SQLException {
		String username = capTestUser2;
		String sessionToken = capTestSessionToken2 + "0";
		
		SessionUser sessionUser = new SessionUser();
		
		assertThrows(SQLException.class, () -> {
			boolean wasSuccessful = TornadoHuntersDao.getInstance().fetchSessionUserFields(sessionUser, username, sessionToken);
		});
	}
	
	@Test
	public void testFetchSessionUserFieldsException() throws SQLException {
		String username = capTestUser2;
		String sessionToken = null;
		
		SessionUser sessionUser = new SessionUser();
		
		assertThrows(SQLException.class, () -> {
			boolean wasSuccessful = TornadoHuntersDao.getInstance().fetchSessionUserFields(sessionUser, username, sessionToken);
		});
	}
	
	@Test
	public void testSetUserSessionTokenRegular() throws SQLException {
		String testSessionToken = capTestSessionToken;
		
		boolean wasSettingUserTokenSuccessful = TornadoHuntersDao.getInstance().setUserSessionToken(capTestId, testSessionToken);

		assertTrue("Session token was not updated into the database correctly...", wasSettingUserTokenSuccessful);
	}
	
	@Test
	public void testSetUserSessionTokenBoundaryIn() throws SQLException {
		String testSessionToken = capTestSessionToken2;

		boolean wasSettingUserTokenSuccessful = TornadoHuntersDao.getInstance().setUserSessionToken(capTestId2, testSessionToken);

		assertTrue("Session token was not updated into the database correctly...", wasSettingUserTokenSuccessful);
	}
	
	@Test
	public void testSetUserSessionTokenBoundaryOut() throws SQLException {	
		String testSessionToken = capTestSessionToken2;

		assertThrows(SQLException.class, () -> {
			boolean wasSettingUserTokenSuccessful = TornadoHuntersDao.getInstance().setUserSessionToken(0, testSessionToken);
		});
	}
	
	@Test
	public void testSetUserSessionTokenException() throws SQLException {
		String testSessionToken = null;

		assertThrows(SQLException.class, () -> {
			boolean wasSettingUserTokenSuccessful = TornadoHuntersDao.getInstance().setUserSessionToken(-100, testSessionToken);
		});
	}
	
	@Test
	public void testFetchUnclaimedJobsRegular() throws SQLException {
		List<Job> jobs = TornadoHuntersDao.getInstance().fetchUnclaimedJobs();
		
		assertTrue("Unclaimed jobs list was null...", jobs != null);
	}
	
	@Test
	public void testFetchArtistJobsRegular() throws SQLException {
		String username = capTestUser;
		String sessionToken = capTestSessionToken;
		
		List<Job> jobs = TornadoHuntersDao.getInstance().fetchArtistJobs(username, sessionToken);
		
		assertTrue("User did not have the expected amount of jobs...", jobs.size() == capTestJobExpectedSize);
	}
	
	@Test
	public void testFetchArtistJobsBoundaryIn() throws SQLException {
		String username = capTestUser2;
		String sessionToken = capTestSessionToken2;
		
		List<Job> jobs = TornadoHuntersDao.getInstance().fetchArtistJobs(username, sessionToken);
		
		assertTrue("User did not have the expected amount of jobs...", jobs.size() == capTestJobExpectedSize2);
	}
	
	@Test
	public void testFetchArtistJobsBoundaryOut() throws SQLException {
		String username = capTestUser2;
		String sessionToken = capTestSessionToken2 + "0";
		
		assertThrows(SQLException.class, () -> {
			List<Job> jobs = TornadoHuntersDao.getInstance().fetchArtistJobs(username, sessionToken);
		});
	}
	
	@Test
	public void testFetchArtistJobsException() throws SQLException {
		String username = capTestUser2;
		String sessionToken = null;

		assertThrows(SQLException.class, () -> {
			List<Job> jobs = TornadoHuntersDao.getInstance().fetchArtistJobs(username, sessionToken);
		});
	}
	
	@Test
	public void testClaimJobRegular() throws SQLException {
		String username = capTestUser;
		String sessionToken = capTestSessionToken;
		int jobId = capTestJobId;
		
		boolean wasSuccessful = TornadoHuntersDao.getInstance().claimJob(jobId, username, sessionToken);
	}
	
	@Test
	public void testClaimJobException() throws SQLException {
		String username = capTestUser;
		String sessionToken = capTestSessionToken + "0";
		int jobId = capTestJobId;
		
		assertThrows(SQLException.class, () -> {
			boolean wasSuccessful = TornadoHuntersDao.getInstance().claimJob(jobId, username, sessionToken);
		});
	}
	
	@Test
	public void testRemoveSessionTokenRegular() throws SQLException {
		int userId = capTestId;
		String sessionToken = capTestSessionToken;
		
		boolean successfullyRemoved = TornadoHuntersDao.getInstance().removeSessionToken(userId, sessionToken);
		
		assertTrue("Session token was not deleted when it was supposed to be...", successfullyRemoved);
	}
	
	@Test
	public void testRemoveSessionTokenException() throws SQLException {
		int userId = capTestId;
		String sessionToken = null;
		
		boolean successfullyRemoved = TornadoHuntersDao.getInstance().removeSessionToken(userId, sessionToken);
		
		assertFalse("Session token was deleted when it should not have been...", successfullyRemoved);
		
	}
	
	@Test
	public void testFetchJobMessagesRegular() throws SQLException {
		int jobId = capTestJobId;
		
		List<Message> tempMessages = TornadoHuntersDao.getInstance().fetchJobMessages(jobId);
		
		assertTrue("Job did not have the expected amount of messages...", tempMessages.size() == capTestMessagesExpectedSize);
	}
	
	@Test
	public void testFetchJobMessagesException() throws SQLException {
		int jobId = -1;
		
		List<Message> tempMessages = TornadoHuntersDao.getInstance().fetchJobMessages(jobId);
		
		assertTrue("Job did not have the expected amount of messages...", tempMessages.size() == 0);
	}
	
	@Test
	public void testSendStringMessageRegular() throws SQLException{
		int jobId = capTestJobId2;
		String message = "hello world";
		String sessionToken = "";
		
		boolean messageSent = TornadoHuntersDao.getInstance().sendStringMessage(jobId, message, sessionToken);
		
		assertTrue("Correct message was unable to be sent...", messageSent);
	}
	
	@Test
	public void testSendStringMessageBoundaryIn() throws SQLException{
		int jobId = capTestJobId2;
		String message = "hello world";
		String sessionToken = capTestSessionToken2;
		
		boolean messageSent = TornadoHuntersDao.getInstance().sendStringMessage(jobId, message, sessionToken);
		
		assertTrue("Correct message was unable to be sent...", messageSent);
	}
	
	@Test
	public void testSendStringMessageBoundaryOut() throws SQLException{
		int jobId = capTestJobId2;
		String message = "hello world";
		String sessionToken = capTestSessionToken2+"1";
		
		boolean messageSent = TornadoHuntersDao.getInstance().sendStringMessage(jobId, message, sessionToken);
		
		assertFalse("Incorrect message was sent ...", messageSent);
	}
	
	@Test
	public void testSendStringMessageException() throws SQLException{
		int jobId = capTestJobId2;
		String message = null;
		String sessionToken = null;
		
		boolean messageSent = TornadoHuntersDao.getInstance().sendStringMessage(jobId, message, sessionToken);
		
		assertFalse("Message that was not supposed to be sent was sent...", messageSent);
	}
	
	@Test
	public void testFetchUnreadJobMessagesRegular() throws SQLException {
		int jobId = capTestJobId;
		
		List<Message> tempMessages = TornadoHuntersDao.getInstance().fetchUnreadJobMessages(jobId);
		
		assertTrue("Job did not have the expected amount of messages...", tempMessages != null);
	}
	
	@Test
	public void testFetchUnreadJobMessagesException() throws SQLException {
		int jobId = -1;
		
		List<Message> tempMessages = TornadoHuntersDao.getInstance().fetchUnreadJobMessages(jobId);
		
		assertTrue("Job did not have the expected amount of messages...", tempMessages.size() == 0);
	}

}
