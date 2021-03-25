package ca.customtattoodesign.mobilecrm.services;

import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import ca.customtattoodesign.mobilecrm.beans.Job;
import ca.customtattoodesign.mobilecrm.dao.TornadoHuntersDao;

/**
 * The {@code JobService} class is used to fetch various types of jobs using a Data Access Object, it
 * 		acts as a middle-man between the RESTful API and the Database.
 * 
 * @author Roman Krutikov
 *
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
		
		if (jobs == null) {
			LOGGER.error("Unclaimed jobs list returned null");
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not fetch unclaimed jobs...");
		}
		
		return jobs;
		
	}
	
}
