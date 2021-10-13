package ca.customtattoodesign.mobilecrm.beans;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The {@code Design} class is used to hold information about designs for jobs.
 * 
 * @author Roman Krutikov
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Design {

	private int id;
	private String imageFileName;
	private int jobId;
	private int artistId;
	
}
