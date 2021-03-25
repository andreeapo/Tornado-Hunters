package ca.customtattoodesign.mobilecrm.services;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import ca.customtattoodesign.mobilecrm.beans.Job;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JobServiceTest {

	@Autowired
	JobService jobService;
	
	@Test
	public void testFetchUnclaimedJobsRegular() throws SQLException {
		List<Job> jobs = jobService.fetchUnclaimedJobs();
		
		assertTrue("Unclaimed jobs list was null...", jobs != null);
	}

}
