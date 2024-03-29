package ca.customtattoodesign.mobilecrm.beans;

import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;
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
@SuperBuilder
public class SessionUser extends User {
	
	private boolean validUser;
	private String sessionToken;
	
}
