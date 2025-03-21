package dev.galasa.example.simbank.openaccount;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.assertj.core.api.Fail;

import dev.galasa.Test;
import dev.galasa.artifact.BundleResources;
import dev.galasa.artifact.IBundleResources;
import dev.galasa.core.manager.Logger;
import dev.galasa.zos.IZosImage;
import dev.galasa.zos.ZosImage;
import dev.galasa.zosbatch.IZosBatch;
import dev.galasa.zosbatch.IZosBatchJob;
import dev.galasa.zosbatch.IZosBatchJobname;
import dev.galasa.zosbatch.ZosBatch;
import dev.galasa.zosbatch.ZosBatchJobname;

@Test
public class TestOpenAccount {

	// 1. Inject Managers into the test with annotations.

	@ZosImage(imageTag = "SIMBANK")
	public IZosImage image;
	
	@ZosBatch(imageTag = "SIMBANK")
	public IZosBatch zosBatch;
	
	@ZosBatchJobname(imageTag = "SIMBANK")
	public IZosBatchJobname zosBatchJobname;

	@BundleResources
	public IBundleResources resources;
	
	@Logger
	public Log logger;

	/**
	 * Test which uses the SIMBANK batch job to open a number of new accounts.
	 * The test passes if the job completes successfully (RC=0000)
	 * 
	 * This test shows why accounts should not be hard coded.
	 * The initial test run will pass as the hard coded account numbers are unique,
	 * however subsequent runs will fail as these account numbers are being stored while
	 * the Simbank application is running. The accounts cannot be added and a failed return code (RC=0012)
	 * is received resulting in a failed test.
	 * 
	 * Without changing the test code, the only way to make the test pass again would be to restart Simbank before running the test.
	 * 
	 * To avoid this, accounts should not be hard coded.
	 * Account numbers could be randomly generated with each one passing a check if it already exists before being submitted as a batch job.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testBatchOpenAccount() throws Exception {

		// 2. Create a list of accounts to create
		List<String> accountList = new LinkedList<>();
		accountList.add("901000001,20-40-60,1000");
		accountList.add("901000002,20-40-60,1000");
		accountList.add("901000003,20-40-60,1000");
		accountList.add("901000004,20-40-60,1000");
		accountList.add("901000005,20-40-60,1000");
		accountList.add("901000006,20-40-60,1000");
		accountList.add("901000007,20-40-60,1000");
		accountList.add("901000008,20-40-60,1000");
		accountList.add("901000009,20-40-60,1000");

		// 3. Create the substitution parameters for the JCL
		HashMap<String, Object> parameters = new HashMap<>();
		parameters.put("CONTROL", "ACCOUNT_OPEN");
		parameters.put("DATAIN", String.join("\n", accountList));
		
		// 4. Load the JCL with the given substitution parameters
		String jcl = resources.retrieveSkeletonFileAsString("/skeletons/SIMBANK.jcl", parameters);
		
		// 5. Submit the JCL
		IZosBatchJob batchJob = zosBatch.submitJob(jcl, zosBatchJobname);
		
		// 6. Wait for the batch job to complete
		logger.info("batchJob.toString() = " +  batchJob.toString());
		int rc = batchJob.waitForJob();
		
		// If highest CC was not 0, fail the test
		if (rc != 0) {
			// Print the job output to the run log
			batchJob.retrieveOutput().forEach(jobOutput ->
				logger.info("batchJob.retrieveOutput(): " + jobOutput.getDdname() + "\n" + jobOutput.getRecords() + "\n")
			);
			Fail.fail("Batch job failed RETCODE=" + batchJob.getRetcode() + " Check batch job output");
			
		}
		logger.info("Batch job complete RETCODE=" + batchJob.getRetcode());
	}
}
