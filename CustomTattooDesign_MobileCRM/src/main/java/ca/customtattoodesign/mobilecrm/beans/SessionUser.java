package ca.customtattoodesign.mobilecrm.beans;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SessionUser {
	
	private boolean isValidUser;
	private String sessionToken;
	
}
