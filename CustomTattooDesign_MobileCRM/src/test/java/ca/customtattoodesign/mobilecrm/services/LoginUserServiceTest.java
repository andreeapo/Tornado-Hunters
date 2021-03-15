package ca.customtattoodesign.mobilecrm.services;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import ca.customtattoodesign.mobilecrm.services.LoginUserService;

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
		String generatedString = "43baef7b86aea46a09bf3c67acd1a8e830e642f1cf33ee3081ebb1ba845d1838";
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
	
}
