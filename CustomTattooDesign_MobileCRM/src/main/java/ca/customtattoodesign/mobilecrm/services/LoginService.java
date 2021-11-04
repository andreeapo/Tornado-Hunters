package ca.customtattoodesign.mobilecrm.services;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.Base64;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import ca.customtattoodesign.mobilecrm.beans.SessionLogin;
import ca.customtattoodesign.mobilecrm.beans.SessionUser;
import ca.customtattoodesign.mobilecrm.beans.UserLogin;
import ca.customtattoodesign.mobilecrm.dao.TornadoHuntersDao;

/**
 * The {@code LoginService} class is used to verify login credentials, such as making the username
 * 		and password hashes are valid formats, generating session tokens and more.
 * 
 * @author Roman Krutikov
 *
 */
@Service
public class LoginService {
	
	public static final int PASSWORD_LENGTH = 64;
	public static final String PASSWORD_REGEX = String.format("[0-9a-zA-Z]{%d}", PASSWORD_LENGTH);
	
	private Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * Determines whether the UserLogin user is a user that exists in the database, and generates a session token. 
	 * Information about the user is also fetched. All this information is saved within the SessionUser object.
	 * 
	 * @param userLogin the UserLogin that is being authenticated
	 * @return a SessionUser object which holds info on if the user has been authenticated and their session token,
	 * 		and general info about the user
	 */
	public SessionUser getSessionUser(UserLogin userLogin) {
		boolean isValidFormatUser = isValidUserLogin(userLogin);
		boolean isValidDBUser;
		String sessionToken = "";
		SessionUser sessionUser = new SessionUser();
		
		if (isValidFormatUser) {
			try {
				isValidDBUser = TornadoHuntersDao.getInstance()
						.isUserLoginAuthorized(userLogin.getUsername(), userLogin.getPassword());
				sessionUser.setValidUser(isValidDBUser);
			}
			catch (IllegalArgumentException e) {
				LOGGER.error(e.getMessage());
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getLocalizedMessage());
			}
			catch (SQLException e) {
				LOGGER.error(e.getMessage());
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Database connection failed...");
			}
			catch (SecurityException e) {
				LOGGER.error(e.getMessage());
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Cannot access user environment variables...");
			}
		}
		else {
			LOGGER.error("Invalid username ('{}') or password ('{}')", userLogin.getUsername(), userLogin.getPassword());
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid user parameters...");
		}
		
		if (isValidDBUser) {
			try {
				boolean wasSuccessful = TornadoHuntersDao.getInstance()
						.fetchSessionUserId(sessionUser, userLogin.getUsername(), userLogin.getPassword());
				if (!wasSuccessful) {
					throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to get user fields...");
				}
			}
			catch (SQLException e) {
				LOGGER.error(e.getMessage());
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Getting user data from database failed...");
			}
			
			try {
				sessionToken = generateSessionToken(userLogin);
				boolean wasSuccessful = TornadoHuntersDao.getInstance().setUserSessionToken(sessionUser.getId(), sessionToken);
				if (!wasSuccessful) {
					throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Database saving session failed...");
				}
				else {
					sessionUser.setSessionToken(sessionToken);
				}
			}
			catch (SQLException e) {
				LOGGER.error(e.getMessage());
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Database connection failed...");
			}
			catch (NoSuchAlgorithmException e) {
				LOGGER.error(e.getMessage());
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal hashing algorithm failed...");
			}
			
			try {
				boolean wasSuccessful = TornadoHuntersDao.getInstance()
						.fetchSessionUserFields(sessionUser, userLogin.getUsername(), sessionUser.getSessionToken());
				if (!wasSuccessful) {
					throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to get user fields...");
				}
			}
			catch (SQLException e) {
				LOGGER.error(e.getMessage());
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Getting user data from database failed...");
			}
		}
		
