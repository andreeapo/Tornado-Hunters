package ca.customtattoodesign.mobilecrm.services;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import ca.customtattoodesign.mobilecrm.beans.BasicJob;
import ca.customtattoodesign.mobilecrm.beans.ClaimJobLogin;
import ca.customtattoodesign.mobilecrm.beans.DesignImage;
import ca.customtattoodesign.mobilecrm.beans.DesignRequest;
import ca.customtattoodesign.mobilecrm.beans.Job;
import ca.customtattoodesign.mobilecrm.beans.SessionLogin;
import ca.customtattoodesign.mobilecrm.dao.TornadoHuntersDao;

/**
 * The {@code JobService} class is used to fetch various types of jobs using a Data Access Object, it
 * 		acts as a middle-man between the RESTful API and the Database.
 * 
 * @author Roman Krutikov
 * @author Thomas Chapman
 */
@Service
public class JobService {
	
	private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	public static final int JOB_ACCESS_TOKEN_LENGTH = 40;

	@Autowired
	private AWSService awsService;

	private int idShift;
	private String key;
	private String salt;
	private String vector;

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
	 * Initializes environment variables that are required to perform security-related operations
	 *
	 * @throws IllegalArgumentException if a certain environment variable is not set up,
	 * 		with details in the message.
	 */
	private void initializeSecurityEnvironmentVariables() throws NoSuchMethodException{

		String envKey = System.getenv("capSECKey");
		String envSalt = System.getenv("capSECSalt");
		String envVector = System.getenv("capSECVector");
		String envIdShiftStr = System.getenv("capSECIdShift");

		if (envKey == null) {
			throw new NoSuchMethodException("Security 'key' not set in user environment variables...");
		}
		if (envSalt == null) {
			throw new NoSuchMethodException("Security 'salt' not set in user environment variables...");
		}
		if (envVector == null) {
			throw new NoSuchMethodException("Security 'vector' not set in user environment variables...");
		}
		if (envIdShiftStr == null) {
			throw new NoSuchMethodException("Security 'id shift' not set in user environment variables...");
		}

		try {
			this.idShift = Integer.parseInt(envIdShiftStr);
		}
		catch (NumberFormatException e) {
			throw new NoSuchMethodException("Security 'id shift' not set correctly in user environment variables...");
		}

		this.key = envKey;
		this.salt = envSalt;
		this.vector = envVector;

	}

	/**
	 * Converts a jobId to a jobAccessToken.
	 *
	 * @param jobId, the id of the job that requires a job access token
	 * @return a String job access token that corresponds with the jobId provided
	 * @throws NoSuchMethodException if initialization of security environment variables failed
	 * @throws IllegalArgumentException if something went wrong with the algorithm that performs the conversion
	 * @throws UnsupportedEncodingException if something went wrong with the algorithm that performs the conversion
	 * @throws NoSuchAlgorithmException if something went wrong with the algorithm that performs the conversion
	 * @throws NoSuchPaddingException if something went wrong with the algorithm that performs the conversion
	 * @throws InvalidKeyException if something went wrong with the algorithm that performs the conversion
	 * @throws InvalidAlgorithmParameterException if something went wrong with the algorithm that performs the conversion
	 * @throws IllegalBlockSizeException if something went wrong with the algorithm that performs the conversion
	 * @throws BadPaddingException if something went wrong with the algorithm that performs the conversion
	 */
	public String getJobAccessTokenFromJobId(int jobId) throws NoSuchMethodException, IllegalArgumentException,
			UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException{

		initializeSecurityEnvironmentVariables();

		int tempJobId = jobId + this.idShift;
		String jobIdStr= Integer.toString(tempJobId) + this.salt;

		SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
		GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(128, this.vector.getBytes("UTF-8"));
		Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec, gcmParameterSpec);

