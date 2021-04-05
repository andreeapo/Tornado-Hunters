package ca.customtattoodesign.mobilecrm.beans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * The {@code User} class is mainly used as output for the RESTful API.
 * 
 * @author Roman Krutikov
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@SuperBuilder
public class User {
	
	private int id;
	private String firstName;
	private String lastName;
	private String role;
	private String paypalEmail;
	private int overrideJobLimit;
	private int maxJobValue;
	private double averageTimeToCompletion;
	private double averageTimeToIntroduction;
	
	private int refundsLast30Days;
	private int refundsLifetime;
	private double earningsLifetime;
	private int jobsTakenLifetime;
	private int jobsTakenLast30Days;
	
}
