package ca.customtattoodesign.mobilecrm.beans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * The {@code ClaimJobLogin} class is used to store session login information as well as
 * 		information about a message user wants to send.
 * 
 * @author Roman Krutikov
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@SuperBuilder
public class ConversationLogin extends SessionLogin {

	private int jobId;
	private String body;
	
}