		byte[] encrypted = cipher.doFinal(jobIdStr.getBytes());
		return Base64.getEncoder().encodeToString(encrypted);
	}

	/**
	 * Converts a jobAccessToken to a jobId.
	 *
	 * @param jobAccessToken, the access token of the job that requires a job id
	 * @return an Integer job id that corresponds with the jobAccessToken provided
	 * @throws NoSuchMethodException if initialization of security environment variables failed
	 * @throws IllegalArgumentException if something went wrong with the algorithm that performs the conversion
	 * @throws UnsupportedEncodingException if something went wrong with the algorithm that performs the conversion
	 * @throws NoSuchAlgorithmException if something went wrong with the algorithm that performs the conversion
	 * @throws NoSuchPaddingException if something went wrong with the algorithm that performs the conversion
	 * @throws InvalidKeyException if something went wrong with the algorithm that performs the conversion
	 * @throws InvalidAlgorithmParameterException if something went wrong with the algorithm that performs the conversion
	 * @throws IllegalBlockSizeException if something went wrong with the algorithm that performs the conversion
	 * @throws BadPaddingException if something went wrong with the algorithm that performs the conversion
	 */
	public int getJobIdFromJobAccessToken(String jobAccessToken) throws NoSuchMethodException, IllegalArgumentException,
			UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException{

		initializeSecurityEnvironmentVariables();

		SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(128, this.vector.getBytes("UTF-8"));
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, gcmParameterSpec);

        byte[] original = cipher.doFinal(Base64.getDecoder().decode(jobAccessToken));
        String originalStr = new String(original);
        originalStr = originalStr.substring(0, originalStr.length() - this.salt.length());
        int tempJobId = 0;
        try {
        	tempJobId = Integer.parseInt(originalStr);
        }
        catch (NumberFormatException e) {
        	throw new InvalidAlgorithmParameterException(e.getMessage());
        }
        int trueId = tempJobId - this.idShift;

	    return trueId;
	}

	/**
	 * Checks if the provided jobAccessToken is a valid access token
	 *
	 * @param jobAccessToken String, the access token to be checked
	 * @return
	 */
	public boolean isValidJobAccessToken(String jobAccessToken) {
		return jobAccessToken != null && jobAccessToken.length() == JOB_ACCESS_TOKEN_LENGTH;
	}

	/**
	 * Sends a design request to the database and reference images to the database/s3 storage, then
	 *  returns a Basic Job object which holds information about the job created from the design request
	 *
	 * @param designSubmission the Design request that is being submitted
	 * @param images an ArrayList of reference images that were associated with the design request
	 * @return a BasicJob object which holds the id and access token of the created job from the design request
	 */
	public BasicJob sendJobDesignRequest(DesignRequest designSubmission, ArrayList<MultipartFile> images) {

		int newJobId = -1;
		try {
			newJobId = TornadoHuntersDao.getInstance().submitDesignRequest(designSubmission);
		}
		catch (SQLException e) {
			LOGGER.error(e.getMessage());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Database connection failed...");
		}

		if (newJobId != -1) {
			for (MultipartFile image : images) {
				boolean uploadSuccessful = false;
				int designId = -1;
				try {
					designId = TornadoHuntersDao.getInstance().getNextAvailableDesignId();
					if (designId != -1) {
						awsService.uploadDesignImage(designId, image);
						uploadSuccessful = TornadoHuntersDao.getInstance()
								.recordDesignRequestImage(designId, newJobId, image.getOriginalFilename());
						if (!uploadSuccessful) {
							LOGGER.warn("Failed to upload image '{}' for job '{}', designId '{}' was unused",
									image.getOriginalFilename(), newJobId, designId);
						}
					}
					else {
						LOGGER.error("Database failed to get an available design id");
						throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
								"Unable to get new design id to upload reference image...");
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
		}
		else {
			LOGGER.error("Unable to submit job request");
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Was unable to submit the design request...");
		}

		BasicJob newlyCreatedJob;
		try {
			newlyCreatedJob = BasicJob.builder()
					.jobId(newJobId)
					.jobAccessToken(this.getJobAccessTokenFromJobId(newJobId))
					.build();
		}
		catch (NoSuchMethodException e) {
			LOGGER.error(e.getMessage());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
		catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
					"An unexpected error occurred when submitting the design request");
		}

		return newlyCreatedJob;
	}

	/**
	 * Returns the job for the customer's unique ID
	 *
	 * @param bJob BasicJob which holds the job access token, a unique public token given to the customer 
	 * 		to access their jobs
	 * @return {@code job} a Job object that matches the BasicJob object
	 */
	public Job fetchCustomerJob(BasicJob bJob) {
		Job job;

		try{
			int id = getJobIdFromJobAccessToken(bJob.getJobAccessToken());
			job = TornadoHuntersDao.getInstance().fetchCustomerJob(id);
		}
		catch (NoSuchMethodException e){
			LOGGER.error(e.getMessage());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
		catch (SQLException e){
			LOGGER.error(e.getMessage());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "SQL Database connection failed...");
		}
		catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()+"\n"+e.getClass());
		}

		return job;

    }
	
	/**
	 * Gets a list of all the design images associated with a job
	 *
	 * @param basicJob BasicJob which holds the job access token and/or a job id
	 * @return List&lt;DesignImage&gt; a list of the design images of a job
	 */
	public List<DesignImage> getDesigns(BasicJob basicJob){
		List<DesignImage> designImages = new ArrayList<DesignImage>();
		
		int jobId = -1;
		
		if (!this.isJobIdValid(basicJob.getJobId())) {
			String jobAccessToken = basicJob.getJobAccessToken();
			if (this.isValidJobAccessToken(jobAccessToken)) {
				try {
					jobId = this.getJobIdFromJobAccessToken(basicJob.getJobAccessToken());
				}
				catch (Exception e) {
					LOGGER.error(e.getMessage()+"\n"+e.getClass());
					throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
				}
			}
			else {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Job access token was not formatted correctly");
			}
		}
		else {
			jobId = basicJob.getJobId();
		}
		
		try {
			designImages = TornadoHuntersDao.getInstance().fetchJobDesigns(jobId);
		}
		catch (SQLException e){
			LOGGER.error(e.getMessage());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "SQL Database connection failed...");
		}
		
		for (DesignImage designImage : designImages) {
			designImage.setImageByteRepresentation(
					awsService.downloadDesignImage(designImage.getDesignId(), designImage.getImageName())
					);
		}

		return designImages;
	}

}
