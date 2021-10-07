package ca.customtattoodesign.mobilecrm.services;

import java.sql.SQLException;
import java.util.List;

import ca.customtattoodesign.mobilecrm.beans.ConversationLogin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import ca.customtattoodesign.mobilecrm.beans.ClaimJobLogin;
import ca.customtattoodesign.mobilecrm.beans.Job;
import ca.customtattoodesign.mobilecrm.beans.SessionLogin;
import ca.customtattoodesign.mobilecrm.dao.TornadoHuntersDao;

/**
 * The {@code JobService} class is used to fetch various types of jobs using a Data Access Object, it
 * 		acts as a middle-man between the RESTful API and the Database.
 * 
 * @author Roman Krutikov
 * @co-author Thomas Chapman
 */
@Service
public class JobService {
	
	private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	/**
	 * Fetches all unclaimed jobs from the database.
	 * 
	 * @return a List of unclaimed jobs
	 */
	public List<Job> fetchUnclaimedJobs() {
		
		List<Job> jobs = null;
		
		try {
			jobs = TornadoHuntersDao.getInstance().fetchUnclaimedJobs();
		} 
		catch (SQLException e) {
			LOGGER.error(e.getMessage());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Database connection failed...");
		}
		catch (SecurityException e) {
			LOGGER.error(e.getMessage());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Cannot access user environment variables...");
		}
		
		if (jobs == null) {
			LOGGER.error("Unclaimed jobs list returned null");
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not fetch unclaimed jobs...");
		}
		
		return jobs;
		
	}
	
	/**
	 * Fetches all artist's jobs from the database.
	 * 
	 * @param sessionLogin the Session Login object containing authentication info
	 * @return a List of unclaimed jobs
	 */
	public List<Job> fetchArtistJobs(SessionLogin sessionLogin) {
		
		List<Job> jobs = null;
		
		try {
			jobs = TornadoHuntersDao.getInstance().fetchArtistJobs(sessionLogin.getUsername(), sessionLogin.getSessionToken());
		} 
		catch (SQLException e) {
			if (e.getMessage().contains("ERROR: Not Authorized")) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid username or session token...");
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
		
		if (jobs == null) {
			LOGGER.error("Artist jobs list returned null");
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not fetch artist jobs...");
		}
		
		return jobs;
		
	}
	
	/**
	 * Claims a job for the sessionUser and returns if the claiming the job was successful.
	 * 
	 * @param sessionLogin the Session Login object containing authentication info
	 * @param jobId ID of the job that the user is trying to claim
	 * @return {@code true} if the claim was successful<br>
	 *	       {@code false} if the claim failed
	 */
	public boolean claimJob(ClaimJobLogin claimJobLogin) {
		boolean claimedSuccessfully = false;
		
		if (isJobIdValid(claimJobLogin.getJobId())) {
			try {
				claimedSuccessfully = TornadoHuntersDao.getInstance().claimJob(claimJobLogin.getJobId(), 
						claimJobLogin.getUsername(), claimJobLogin.getSessionToken());
			} 
			catch (SQLException e) {
				if (e.getMessage().contains("ERROR: Not Authorized")) {
					throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid username or session token...");
				}
				else if (e.getMessage().contains("ERROR: Job Does Not Exist")) {
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
		
		return claimedSuccessfully;
	}
	
	/**
	 * Returns if the jobId specified is of a job that could exist in the database.
	 * 
	 * @param jobId an Integer job id
	 * @return {@code true} if the id could exist<br>
	 *	       {@code false} if the id could not exist
	 */
	public boolean isJobIdValid(int jobId) {
		return jobId > 0;
	}


	/**
	 * Returns the job for the customer's unique ID
	 *
	 * @param jobAccessToken unique public token given to the customer to access their jobs
	 * @return {@code job} with the access token
	 */
    public Job fetchCustomerJob(String jobAccessToken) {
		Job job;

		try{
			job = TornadoHuntersDao.getInstance().fetchCustomerJob(jobAccessToken);
		}
		catch (SQLException e) {
			if (e.getMessage().contains("ERROR: Not Authorized")) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid username or session token...");
			}
			else {
				LOGGER.error(e.getMessage());
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Database connection failed...");
			}
		}

		return job;

    }

}
