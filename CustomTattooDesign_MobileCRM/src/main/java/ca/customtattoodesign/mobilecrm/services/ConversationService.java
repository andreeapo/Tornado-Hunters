package ca.customtattoodesign.mobilecrm.services;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import ca.customtattoodesign.mobilecrm.beans.ConversationLogin;
import ca.customtattoodesign.mobilecrm.beans.Message;
import ca.customtattoodesign.mobilecrm.dao.TornadoHuntersDao;

/**
 * The {@code ConversationService} class is used to send and fetch messages using a Data Access Object, it
 * 		acts as a middle-man between the RESTful API and the Database.
 * 
 * @author Roman Krutikov
 *
 */
@Service
public class ConversationService {

	@Autowired
	private JobService jobService;
	
	@Autowired
	private AWSService awsService;
	
	private Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * Fetches all messages for a specified job.
	 * 
	 * @param convoLogin a ConversationLog object which should contain a value for the
	 * 		jobId (the ID of the job to which to send the message to)  
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
	 * @param convoLogin a ConversationLog object which should contain a value for the
	 * 		jobId (the ID of the job to which to send the message to)  
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
	 * @param convoLogin a ConversationLog object which should contain values for 
	 * 		jobId (the ID of the job to which to send the message to),  
	 * 		body (the text of the message that is being sent) and
	 *      sessionToken (a sessionToken to verify if the artist or the customer are sending the message)
	 * @return {@code true} if the String message was sent successfully<br>
	 *	       {@code false} if sending the String message failed
	 */
	public boolean sendStringMessage(ConversationLogin convoLogin) {
		boolean messageSentSuccessfully = false;
		
		if (jobService.isJobIdValid(convoLogin.getJobId())) {
			if (this.isStringMessageValid(convoLogin.getBody())) {
				try {
					messageSentSuccessfully = TornadoHuntersDao.getInstance().sendStringMessage(convoLogin.getJobId(), 
							convoLogin.getBody(), convoLogin.getSessionToken());
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
	
	/**
	 * Uploads a design draft image to the AWS S3 Storage and records it in the database based on the image
	 * and login of the user.
	 * 
	 * @param convoLogin a ConversationLogin object which should contain values for 
	 * 		jobId (the ID of the job to which to send the message to) and
	 *      sessionToken (a sessionToken to verify if the artist or the customer are sending the message)
	 * @param image a MultipartFile representation of the design draft begin sent
	 * @return {@code true} if the design draft was uploaded successfully<br>
	 *	       {@code false} if uploading the design draft failed
	 */
	public boolean sendDesignDraft(ConversationLogin convoLogin, MultipartFile image) {
		boolean recordedSuccessfully = false;
		
		int jobId = convoLogin.getJobId();
		String sessionToken = convoLogin.getSessionToken();
		if (sessionToken == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"No session token specified...");
		}
		if (jobService.isJobIdValid(jobId)) {
			int designId = -1;
			try {
				designId = TornadoHuntersDao.getInstance().getNextAvailableDesignId();
				if (designId != -1) {
					awsService.uploadDesignImage(designId, image);
					recordedSuccessfully = TornadoHuntersDao.getInstance()
							.recordDesignDraft(designId, jobId, image.getOriginalFilename(), sessionToken);
					if (!recordedSuccessfully) {
						LOGGER.warn("Failed to upload image '{}' for job '{}', with session token '{}', designId '{}' was unused",
								image.getOriginalFilename(), jobId, sessionToken, designId);
					}
				}
				else {
					LOGGER.error("Database failed to get an available design id");
					throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
							"Unable to get new design id to upload draft image...");
				}
			}
			catch (ResponseStatusException e) {
				throw e;
			}
			
			catch (SQLException e) {
				LOGGER.error(e.getMessage());
				if (e.getMessage().contains("Invalid session token") 
						|| e.getMessage().contains("Expired Session Token") || e.getMessage().contains("design id duplicated")) {
					String onlyMessage = e.getMessage().split("\\n")[0];
					throw new ResponseStatusException(HttpStatus.BAD_REQUEST, onlyMessage);
				}
				else {
					throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Database connection failed...");
				}
				
			}
		}
		
		return recordedSuccessfully;
	}
	
	/**
	 * Uploads a design draft image to the AWS S3 Storage and records it in the database based on the image
	 * and login of the user.
	 * 
	 * @param convoLogin a ConversationLogin object which should contain values for 
	 * 		jobId (the ID of the job to which to send the message to) and
	 *      sessionToken (a sessionToken to verify if the artist or the customer are sending the message)
	 * @param image a MultipartFile representation of the design draft begin sent
	 * @return {@code true} if the design draft was uploaded successfully<br>
	 *	       {@code false} if uploading the design draft failed
	 */
	public boolean sendDesignDraft(ConversationLogin convoLogin, File image) {
		boolean recordedSuccessfully = false;
		
		int jobId = convoLogin.getJobId();
		String sessionToken = convoLogin.getSessionToken();
		if (sessionToken == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"No session token specified...");
		}
		if (jobService.isJobIdValid(jobId)) {
			int designId = -1;
			try {
				designId = TornadoHuntersDao.getInstance().getNextAvailableDesignId();
				if (designId != -1) {
					awsService.uploadDesignImage(designId, image);
					recordedSuccessfully = TornadoHuntersDao.getInstance()
							.recordDesignDraft(designId, jobId, image.getName(), sessionToken);
					if (!recordedSuccessfully) {
						LOGGER.warn("Failed to upload image '{}' for job '{}', with session token '{}', designId '{}' was unused",
								image.getName(), jobId, sessionToken, designId);
					}
				}
				else {
					LOGGER.error("Database failed to get an available design id");
					throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
							"Unable to get new design id to upload draft image...");
				}
			}
			catch (ResponseStatusException e) {
				throw e;
			}
			catch (SQLException e) {
				LOGGER.error(e.getMessage());
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Database connection failed...");
			}
		}
		
		return recordedSuccessfully;
	}
		
	
}