		return sessionUser;
	}
	
	/**
	 * Determines whether the SessionLogin (authentication via session token) exists in the database, and generates 
	 * 		a session token if the user exists and they requested for it. Information about the user is also fetched.  
	 * 		All this information is saved within the SessionUser object.
	 * 
	 * @param sessionLogin the SessionLogin that is being authenticated
	 * @return a SessionUser object which holds info on if the user has been authenticated and their session token,
	 * 		and general info about the user
	 */
	public SessionUser getSessionUser(SessionLogin sessionLogin) {
		
		boolean isValidFormatSessionUser = isValidSessionLogin(sessionLogin);
		boolean isValidDBUser = false;
		SessionUser sessionUser = new SessionUser();
		
		if (isValidFormatSessionUser) {
			
			try {
				isValidDBUser = TornadoHuntersDao.getInstance()
						.isSessionLoginAuthorized(sessionLogin.getUsername(), sessionLogin.getSessionToken());
				sessionUser.setValidUser(isValidDBUser);
			}
			catch (SQLException e) {
				LOGGER.error(e.getMessage());
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Database connection failed...");
			}
			catch (SecurityException e) {
				LOGGER.error(e.getMessage());
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Cannot access user environment variables...");
			}
			
			if (isValidDBUser) {
				
				sessionUser.setId(sessionLogin.getId());
				sessionUser.setSessionToken(sessionLogin.getSessionToken());
				
				try {
					boolean wasSuccessful = TornadoHuntersDao.getInstance()
							.fetchSessionUserFields(sessionUser, sessionLogin.getUsername(), sessionLogin.getSessionToken());
					if (!wasSuccessful) {
						throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to get user fields...");
					}
					
				}
				catch (SQLException e) {
					LOGGER.error(e.getMessage());
					throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Getting user data from database failed...");
				}
			}
			else {
				sessionUser.setSessionToken("");
			}

		}
		else {
			LOGGER.error("Invalid username ('{}') or session token ('{}')", 
					sessionLogin.getUsername(), sessionLogin.getSessionToken());
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid session login parameters...");
		}
		
		return sessionUser;
	}
	
	/**
	 * Checks whether or not a UserLogin's fields are in the proper format, if
	 * they are in a proper format that means this user could exist in the database.
	 * 
	 * @param user the UserLogin that is being authenticated
	 * @return {@code true} if the UserLogin's fields could make a user in the database<br>
	 *	       {@code false} if the UserLogin's fields would never appear in the database
	 */
	public boolean isValidUserLogin(UserLogin user) {
		String userPassword = user.getPassword();
		boolean isPasswordSha256 = isPasswordLengthCorrect(userPassword) && isPasswordRegexCorrect(userPassword);
		
		String userName = user.getUsername();
		boolean isUsernameValid = isUsernameNotNullOrEmpty(userName);
		
		return isPasswordSha256 && isUsernameValid;
	}
	
	/**
	 * Checks whether or not a SessionLogin's fields are in the proper format, if
	 * they are in a proper format that means this user could exist in the database.
	 * 
	 * @param sessionLogin the SessionLogin that is being authenticated
	 * @return {@code true} if the SessionLogin's fields could exist in the database<br>
	 *	       {@code false} if the SessionLogin's fields would never appear in the database
	 */
	public boolean isValidSessionLogin(SessionLogin sessionLogin) {
		String username = sessionLogin.getUsername();
		boolean isUsernameValid = isUsernameNotNullOrEmpty(username);
		
		String sessionToken = sessionLogin.getSessionToken();
		boolean isSessionTokenValid = isSessionTokenNotNullOrEmpty(sessionToken);
		
		int userId = sessionLogin.getId();
		boolean isUserIdValid = isUserIdValid(userId);
		
		return isUsernameValid && isSessionTokenValid && isUserIdValid;
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
		return isStringNotNullOrEmpty(username);
	}
	
	/**
	 * Checks whether or not the String (sessionToken) is empty or null.
	 * 
	 * @param sessionToken is the String that is being checked
	 * @return {@code true} if String (sessionToken) is not null or empty <br>
			   {@code false} if String (sessionToken) is null or empty
	 */
	public boolean isSessionTokenNotNullOrEmpty(String sessionToken) {
		return isStringNotNullOrEmpty(sessionToken);
	}
	
	/**
	 * Checks whether or not the String (str) is empty or null.
	 * 
	 * @param str is the String that is being checked
	 * @return {@code true} if String (str) is not null or empty <br>
			   {@code false} if String (str) is null or empty
	 */
	public boolean isStringNotNullOrEmpty(String str) {
		return (str != null && str.length() > 0);
	}
	
	/**
	 * 
	 * @param userId the user id that is being checked
	 * @return {@code true} if the id could exist in the database <br>
			   {@code false} if the id could never exist in the database
	 */
	public boolean isUserIdValid(int userId) {
		return userId > 0;
	}
	
	/**
	 * Generates a unique session token for a UserLogin.
	 * 
	 * @param user is a UserLogin for whom the session token is being generated
	 * 
	 * @return a String session token
	 * @throws NoSuchAlgorithmException if the algorithm specified for making the token is invalid
	 */
	public String generateSessionToken(UserLogin user) throws NoSuchAlgorithmException {
		String sessionId = "";
		
		if (isUsernameNotNullOrEmpty(user.getUsername())) {
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
