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

/**
 * The {@code LoginUserService} class is used to verify login credentials, such as making the username
 * 		and password hashes are valid formats, generating session tokens and more.
 * 
 * @author Roman Krutikov
 *
 */
@Service
public class LoginUserService {
	
	public static final int PASSWORD_LENGTH = 64;
	public static final String PASSWORD_REGEX = String.format("[0-9a-zA-Z]{%d}", PASSWORD_LENGTH);
	
	/**
	 * Determines whether the LoginUser user is a user that exists in the database, and generates a session token
	 * 	 if the user exists and they requested for it. All this information is saved within the SessionUser object.
	 * 
	 * @param user the LoginUser that is being authenticated
	 * @return a SessionUser object which will 
	 */
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
	
	/**
	 * Checks whether or not the String (password) is the correct length.
	 * 
	 * @param password is the String that is being checked
	 * @return {@code true} if String (password) length is correct <br>
			   {@code false} if String (password) length is incorrect
	 */
	public boolean isPasswordLengthCorrect(String password) {
		return (password != null && password.length() == PASSWORD_LENGTH);
	}
	
	/**
	 * Checks whether or not the String (password) matches the required pattern of a password.
	 * 
	 * @param password is the String that is being checked
	 * @return {@code true} if String (password) matches the required password pattern<br>
			   {@code false} if String (password) does not match the required password pattern
	 */
	public boolean isPasswordRegexCorrect(String password) {
		return (password != null && Pattern.matches(PASSWORD_REGEX, password));
	}

	/**
	 * Checks whether or not the String (username) is empty or null.
	 * 
	 * @param username is the String that is being checked
	 * @return {@code true} if String (username) is not null or empty <br>
			   {@code false} if String (username) is null or empty
	 */
	public boolean isUsernameNotNullOrEmpty(String username) {
		return (username != null && username.length() > 0);
	}
	
	/**
	 * Generates a unique session token for a LoginUser.
	 * 
	 * @param user is a LoginUser for whom the session token is being generated
	 * @return a String session token
	 * @throws NoSuchAlgorithmException if the algorithm specified for making the token is invalid
	 */
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
