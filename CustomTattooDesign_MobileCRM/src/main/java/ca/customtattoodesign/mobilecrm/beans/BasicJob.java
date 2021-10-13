package ca.customtattoodesign.mobilecrm.beans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * The {@code BasicJob} class is used to hold the basics of a job.
 * 
 * @author Roman Krutikov
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@SuperBuilder
public class BasicJob {

	private int jobId;
	private String jobAccessToken;
}
