package ca.customtattoodesign.mobilecrm.services;

import java.sql.SQLException;
import java.util.regex.Pattern;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import ca.customtattoodesign.mobilecrm.beans.LoginUser;
import ca.customtattoodesign.mobilecrm.dao.TornadoHuntersDao;

@Service
public class LoginUserService {
	
	public static final int PASSWORD_LENGTH = 64;
	public static final String PASSWORD_REGEX = String.format("[0-9a-zA-Z]{%d}", PASSWORD_LENGTH);
	
	public boolean isValidLogin(LoginUser user) {
		boolean isValidUser = false;
		
		String userPassword = user.getPassword();
		boolean isPasswordSha256 = isPasswordLengthCorrect(userPassword) && isPasswordRegexCorrect(userPassword);
		
		String userName = user.getUsername();
		boolean isUsernameValid = isUsernameNotNullOrEmpty(userName);
		
		if (isPasswordSha256 && isUsernameValid) {
			try {
				isValidUser = TornadoHuntersDao.getInstance().isUserAuthorized(user);
			}
			catch (NullPointerException | NumberFormatException e) {
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getLocalizedMessage());
			}
			catch (SQLException e) {
				e.printStackTrace();
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Database connection failed...");
			}
			catch (SecurityException e) {
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Cannot access user environment variables...");
			}
			catch (ClassNotFoundException e) {
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Database driver failed...");
			}

		}
		else {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid user parameters...");
		}
		
		return isValidUser;
	}
	
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
