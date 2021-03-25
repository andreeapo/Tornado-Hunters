package ca.customtattoodesign.mobilecrm.services;

import static org.junit.Assert.*;

import java.security.NoSuchAlgorithmException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.server.ResponseStatusException;

import ca.customtattoodesign.mobilecrm.beans.UserLogin;
import ca.customtattoodesign.mobilecrm.beans.SessionUser;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LoginServiceTest {

	@Autowired
	private LoginService loginService;
	
	@Test
	public void testIsPasswordLengthCorrectRegular() {
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < LoginService.PASSWORD_LENGTH; i++) {
			stringBuilder.append("a");
		}
		boolean isPasswordLengthCorrect = loginService.isPasswordLengthCorrect(stringBuilder.toString());
		assertTrue("Password length function failed...", isPasswordLengthCorrect);
	}
	
	@Test
	public void testIsPasswordLengthCorrectBoundaryIn() {
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < LoginService.PASSWORD_LENGTH; i++) {
			stringBuilder.append("b");
		}
		boolean isPasswordLengthCorrect = loginService.isPasswordLengthCorrect(stringBuilder.toString());
		assertTrue("Password length function failed...", isPasswordLengthCorrect);
	}
	
	@Test
	public void testIsPasswordLengthCorrectBoundaryOut() {
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < LoginService.PASSWORD_LENGTH + 1; i++) {
			stringBuilder.append("b");
		}
		boolean isPasswordLengthCorrect = loginService.isPasswordLengthCorrect(stringBuilder.toString());
		assertFalse("Password length function failed...", isPasswordLengthCorrect);
	}
	
	@Test
	public void testIsPasswordLengthCorrectException() {
		String generatedString = null;
		boolean isPasswordLengthCorrect = loginService.isPasswordLengthCorrect(generatedString);
		assertFalse("Password length function failed...", isPasswordLengthCorrect);
	}
	
	@Test
	public void testIsPasswordRegexCorrectRegular() {
		String generatedString = System.getenv("capTestPassword");
		boolean isPasswordRegexCorrect = loginService.isPasswordRegexCorrect(generatedString);
		assertTrue("Password SHA regex function failed...", isPasswordRegexCorrect);
	}
	
	@Test
	public void testIsPasswordRegexCorrectBoundaryIn() {
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < LoginService.PASSWORD_LENGTH; i++) {
			stringBuilder.append("b");
		}
		boolean isPasswordRegexCorrect = loginService.isPasswordRegexCorrect(stringBuilder.toString());
		assertTrue("Password SHA regex function failed...", isPasswordRegexCorrect);
	}
	
	@Test
	public void testIsPasswordRegexCorrectBoundaryOut() {
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < LoginService.PASSWORD_LENGTH-1; i++) {
			stringBuilder.append("b");
		}
		stringBuilder.append("+");
		boolean isPasswordRegexCorrect = loginService.isPasswordRegexCorrect(stringBuilder.toString());
		assertFalse("Password SHA regex function failed...", isPasswordRegexCorrect);
	}
	
	@Test
	public void testIsPasswordRegexCorrectException() {
		String generatedString = null;
		boolean isPasswordRegexCorrect = loginService.isPasswordRegexCorrect(generatedString);
		assertFalse("Password SHA regex function failed...", isPasswordRegexCorrect);
	}
	
	@Test
	public void testIsUsernameNotNullOrEmptyRegular() {
		String username = "Billy";
		boolean isUsernameNotNullOrEmpty = loginService.isUsernameNotNullOrEmpty(username);
		assertTrue("Username not null or empty function failed ...", isUsernameNotNullOrEmpty);
	}
	
	@Test
	public void testIsUsernameNotNullOrEmptyBoundaryIn() {
		String username = "B";
		boolean isUsernameNotNullOrEmpty = loginService.isUsernameNotNullOrEmpty(username);
		assertTrue("Username not null or empty function failed ...", isUsernameNotNullOrEmpty);
	}
	
	@Test
	public void testIsUsernameNotNullOrEmptyBoundaryOut() {
		String username = "";
		boolean isUsernameNotNullOrEmpty = loginService.isUsernameNotNullOrEmpty(username);
		assertFalse("Username not null or empty function failed ...", isUsernameNotNullOrEmpty);
	}
	
	@Test
	public void testIsUsernameNotNullOrEmptyException() {
		String username = null;
		boolean isUsernameNotNullOrEmpty = loginService.isUsernameNotNullOrEmpty(username);
		assertFalse("Username not null or empty function failed ...", isUsernameNotNullOrEmpty);
	}
	
	@Test
	public void testIsValidUserLoginRegular() {
		String username = System.getenv("capTestUser");
		String password = System.getenv("capTestPassword");
		
		UserLogin user = UserLogin.builder().username(username).password(password).build();
		boolean isValidUser = loginService.isValidUserLogin(user); 
		assertTrue("User was not validated correctly locally...", isValidUser);
	}
	
	@Test
	public void testIsValidUserLoginBoundaryIn() {
		String username = System.getenv("capTestUser2");
		String password = System.getenv("capTestPassword2");
		
		UserLogin user = UserLogin.builder().username(username).password(password).build();
		boolean isValidUser = loginService.isValidUserLogin(user); 
		assertTrue("User was not validated correctly locally...", isValidUser);
	}
	
	@Test
	public void testIsValidUserLoginBoundaryOut() {
		String username = System.getenv("capTestUser");
		String password = System.getenv("capTestPassword") + "0";
		
		UserLogin user = UserLogin.builder().username(username).password(password).build();
		boolean isValidUser = loginService.isValidUserLogin(user); 
		assertFalse("User was not validated correctly locally...", isValidUser);
	}
	
	@Test
	public void testIsValidUserLoginException() {
		String username = null;
		String password = System.getenv("capTestPassword");
		
		UserLogin user = UserLogin.builder().username(username).password(password).build();
		boolean isValidUser = loginService.isValidUserLogin(user); 
		assertFalse("User was not validated correctly...", isValidUser);
	}
	
	@Test
	public void testGetSessionUserRegular() {
		String username = System.getenv("capTestUser");
		String password = System.getenv("capTestPassword");
		
		UserLogin user = UserLogin.builder().username(username).password(password).build();
		
		SessionUser sessionUser = loginService.getSessionUser(user);
		
		assertTrue("User session was not generated correctly...", sessionUser != null 
				&& sessionUser.isValidUser() && !sessionUser.getSessionToken().equals(""));
	}
	
	@Test
	public void testGetSessionUserBoundaryIn() {
		String username = System.getenv("capTestUser2");
		String password = System.getenv("capTestPassword2");
		
		UserLogin user = UserLogin.builder().username(username).password(password).build();
		
		SessionUser sessionUser = loginService.getSessionUser(user); 

		assertTrue("User session was not generated correctly...", sessionUser != null 
				&& sessionUser.isValidUser() == true && !sessionUser.getSessionToken().equals(""));
	}
	
	@Test(expected = ResponseStatusException.class)
	public void testGetSessionUserBoundaryOut() {
		String username = System.getenv("capTestUser");
		String password = System.getenv("capTestPassword") + "0";
		
		UserLogin user = UserLogin.builder().username(username).password(password).build();
		
		SessionUser sessionUser = loginService.getSessionUser(user); 

		assertFalse("User session was not generated correctly...", sessionUser != null 
				&& sessionUser.isValidUser() && !sessionUser.getSessionToken().equals(""));
	}
	
	@Test(expected = ResponseStatusException.class)
	public void testGetSessionUserException() {
		String username = null;
		String password = System.getenv("capTestPassword");
		
		UserLogin user = UserLogin.builder().username(username).password(password).build();
		
		SessionUser sessionUser = loginService.getSessionUser(user); 

		assertFalse("User session was not generated correctly...", sessionUser != null 
				&& sessionUser.isValidUser() && !sessionUser.getSessionToken().equals(""));
	}
	
	@Test
	public void testGenerateSessionTokenRegular() throws NoSuchAlgorithmException {
		String username = System.getenv("capTestUser");
		String password = System.getenv("capTestPassword");
		
		UserLogin user = UserLogin.builder().username(username).password(password).build();
		
		String sessionId = loginService.generateSessionToken(user); 

		assertNotEquals("User session was not generated correctly...", "", sessionId);
	}
	
	@Test
	public void testGenerateSessionTokenBoundaryIn() throws NoSuchAlgorithmException {
		String username = "0";
		String password = System.getenv("capTestPassword2");
		
		UserLogin user = UserLogin.builder().username(username).password(password).build();
		
		String sessionId = loginService.generateSessionToken(user); 

		assertNotEquals("User session was not generated correctly...", "", sessionId);
	}
	
	@Test
	public void testGenerateSessionTokenBoundaryOut() throws NoSuchAlgorithmException {
		String username = "";
		String password = System.getenv("capTestPassword2");
		
		UserLogin user = UserLogin.builder().username(username).password(password).build();
		
		String sessionId = loginService.generateSessionToken(user); 

		assertEquals("User session was not generated correctly...", "", sessionId);
	}
	
	@Test
	public void testGenerateSessionTokenException() throws NoSuchAlgorithmException {
		String username = null;
		String password = System.getenv("capTestPassword2");
		
		UserLogin user = UserLogin.builder().username(username).password(password).build();
		
		String sessionId = loginService.generateSessionToken(user); 

		assertEquals("User session was not generated correctly...", "", sessionId);
	}
	
}
