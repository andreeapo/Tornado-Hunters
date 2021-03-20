package ca.customtattoodesign.mobilecrm.dao;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import ca.customtattoodesign.mobilecrm.beans.LoginUser;

@RunWith(SpringRunner.class)
@SpringBootTest
class TornadoHuntersDaoTest {

	@Test
	public void testIsUserAuthorizedRegular() throws SQLException, ClassNotFoundException {
		String username = System.getenv("capTestUser");
		String password = System.getenv("capTestPassword");
		LoginUser user = LoginUser.builder().username(username).password(password).build();
		
		boolean isValid = TornadoHuntersDao.getInstance().isUserAuthorized(user.getUsername(), user.getPassword());
		
		assertTrue("User was not validated correctly...", isValid);
	}
	
	@Test
	public void testIsUserAuthorizedBoundaryIn() throws SQLException, ClassNotFoundException {
		String username = System.getenv("capTestUser2");
		String password = System.getenv("capTestPassword2");
		LoginUser user = LoginUser.builder().username(username).password(password).build();
		
		boolean isValid = TornadoHuntersDao.getInstance().isUserAuthorized(user.getUsername(), user.getPassword());

		assertTrue("User was not validated correctly...", isValid);
	}
	
	@Test
	public void testIsUserAuthorizedBoundaryOut() throws SQLException, ClassNotFoundException {
		String username = System.getenv("capTestUser");
		String password = System.getenv("capTestPassword")+"0";
		LoginUser user = LoginUser.builder().username(username).password(password).build();
		
		boolean isValid = TornadoHuntersDao.getInstance().isUserAuthorized(user.getUsername(), user.getPassword());

		assertFalse("User was not validated correctly...", isValid);
	}
	
	@Test
	public void testIsUserAuthorizedException() throws SQLException, ClassNotFoundException {
		String username = null;
		String password = System.getenv("capTestPassword")+"0";
		LoginUser user = LoginUser.builder().username(username).password(password).build();
		
		boolean isValid = TornadoHuntersDao.getInstance().isUserAuthorized(user.getUsername(), user.getPassword());
	
		assertFalse("User was not validated correctly...", isValid);
	}
	
	@Test
	public void testSetUserSessionTokenRegular() throws SQLException, ClassNotFoundException {
		String username = System.getenv("capTestUser");
		String password = System.getenv("capTestPassword");
		
		String testSessionToken = "thisisatesttoken12notreal123butifitwasrealthatwouldbeinteresting";
		LoginUser user = LoginUser.builder().username(username).password(password).build();
		
		boolean wasSettingUserTokenSuccessful = TornadoHuntersDao.getInstance().setUserSessionToken(user, testSessionToken);

		assertTrue("Session token was not updated into the database correctly...", wasSettingUserTokenSuccessful);
	}
	
	@Test
	public void testSetUserSessionTokenBoundaryIn() throws SQLException, ClassNotFoundException {
		String username = System.getenv("capTestUser2");
		String password = System.getenv("capTestPassword2");
		
		String testSessionToken = "thisisatesttoken12notreal123butifitwasrealthatwouldbeinteresting";
		LoginUser user = LoginUser.builder().username(username).password(password).build();

		boolean wasSettingUserTokenSuccessful = TornadoHuntersDao.getInstance().setUserSessionToken(user, testSessionToken);

		assertTrue("Session token was not updated into the database correctly...", wasSettingUserTokenSuccessful);
	}
	
	@Test
	public void testSetUserSessionTokenBoundaryOut() throws SQLException, ClassNotFoundException {
		String username = System.getenv("capTestUser2");
		String password = System.getenv("capTestPassword2")+"0";
		
		String testSessionToken = "thisisatesttoken12notreal123butifitwasrealthatwouldbeinteresting";
		LoginUser user = LoginUser.builder().username(username).password(password).build();
		
		boolean wasSettingUserTokenSuccessful = TornadoHuntersDao.getInstance().setUserSessionToken(user, testSessionToken);

		assertFalse("Session token was not updated into the database correctly...", wasSettingUserTokenSuccessful);
	}
	
	@Test
	public void testSetUserSessionTokenException() throws SQLException, ClassNotFoundException {
		String username = null;
		String password = System.getenv("capTestPassword2");
		
		String testSessionToken = "thisisatesttoken12notreal123butifitwasrealthatwouldbeinteresting";
		LoginUser user = LoginUser.builder().username(username).password(password).build();

		boolean wasSettingUserTokenSuccessful = TornadoHuntersDao.getInstance().setUserSessionToken(user, testSessionToken);

		assertFalse("Session token was not updated into the database correctly...", wasSettingUserTokenSuccessful);
	}

}
