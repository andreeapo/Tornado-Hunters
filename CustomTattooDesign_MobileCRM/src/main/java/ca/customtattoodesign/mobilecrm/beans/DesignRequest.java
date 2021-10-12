package ca.customtattoodesign.mobilecrm.beans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * The {@code DesignSubmission} class is used to hold info from when a design request is submitted
 * 		to the API.
 * 
 * @author Roman Krutikov
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@SuperBuilder
public class DesignRequest {

	private String firstName;
	private String lastName;
	private String email;
	private String identifyAs;
	private boolean firstTattoo;
	private boolean color;
	private boolean coverExistingTattoo;
	private String styleOfTattoo;
	private String estimateSize;
	private String description;
	private String position;
	
}
