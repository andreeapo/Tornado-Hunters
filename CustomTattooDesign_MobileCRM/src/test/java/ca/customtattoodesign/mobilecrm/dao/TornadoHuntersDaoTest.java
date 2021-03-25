package ca.customtattoodesign.mobilecrm.dao;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import ca.customtattoodesign.mobilecrm.beans.UserLogin;
import ca.customtattoodesign.mobilecrm.beans.Job;
import ca.customtattoodesign.mobilecrm.beans.SessionUser;

@RunWith(SpringRunner.class)
@SpringBootTest
class TornadoHuntersDaoTest {

	@Test
	public void testIsUserAuthorizedRegular() throws SQLException {
		String username = System.getenv("capTestUser");
		String password = System.getenv("capTestPassword");
		UserLogin user = UserLogin.builder().username(username).password(password).build();
		
		boolean isValid = TornadoHuntersDao.getInstance().isUserAuthorized(user.getUsername(), user.getPassword());
		
		assertTrue("User was not validated correctly...", isValid);
	}
	
	@Test
	public void testIsUserAuthorizedBoundaryIn() throws SQLException {
		String username = System.getenv("capTestUser2");
		String password = System.getenv("capTestPassword2");
		UserLogin user = UserLogin.builder().username(username).password(password).build();
		
		boolean isValid = TornadoHuntersDao.getInstance().isUserAuthorized(user.getUsername(), user.getPassword());

		assertTrue("User was not validated correctly...", isValid);
	}
	
	@Test
	public void testIsUserAuthorizedBoundaryOut() throws SQLException {
		String username = System.getenv("capTestUser");
		String password = System.getenv("capTestPassword")+"0";
		UserLogin user = UserLogin.builder().username(username).password(password).build();
		
		boolean isValid = TornadoHuntersDao.getInstance().isUserAuthorized(user.getUsername(), user.getPassword());

		assertFalse("User was not validated correctly...", isValid);
	}
	
	@Test
	public void testIsUserAuthorizedException() throws SQLException {
		String username = null;
		String password = System.getenv("capTestPassword")+"0";
		UserLogin user = UserLogin.builder().username(username).password(password).build();
		
		boolean isValid = TornadoHuntersDao.getInstance().isUserAuthorized(user.getUsername(), user.getPassword());
	
		assertFalse("User was not validated correctly...", isValid);
	}
	
	@Test
	public void testFetchSessionUserFieldsRegular() throws SQLException {
		String username = System.getenv("capTestUser");
		String password = System.getenv("capTestPassword");
		
		SessionUser sessionUser = new SessionUser();
		boolean wasSuccessful = TornadoHuntersDao.getInstance().fetchSessionUserFields(sessionUser, username, password);
		boolean fieldExists = sessionUser.getFirstName() != null && !"".equals(sessionUser.getFirstName());
		
		assertTrue("SessionUser fields were fetching failed...", wasSuccessful && fieldExists);
	}
	
	@Test
	public void testFetchSessionUserFieldsBoundaryIn() throws SQLException {
		String username = System.getenv("capTestUser2");
		String password = System.getenv("capTestPassword2");
		
		SessionUser sessionUser = new SessionUser();
		boolean wasSuccessful = TornadoHuntersDao.getInstance().fetchSessionUserFields(sessionUser, username, password);
		boolean fieldExists = sessionUser.getFirstName() != null && !"".equals(sessionUser.getFirstName());
		
		assertTrue("SessionUser fields were fetching failed...", wasSuccessful && fieldExists);
	}
	
	@Test
	public void testFetchSessionUserFieldsBoundaryOut() throws SQLException {
		String username = System.getenv("capTestUser2");
		String password = System.getenv("capTestPassword2")+"0";
		
		SessionUser sessionUser = new SessionUser();
		boolean wasSuccessful = TornadoHuntersDao.getInstance().fetchSessionUserFields(sessionUser, username, password);
		boolean fieldExists = sessionUser.getFirstName() != null && !"".equals(sessionUser.getFirstName());
		
		assertFalse("SessionUser fields were fetching failed...", wasSuccessful && fieldExists);
	}
	
	@Test
	public void testFetchSessionUserFieldsException() throws SQLException {
		String username = System.getenv("capTestUser2");
		String password = null;
		
		SessionUser sessionUser = new SessionUser();
		boolean wasSuccessful = TornadoHuntersDao.getInstance().fetchSessionUserFields(sessionUser, username, password);
		boolean fieldExists = sessionUser.getFirstName() != null && !"".equals(sessionUser.getFirstName());
		
		assertFalse("SessionUser fields were fetching failed...", wasSuccessful && fieldExists);
	}
	
	@Test
	public void testSetUserSessionTokenRegular() throws SQLException {
		String username = System.getenv("capTestUser");
		String password = System.getenv("capTestPassword");
		
		String testSessionToken = "thisisatesttoken12notreal123butifitwasrealthatwouldbeinteresting";
		UserLogin user = UserLogin.builder().username(username).password(password).build();
		
		boolean wasSettingUserTokenSuccessful = TornadoHuntersDao.getInstance().setUserSessionToken(user, testSessionToken);

		assertTrue("Session token was not updated into the database correctly...", wasSettingUserTokenSuccessful);
	}
	
	@Test
	public void testSetUserSessionTokenBoundaryIn() throws SQLException {
		String username = System.getenv("capTestUser2");
		String password = System.getenv("capTestPassword2");
		
		String testSessionToken = "thisisatesttoken12notreal123butifitwasrealthatwouldbeinteresting";
		UserLogin user = UserLogin.builder().username(username).password(password).build();

		boolean wasSettingUserTokenSuccessful = TornadoHuntersDao.getInstance().setUserSessionToken(user, testSessionToken);

		assertTrue("Session token was not updated into the database correctly...", wasSettingUserTokenSuccessful);
	}
	
	@Test
	public void testSetUserSessionTokenBoundaryOut() throws SQLException {
		String username = System.getenv("capTestUser2");
		String password = System.getenv("capTestPassword2")+"0";
		
		String testSessionToken = "thisisatesttoken12notreal123butifitwasrealthatwouldbeinteresting";
		UserLogin user = UserLogin.builder().username(username).password(password).build();
		
		boolean wasSettingUserTokenSuccessful = TornadoHuntersDao.getInstance().setUserSessionToken(user, testSessionToken);

		assertFalse("Session token was not updated into the database correctly...", wasSettingUserTokenSuccessful);
	}
	
	@Test
	public void testSetUserSessionTokenException() throws SQLException {
		String username = null;
		String password = System.getenv("capTestPassword2");
		
		String testSessionToken = "thisisatesttoken12notreal123butifitwasrealthatwouldbeinteresting";
		UserLogin user = UserLogin.builder().username(username).password(password).build();

		boolean wasSettingUserTokenSuccessful = TornadoHuntersDao.getInstance().setUserSessionToken(user, testSessionToken);

		assertFalse("Session token was not updated into the database correctly...", wasSettingUserTokenSuccessful);
	}
	
	@Test
	public void testFetchUnclaimedJobsRegular() throws SQLException {
		List<Job> jobs = TornadoHuntersDao.getInstance().fetchUnclaimedJobs();
		
		assertTrue("Unclaimed jobs list was null...", jobs != null);
	}

}
