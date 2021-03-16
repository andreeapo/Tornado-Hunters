package ca.customtattoodesign.mobilecrm.beans;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The {@code LoginUser} class is mainly used as an input to the RESTful API.
 * 
 * @author Roman Krutikov
 */
@NoArgsConstructor
@Data
@Builder
public class LoginUser {
	private String username;
	private String password;
	private boolean isPersistent;
	
	/**
	 * Constructor that is used when constructing the object from a JSON object
	 * 
	 * @param username
	 *        Username of the user trying to log in.
	 *        
	 * @param password
	 * 	      Password (hashed or plain text) of the user trying to log in,
	 * 
	 * @param isPersistant
	 *        Whether or not the user wants to stay logged after exiting the application they are using.
	 * 
	 */
	public LoginUser(@JsonProperty("username") String username, @JsonProperty("password") String password, @JsonProperty("isPersistent") boolean isPersistant) {
		this.username = username;
		this.password = password;
		this.isPersistent = isPersistant;
	}
}
