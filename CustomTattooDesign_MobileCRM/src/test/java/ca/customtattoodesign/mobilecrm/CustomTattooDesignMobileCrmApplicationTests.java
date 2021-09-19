package ca.customtattoodesign.mobilecrm;

import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CustomTattooDesignMobileCrmApplicationTests {
	
	@Test
	void testAllEnvironmentVariablesExist() throws NumberFormatException{
		int capTestId = Integer.parseInt(System.getenv("capTestId"));
		int capTestId2 = Integer.parseInt(System.getenv("capTestId2"));
		String capTestUser = System.getenv("capTestUser");
		String capTestUser2 = System.getenv("capTestUser2");
		String capTestPassword = System.getenv("capTestPassword");
		String capTestPassword2 = System.getenv("capTestPassword2");
		String capTestSessionToken = System.getenv("capTestSessionToken");
		String capTestSessionToken2 = System.getenv("capTestSessionToken2");
		int capTestJobId = Integer.parseInt(System.getenv("capTestJobId"));
		int capTestJobId2 = Integer.parseInt(System.getenv("capTestJobId2"));
		int capTestJobExpectedSize = Integer.parseInt(System.getenv("capTestJobExpectedSize"));
		int capTestJobExpectedSize2 = Integer.parseInt(System.getenv("capTestJobExpectedSize2"));
		int capTestMessagesExpectedSize = Integer.parseInt(System.getenv("capTestMessagesExpectedSize"));
		int capTestMessagesExpectedSize2 = Integer.parseInt(System.getenv("capTestMessagesExpectedSize2"));
		
		String capDBusername = System.getenv("capDBusername");
		String capDBpassword = System.getenv("capDBpassword");
		String capDBhost = System.getenv("capDBhost");
		String capDBdatabase = System.getenv("capDBdatabase");
		int capDBport = Integer.parseInt(System.getenv("capDBport"));
		
		assertTrue("An environment variable is not set up correctly...", 
				capTestUser != null && capTestUser.length() > 0 && !capTestUser.equals("null") &&
				capTestUser2 != null && capTestUser2.length() > 0 && !capTestUser2.equals("null") &&
				capTestPassword != null && capTestPassword.length() > 0 && !capTestPassword.equals("null") &&
				capTestPassword2 != null && capTestPassword2.length() > 0 && !capTestPassword2.equals("null") &&
				capTestSessionToken != null && capTestSessionToken.length() > 0 && !capTestSessionToken.equals("null") &&
				capTestSessionToken2 != null && capTestSessionToken2.length() > 0 && !capTestSessionToken2.equals("null")
				);
	}

}
