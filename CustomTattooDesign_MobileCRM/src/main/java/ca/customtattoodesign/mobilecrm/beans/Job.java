package ca.customtattoodesign.mobilecrm.beans;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * The {@code Job} class is used to hold detailed information about jobs.
 * 
 * @author Roman Krutikov
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@SuperBuilder
public class Job extends BasicJob{

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
	private List<Design> designs;
}
