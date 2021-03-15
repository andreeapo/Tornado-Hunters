package ca.customtattoodesign.mobilecrm.services;

import static org.junit.Assert.*;

import java.security.NoSuchAlgorithmException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.server.ResponseStatusException;

import ca.customtattoodesign.mobilecrm.beans.LoginUser;
import ca.customtattoodesign.mobilecrm.beans.SessionUser;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LoginUserServiceTest {

	@Autowired
	private LoginUserService loginUserService;
	
	@Test
	public void testIsPasswordLengthCorrectRegular() {
		String generatedString = "";
		for (int i = 0; i < LoginUserService.PASSWORD_LENGTH; i++) {
			generatedString += "a";
		}
		boolean isPasswordLengthCorrect = loginUserService.isPasswordLengthCorrect(generatedString);
		assertTrue("Password length function failed...", isPasswordLengthCorrect == true);
	}
	
	@Test
	public void testIsPasswordLengthCorrectBoundaryIn() {
		String generatedString = "";
		for (int i = 0; i < LoginUserService.PASSWORD_LENGTH; i++) {
			generatedString += "b";
		}
		boolean isPasswordLengthCorrect = loginUserService.isPasswordLengthCorrect(generatedString);
		assertTrue("Password length function failed...", isPasswordLengthCorrect == true);
	}
	
	@Test
	public void testIsPasswordLengthCorrectBoundaryOut() {
		String generatedString = "";
		for (int i = 0; i < LoginUserService.PASSWORD_LENGTH + 1; i++) {
			generatedString += "b";
		}
		boolean isPasswordLengthCorrect = loginUserService.isPasswordLengthCorrect(generatedString);
		assertFalse("Password length function failed...", isPasswordLengthCorrect == true);
	}
	
	@Test
	public void testIsPasswordLengthCorrectException() {
		String generatedString = null;
		boolean isPasswordLengthCorrect = loginUserService.isPasswordLengthCorrect(generatedString);
		assertFalse("Password length function failed...", isPasswordLengthCorrect == true);
	}
	
	@Test
	public void testIsPasswordRegexCorrectRegular() {
		String generatedString = "tJ8ZY31n0nefxUaPKa0JOaRWetPU59sS8MZyvVqSMoMrLdE39C2WQDt1eZZGgqwN";
		boolean isPasswordRegexCorrect = loginUserService.isPasswordRegexCorrect(generatedString);
		assertTrue("Password SHA regex function failed...", isPasswordRegexCorrect == true);
	}
	
	@Test
	public void testIsPasswordRegexCorrectBoundaryIn() {
		String generatedString = "";
		for (int i = 0; i < LoginUserService.PASSWORD_LENGTH; i++) {
			generatedString += "b";
		}
		boolean isPasswordRegexCorrect = loginUserService.isPasswordRegexCorrect(generatedString);
		assertTrue("Password SHA regex function failed...", isPasswordRegexCorrect == true);
	}
	
	@Test
	public void testIsPasswordRegexCorrectBoundaryOut() {
		String generatedString = "";
		for (int i = 0; i < LoginUserService.PASSWORD_LENGTH-1; i++) {
			generatedString += "b";
		}
		generatedString += "+";
		boolean isPasswordRegexCorrect = loginUserService.isPasswordRegexCorrect(generatedString);
		assertFalse("Password SHA regex function failed...", isPasswordRegexCorrect == true);
	}
	
	@Test
	public void testIsPasswordRegexCorrectException() {
		String generatedString = null;
		boolean isPasswordRegexCorrect = loginUserService.isPasswordRegexCorrect(generatedString);
		assertFalse("Password SHA regex function failed...", isPasswordRegexCorrect == true);
	}
	
	@Test
	public void testIsUsernameNotNullOrEmptyRegular() {
		String username = "Billy";
		boolean isUsernameNotNullOrEmpty = loginUserService.isUsernameNotNullOrEmpty(username);
		assertTrue("Username not null or empty function failed ...", isUsernameNotNullOrEmpty == true);
	}
	
	@Test
	public void testIsUsernameNotNullOrEmptyBoundaryIn() {
		String username = "B";
		boolean isUsernameNotNullOrEmpty = loginUserService.isUsernameNotNullOrEmpty(username);
		assertTrue("Username not null or empty function failed ...", isUsernameNotNullOrEmpty == true);
	}
	
	@Test
	public void testIsUsernameNotNullOrEmptyBoundaryOut() {
		String username = "";
		boolean isUsernameNotNullOrEmpty = loginUserService.isUsernameNotNullOrEmpty(username);
		assertFalse("Username not null or empty function failed ...", isUsernameNotNullOrEmpty == true);
	}
	
	@Test
	public void testIsUsernameNotNullOrEmptyBoundaryException() {
		String username = null;
		boolean isUsernameNotNullOrEmpty = loginUserService.isUsernameNotNullOrEmpty(username);
		assertFalse("Username not null or empty function failed ...", isUsernameNotNullOrEmpty == true);
	}
	
	@Test
	public void testIsValidLoginRegular() {
		String username = System.getenv("capTestUser");
		String password = System.getenv("capTestPassword");
		
		LoginUser user = LoginUser.builder().username(username).password(password).build();
		SessionUser sessionUser = loginUserService.isValidLogin(user); 
		assertTrue("User was not validated correctly...", sessionUser.isValidUser() == true);
	}
	
	@Test
	public void testIsValidLoginBoundaryIn() {
		String username = System.getenv("capTestUser2");
		String password = System.getenv("capTestPassword2");
		
		LoginUser user = LoginUser.builder().username(username).password(password).build();
		SessionUser sessionUser = loginUserService.isValidLogin(user); 
		assertTrue("User was not validated correctly...", sessionUser.isValidUser() == true);
	}
	
	@Test(expected = ResponseStatusException.class)
	public void testIsValidLoginBoundaryOut() {
		String username = System.getenv("capTestUser");
		String password = System.getenv("capTestPassword") + "0";
		
		LoginUser user = LoginUser.builder().username(username).password(password).build();
		SessionUser sessionUser = loginUserService.isValidLogin(user); 
		assertFalse("User was not validated correctly...", sessionUser.isValidUser() == true);
	}
	
	@Test(expected = ResponseStatusException.class)
	public void testIsValidLoginException() {
		String username = null;
		String password = System.getenv("capTestPassword");
		
		LoginUser user = LoginUser.builder().username(username).password(password).build();
		SessionUser sessionUser = loginUserService.isValidLogin(user); 
		assertFalse("User was not validated correctly...", sessionUser.isValidUser() == true);
	}
	
	@Test
	public void testGenerateSessionTokenRegular() {
		String username = System.getenv("capTestUser");
		String password = System.getenv("capTestPassword");
		
		LoginUser user = LoginUser.builder().username(username).password(password).isPersistent(true).build();
		
		String sessionId = "";
		try {
			sessionId = loginUserService.generateSessionToken(user); 
		}
		catch (NoSuchAlgorithmException e) {}
		
		assertTrue("User session was not generated correctly...", !sessionId.equals(""));
	}
	
	@Test
	public void testGenerateSessionTokenBoundaryIn() {
		String username = System.getenv("capTestUser2");
		String password = System.getenv("capTestPassword2");
		
		LoginUser user = LoginUser.builder().username(username).password(password).isPersistent(true).build();
		
		String sessionId = "";
		try {
			sessionId = loginUserService.generateSessionToken(user); 
		}
		catch (NoSuchAlgorithmException e) {}
		
		assertTrue("User session was not generated correctly...", !sessionId.equals(""));
	}
	
	@Test
	public void testGenerateSessionTokenBoundaryOut() {
		String username = System.getenv("capTestUser2");
		String password = System.getenv("capTestPassword2");
		
		LoginUser user = LoginUser.builder().username(username).password(password).isPersistent(false).build();
		
		String sessionId = "";
		try {
			sessionId = loginUserService.generateSessionToken(user); 
		}
		catch (NoSuchAlgorithmException e) {}
		
		assertFalse("User session was not generated correctly...", !sessionId.equals(""));
	}
	
	@Test
	public void testGenerateSessionTokenException() {
		String username = null;
		String password = System.getenv("capTestPassword2");
		
		LoginUser user = LoginUser.builder().username(username).password(password).isPersistent(true).build();
		
		String sessionId = "";
		try {
			sessionId = loginUserService.generateSessionToken(user); 
		}
		catch (NoSuchAlgorithmException e) {}
		
		assertFalse("User session was not generated correctly...", !sessionId.equals(""));
	}
	
}
