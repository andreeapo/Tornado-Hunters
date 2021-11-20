package ca.customtattoodesign.mobilecrm.beans;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The {@code DesignImage} class is used to hold info about an image being downloaded by a client
 * 		via the API from AWS S3 Storage.
 * 
 * @author Roman Krutikov
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class DesignImage {

	private int designId;
	private String imageName;
	private byte[] imageByteRepresentation;
	private Date submissionDate;
}
