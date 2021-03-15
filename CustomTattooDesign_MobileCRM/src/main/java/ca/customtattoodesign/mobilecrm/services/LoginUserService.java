package ca.customtattoodesign.mobilecrm.services;

import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

@Service
public class LoginUserService {
	
	public static final int PASSWORD_LENGTH = 64;
	public static final String PASSWORD_REGEX = String.format("[0-9a-z]{%d}", PASSWORD_LENGTH);
	
	public boolean isPasswordLengthCorrect(String password) {
		return (password != null && password.length() == PASSWORD_LENGTH);
	}
	
	public boolean isPasswordRegexCorrect(String password) {
		return (password != null && Pattern.matches(PASSWORD_REGEX, password));
	}

	public boolean isUsernameNotNullOrEmpty(String username) {
		return (username != null && username.length() > 0);
	}

}
