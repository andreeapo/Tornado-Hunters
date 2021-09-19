package ca.customtattoodesign.mobilecrm.services;

import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import ca.customtattoodesign.mobilecrm.beans.ConversationLogin;
import ca.customtattoodesign.mobilecrm.beans.Message;
import ca.customtattoodesign.mobilecrm.dao.TornadoHuntersDao;

/**
 * The {@code ConversationService} class is used to fetch various types of jobs using a Data Access Object, it
 * 		acts as a middle-man between the RESTful API and the Database.
 * 
 * @author Roman Krutikov
 *
 */
@Service
public class ConversationService {

	@Autowired
	private JobService jobService;
	
	private Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * Fetches all messages for a specified job.
	 * 
	 * @param jobId the ID of the job for which we want to fetch messages
	 * @return A list of messages that are associated with the job id
	 */
	public List<Message> fetchJobMessages(ConversationLogin convoLogin){
		
		List<Message> messages = null;
		
		if (jobService.isJobIdValid(convoLogin.getJobId())) {
			try {
				messages = TornadoHuntersDao.getInstance().fetchJobMessages(convoLogin.getJobId());
			}
			catch (SQLException e) {
				if (e.getMessage().contains("ERROR: Job Does Not Exist")) {
					throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Job does not exist...");
				}
				else {
					LOGGER.error(e.getMessage());
					throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Database connection failed...");
				}
			}
			catch (SecurityException e) {
				LOGGER.error(e.getMessage());
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Cannot access user environment variables...");
			}
		}
		else {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid job id...");
		}
		
		if (messages == null) {
			LOGGER.error("Messages list returned null");
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not fetch messages for job...");
		}
		
		return messages;
	}
	
	/**
	 * Fetches all messages for a specified job that have not yet been read by the other party.
	 * 
	 * @param jobId the ID of the job for which we want to fetch messages
	 * @return A list of unread messages that are associated with the job id
	 */
	public List<Message> fetchUnreadJobMessages(ConversationLogin convoLogin){
		
		List<Message> messages = null;
		
		if (jobService.isJobIdValid(convoLogin.getJobId())) {
			try {
				messages = TornadoHuntersDao.getInstance().fetchUnreadJobMessages(convoLogin.getJobId());
			}
			catch (SQLException e) {
				if (e.getMessage().contains("ERROR: Job Does Not Exist")) {
					throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Job does not exist...");
				}
				else {
					LOGGER.error(e.getMessage());
					throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Database connection failed...");
				}
			}
			catch (SecurityException e) {
				LOGGER.error(e.getMessage());
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Cannot access user environment variables...");
			}
		}
		else {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid job id...");
		}
		
		if (messages == null) {
			LOGGER.error("Messages list returned null");
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not fetch unread messages for job...");
		}
		
		return messages;
	}
	
	/**
	 * Sends a String message to a job conversation and returns if the message was sent successfully.
	 * 
	 * @param jobId the ID of the job to which to send the message to
	 * @param stringMessage the text of the message that is being sent
	 * @param sessionToken a sessionToken to verify if the artist or the customer are sending the message
	 * @return {@code true} if the String message was sent successfully<br>
	 *	       {@code false} if sending the String message failed
	 */
	public boolean sendStringMessage(ConversationLogin convoLogin) {
		boolean messageSentSuccessfully = false;
		
		if (jobService.isJobIdValid(convoLogin.getJobId())) {
			if (this.isStringMessageValid(convoLogin.getBody())) {
				try {
					messageSentSuccessfully = TornadoHuntersDao.getInstance().sendStringMessage(convoLogin.getJobId(), convoLogin.getBody(), convoLogin.getSessionToken());
				}
				catch (SQLException e) {
					if (e.getMessage().contains("ERROR: Job Does Not Exist")) {
						throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Job does not exist...");
					}
					else {
						LOGGER.error(e.getMessage());
						throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Database connection failed...");
					}
				}
				catch (SecurityException e) {
					LOGGER.error(e.getMessage());
					throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Cannot access user environment variables...");
				}
			}
			else {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "String message was empty...");
			}
			
		}
		
		return messageSentSuccessfully;
		
	}
	
	/**
	 * Returns the string specified meets the qualifications of a string message.
	 * 
	 * @param stringMessage a String with the desired message.
	 * @return {@code true} if the string message is valid<br>
	 *	       {@code false} if the string message is invalid
	 */
	public boolean isStringMessageValid(String stringMessage) {
		return stringMessage != null && stringMessage.length() > 0;
	}
	
}
