package ca.customtattoodesign.mobilecrm.beans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * The {@code ClaimJobLogin} class is used to store session login information as well as
 * 		the id of the Job a user wants.
 * 
 * @author Roman Krutikov
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@SuperBuilder
public class ClaimJobLogin extends SessionLogin {

	private int jobId;
}
