package ca.customtattoodesign.mobilecrm.dao;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.server.ResponseStatusException;

import ca.customtattoodesign.mobilecrm.beans.LoginUser;
import ca.customtattoodesign.mobilecrm.beans.SessionUser;

@RunWith(SpringRunner.class)
@SpringBootTest
class TornadoHuntersDaoTest {

	@Test
	public void testIsUserAuthorizedRegular() {
		
		String username = System.getenv("capTestUser");
		String password = System.getenv("capTestPassword");
		LoginUser user = LoginUser.builder().username(username).password(password).build();
		
		boolean isValid = false;
		TornadoHuntersDao dao = null;
		
		try {
			dao = TornadoHuntersDao.getInstance();
			isValid = dao.isUserAuthorized(user);
		}
		catch (SQLException | ClassNotFoundException e) {}
		
		assertTrue("User was not validated correctly...", isValid == true);
	}
	
	@Test
	public void testIsUserAuthorizedBoundaryIn() {
		String username = System.getenv("capTestUser2");
		String password = System.getenv("capTestPassword2");
		LoginUser user = LoginUser.builder().username(username).password(password).build();
		
		boolean isValid = false;
		TornadoHuntersDao dao = null;
		
		try {
			dao = TornadoHuntersDao.getInstance();
			isValid = dao.isUserAuthorized(user);
		}
		catch (SQLException | ClassNotFoundException e) {}
		
		assertTrue("User was not validated correctly...", isValid == true);
	}
	
	@Test
	public void testIsUserAuthorizedBoundaryOut() {
		String username = System.getenv("capTestUser");
		String password = System.getenv("capTestPassword")+"0";
		LoginUser user = LoginUser.builder().username(username).password(password).build();
		
		boolean isValid = false;
		TornadoHuntersDao dao = null;
		
		try {
			dao = TornadoHuntersDao.getInstance();
			isValid = dao.isUserAuthorized(user);
		}
		catch (SQLException | ClassNotFoundException e) {}
		
		assertFalse("User was not validated correctly...", isValid == true);
	}
	
	@Test
	public void testIsUserAuthorizedException() {
		String username = null;
		String password = System.getenv("capTestPassword")+"0";
		LoginUser user = LoginUser.builder().username(username).password(password).build();
		
		boolean isValid = false;
		TornadoHuntersDao dao = null;
		
		try {
			dao = TornadoHuntersDao.getInstance();
			isValid = dao.isUserAuthorized(user);
		}
		catch (SQLException | ClassNotFoundException e) {}
		
		assertFalse("User was not validated correctly...", isValid == true);
	}
	
	@Test
	public void testSetUserSessionTokenRegular() {
		String username = System.getenv("capTestUser");
		String password = System.getenv("capTestPassword");
		
		String testSessionToken = "thisisatesttoken12notreal123butifitwasrealthatwouldbeinteresting";
		LoginUser user = LoginUser.builder().username(username).password(password).build();
		
		TornadoHuntersDao dao = null;
		boolean wasSettingUserTokenSuccessful = false;
		
		try {
			dao = TornadoHuntersDao.getInstance();
			wasSettingUserTokenSuccessful = dao.setUserSessionToken(user, testSessionToken);
		}
		catch (SQLException | ClassNotFoundException e) {}
		assertTrue("Session token was not updated into the database correctly...", wasSettingUserTokenSuccessful == true);
	}
	
	@Test
	public void testSetUserSessionTokenBoundaryIn() {
		String username = System.getenv("capTestUser2");
		String password = System.getenv("capTestPassword2");
		
		String testSessionToken = "thisisatesttoken12notreal123butifitwasrealthatwouldbeinteresting";
		LoginUser user = LoginUser.builder().username(username).password(password).build();
		
		TornadoHuntersDao dao = null;
		boolean wasSettingUserTokenSuccessful = false;
		
		try {
			dao = TornadoHuntersDao.getInstance();
			wasSettingUserTokenSuccessful = dao.setUserSessionToken(user, testSessionToken);
		}
		catch (SQLException | ClassNotFoundException e) {}
		assertTrue("Session token was not updated into the database correctly...", wasSettingUserTokenSuccessful == true);
	}
	
	@Test
	public void testSetUserSessionTokenBoundaryOut() {
		String username = System.getenv("capTestUser2");
		String password = System.getenv("capTestPassword2")+"0";
		
		String testSessionToken = "thisisatesttoken12notreal123butifitwasrealthatwouldbeinteresting";
		LoginUser user = LoginUser.builder().username(username).password(password).build();
		
		TornadoHuntersDao dao = null;
		boolean wasSettingUserTokenSuccessful = false;
		
		try {
			dao = TornadoHuntersDao.getInstance();
			wasSettingUserTokenSuccessful = dao.setUserSessionToken(user, testSessionToken);
		}
		catch (SQLException | ClassNotFoundException e) {}
		assertFalse("Session token was not updated into the database correctly...", wasSettingUserTokenSuccessful == true);
	}
	
	@Test
	public void testSetUserSessionTokenException() {
		String username = null;
		String password = System.getenv("capTestPassword2");
		
		String testSessionToken = "thisisatesttoken12notreal123butifitwasrealthatwouldbeinteresting";
		LoginUser user = LoginUser.builder().username(username).password(password).build();
		
		TornadoHuntersDao dao = null;
		boolean wasSettingUserTokenSuccessful = false;
		
		try {
			dao = TornadoHuntersDao.getInstance();
			wasSettingUserTokenSuccessful = dao.setUserSessionToken(user, testSessionToken);
		}
		catch (SQLException | ClassNotFoundException e) {}
		assertFalse("Session token was not updated into the database correctly...", wasSettingUserTokenSuccessful == true);
	}

}
