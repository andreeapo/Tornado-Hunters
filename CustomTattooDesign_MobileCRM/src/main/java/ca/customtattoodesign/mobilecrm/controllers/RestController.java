package ca.customtattoodesign.mobilecrm.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import ca.customtattoodesign.mobilecrm.beans.LoginUser;
import ca.customtattoodesign.mobilecrm.beans.SessionUser;
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
	private LoginService loginUserService;

	/**
	 * Authenticates the credentials of the user and returns an object with info on whether or not the user is 
	 * valid and/or their session token.
	 * 
	 * @param response handles adding special headers to the HTTP response object
	 * @param user is a LoginUser that is attempting to authenticate their login credentials
	 * @return {@code SessionUser} object which contains info on whether or not the user is valid 
	 * 		and/or their session token
	 * @throws ResponseStatusException gives details on which type of exception was thrown internally and why.
	 */
	@PostMapping("/authenticateCredentials")
	public SessionUser authenticateCredentials(HttpServletRequest request, @RequestBody @NonNull LoginUser user) 
			throws ResponseStatusException {
		
		LOGGER.info("Caller Address: '{}', Api Call Made: '{}'", request.getRemoteHost(), request.getServletPath());
		
		return loginUserService.getSessionUser(user);
	}
	
	/**
	 * A simple method that runs "Pong!", this method can be used for testing if the API is online.
	 * 
	 * @param response handles adding special headers to the HTTP response object
	 * @return {@code Pong!}
	 */
	@GetMapping("/ping")
	public String ping(HttpServletRequest request) {

		LOGGER.info("Caller Address: '{}', Api Call Made: '{}'", request.getRemoteHost(), request.getServletPath());
		
		return "Pong!";
	}
	
}
