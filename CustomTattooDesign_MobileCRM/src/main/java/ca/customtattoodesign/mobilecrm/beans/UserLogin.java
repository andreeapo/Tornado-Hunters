package ca.customtattoodesign.mobilecrm.beans;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The {@code UserLogin} class is mainly used as an input to the RESTful API.
 * 
 * @author Roman Krutikov
 */
@NoArgsConstructor
@Data
@Builder
public class UserLogin {
	private String username;
	private String password;
	
	/**
	 * Constructor that is used when constructing the object from a JSON object
	 * 
	 * @param username
	 *        Username of the user trying to log in.
	 *        
	 * @param password
	 * 	      Password (hashed or plain text) of the user trying to log in,
	 * 
	 */
	public UserLogin(@JsonProperty("username") String username, @JsonProperty("password") String password) {
		this.username = username;
		this.password = password;
	}
}
