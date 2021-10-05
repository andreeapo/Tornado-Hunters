package ca.customtattoodesign.mobilecrm.beans;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The {@code Message} class is used to hold info about a message sent within a job.
 * 
 * @author Roman Krutikov
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Message {
	
	private int messageId;
	private int designId;
	private String body;
	private Timestamp createdAt;
	private int designerId;
	private String commentPicture;
	private boolean read;
	private int jobId;
	
}
