package ca.customtattoodesign.mobilecrm.services;

import static org.junit.Assert.*;

import java.security.NoSuchAlgorithmException;
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
import org.springframework.web.server.ResponseStatusException;

import ca.customtattoodesign.mobilecrm.beans.UserLogin;
import ca.customtattoodesign.mobilecrm.dao.TornadoHuntersDao;
import ca.customtattoodesign.mobilecrm.beans.SessionLogin;
import ca.customtattoodesign.mobilecrm.beans.SessionUser;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LoginServiceTest {

	@Autowired
	private LoginService loginService;
	
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
		String generatedString = capTestPassword;
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
	public void testIsStringNotNullOrEmptyRegular(){
		String str = "Hello";
		boolean isStringNotNullOrEmpty = loginService.isStringNotNullOrEmpty(str);
		assertTrue("String not null or empty function failed ...", isStringNotNullOrEmpty);
	}
	
	@Test
	public void testIsStringNotNullOrEmptyBoundaryIn(){
		String str = "1";
		boolean isStringNotNullOrEmpty = loginService.isStringNotNullOrEmpty(str);
		assertTrue("String not null or empty function failed ...", isStringNotNullOrEmpty);
	}
	
	@Test
	public void testIsStringNotNullOrEmptyBoundaryOut(){
		String str = "";
		boolean isStringNotNullOrEmpty = loginService.isStringNotNullOrEmpty(str);
		assertFalse("String not null or empty function failed ...", isStringNotNullOrEmpty);
	}
	
	@Test
	public void testIsStringNotNullOrEmptyException(){
		String str = null;
		boolean isStringNotNullOrEmpty = loginService.isStringNotNullOrEmpty(str);
		assertFalse("String not null or empty function failed ...", isStringNotNullOrEmpty);
	}
	
	@Test
	public void testIsUserIdValidRegular() {
		int userId = capTestId;
		boolean isValidId = loginService.isUserIdValid(userId);
		assertTrue("User Id was invalid when it should have been valid...", isValidId);
	}
	
	@Test
	public void testIsUserIdValidBoundaryIn() {
		int userId = 1;
		boolean isValidId = loginService.isUserIdValid(userId);
		assertTrue("User Id was invalid when it should have been valid...", isValidId);
	}
	
	@Test
	public void testIsUserIdValidBoundaryOut() {
		int userId = 0;
		boolean isValidId = loginService.isUserIdValid(userId);
		assertFalse("User Id was valid when it should have been invalid...", isValidId);
	}
	
	@Test
	public void testIsUserIdValidException() {
		int userId = -10;
		boolean isValidId = loginService.isUserIdValid(userId);
		assertFalse("User Id was valid when it should have been invalid...", isValidId);
	}
	
	@Test
	public void testIsValidUserLoginRegular() {
		String username = capTestUser;
		String password = capTestPassword;
		
		UserLogin user = UserLogin.builder().username(username).password(password).build();
		boolean isValidUser = loginService.isValidUserLogin(user); 
		assertTrue("User was not validated correctly locally...", isValidUser);
	}
	
	@Test
	public void testIsValidUserLoginBoundaryIn() {
		String username = capTestUser2;
		String password = capTestPassword2;
		
		UserLogin user = UserLogin.builder().username(username).password(password).build();
		boolean isValidUser = loginService.isValidUserLogin(user); 
		assertTrue("User was not validated correctly locally...", isValidUser);
	}
	
	@Test
	public void testIsValidUserLoginBoundaryOut() {
		String username = capTestUser2;
		String password = capTestPassword2 + "0";
		
		UserLogin user = UserLogin.builder().username(username).password(password).build();
		boolean isValidUser = loginService.isValidUserLogin(user); 
		assertFalse("User was not validated correctly locally...", isValidUser);
	}
	
	@Test
	public void testIsValidUserLoginException() {
		String username = null;
		String password = capTestPassword;
		
		UserLogin user = UserLogin.builder().username(username).password(password).build();
		boolean isValidUser = loginService.isValidUserLogin(user); 
		assertFalse("User was not validated correctly...", isValidUser);
	}
	
	@Test
	public void testIsValidSessionLoginRegular(){
		int userId = capTestId;
		String username = capTestUser;
		String sessionToken = capTestSessionToken;
		
		SessionLogin sessionLogin = SessionLogin.builder().username(username).sessionToken(sessionToken).id(userId).build();
		boolean isValidSession = loginService.isValidSessionLogin(sessionLogin); 
		assertTrue("Session Login was not validated correctly locally...", isValidSession);
	}
	
	@Test
	public void testIsValidSessionLoginBoundaryIn(){
		int userId = capTestId2;
		String username = capTestUser2;
		String sessionToken = capTestSessionToken2;
		
		SessionLogin sessionLogin = SessionLogin.builder().username(username).sessionToken(sessionToken).id(userId).build();
		boolean isValidSession = loginService.isValidSessionLogin(sessionLogin); 
		assertTrue("Session Login was not validated correctly locally...", isValidSession);
	}
	
	@Test
	public void testIsValidSessionLoginBoundaryOut(){
		int userId = capTestId2;
		String username = capTestUser2;
		String sessionToken = "";
		
		SessionLogin sessionLogin = SessionLogin.builder().username(username).sessionToken(sessionToken).id(userId).build();
		boolean isValidSession = loginService.isValidSessionLogin(sessionLogin); 
		assertFalse("Session Login was not validated correctly locally...", isValidSession);
	}
	
	@Test
	public void testIsValidSessionLoginException(){
		int userId = capTestId2;
		String username = null;
		String sessionToken = null;
		
		SessionLogin sessionLogin = SessionLogin.builder().username(username).sessionToken(sessionToken).id(userId).build();
		boolean isValidSession = loginService.isValidSessionLogin(sessionLogin); 
		assertFalse("Session Login was not validated correctly locally...", isValidSession);
	}
	
	@Test
	public void testGetSessionUserRegular() {
		String username = capTestUser;
		String password = capTestPassword;
		
		UserLogin user = UserLogin.builder().username(username).password(password).build();
		
		SessionUser sessionUser = loginService.getSessionUser(user);
		
		assertTrue("User session was not generated correctly...", sessionUser != null 
				&& sessionUser.isValidUser() && !sessionUser.getSessionToken().equals(""));
	}
	
	@Test
	public void testGetSessionUserBoundaryIn() {
		String username = capTestUser2;
		String password = capTestPassword2;
		
		UserLogin user = UserLogin.builder().username(username).password(password).build();
		
		SessionUser sessionUser = loginService.getSessionUser(user); 

		assertTrue("User session was not generated correctly...", sessionUser != null 
				&& sessionUser.isValidUser() == true && !sessionUser.getSessionToken().equals(""));
	}
	
	@Test(expected = ResponseStatusException.class)
	public void testGetSessionUserBoundaryOut() {
		String username = capTestUser;
		String password = capTestPassword + "0";
		
		UserLogin user = UserLogin.builder().username(username).password(password).build();
		
		SessionUser sessionUser = loginService.getSessionUser(user); 

		assertFalse("User session was not generated correctly...", sessionUser != null 
				&& sessionUser.isValidUser() && !sessionUser.getSessionToken().equals(""));
	}
	
	@Test(expected = ResponseStatusException.class)
	public void testGetSessionUserException() {
		String username = null;
		String password = capTestPassword;
		
		UserLogin user = UserLogin.builder().username(username).password(password).build();
		
		SessionUser sessionUser = loginService.getSessionUser(user); 

		assertFalse("User session was not generated correctly...", sessionUser != null 
				&& sessionUser.isValidUser() && !sessionUser.getSessionToken().equals(""));
	}
	
	@Test
	public void testGetSessionUser2Regular() throws SQLException{
		
		int userId = capTestId;
		String username = capTestUser;
		String sessionToken = capTestSessionToken;
		
		SessionLogin sessionLogin = SessionLogin.builder().username(username).sessionToken(sessionToken).id(userId).build();
		
		SessionUser sessionUser = loginService.getSessionUser(sessionLogin); 
		
		
		assertTrue("User session was not generated correctly...", sessionUser != null 
				&& sessionUser.isValidUser() && !sessionUser.getSessionToken().equals(""));
	}
	
	@Test
	public void testGetSessionUser2BoundaryIn() throws SQLException{
		
		int userId = capTestId2;
		String username = capTestUser2;
		String sessionToken = capTestSessionToken2;
		
		SessionLogin sessionLogin = SessionLogin.builder().username(username).sessionToken(sessionToken).id(userId).build();
		
		SessionUser sessionUser = loginService.getSessionUser(sessionLogin); 
		
		assertTrue("User session was not generated correctly...", sessionUser != null 
				&& sessionUser.isValidUser() && !sessionUser.getSessionToken().equals(""));
	}
	
	@Test
	public void testGetSessionUser2BoundaryOut(){
		int userId = capTestId2;
		String username = capTestUser2;
		String sessionToken = capTestSessionToken2 + "0";
		
		SessionLogin sessionLogin = SessionLogin.builder().username(username).sessionToken(sessionToken).id(userId).build();
		
		SessionUser sessionUser = loginService.getSessionUser(sessionLogin); 
		
		assertFalse("User session was not generated correctly...", sessionUser != null 
				&& sessionUser.isValidUser() && !sessionUser.getSessionToken().equals(""));
	}
	
	@Test(expected = ResponseStatusException.class)
	public void testGetSessionUser2Exception(){
		int userId = capTestId2;
		String username = capTestUser2;
		String sessionToken = null;
		
		SessionLogin sessionLogin = SessionLogin.builder().username(username).sessionToken(sessionToken).id(userId).build();
		
		SessionUser sessionUser = loginService.getSessionUser(sessionLogin); 
		
		assertFalse("User session was not generated correctly...", sessionUser != null 
				&& sessionUser.isValidUser() && !sessionUser.getSessionToken().equals(""));
	}
	
	@Test
	public void testGenerateSessionTokenRegular() throws NoSuchAlgorithmException {
		String username = capTestUser;
		String password = capTestPassword;
		
		UserLogin user = UserLogin.builder().username(username).password(password).build();
		
		String sessionId = loginService.generateSessionToken(user); 

		assertNotEquals("User session was not generated correctly...", "", sessionId);
	}
	
	@Test
	public void testGenerateSessionTokenBoundaryIn() throws NoSuchAlgorithmException {
		String username = "0";
		String password = capTestPassword2;
		
		UserLogin user = UserLogin.builder().username(username).password(password).build();
		
		String sessionId = loginService.generateSessionToken(user); 

		assertNotEquals("User session was not generated correctly...", "", sessionId);
	}
	
	@Test
	public void testGenerateSessionTokenBoundaryOut() throws NoSuchAlgorithmException {
		String username = "";
		String password = capTestPassword2;
		
		UserLogin user = UserLogin.builder().username(username).password(password).build();
		
		String sessionId = loginService.generateSessionToken(user); 

		assertEquals("User session was not generated correctly...", "", sessionId);
	}
	
	@Test
	public void testGenerateSessionTokenException() throws NoSuchAlgorithmException {
		String username = null;
		String password = capTestPassword2;
		
		UserLogin user = UserLogin.builder().username(username).password(password).build();
		
		String sessionId = loginService.generateSessionToken(user); 

		assertEquals("User session was not generated correctly...", "", sessionId);
	}
	
}
