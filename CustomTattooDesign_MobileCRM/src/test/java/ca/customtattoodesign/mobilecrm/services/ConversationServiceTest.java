package ca.customtattoodesign.mobilecrm.services;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.server.ResponseStatusException;

import ca.customtattoodesign.mobilecrm.beans.ConversationLogin;
import ca.customtattoodesign.mobilecrm.beans.Message;
import ca.customtattoodesign.mobilecrm.dao.TornadoHuntersDao;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ConversationServiceTest {

	@Autowired
	ConversationService conversationService;
	
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
	private static int capTestDesignId;
	private static int capTestDesignId2;
	private static String capTestImageName;
	private static String capTestImageName2;
	private static String capTestEncodedJobId;
	private static String capTestEncodedJobId2;
	
	@BeforeClass
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
		capTestDesignId = Integer.parseInt(System.getenv("capTestDesignId"));
		capTestDesignId2 = Integer.parseInt(System.getenv("capTestDesignId2"));
		capTestImageName = System.getenv("capTestImageName");
		capTestImageName2 = System.getenv("capTestImageName2");
		capTestEncodedJobId = System.getenv("capTestEncodedJobId");
		capTestEncodedJobId2 = System.getenv("capTestEncodedJobId2");
	}
	
	@AfterClass
	public static void afterAll() {
		
	}
	
	@Before
	public void setSessionToken() throws SQLException{
		TornadoHuntersDao.getInstance().setUserSessionToken(capTestId, capTestSessionToken);
		TornadoHuntersDao.getInstance().setUserSessionToken(capTestId2, capTestSessionToken2);
	}
	
	@After
	public void removeSessionToken() throws SQLException{
		TornadoHuntersDao.getInstance().removeSessionToken(capTestId, capTestSessionToken);
		TornadoHuntersDao.getInstance().removeSessionToken(capTestId2, capTestSessionToken2);
	}
	
	@Test
	public void testFetchJobMessagesRegular() throws SQLException {
		int jobId = capTestJobId;
		
		List<Message> tempMessages = conversationService.fetchJobMessages(
				ConversationLogin.builder()
				.jobId(jobId).build()
		);
		
		assertTrue("Job did not have the expected amount of messages...", tempMessages.size() == capTestMessagesExpectedSize);
	}
	
	@Test
	public void testFetchJobMessagesBoundaryIn() throws SQLException {
		int jobId = 1;
		
		List<Message> tempMessages = conversationService.fetchJobMessages(
				ConversationLogin.builder()
				.jobId(jobId).build()
		);
		
		assertTrue("Job did not generate message list correctly...", tempMessages != null);
	}
	
	
	@Test
	public void testFetchJobMessagesBoundaryOut() throws SQLException {
		int jobId = 0;
		
		assertThrows(ResponseStatusException.class, () -> {
			List<Message> tempMessages = conversationService.fetchJobMessages(
					ConversationLogin.builder()
					.jobId(jobId).build()
			);
		});
	}
	
	@Test
	public void testFetchJobMessagesException() throws SQLException {
		int jobId = -1;
		
		assertThrows(ResponseStatusException.class, () -> {
			List<Message> tempMessages = conversationService.fetchJobMessages(
					ConversationLogin.builder()
					.jobId(jobId).build()
			);
		});
	}
	
	@Test
	public void testSendStringMessageRegular() {
		boolean messageSent = conversationService.sendStringMessage(
				ConversationLogin.builder()
				.jobId(capTestJobId2)
				.body("hello world")
				.sessionToken("").build());
		
		assertTrue("Correct message was unable to be sent...", messageSent);
	}
	
	@Test
	public void testSendStringMessageBoundaryIn() {
		boolean messageSent = conversationService.sendStringMessage(
				ConversationLogin.builder()
				.jobId(capTestJobId2)
				.body("hello world")
				.sessionToken(capTestSessionToken2).build());
		
		assertTrue("Correct message was unable to be sent...", messageSent);
	}
	
	@Test
	public void testSendStringMessageBoundaryOut() {
		boolean messageSent = conversationService.sendStringMessage(
				ConversationLogin.builder()
				.jobId(capTestJobId2)
				.body("hello world")
				.sessionToken(capTestSessionToken2+"1").build());
		
		assertFalse("Incorrect message was sent ...", messageSent);
	}
	
	@Test
	public void testSendStringMessageException() {
		
		assertThrows(ResponseStatusException.class, () -> {
			boolean messageSent = conversationService.sendStringMessage(
					ConversationLogin.builder()
					.jobId(capTestJobId2)
					.body(null)
					.sessionToken(null).build());
		});
	}
	
	@Test
	public void testFetchUnreadJobMessagesRegular() throws SQLException {
		int jobId = capTestJobId;
		
		List<Message> tempMessages = conversationService.fetchUnreadJobMessages(
				ConversationLogin.builder()
				.jobId(jobId).build()
		);
		
		assertTrue("Job did not have the expected amount of messages...", tempMessages != null);
	}
	
	@Test
	public void testFetchUnreadJobMessagesBoundaryIn() throws SQLException {
		int jobId = 1;
		
		List<Message> tempMessages = conversationService.fetchUnreadJobMessages(
				ConversationLogin.builder()
				.jobId(jobId).build()
		);
		
		assertTrue("Job did not generate message list correctly...", tempMessages != null);
	}
	
	@Test
	public void testFetchUnreadJobMessagesBoundaryOut() throws SQLException {
		int jobId = 0;
		
		assertThrows(ResponseStatusException.class, () -> {
			List<Message> tempMessages = conversationService.fetchUnreadJobMessages(
					ConversationLogin.builder()
					.jobId(jobId).build()
			);
		});
	}
	
	@Test
	public void testFetchUnreadJobMessagesException() throws SQLException {
		int jobId = -1;
		
		assertThrows(ResponseStatusException.class, () -> {
			List<Message> tempMessages = conversationService.fetchUnreadJobMessages(
					ConversationLogin.builder()
					.jobId(jobId).build()
			);
		});
	}
	
	@Test
	public void testIsStringMessageValidRegular() {
		String message = "Hello world";
		
		assertTrue("A message that was valid returned invalid...", conversationService.isStringMessageValid(message));
	}
	
	@Test
	public void testIsStringMessageValidBoundaryIn() {
		String message = "H";
		
		assertTrue("A message that was valid returned invalid...", conversationService.isStringMessageValid(message));
	}
	
	@Test
	public void testIsStringMessageValidBoundaryOut() {
		String message = "";
		
		assertFalse("A message that was invalid returned valid...", conversationService.isStringMessageValid(message));
	}
	
	@Test
	public void testIsStringMessageValidException() {
		String message = null;
		
		assertFalse("A message that was invalid returned valid...", conversationService.isStringMessageValid(message));
	}

}
