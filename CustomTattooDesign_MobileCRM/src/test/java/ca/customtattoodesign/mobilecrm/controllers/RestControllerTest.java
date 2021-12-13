package ca.customtattoodesign.mobilecrm.controllers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import ca.customtattoodesign.mobilecrm.dao.TornadoHuntersDao;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RestControllerTest {
	
	@Autowired
	private RestController restController = new RestController();
	
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

	@BeforeClass
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
	public void testIsBearerTokenValidBoundaryIn() {
		String bearer = capTestBearer;
		assertTrue("Bearer token was invalid when it was valid", restController.isBearerTokenValid(bearer));
	}
	
	@Test
	public void testIsBearerTokenValidBoundaryOut() {
		String bearer = capTestBearer+"1";
		assertFalse("Bearer token was valid when it was invalid", restController.isBearerTokenValid(bearer));
	}
	
	@Test
	public void testIsBearerTokenValidBoundaryException() {
		String bearer = null;
		assertFalse("Bearer token was valid when it was invalid", restController.isBearerTokenValid(bearer));
	}
}
