package ca.customtattoodesign.mobilecrm.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import ca.customtattoodesign.mobilecrm.beans.LoginUser;
import ca.customtattoodesign.mobilecrm.services.LoginUserService;

@RequestMapping("api")
@org.springframework.web.bind.annotation.RestController
public class RestController {
	
	@Autowired
	private LoginUserService loginUserService;

	@PostMapping("/authenticateCredentials")
	public boolean authenticateCredentials(@Validated @NonNull @RequestBody LoginUser user) throws ResponseStatusException {
		return loginUserService.isValidLogin(user);
	}
	
	@GetMapping("/ping")
	public String ping() {
		return "Pong!";
	}
	
}
