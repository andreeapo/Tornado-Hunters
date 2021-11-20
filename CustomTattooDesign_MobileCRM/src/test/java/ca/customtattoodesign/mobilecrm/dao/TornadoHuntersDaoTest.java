package ca.customtattoodesign.mobilecrm.dao;

import static org.junit.Assert.*;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.server.ResponseStatusException;

import ca.customtattoodesign.mobilecrm.beans.UserLogin;
import ca.customtattoodesign.mobilecrm.services.AWSService;
import ca.customtattoodesign.mobilecrm.beans.BasicUser;
import ca.customtattoodesign.mobilecrm.beans.DesignImage;
import ca.customtattoodesign.mobilecrm.beans.DesignRequest;
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
	private static String capTestUserFirstName;
	private static String capTestUserLastName;
	private static String capTestUser2FirstName;
	private static String capTestUser2LastName;
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

	private static String capTestJobAccessToken1;
	private static String capTestJobAccessToken2;
	private static int capTestDesignId;
	private static int capTestDesignId2;
	private static String capTestImageName;
	private static String capTestImageName2;
	private static String capTestImagePath;
	private static String capTestImagePath2;
	private static String capTestEncodedJobId;
	private static String capTestEncodedJobId2;

	private static String capTestBearer;

	@BeforeAll
	public static void fetchEnvironmentVariables() {
		capTestId = Integer.parseInt(System.getenv("capTestId"));
		capTestId2 = Integer.parseInt(System.getenv("capTestId2"));
		capTestUser = System.getenv("capTestUser");
		capTestUser2 = System.getenv("capTestUser2");
		capTestUserFirstName = System.getenv("capTestUserFirstName");
		capTestUserLastName = System.getenv("capTestUserLastName");
		capTestUser2FirstName = System.getenv("capTestUser2FirstName");
		capTestUser2LastName = System.getenv("capTestUser2LastName");
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
		
		capTestJobAccessToken1 = System.getenv("capTestEncodedJobId");
		capTestJobAccessToken2 = System.getenv("capTestEncodedJobId2");
		capTestDesignId = Integer.parseInt(System.getenv("capTestDesignId"));
		capTestDesignId2 = Integer.parseInt(System.getenv("capTestDesignId2"));
		capTestImageName = System.getenv("capTestImageName");
		capTestImageName2 = System.getenv("capTestImageName2");
		capTestImagePath = System.getenv("capTestImagePath");
		capTestImagePath2 = System.getenv("capTestImagePath2");
		capTestEncodedJobId = System.getenv("capTestEncodedJobId");
		capTestEncodedJobId2 = System.getenv("capTestEncodedJobId2");
		
		capTestBearer = System.getenv("capTestBearer");
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
	
	@Autowired
	private AWSService awsService;
	
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

	@Test
	public void testSubmitDesignRequestException() throws SQLException {
		DesignRequest designRequest = null;

		assertThrows(NullPointerException.class, () -> {
			int newJobId = TornadoHuntersDao.getInstance().submitDesignRequest(designRequest);
		});
	}

	@Test
	public void testGetNextAvailableDesignIdRegular() throws SQLException {
		int nextDesignId = TornadoHuntersDao.getInstance().getNextAvailableDesignId();

		assertTrue("No next available designer id", nextDesignId != -1);
	}

	@Test
	public void testRecordDesignRequestImageRegular() throws Exception {
		int designId = TornadoHuntersDao.getInstance().getNextAvailableDesignId();
		int jobId = capTestJobId;
		String imageName = capTestImageName;
		File image = new File(capTestImagePath);
		
		boolean wasRecordedSuccessfully = false;
		
		if (awsService.uploadDesignImage(designId, image)) {
			wasRecordedSuccessfully = TornadoHuntersDao.getInstance().recordDesignRequestImage(designId, jobId, imageName);
		}

		assertTrue("Recording of a design image was unsuccessful when it should have succeeded", wasRecordedSuccessfully);
	}

	@Test
	public void testRecordDesignRequestImageBoundaryIn() throws SQLException {
		int designId = TornadoHuntersDao.getInstance().getNextAvailableDesignId();
		int jobId = capTestJobId2;
		String imageName = capTestImageName2;
		File image = new File(capTestImagePath2);
		
		boolean wasRecordedSuccessfully = false;
		
		if (awsService.uploadDesignImage(designId, image)) {
			wasRecordedSuccessfully = TornadoHuntersDao.getInstance().recordDesignRequestImage(designId, jobId, imageName);
		}

		assertTrue("Recording of a design image was unsuccessful when it should have succeeded", wasRecordedSuccessfully);
	}

	@Test
	public void testRecordDesignRequestImageBoundaryOut() throws SQLException {
		int designId = TornadoHuntersDao.getInstance().getNextAvailableDesignId()-1;
		int jobId = capTestJobId2;
		String imageName = capTestImageName2;
		File image = new File(capTestImagePath2);
		
		boolean wasRecordedSuccessfully = false;
		
		if (awsService.uploadDesignImage(designId, image)) {
			wasRecordedSuccessfully = TornadoHuntersDao.getInstance().recordDesignRequestImage(designId, jobId, imageName);
		}

		assertFalse("Recording of a design image was successful when it should have failed", wasRecordedSuccessfully);
	}

	@Test
	public void testRecordDesignRequestImageException() throws SQLException {
		int designId = -1;
		int jobId = capTestJobId2;
		String imageName = capTestImageName2;
		File image = new File(capTestImagePath2);
		
		boolean wasRecordedSuccessfully = false;
		
		if (awsService.uploadDesignImage(designId, image)) {
			wasRecordedSuccessfully = TornadoHuntersDao.getInstance().recordDesignRequestImage(designId, jobId, imageName);
		}

		assertFalse("Recording of a design image was successful when it should have failed", wasRecordedSuccessfully);
	}

	@Test
	public void testFetchCustomerJobRegular() throws SQLException {
		int accessToken = capTestJobId;
		Job job = TornadoHuntersDao.getInstance().fetchCustomerJob(accessToken);

		assertNotNull("User did not have the expected amount of jobs...", job);
	}

	@Test
	public void testFetchCustomerJobBoundaryIn() throws SQLException {
		int accessToken = capTestJobId2;
		Job job = TornadoHuntersDao.getInstance().fetchCustomerJob(accessToken);

		assertNotNull("Job doesnt exist", job);
	}

	@Test
	public void testFetchCustomerJobBoundaryOut() throws SQLException {
		int accessToken = 0;
		Job job = TornadoHuntersDao.getInstance().fetchCustomerJob(accessToken);

		assertNull("Jobs returned false", job);
	}

	@Test
	public void testFetchCustomerJobException() throws SQLException {
		int accessToken = -1;
		Job job = TornadoHuntersDao.getInstance().fetchCustomerJob(accessToken);

		assertNull("Jobs returned false", job);
	}
	
	@Test
	public void testFetchBasicArtistInfoRegular() throws SQLException {
		int userId = capTestId;
		String expectedFirstName = capTestUserFirstName;
		String expectedLastName = capTestUserLastName;
		
		BasicUser basicUser = TornadoHuntersDao.getInstance().fetchBasicArtistInfo(userId);
		
		assertTrue("Invalid first or last name were returned", 
				basicUser.getFirstName().equals(expectedFirstName) && basicUser.getLastName().equals(expectedLastName));
	}
	
	@Test
	public void testFetchBasicArtistInfoBoundaryIn() throws SQLException {
		int userId = capTestId2;
		String expectedFirstName = capTestUser2FirstName;
		String expectedLastName = capTestUser2LastName;
		
		BasicUser basicUser = TornadoHuntersDao.getInstance().fetchBasicArtistInfo(userId);
		
		assertTrue("Invalid first or last name were returned", 
				basicUser.getFirstName().equals(expectedFirstName) && basicUser.getLastName().equals(expectedLastName));
	}
	
	@Test
	public void testFetchBasicArtistInfoBoundaryOut() throws SQLException {
		int userId = capTestId2+1;
		String expectedFirstName = capTestUser2FirstName;
		String expectedLastName = capTestUser2LastName;
		
		BasicUser basicUser = TornadoHuntersDao.getInstance().fetchBasicArtistInfo(userId);
		
		assertFalse("Invalid first or last name were returned", 
				basicUser.getFirstName().equals(expectedFirstName) && basicUser.getLastName().equals(expectedLastName));
	}
	
	@Test
	public void testFetchBasicArtistInfoException() throws SQLException {
		int userId = -1;
		
		BasicUser basicUser = TornadoHuntersDao.getInstance().fetchBasicArtistInfo(userId);
		
		assertTrue("BasicUser was returned when there should have been none", basicUser == null);
	}
	
	@Test
	public void testRecordDesignDraftRegular() throws SQLException {
		int designId = TornadoHuntersDao.getInstance().getNextAvailableDesignId();
		int jobId = capTestJobId;
		String imageName = capTestImageName;
		String sessionToken = capTestSessionToken;
		File image = new File (capTestImagePath);
		
		boolean wasRecordedSuccessfully = false;
		
		if (awsService.uploadDesignImage(designId, image)) {
			wasRecordedSuccessfully = TornadoHuntersDao.getInstance().recordDesignDraft(designId, jobId, imageName, sessionToken);
		}

		assertTrue("Recording of a design image was unsuccessful when it should have succeeded", wasRecordedSuccessfully);
	}
	
	@Test
	public void testRecordDesignDraftRegular2() throws SQLException {
		int designId = TornadoHuntersDao.getInstance().getNextAvailableDesignId();
		int jobId = capTestJobId;
		String imageName = capTestImageName;
		String sessionToken = "";
		File image = new File (capTestImagePath);
		
		boolean wasRecordedSuccessfully = false;
		
		if (awsService.uploadDesignImage(designId, image)) {
			wasRecordedSuccessfully = TornadoHuntersDao.getInstance().recordDesignDraft(designId, jobId, imageName, sessionToken);
		}

		assertTrue("Recording of a design image was unsuccessful when it should have succeeded", wasRecordedSuccessfully);
	}
	
	@Test
	public void testRecordDesignDraftBoundaryIn() throws SQLException {
		int designId = TornadoHuntersDao.getInstance().getNextAvailableDesignId();
		int jobId = capTestJobId2;
		String imageName = capTestImageName2;
		String sessionToken = capTestSessionToken2;
		File image = new File (capTestImagePath2);
		
		boolean wasRecordedSuccessfully = false;
		
		if (awsService.uploadDesignImage(designId, image)) {
			wasRecordedSuccessfully = TornadoHuntersDao.getInstance().recordDesignDraft(designId, jobId, imageName, sessionToken);
		}

		assertTrue("Recording of a design image was unsuccessful when it should have succeeded", wasRecordedSuccessfully);
	}

	@Test
	public void testRecordDesignDraftBoundaryOut() throws SQLException {
		int designId = TornadoHuntersDao.getInstance().getNextAvailableDesignId();
		int jobId = capTestJobId2;
		String imageName = capTestImageName2;
		String sessionToken = capTestSessionToken2+"1";
		File image = new File (capTestImagePath2);
		
		boolean wasRecordedSuccessfully = false;
		
		if (awsService.uploadDesignImage(designId, image)) {
			assertThrows(SQLException.class, () -> {
				boolean wasRecordedSuccessfully2 = TornadoHuntersDao.getInstance().recordDesignDraft(designId, jobId, imageName, sessionToken);
			});
		}

		assertFalse("Recording of a design image was successful when it should have failed", wasRecordedSuccessfully);
	}
	
	@Test
	public void testRecordDesignDraftException() throws SQLException {
		int designId = -1;
		int jobId = capTestJobId2;
		String imageName = capTestImageName2;
		String sessionToken = null;
		File image = new File (capTestImagePath2);
		
		boolean wasRecordedSuccessfully = false;
		
		if (awsService.uploadDesignImage(designId, image)) {
			assertThrows(SQLException.class, () -> {
				boolean wasRecordedSuccessfully2 = TornadoHuntersDao.getInstance().recordDesignDraft(designId, jobId, imageName, sessionToken);
			});
		}

		assertFalse("Recording of a design image was successful when it should have failed", wasRecordedSuccessfully);
	}
	
	@Test
	public void testFetchJobDesignsRegular() throws SQLException {
		int jobId = capTestJobId;
		
		List<DesignImage> designImages = TornadoHuntersDao.getInstance().fetchJobDesigns(jobId);
		assertTrue("No designImages were found for job that has images", designImages.size() > 0);
	}
	
	@Test
	public void testFetchJobDesignsBoundaryIn() throws SQLException {
		int jobId = capTestJobId2;
		
		List<DesignImage> designImages = TornadoHuntersDao.getInstance().fetchJobDesigns(jobId);
		assertTrue("No designImages were found for job that has images", designImages.size() > 0);
	}
	
	@Test
	public void testFetchJobDesignsBoundaryOut() throws SQLException {
		int jobId = 0;
		
		List<DesignImage> designImages = TornadoHuntersDao.getInstance().fetchJobDesigns(jobId);
		assertFalse("DesignImages were found for an invalid job", designImages.size() > 0);
	}
	
	@Test
	public void testFetchJobDesignsException() throws SQLException {
		int jobId = -1;
		
		List<DesignImage> designImages = TornadoHuntersDao.getInstance().fetchJobDesigns(jobId);
		assertFalse("DesignImages were found for an invalid job", designImages.size() > 0);
	}
	
}
