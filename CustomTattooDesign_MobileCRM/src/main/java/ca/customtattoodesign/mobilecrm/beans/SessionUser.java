package ca.customtattoodesign.mobilecrm.beans;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The {@code SessionUser} class is mainly used as output for the RESTful API.
 * 
 * @author Roman Krutikov
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SessionUser {
	
	private boolean validUser;
	private String sessionToken;
	
	private String firstName;
	private String lastName;
	private String role;
	private String paypalEmail;
	private int overrideJobLimit;
	private int maxJobValue;
	private double averageTimeToCompletion;
	private double averageTimeToIntroduction;
	
}
