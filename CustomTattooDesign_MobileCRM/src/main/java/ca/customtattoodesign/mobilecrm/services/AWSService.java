package ca.customtattoodesign.mobilecrm.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;

/**
 * The {@code AWSService} class is used to upload and download images (and other files)
 * 		from an AWS S3 bucket.
 * 
 * @author Roman Krutikov
 *
 */
@Service
public class AWSService {
	
	public static final int MAX_IMAGE_SIZE_BYTES = 6291456;
	public static final String[] ALLOWED_IMAGE_EXTENSIONS = new String[] {"jpeg", "JPEG", "jpg", "JPG", "png", "PNG"};
	public static final String BASE_S3_PATH = "public/system/uploads/";
	
	private Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	private String envBucketName = System.getenv("capAWSBucketName");
	private String envAccessKey = System.getenv("capAWSAccessKey");
	private String envSecretKey = System.getenv("capAWSSecretKey");
	private String envRegion = System.getenv("capAWSRegion");
	
	private AmazonS3 s3Client = null;

	/**
	 * Starts the s3Client
	 * 
	 * @throws IllegalArgumentException when required user environment variables are not set
	 */
	private void startS3Client() throws IllegalArgumentException {
		
		if (envBucketName == null) {
			throw new IllegalArgumentException("AWS 'bucketName' not set in user environment variables...");
		}
		if (envAccessKey == null) {
			throw new IllegalArgumentException("AWS 'accessKey' not set in user environment variables...");
		}
		if (envSecretKey == null) {
			throw new IllegalArgumentException("AWS 'secretKey' not set in user environment variables...");
		}
		if (envRegion == null) {
			throw new IllegalArgumentException("AWS 'region' not set in user environment variables...");
		}
		
		this.s3Client = AmazonS3ClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(envAccessKey, envSecretKey)))
				.withRegion(envRegion).build();
	}
	
	/**
	 * Safely stops the s3Client
	 */
	private void stopS3Client() {
		this.s3Client.shutdown();
	}
	
	/**
	 * Uploads an image with the design id to the S3 storage
	 * 
	 * @param designId Integer id of the design
	 * @param image MultipartFile image that will be saved to the S3 bucket
	 * @return {@code true} if the image was uploaded successfully<br>
	 *	       {@code false} if the image failed to be uploaded
	 */
	public boolean uploadDesignImage(int designId, MultipartFile image) {
		
		boolean uploadSuccessful = false;
		
		if (!this.isValidImage(image)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Uploaded image does not meet expected standards ...");
		}
		try {
			startS3Client();
		}
		catch (IllegalArgumentException e) {
			LOGGER.error(e.getMessage());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "AWS connection failed ...");
		}
		
		String imageName = image.getOriginalFilename();
		
		File uploadableImage = null;
		try {
			uploadableImage = convertMultiPartFileToFile(image);
		}
		catch (IOException e) {
			LOGGER.error(e.getMessage());
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unable to convert image to uploadable format ...");
		}
		if (uploadableImage != null) {
			PutObjectResult results = s3Client.putObject(new PutObjectRequest(envBucketName,
					BASE_S3_PATH + designId + "/" + imageName, uploadableImage));
			if (results != null) {
				uploadSuccessful = true;
			}
			uploadableImage.delete();
		}

		stopS3Client();
		
		return uploadSuccessful;
	}
	
	/**
	 * Uploads an image with the design id to the S3 storage
	 * 
	 * @param designId Integer id of the design
	 * @param image File image that will be saved to the S3 bucket
	 * @return {@code true} if the image was uploaded successfully<br>
	 *	       {@code false} if the image failed to be uploaded
	 */
	public boolean uploadDesignImage(int designId, File image) {
		boolean wasUploadSuccessful = false;
		try {
			if (!this.isValidImage(image)) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Uploaded image does not meet expected standards ...");
			}
		}
		catch (IOException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unable to load requested image ...");
		}

		try {
			startS3Client();
		}
		catch (IllegalArgumentException e) {
			LOGGER.error(e.getMessage());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "AWS connection failed ...");
		}
		
		String imageName = image.getName();
		PutObjectResult result = s3Client.putObject(new PutObjectRequest(envBucketName,
				BASE_S3_PATH + designId + "/" + imageName, image));
		if (result != null) {
			wasUploadSuccessful = true;
		}
		
		stopS3Client();
		return wasUploadSuccessful;
	}
	
	/**
	 * Fetches an image from the design id specified as a byte array
	 * 
	 * @param designId Integer id of the design
	 * @param imageName String name of the image when it was saved to the S3 bucket
	 * @return byte array representation of the image from the S3 bucket (if it exists)
	 */
	public byte[] downloadDesignImage(int designId, String imageName) {
		byte[] content = null;
		
		if (this.isValidImageName(imageName)){
			try {
				startS3Client();
			}
			catch (IllegalArgumentException e) {
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "AWS connection failed ...");
			}
			
			try {
				S3Object s3Object = s3Client.getObject(envBucketName, BASE_S3_PATH + designId + "/" + imageName);
				S3ObjectInputStream inputStream = s3Object.getObjectContent();
				content = IOUtils.toByteArray(inputStream);
				inputStream.close();
			} 
			catch (IOException | AmazonS3Exception e) {
				LOGGER.error(e.getMessage());
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unable to download requested image ...");
			}
			
			stopS3Client();
		}
		else {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid image name specified ...");
		}

		return content;
	}
	
	/**
	 * Checks if the image provided meets the parameters set out by the client.
	 * 
	 * @param image a MultipartFile file version of the image
	 * @return {@code true} if the image is valid<br>
	 *	       {@code false} if the image is invalid
	 */
	public boolean isValidImage(MultipartFile image) {
		boolean basicCheck = image != null && image.getSize() <= MAX_IMAGE_SIZE_BYTES;
		if (basicCheck) {
			boolean fileExtensionValid = false;
			for (String fileExtension : ALLOWED_IMAGE_EXTENSIONS) {
				fileExtensionValid |= image.getContentType().endsWith(fileExtension);
			}
			if (fileExtensionValid) {
				return isValidImageName(image.getOriginalFilename());
			}
		}
		return false; 
	}
	
	/**
	 * Checks if the image provided meets the parameters set out by the client.
	 * 
	 * @param image a File object of the image
	 * @return {@code true} if the image is valid<br>
	 *	       {@code false} if the image is invalid
	 */
	public boolean isValidImage(File image) throws IOException{
		
		boolean basicCheck = image != null && image.exists() && image.isFile();
		if (basicCheck) {
			boolean fileExtensionValid = false;
			
			for (String fileExtension : ALLOWED_IMAGE_EXTENSIONS) {
				fileExtensionValid |= image.getName().endsWith(fileExtension);
			}
			if (fileExtensionValid) {
				byte[] imageBytes = Files.readAllBytes(Paths.get(image.getPath()));
				if (imageBytes.length <= MAX_IMAGE_SIZE_BYTES) {
					return isValidImageName(image.getName());
				}
			}
		}
		
		return false; 
	}
	
	/**
	 * Checks if the imageName provided is the name of a valid Image.
	 * 
	 * @param image a MultipartFile file version of the image
	 * @return {@code true} if the image is valid<br>
	 *	       {@code false} if the image is invalid
	 */
	public boolean isValidImageName(String imageName) {
		
		// 1 character for the name of the file
		int minimumSize = 1;
		
		int minimumExtensionSize = 0;
		for (String fileExtension : ALLOWED_IMAGE_EXTENSIONS) {
			if (minimumExtensionSize == 0) {
				minimumExtensionSize = fileExtension.length();
			}
			else {
				if (fileExtension.length() < minimumExtensionSize) {
					minimumExtensionSize = fileExtension.length();
				}
			}
		}
		minimumSize += minimumExtensionSize;
		
		return imageName != null && imageName.length() > minimumSize;
	}
	
	/**
	 * Converts a SpringBoot MulitpartFile object to a standard File object.
	 * 
	 * @param file a MultipartFile file
	 * @return File version of the MultipartFile input
	 */
	public File convertMultiPartFileToFile(MultipartFile file) throws IOException {
		File convertedFile = null;
		
		if (file != null) {
			convertedFile = new File(file.getOriginalFilename());
			try (FileOutputStream outputStream = new FileOutputStream(convertedFile)){
				outputStream.write(file.getBytes());
			}
		}

		return convertedFile;
	}
	
}
