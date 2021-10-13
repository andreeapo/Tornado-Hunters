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

import ca.customtattoodesign.mobilecrm.beans.BasicJob;
import ca.customtattoodesign.mobilecrm.beans.ClaimJobLogin;
import ca.customtattoodesign.mobilecrm.beans.DesignRequest;
import ca.customtattoodesign.mobilecrm.beans.Job;
import ca.customtattoodesign.mobilecrm.beans.SessionLogin;
import ca.customtattoodesign.mobilecrm.dao.TornadoHuntersDao;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JobServiceTest {

	@Autowired
	JobService jobService;
	
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

	private static String capTestJobAccessToken1;
	private static int capTestJobAccessTokenExpectedSize1;
	private static String capTestJobAccessToken2;
	private static int capTestJobAccessTokenExpectedSize2;
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

		capTestJobAccessToken1 = "mAzADzDyHXY9nibXOOlZVrpxb20pndgPFxGWnUIcMzYs5Gf8t9";
		capTestJobAccessTokenExpectedSize1 = 1;
		capTestJobAccessToken2 = "HL6Rvknt6ofXsuzA2S2dUgdsTIBnMba6jGM6tD13";
		capTestJobAccessTokenExpectedSize2 = 1;
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
	public void testFetchUnclaimedJobsRegular() throws ResponseStatusException {
		List<Job> jobs = jobService.fetchUnclaimedJobs();
		
		assertTrue("Unclaimed jobs list was null...", jobs != null);
	}
	
	@Test
	public void testFetchArtistJobsRegular() throws ResponseStatusException {
		String username = capTestUser;
		String sessionToken = capTestSessionToken;
		
		SessionLogin sessionLogin = SessionLogin.builder().username(username).sessionToken(sessionToken).build();
		
		List<Job> jobs = jobService.fetchArtistJobs(sessionLogin);
		
		assertTrue("User did not have the expected amount of jobs...", jobs.size() == capTestJobExpectedSize);
	}
	
	@Test
	public void testFetchArtistJobsBoundaryIn() throws ResponseStatusException {
		String username = capTestUser2;
		String sessionToken = capTestSessionToken2;
		
		SessionLogin sessionLogin = SessionLogin.builder().username(username).sessionToken(sessionToken).build();
		
		List<Job> jobs = jobService.fetchArtistJobs(sessionLogin);
		
		assertTrue("User did not have the expected amount of jobs...", jobs.size() == capTestJobExpectedSize2);
	}
	
	@Test
	public void testFetchArtistJobsBoundaryOut() throws ResponseStatusException {
		
		String username = capTestUser2;
		String sessionToken = capTestSessionToken2 + "0";
		
		SessionLogin sessionLogin = SessionLogin.builder().username(username).sessionToken(sessionToken).build();
		
		assertThrows(ResponseStatusException.class, () -> {
			List<Job> jobs = jobService.fetchArtistJobs(sessionLogin);
		});
		
	}
	
	@Test
	public void testFetchArtistJobsException() throws ResponseStatusException {
		String username = capTestUser2;
		String sessionToken = null;
		
		SessionLogin sessionLogin = SessionLogin.builder().username(username).sessionToken(sessionToken).build();

		assertThrows(ResponseStatusException.class, () -> {
			List<Job> jobs = jobService.fetchArtistJobs(sessionLogin);
		});
	}
	
	@Test
	public void testClaimJobRegular() throws ResponseStatusException {
		String username = capTestUser;
		String sessionToken = capTestSessionToken;
		int jobId = capTestJobId;
		
		ClaimJobLogin claimJobLogin = ClaimJobLogin.builder().username(username).sessionToken(sessionToken).jobId(jobId).build();
		
		jobService.claimJob(claimJobLogin);
	}
	
	@Test
	public void testClaimJobException() throws ResponseStatusException {
		String username = capTestUser;
		String sessionToken = capTestSessionToken + "0";
		int jobId = capTestJobId;
		
		ClaimJobLogin claimJobLogin = ClaimJobLogin.builder().username(username).sessionToken(sessionToken).jobId(jobId).build();
		
		assertThrows(ResponseStatusException.class, () -> {
			jobService.claimJob(claimJobLogin);
		});
		
	}
	
	@Test
	public void testIsJobIdValidRegular() {
		int jobId = capTestJobId;
		
		boolean isValidId = jobService.isJobIdValid(jobId);
		
		assertTrue("Job id was invalid when it is valid...", isValidId);
	}
	
	@Test
	public void testIsJobIdValidBoundaryIn() {
		int jobId = 1;
		
		boolean isValidId = jobService.isJobIdValid(jobId);
		
		assertTrue("Job id was invalid when it is valid...", isValidId);
	}
	
	@Test
	public void testIsJobIdValidBoundaryOut() {
		int jobId = 0;
		
		boolean isValidId = jobService.isJobIdValid(jobId);
		
		assertFalse("Job id was valid when it is invalid...", isValidId);
	}
	
	@Test
	public void testIsJobIdValidBoundaryException() {
		int jobId = -10;
		
		boolean isValidId = jobService.isJobIdValid(jobId);
		
		assertFalse("Job id was valid when it is invalid...", isValidId);
	}

	@Test
	public void testGetJobAccessTokenFromJobIdRegular() throws Exception{
		int jobId = capTestJobId;
		String expectedJobAccessToken = capTestEncodedJobId;

		String jobAccessToken = jobService.getJobAccessTokenFromJobId(jobId);

		assertTrue("Job Id returned an invalid job access token", jobAccessToken.equals(expectedJobAccessToken));
	}

	@Test
	public void testGetJobAccessTokenFromJobIdBoundaryIn() throws Exception{
		int jobId = capTestJobId2;
		String expectedJobAccessToken = capTestEncodedJobId2;

		String jobAccessToken = jobService.getJobAccessTokenFromJobId(jobId);

		assertTrue("Job Id returned an invalid job access token", jobAccessToken.equals(expectedJobAccessToken));
	}

	@Test
	public void testGetJobAccessTokenFromJobIdBoundaryOut() throws Exception{
		int jobId = capTestJobId2+1;
		String expectedJobAccessToken = capTestEncodedJobId2;

		String jobAccessToken = jobService.getJobAccessTokenFromJobId(jobId);

		assertFalse("Job Id returned an invalid job access token", jobAccessToken.equals(expectedJobAccessToken));
	}

	@Test
	public void testGetJobAccessTokenFromJobIdException() throws Exception{
		int jobId = capTestJobId;
		String expectedJobAccessToken = capTestEncodedJobId+"123";

		String jobAccessToken = jobService.getJobAccessTokenFromJobId(jobId);

		assertFalse("Job Id returned an invalid job access token", jobAccessToken.equals(expectedJobAccessToken));
	}

	@Test
	public void testGetJobIdFromJobAccessTokenRegular() throws Exception {
		String jobAccessToken = capTestEncodedJobId;
		int expectedJobId = capTestJobId;

		int jobId = jobService.getJobIdFromJobAccessToken(jobAccessToken);

		assertTrue("Job access token returned an invalid job id", jobId == expectedJobId);
	}

	@Test
	public void testGetJobIdFromJobAccessTokenBoundaryIn() throws Exception {
		String jobAccessToken = capTestEncodedJobId2;
		int expectedJobId = capTestJobId2;

		int jobId = jobService.getJobIdFromJobAccessToken(jobAccessToken);

		assertTrue("Job access token returned an invalid job id", jobId == expectedJobId);
	}

	@Test
	public void testGetJobIdFromJobAccessTokenBoundaryOut() throws Exception {
		String jobAccessToken = capTestEncodedJobId2+"1";
		int expectedJobId = capTestJobId2;

		assertThrows(IllegalArgumentException.class, () -> {
			int jobId = jobService.getJobIdFromJobAccessToken(jobAccessToken);
		});

	}

	@Test
	public void testGetJobIdFromJobAccessTokenException() throws Exception {
		String jobAccessToken = null;
		int expectedJobId = capTestJobId2;

		assertThrows(NullPointerException.class, () -> {
			int jobId = jobService.getJobIdFromJobAccessToken(jobAccessToken);
		});

	}

	@Test
	public void testIsValidJobAccessTokenRegular() {
		String jobAccessToken = capTestEncodedJobId;

		boolean isValidToken = jobService.isValidJobAccessToken(jobAccessToken);

		assertTrue("Valid job access token returned invalid when it was valid", isValidToken);
	}

	@Test
	public void testIsValidJobAccessTokenBoundaryIn() {
		String jobAccessToken = capTestEncodedJobId2;

		boolean isValidToken = jobService.isValidJobAccessToken(jobAccessToken);

		assertTrue("Valid job access token returned invalid when it was valid", isValidToken);
	}

	@Test
	public void testIsValidJobAccessTokenBoundaryOut() {
		String jobAccessToken = capTestEncodedJobId2+"1";

		boolean isValidToken = jobService.isValidJobAccessToken(jobAccessToken);

		assertFalse("Valid job access token returned valid when it was invalid", isValidToken);
	}

	@Test
	public void testIsValidJobAccessTokenException() {
		String jobAccessToken = null;

		boolean isValidToken = jobService.isValidJobAccessToken(jobAccessToken);

		assertFalse("Valid job access token returned valid when it was invalid", isValidToken);
	}

	@Test
	public void testSendJobDesignRequestException() {
		DesignRequest designRequest = null;

		assertThrows(NullPointerException.class, () -> {
			BasicJob bJob = jobService.sendJobDesignRequest(designRequest, null);
		});

	}



	@Test
	public void testFetchCustomerJobRegular(){

		String accessToken = capTestJobAccessToken1;

		Job job = jobService.fetchCustomerJob(accessToken);

		assertNotNull("Customer did not have any jobs", job);
	}

	@Test
	public void testFetchCustomerJobBoundaryIn() {


		String accessToken = capTestJobAccessToken2;

		Job job = jobService.fetchCustomerJob(accessToken);

		assertNotNull("Customer did not have any jobs", job);
	}

	@Test
	public void testFetchCustomerJobBoundaryOut(){
		String accessToken = capTestJobAccessToken1 + "0";

		Job job = jobService.fetchCustomerJob(accessToken);

		assertNull("Jobs returned false", job);
	}

	@Test
	public void testFetchCustomerJobException(){
		String accessToken = null;

		Job job = jobService.fetchCustomerJob(accessToken);

		assertNull("Jobs returned false", job);
	}



}
