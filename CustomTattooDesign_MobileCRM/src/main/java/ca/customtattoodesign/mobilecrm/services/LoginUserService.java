package ca.customtattoodesign.mobilecrm.services;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.Base64;
import java.util.regex.Pattern;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import ca.customtattoodesign.mobilecrm.beans.LoginUser;
import ca.customtattoodesign.mobilecrm.beans.SessionUser;
import ca.customtattoodesign.mobilecrm.dao.TornadoHuntersDao;

@Service
public class LoginUserService {
	
	public static final int PASSWORD_LENGTH = 64;
	public static final String PASSWORD_REGEX = String.format("[0-9a-zA-Z]{%d}", PASSWORD_LENGTH);
	
	public SessionUser isValidLogin(LoginUser user) {
		boolean isValidUser = false;
		String sessionToken = "";
		
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
			
			if (isValidUser) {
				try {
					sessionToken = generateSessionToken(user);
				}
				catch (NoSuchAlgorithmException e) {
					throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal hashing algorithm failed...");
				}
				
				try {
					boolean wasSuccessful = TornadoHuntersDao.getInstance().setUserSessionToken(user, sessionToken);
					if (!wasSuccessful) {
						throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Database saving session failed...");
					}
				}
				catch (SQLException e) {
					e.printStackTrace();
					throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Database connection failed...");
				}
				catch (ClassNotFoundException e) {
					throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Database driver failed...");
				}
			}

		}
		else {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid user parameters...");
		}
		
		SessionUser resultingUser = SessionUser.builder().isValidUser(isValidUser).sessionToken(sessionToken).build();
		
		return resultingUser;
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
	
	public String generateSessionToken(LoginUser user) throws NoSuchAlgorithmException {
		String sessionId = "";
		
		if (user != null && isUsernameNotNullOrEmpty(user.getUsername()) && user.isPersistent()) {
			String username = user.getUsername();
			SecureRandom rand = new SecureRandom();
			
			int randomNumber = rand.nextInt();
			long currentMilliseconds = System.currentTimeMillis();
			
			String sessionStrForGeneration = String.format("%s-%d-%d", username, randomNumber, currentMilliseconds);
			
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(sessionStrForGeneration.getBytes(StandardCharsets.UTF_8));
			sessionId = Base64.getEncoder().encodeToString(hash);
		}
		
		return sessionId;
	}

}
