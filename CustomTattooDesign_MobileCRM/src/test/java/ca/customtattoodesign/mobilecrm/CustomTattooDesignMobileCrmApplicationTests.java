package ca.customtattoodesign.mobilecrm;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
class CustomTattooDesignMobileCrmApplicationTests {
	
	@Test
	void testRequiredEnvironmentVariablesExist() throws NumberFormatException{		
		String capDBusername = System.getenv("capDBusername");
		String capDBpassword = System.getenv("capDBpassword");
		String capDBhost = System.getenv("capDBhost");
		String capDBdatabase = System.getenv("capDBdatabase");
		int capDBport = Integer.parseInt(System.getenv("capDBport"));
		
		String capAWSBucketName = System.getenv("capAWSBucketName");
		String capAWSAccessKey = System.getenv("capAWSAccessKey");
		String capAWSSecretKey = System.getenv("capAWSSecretKey");
		String capAWSRegion = System.getenv("capAWSRegion");
		
		int capSECIdShift = Integer.parseInt(System.getenv("capSECIdShift"));
		String capSECKey = System.getenv("capSECKey");
		String capSECSalt = System.getenv("capSECSalt");
		String capSECVector = System.getenv("capSECVector");
		String capSECBearer = System.getenv("capSECBearer");
		
		assertTrue("An environment variable is not set up correctly...", 
				isValidStringEnvironmentVariable(capDBusername) &&
				isValidStringEnvironmentVariable(capDBpassword) &&
				isValidStringEnvironmentVariable(capDBhost) &&
				isValidStringEnvironmentVariable(capDBdatabase) &&
				
				isValidStringEnvironmentVariable(capAWSBucketName) &&
				isValidStringEnvironmentVariable(capAWSAccessKey) &&
				isValidStringEnvironmentVariable(capAWSSecretKey) &&
				isValidStringEnvironmentVariable(capAWSRegion) &&
				
				isValidStringEnvironmentVariable(capSECKey) &&
				isValidStringEnvironmentVariable(capSECSalt) &&
				isValidStringEnvironmentVariable(capSECVector) &&
				isValidStringEnvironmentVariable(capSECBearer) 
				);
	}
	
	@Test
	void testTestEnvironmentVariablesExist() throws NumberFormatException{
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
		String capTestBearer = System.getenv("capTestBearer");
		
		assertTrue("An environment variable is not set up correctly...",  
				isValidStringEnvironmentVariable(capTestUser) &&
				isValidStringEnvironmentVariable(capTestUser2) &&
				isValidStringEnvironmentVariable(capTestUserFirstName) &&
				isValidStringEnvironmentVariable(capTestUserLastName) &&
				isValidStringEnvironmentVariable(capTestUser2FirstName) &&
				isValidStringEnvironmentVariable(capTestUser2LastName) &&
				isValidStringEnvironmentVariable(capTestPassword) &&
				isValidStringEnvironmentVariable(capTestPassword2) &&
				isValidStringEnvironmentVariable(capTestSessionToken) &&
				isValidStringEnvironmentVariable(capTestSessionToken2) &&
				isValidStringEnvironmentVariable(capTestImageName) &&
				isValidStringEnvironmentVariable(capTestImageName2) &&
				isValidStringEnvironmentVariable(capTestEncodedJobId) &&
				isValidStringEnvironmentVariable(capTestEncodedJobId2) &&
				isValidStringEnvironmentVariable(capTestImagePath) &&
				isValidStringEnvironmentVariable(capTestImagePath2) &&
				isValidStringEnvironmentVariable(capTestBearer)
				);
	}
	
	@Test
	void testIsValidStringEnvironmentVariableRegular() {
		String envValue = "hello world";
		assertTrue("String environment variable was valid and was considered invalid ...",
				this.isValidStringEnvironmentVariable(envValue));
	}
	
	@Test
	void testIsValidStringEnvironmentVariableBoundaryIn() {
		String envValue = "h";
		assertTrue("String environment variable was valid and was considered invalid ...",
				this.isValidStringEnvironmentVariable(envValue));
	}
	
	@Test
	void testIsValidStringEnvironmentVariableBoundaryOut() {
		String envValue = "";
		assertFalse("String environment variable was invalid and was considered valid ...",
				this.isValidStringEnvironmentVariable(envValue));
	}
	
	@Test
	void testIsValidStringEnvironmentVariableException() {
		String envValue = null;
		assertFalse("String environment variable was invalid and was considered valid ...",
				this.isValidStringEnvironmentVariable(envValue));
	}
	
	private boolean isValidStringEnvironmentVariable(String envVariable) {
		return envVariable != null && envVariable.length() > 0 && !envVariable.equals("null");
	}

}
