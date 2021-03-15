package ca.customtattoodesign.mobilecrm.controllers;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import ca.customtattoodesign.mobilecrm.beans.LoginUser;
import ca.customtattoodesign.mobilecrm.dao.TornadoHuntersDao;
import ca.customtattoodesign.mobilecrm.services.LoginUserService;

@RequestMapping("api")
@org.springframework.web.bind.annotation.RestController
public class RestController {
	
	@Autowired
	private LoginUserService loginUserService;

	@PostMapping("/authenticateCredentials")
	public boolean authenticateCredentials(@Validated @NonNull @RequestBody LoginUser user){
		boolean isValidUser = false;
		
		String userPassword = user.getPassword();
		boolean isPasswordSha256 = loginUserService.isPasswordLengthCorrect(userPassword) && loginUserService.isPasswordRegexCorrect(userPassword);
		
		String userName = user.getUsername();
		boolean isUsernameValid = loginUserService.isUsernameNotNullOrEmpty(userName);
		
		if (isPasswordSha256 && isUsernameValid) {
			try {
				isValidUser = TornadoHuntersDao.getInstance().isUserAuthenticated(user);
			}
			catch (NullPointerException e) {
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getLocalizedMessage());
			}
			catch (SQLException e) {
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Database connection failed...");
			}
			catch (SecurityException e) {
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Cannot access user environment variables...");
			}
			catch (ClassNotFoundException e) {
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Database driver failed...");
			}

		}
		else {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid user paramters...");
		}
		
		return isValidUser;
	}
	
	@GetMapping("/ping")
	public String ping() {
		return "Pong!";
	}
	
	
	@GetMapping("/testDB")
	public String testDB() {
		
		try {
			TornadoHuntersDao db = TornadoHuntersDao.getInstance();
			return db.testQuery();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return "Something went wrong!";
	}
	
}
