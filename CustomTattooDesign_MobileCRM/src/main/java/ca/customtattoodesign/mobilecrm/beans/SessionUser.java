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
	
}
