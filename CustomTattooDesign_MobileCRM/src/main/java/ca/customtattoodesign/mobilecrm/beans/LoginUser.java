package ca.customtattoodesign.mobilecrm.beans;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Builder
public class LoginUser {
	private String username;
	private String password;
	private boolean isPersistent;
	
	public LoginUser(@JsonProperty("username") String username, @JsonProperty("password") String password, @JsonProperty("isPersistent") boolean isPersistant) {
		this.username = username;
		this.password = password;
		this.isPersistent = isPersistant;
	}
}
