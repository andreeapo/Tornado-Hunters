package ca.customtattoodesign.mobilecrm.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import ca.customtattoodesign.mobilecrm.beans.UserLogin;
import ca.customtattoodesign.mobilecrm.beans.ClaimJobLogin;
import ca.customtattoodesign.mobilecrm.beans.ConversationLogin;
import ca.customtattoodesign.mobilecrm.beans.Job;
import ca.customtattoodesign.mobilecrm.beans.Message;
import ca.customtattoodesign.mobilecrm.beans.SessionLogin;
import ca.customtattoodesign.mobilecrm.beans.SessionUser;
import ca.customtattoodesign.mobilecrm.services.ConversationService;
import ca.customtattoodesign.mobilecrm.services.JobService;
import ca.customtattoodesign.mobilecrm.services.LoginService;

/**
 * The {@code RestController} class is used for handling RESTful API requests.
 * 
 * @author Roman Krutikov
 */
@RequestMapping("api")
@org.springframework.web.bind.annotation.RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RestController {
	
	private Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private LoginService loginService;
	
	@Autowired
	private JobService jobService;
	
	@Autowired
	private ConversationService conversationService;

	/**
	 * Authenticates the credentials of the user and returns an object with info on whether or not the user is 
	 * valid, their session token and general info about the user.
	 * 
	 * @param user is a UserLogin that is attempting to authenticate their login credentials
	 * @return {@code SessionUser} object which contains info on whether on whether or not the user is 
	 * 		valid, their session token and general info about the user
	 * @throws ResponseStatusException gives details on which type of exception was thrown internally and why
	 */
	@PostMapping("/authenticateCredentials")
	public SessionUser authenticateCredentials(HttpServletRequest request, @RequestBody @NonNull UserLogin user) 
			throws ResponseStatusException {
		
		LOGGER.info("Caller Address: '{}', Api Call Made: '{}'", request.getRemoteHost(), request.getServletPath());
		
		return loginService.getSessionUser(user);
	}
	
	/**
	 * Authenticates the session of the user and returns an object with info on whether on whether or not the user is 
	 * valid, their session token and general info about the user.
	 * 
	 * @param sessionLogin is a SessionLogin object that holds a username and their session token
	 * @return {@code SessionUser} object which contains info on whether on whether or not the user is 
	 * 		valid, their session token and general info about the user
	 * @throws ResponseStatusException gives details on which type of exception was thrown internally and why
	 */
	@PostMapping("/authenticateSession")
	public SessionUser authenticateCredentials(HttpServletRequest request,
			@RequestBody @NonNull SessionLogin sessionLogin) {
		
		LOGGER.info("Caller Address: '{}', Api Call Made: '{}'", request.getRemoteHost(), request.getServletPath());
		
		return loginService.getSessionUser(sessionLogin);
	}
	
	/**
	 * Gets a list of unclaimed jobs, will return an empty list if no unclaimed jobs are found.
	 * 
	 * @return {@code List of Job} which is a list of jobs that have not been claimed
	 * @throws ResponseStatusException gives details on which type of exception was thrown internally and why
	 */
	@PostMapping("/fetchUnclaimedJobs")
	public List<Job> fetchUnclaimedJobs(HttpServletRequest request) throws ResponseStatusException {
		
		LOGGER.info("Caller Address: '{}', Api Call Made: '{}'", request.getRemoteHost(), request.getServletPath());
		
		return jobService.fetchUnclaimedJobs();
	}
	
	/**
	 * Gets a list of artist jobs, will return an empty list if no unclaimed jobs are found.
	 * 
	 * @return {@code List of Job} which is a list of jobs that are assigned to an artist
	 * @throws ResponseStatusException gives details on which type of exception was thrown internally and why
	 */
	@PostMapping("/fetchArtistJobs")
	public List<Job> fetchUnclaimedJobs(HttpServletRequest request, @RequestBody @NonNull SessionLogin sessionLogin) 
			throws ResponseStatusException {
		
		LOGGER.info("Caller Address: '{}', Api Call Made: '{}'", request.getRemoteHost(), request.getServletPath());
		
		List<Job> jobs = new ArrayList<>();
		if (loginService.isValidSessionLogin(sessionLogin)) {
			jobs = jobService.fetchArtistJobs(sessionLogin);
		}
		else {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authorized to make this request...");
		}
		return jobs;
	}
	
	/**
	 * Claims a job for an artist, and returns if claiming the job was successful.
	 * 
	 * @param claimJobLogin object containing session information and the job id the user wants to claim
	 * @return {@code true} if the claim was successful<br>
	 *	       {@code false} if the claim failed
	 * @throws ResponseStatusException gives details on which type of exception was thrown internally and why
	 */
	@PostMapping("/claimJob")
	public boolean claimJob(HttpServletRequest request, @RequestBody @NonNull ClaimJobLogin claimJobLogin) {
		
		LOGGER.info("Caller Address: '{}', Api Call Made: '{}'", request.getRemoteHost(), request.getServletPath());
		
		boolean claimSuccessful = false;
		if (loginService.isValidSessionLogin(claimJobLogin)) {
			claimSuccessful = jobService.claimJob(claimJobLogin);
		}
		else {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authorized to make this request...");
		}
		return claimSuccessful;

	}
	
	/**
	 * Fetches all messages related to a specific job id.
	 * 
	 * @param Conversation login object which contains login and conversation info, 
	 * 		in this use case the jobId from the conversation is used while the other fields can contain default values
	 * @return {@code List of Message} which are associated with the job id in the ConversationLogin.
	 * @throws ResponseStatusException gives details on which type of exception was thrown internally and why
	 */
	@PostMapping("/fetchJobMessages")
	public List<Message> fetchJobMessages(HttpServletRequest request, @RequestBody @NonNull ConversationLogin convoLogin) {
		
		LOGGER.info("Caller Address: '{}', Api Call Made: '{}'", request.getRemoteHost(), request.getServletPath());
		
		return conversationService.fetchJobMessages(convoLogin);
	}
	
	/**
	 * Fetches unread messages related to a specific job id.
	 * 
	 * @param Conversation login object which contains login and conversation info,
	 * 		in this use case the jobId from the conversation is used while the other fields can contain default values
	 * @return {@code List of Message} which are associated with the job id in the ConversationLogin.
	 * @throws ResponseStatusException gives details on which type of exception was thrown internally and why
	 */
	@PostMapping("/fetchUnreadJobMessages")
	public List<Message> fetchUnreadJobMessages(HttpServletRequest request, @RequestBody @NonNull ConversationLogin convoLogin) {
		
		LOGGER.info("Caller Address: '{}', Api Call Made: '{}'", request.getRemoteHost(), request.getServletPath());
		
		return conversationService.fetchUnreadJobMessages(convoLogin);
	}
	
	/**
	 * Sends a text (String) message to a chat within a job based on the jobId
	 * 
	 * @param Conversation login object which contains login and conversation info, 
	 * 		using jobId, sessionToken and body fields while the other fields can contain default values
	 * @return {@code true} if the string message within the conversation login object was sent successfully<br>
	 *	       {@code false} if the string message was not sent
	 * @throws ResponseStatusException gives details on which type of exception was thrown internally and why
	 */
	@PostMapping("/sendStringMessage")
	public boolean sendStringMessage(HttpServletRequest request, @RequestBody @NonNull ConversationLogin convoLogin) {
		
		LOGGER.info("Caller Address: '{}', Api Call Made: '{}'", request.getRemoteHost(), request.getServletPath());
		boolean sentSuccessfully = false;
		
		if (convoLogin.getSessionToken() != null) {
			sentSuccessfully = conversationService.sendStringMessage(convoLogin);
		}
		else {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authorized to make this request...");
		}
		return sentSuccessfully;
	}
	
	/**
	 * A simple method that runs "Pong!", this method can be used for testing if the API is online.
	 * 
	 * @return {@code Pong!}
	 */
	@GetMapping("/ping")
	public String ping(HttpServletRequest request) {

		LOGGER.info("Caller Address: '{}', Api Call Made: '{}'", request.getRemoteHost(), request.getServletPath());
		
		return "Pong!";
	}
	
}
