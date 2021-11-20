package ca.customtattoodesign.mobilecrm.services;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.sql.SQLException;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import ca.customtattoodesign.mobilecrm.beans.ConversationLogin;
import ca.customtattoodesign.mobilecrm.beans.Message;
import ca.customtattoodesign.mobilecrm.dao.TornadoHuntersDao;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AWSServiceTest {

	@Autowired
	AWSService awsService;
	
	private static int capTestId;
	private static int capTestId2;
	private static String capTestUser;
	private static String capTestUser2;
	private static String capTestUserFirstName;
	private static String capTestUserLastName;
	private static String capTestUser2FirstName;
	private static String capTestUser2LastName;
	private static String capTestPassword;
	private static String capTestPassword2;
	private static String capTestSessionToken;
	private static String capTestSessionToken2;
	private static int capTestJobId;
	private static int capTestJobId2;
	private static int capTestJobExpectedSize;
	private static int capTestJobExpectedSize2;
	private static int capTestMessagesExpectedSize;
	private static int capTestMessagesExpectedSize2;

	private static String capTestJobAccessToken1;
	private static String capTestJobAccessToken2;
	private static int capTestDesignId;
	private static int capTestDesignId2;
	private static String capTestImageName;
	private static String capTestImageName2;
	private static String capTestImagePath;
	private static String capTestImagePath2;
	private static String capTestEncodedJobId;
	private static String capTestEncodedJobId2;

	private static String capTestBearer;

	@BeforeClass
	public static void fetchEnvironmentVariables() {
		capTestId = Integer.parseInt(System.getenv("capTestId"));
		capTestId2 = Integer.parseInt(System.getenv("capTestId2"));
		capTestUser = System.getenv("capTestUser");
		capTestUser2 = System.getenv("capTestUser2");
		capTestUserFirstName = System.getenv("capTestUserFirstName");
		capTestUserLastName = System.getenv("capTestUserLastName");
		capTestUser2FirstName = System.getenv("capTestUser2FirstName");
		capTestUser2LastName = System.getenv("capTestUser2LastName");
		capTestPassword = System.getenv("capTestPassword");
		capTestPassword2 = System.getenv("capTestPassword2");
		capTestSessionToken = System.getenv("capTestSessionToken");
		capTestSessionToken2 = System.getenv("capTestSessionToken2");
		capTestJobId = Integer.parseInt(System.getenv("capTestJobId"));
		capTestJobId2 = Integer.parseInt(System.getenv("capTestJobId2"));
		capTestJobExpectedSize = Integer.parseInt(System.getenv("capTestJobExpectedSize"));
		capTestJobExpectedSize2 = Integer.parseInt(System.getenv("capTestJobExpectedSize2"));
		capTestMessagesExpectedSize = Integer.parseInt(System.getenv("capTestMessagesExpectedSize"));
		capTestMessagesExpectedSize2 = Integer.parseInt(System.getenv("capTestMessagesExpectedSize2"));
		
		capTestJobAccessToken1 = System.getenv("capTestEncodedJobId");
		capTestJobAccessToken2 = System.getenv("capTestEncodedJobId2");
		capTestDesignId = Integer.parseInt(System.getenv("capTestDesignId"));
		capTestDesignId2 = Integer.parseInt(System.getenv("capTestDesignId2"));
		capTestImageName = System.getenv("capTestImageName");
		capTestImageName2 = System.getenv("capTestImageName2");
		capTestImagePath = System.getenv("capTestImagePath");
		capTestImagePath2 = System.getenv("capTestImagePath2");
		capTestEncodedJobId = System.getenv("capTestEncodedJobId");
		capTestEncodedJobId2 = System.getenv("capTestEncodedJobId2");
		
		capTestBearer = System.getenv("capTestBearer");
	}
	
	@AfterClass
	public static void afterAll() {
		
	}
	
	@Before
	public void setSessionToken() throws SQLException{
		TornadoHuntersDao.getInstance().setUserSessionToken(capTestId, capTestSessionToken);
		TornadoHuntersDao.getInstance().setUserSessionToken(capTestId2, capTestSessionToken2);
	}
	
	@After
	public void removeSessionToken() throws SQLException{
		TornadoHuntersDao.getInstance().removeSessionToken(capTestId, capTestSessionToken);
		TornadoHuntersDao.getInstance().removeSessionToken(capTestId2, capTestSessionToken2);
	}
	
	@Test
	public void testUploadDesignImageRegular() throws SQLException, IOException{
		int designId = TornadoHuntersDao.getInstance().getNextAvailableDesignId();
		File image = new File(capTestImagePath);
		
		MultipartFile multipartFile = new MockMultipartFile(image.getName(),
				image.getName(), MediaType.IMAGE_PNG_VALUE, Files.readAllBytes(image.toPath()));
		
		boolean uploadSuccessful = awsService.uploadDesignImage(designId, multipartFile);
		
		assertTrue("Image failed to upload when it should have succeeded ...", uploadSuccessful);
	}
	
	@Test
	public void testUploadDesignImageBoundaryIn() throws SQLException, IOException{
		int designId = TornadoHuntersDao.getInstance().getNextAvailableDesignId();
		File image = new File(capTestImagePath2);
		
		MultipartFile multipartFile = new MockMultipartFile(image.getName(),
				image.getName(), MediaType.IMAGE_PNG_VALUE, Files.readAllBytes(image.toPath()));
		
		boolean uploadSuccessful = awsService.uploadDesignImage(designId, multipartFile);
		
		assertTrue("Image failed to upload when it should have succeeded ...", uploadSuccessful);
	}
	
	@Test
	public void testUploadDesignImageBoundaryOut() throws SQLException, IOException{
		int designId = TornadoHuntersDao.getInstance().getNextAvailableDesignId();
		File image = new File(capTestImagePath2+"1");
		
		assertThrows(NoSuchFileException.class, () -> {
			MultipartFile multipartFile = new MockMultipartFile(image.getName(),
					image.getName(), MediaType.IMAGE_PNG_VALUE, Files.readAllBytes(image.toPath()));
			boolean uploadSuccessful = awsService.uploadDesignImage(designId, multipartFile);
		});
		
	}
	
	@Test
	public void testUploadDesignImageException() {
		int designId = capTestDesignId;
		MultipartFile image = null;
		
		assertThrows(ResponseStatusException.class, () -> {
			boolean uploadSuccessful = awsService.uploadDesignImage(designId, image);
		});
	}
	
	@Test
	public void testUploadDesignImageRegular2() throws SQLException{
		int designId = TornadoHuntersDao.getInstance().getNextAvailableDesignId();
		File image = new File(capTestImagePath);
		
		boolean uploadSuccessful = awsService.uploadDesignImage(designId, image);
		
		assertTrue("Image failed to upload when it should have succeeded ...", uploadSuccessful);
	}
	
	@Test
	public void testUploadDesignImageBoundaryIn2() throws SQLException{
		int designId = TornadoHuntersDao.getInstance().getNextAvailableDesignId();
		File image = new File(capTestImagePath2);
		
		boolean uploadSuccessful = awsService.uploadDesignImage(designId, image);
		
		assertTrue("Image failed to upload when it should have succeeded ...", uploadSuccessful);
	}
	
	@Test
	public void testUploadDesignImageBoundaryOut2() throws SQLException{
		int designId = TornadoHuntersDao.getInstance().getNextAvailableDesignId();
		File image = new File(capTestImagePath2+"1");
		
		assertThrows(ResponseStatusException.class, () -> {
			boolean uploadSuccessful = awsService.uploadDesignImage(designId, image);
		});
		
	}
	
	@Test
	public void testUploadDesignImageException2() throws SQLException{
		int designId = TornadoHuntersDao.getInstance().getNextAvailableDesignId();
		File image = new File("hello");

		assertThrows(ResponseStatusException.class, () -> {
			boolean uploadSuccessful = awsService.uploadDesignImage(designId, image);
		});
	}
	
	@Test
	public void testDownloadDesignImageRegular() {
		int designId = capTestDesignId;
		String imageName = capTestImageName;
		
		byte[] image = awsService.downloadDesignImage(designId, imageName);
		
		assertTrue("Design image was not downloaded as expected...", image != null);
	}
	
	@Test
	public void testDownloadDesignImageBoundaryIn() {
		int designId = capTestDesignId2;
		String imageName = capTestImageName2;
		
		byte[] image = awsService.downloadDesignImage(designId, imageName);
		
		assertTrue("Design image was not downloaded as expected...", image != null);
	}
	
	@Test
	public void testDownloadDesignImageBoundaryOut() {
		int designId = capTestDesignId2;
		String imageName = "_" + capTestImageName2;
		
		assertThrows(ResponseStatusException.class, () -> {
			byte[] image = awsService.downloadDesignImage(designId, imageName);
		});
	}
	
	@Test
	public void testDownloadDesignImageException() {
		int designId = capTestDesignId;
		String imageName = null;
		
		assertThrows(ResponseStatusException.class, () -> {
			byte[] image = awsService.downloadDesignImage(designId, imageName);
		});
	}
	
	@Test
	public void testIsValidImageRegular() throws IOException {
		File image = new File(capTestImagePath);
		
		MultipartFile multipartFile = new MockMultipartFile(image.getName(),
				image.getName(), MediaType.IMAGE_PNG_VALUE, Files.readAllBytes(image.toPath()));
		
		boolean isValid = awsService.isValidImage(multipartFile);

		assertTrue("Design image was considered invalid when it is valid...", isValid);
	}
	
	@Test
	public void testIsValidImageRegular2() throws IOException {
		File image = new File(capTestImagePath);
		
		boolean isValid = awsService.isValidImage(image);

		assertTrue("Design image was considered invalid when it is valid...", isValid);
	}
	
	@Test
	public void testIsValidImageBoundaryIn() throws IOException {
		File image = new File(capTestImagePath2);
		
		MultipartFile multipartFile = new MockMultipartFile(image.getName(),
				image.getName(), MediaType.IMAGE_PNG_VALUE, Files.readAllBytes(image.toPath()));
		
		boolean isValid = awsService.isValidImage(multipartFile);

		assertTrue("Design image was considered invalid when it is valid...", isValid);
	}
	
	@Test
	public void testIsValidImageBoundaryIn2() throws IOException {
		File image = new File(capTestImagePath2);
		
		boolean isValid = awsService.isValidImage(image);

		assertTrue("Design image was considered invalid when it is valid...", isValid);
	}
	
	@Test
	public void testIsValidImageBoundaryOut() throws IOException {
		File image = new File(capTestImagePath2+"1");
		
		assertThrows(NoSuchFileException.class, () -> {
			MultipartFile multipartFile = new MockMultipartFile(image.getName(),
					image.getName(), MediaType.IMAGE_PNG_VALUE, Files.readAllBytes(image.toPath()));
			
			boolean isValid = awsService.isValidImage(multipartFile);
		});
	}
	
	@Test
	public void testIsValidImageBoundaryOut2() throws IOException {
		File image = new File(capTestImagePath2+"1");
		
		boolean isValid = awsService.isValidImage(image);

		assertFalse("Design image was considered valid when it is invalid...", isValid);
	}
	
	@Test
	public void testIsValidImageException() throws IOException {
		String fileName = null;
		
		assertThrows(NullPointerException.class, () -> {
			File image = new File(fileName);
			MultipartFile multipartFile = new MockMultipartFile(image.getName(),
					image.getName(), MediaType.IMAGE_PNG_VALUE, Files.readAllBytes(image.toPath()));
			
			boolean isValid = awsService.isValidImage(multipartFile);
		});
	}
	
	@Test
	public void testIsValidImageException2() {
		MultipartFile image = null;
		
		boolean isValid = awsService.isValidImage(image);

		assertFalse("Design image was considered valid when it is invalid...", isValid);
	}
	
	@Test
	public void testIsValidImageNameRegular() {
		String imageName = capTestImageName;
		
		boolean isValid = awsService.isValidImageName(imageName);
		
		assertTrue("Design image name was considered invalid when it is valid...", isValid);
	}
	
	@Test
	public void testIsValidImageNameBoundaryIn() {
		String imageName = capTestImageName2;
		
		boolean isValid = awsService.isValidImageName(imageName);
		
		assertTrue("Design image name was considered invalid when it is valid...", isValid);
	}
	
	@Test
	public void testIsValidImageNameBoundaryOut() {
		String imageName = capTestImageName2.substring(0,1);
		
		boolean isValid = awsService.isValidImageName(imageName);
		
		assertFalse("Design image name was considered valid when it is invalid...", isValid);
	}
	
	@Test
	public void testIsValidImageNameException() {
		String imageName = null;
		
		boolean isValid = awsService.isValidImageName(imageName);
		
		assertFalse("Design image name was considered valid when it is invalid...", isValid);
	}
	
	@Test
	public void testConvertMultiPartFileToFileRegular() throws IOException{
		File image = new File(capTestImagePath);
		
		MultipartFile multipartFile = new MockMultipartFile(image.getName(),
				image.getName(), MediaType.IMAGE_PNG_VALUE, Files.readAllBytes(image.toPath()));
		
		File newFile = awsService.convertMultiPartFileToFile(multipartFile);
		
		assertTrue("A non-null Multipart file was incorrectly converted to a null File ...", newFile != null);
	}
	
	@Test
	public void testConvertMultiPartFileToFileBoundaryIn() throws IOException{
		File image = new File(capTestImagePath2);
		
		MultipartFile multipartFile = new MockMultipartFile(image.getName(),
				image.getName(), MediaType.IMAGE_PNG_VALUE, Files.readAllBytes(image.toPath()));
		
		File newFile = awsService.convertMultiPartFileToFile(multipartFile);
		
		assertTrue("A non-null Multipart file was incorrectly converted to a null File ...", newFile != null);
	}
	
	@Test
	public void testConvertMultiPartFileToFileBoundaryOut() throws IOException{
		File image = new File(capTestImagePath2+"1");
		
		assertThrows(NoSuchFileException.class, () -> {
			MultipartFile multipartFile = new MockMultipartFile(image.getName(),
					image.getName(), MediaType.IMAGE_PNG_VALUE, Files.readAllBytes(image.toPath()));
			
			File newFile = awsService.convertMultiPartFileToFile(multipartFile);
		});

	}
	
	@Test
	public void testConvertMultiPartFileToFileException() throws IOException{
		MultipartFile file = null;
		
		File newFile = awsService.convertMultiPartFileToFile(file);
		
		assertFalse("A null Multipart filewas incorrectly converted to a non-null File ...", newFile != null);
	}
	

}
