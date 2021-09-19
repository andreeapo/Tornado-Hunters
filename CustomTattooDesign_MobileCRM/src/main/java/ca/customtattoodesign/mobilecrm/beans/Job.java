package ca.customtattoodesign.mobilecrm.beans;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The {@code Job} class is used to hold information about jobs.
 * 
 * @author Roman Krutikov
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Job {

	private int jobId;
	private int artistId;
	private String state;
	private String title;
	private String customerName;
	private String tattooLocation;
	private String tattooType;
	private String tattooStyle;
	private boolean color;
	private double commission;
	private String description;
	private List<Message> messages;
	
}
