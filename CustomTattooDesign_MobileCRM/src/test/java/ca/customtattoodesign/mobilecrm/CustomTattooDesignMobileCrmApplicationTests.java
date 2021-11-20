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
		String capTestUserFirstName = System.getenv("capTestUserFirstName");
		String capTestUserLastName = System.getenv("capTestUserLastName");
		String capTestUser2FirstName = System.getenv("capTestUser2FirstName");
		String capTestUser2LastName = System.getenv("capTestUser2LastName");
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
		int capTestDesignId = Integer.parseInt(System.getenv("capTestDesignId"));
		int capTestDesignId2 = Integer.parseInt(System.getenv("capTestDesignId2"));
		String capTestImageName = System.getenv("capTestImageName");
		String capTestImageName2 = System.getenv("capTestImageName2");
		String capTestImagePath = System.getenv("capTestImagePath");
		String capTestImagePath2 = System.getenv("capTestImagePath2");
		String capTestEncodedJobId = System.getenv("capTestEncodedJobId");
		String capTestEncodedJobId2 = System.getenv("capTestEncodedJobId2");
		
		
		String capDBusername = System.getenv("capDBusername");
		String capDBpassword = System.getenv("capDBpassword");
		String capDBhost = System.getenv("capDBhost");
		String capDBdatabase = System.getenv("capDBdatabase");
		int capDBport = Integer.parseInt(System.getenv("capDBport"));
		
		String capAWSBucketName = System.getenv("capAWSBucketName");
		String capAWSAccessKey = System.getenv("capAWSAccessKey");
		String capAWSSecretKey = System.getenv("capAWSSecretKey");
		String capAWSRegion = System.getenv("capAWSRegion");
		
		assertTrue("An environment variable is not set up correctly...", 
				capDBusername != null && capDBusername.length() > 0 && !capDBusername.equals("null") &&
				capDBpassword != null && capDBpassword.length() > 0 && !capDBpassword.equals("null") &&
				capDBhost != null && capDBhost.length() > 0 && !capDBhost.equals("null") &&
				capDBdatabase != null && capDBdatabase.length() > 0 && !capDBdatabase.equals("null") &&
				
				capAWSBucketName != null && capAWSBucketName.length() > 0 && !capAWSBucketName.equals("null") &&
				capAWSAccessKey != null && capAWSAccessKey.length() > 0 && !capAWSAccessKey.equals("null") &&
				capAWSSecretKey != null && capAWSSecretKey.length() > 0 && !capAWSSecretKey.equals("null") &&
				capAWSRegion != null && capAWSRegion.length() > 0 && !capAWSRegion.equals("null") &&
				
				capTestUser != null && capTestUser.length() > 0 && !capTestUser.equals("null") &&
				capTestUser2 != null && capTestUser2.length() > 0 && !capTestUser2.equals("null") &&
				capTestUserFirstName != null && capTestUserFirstName.length() > 0 && !capTestUserFirstName.equals("null") &&
				capTestUserLastName != null && capTestUserLastName.length() > 0 && !capTestUserLastName.equals("null") &&
				capTestUser2FirstName != null && capTestUser2FirstName.length() > 0 && !capTestUser2FirstName.equals("null") &&
				capTestUser2LastName != null && capTestUser2LastName.length() > 0 && !capTestUser2LastName.equals("null") &&
				capTestPassword != null && capTestPassword.length() > 0 && !capTestPassword.equals("null") &&
				capTestPassword2 != null && capTestPassword2.length() > 0 && !capTestPassword2.equals("null") &&
				capTestSessionToken != null && capTestSessionToken.length() > 0 && !capTestSessionToken.equals("null") &&
				capTestSessionToken2 != null && capTestSessionToken2.length() > 0 && !capTestSessionToken2.equals("null") &&
				capTestImageName != null && capTestImageName.length() > 0 && !capTestImageName.equals("null") &&
				capTestImageName2 != null && capTestImageName2.length() > 0 && !capTestImageName2.equals("null") &&
				capTestEncodedJobId != null && capTestEncodedJobId.length() > 0 && !capTestEncodedJobId.equals("null") &&
				capTestEncodedJobId2 != null && capTestEncodedJobId2.length() > 0 && !capTestEncodedJobId2.equals("null") &&
				capTestImagePath != null && capTestImagePath.length() > 0 && !capTestImagePath.equals("null") &&
				capTestImagePath2 != null && capTestImagePath2.length() > 0 && !capTestImagePath2.equals("null") 
				);
	}

}
