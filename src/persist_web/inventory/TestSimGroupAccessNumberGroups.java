package persist_web.inventory;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import persist.inventory.AccessNumberGroups;
import persist.inventory.SimGroupAccessNumberGroups;
import persist.inventory.SimGroups;

public class TestSimGroupAccessNumberGroups {
	public String output = "";
	public String testName;

	String sgName = "SimGroup";
	String accessGroupName = "AccessNumGroup";
	String priority = "1";

	AccessNumberGroups accessNumberGroup;
	SimGroups simGroups;
	SimGroupAccessNumberGroups simGroupAccessNumberGroups;

	@BeforeClass
	public void beforeTest() {
		this.simGroups = new SimGroups();
		this.accessNumberGroup = new AccessNumberGroups();
		this.simGroupAccessNumberGroups = new SimGroupAccessNumberGroups();

		// Add a 2 Sim groups
		String newSgName = "newSimGroup";
		simGroups.add(sgName);
		simGroups.add(newSgName);

		accessNumberGroup.add(accessGroupName);
	}

	@BeforeMethod
	public void beforeMethod() {
		simGroupAccessNumberGroups.deleteAll();
	}

	@Test
	public void addSimGroupAccessNumberGroup() {
		output = simGroupAccessNumberGroups.add(sgName, accessGroupName);

		assertThat(output, containsString("successfully"));
	}

	@Test
	public void addDuplicateSimGroupAccessNumberGroup() {
		simGroupAccessNumberGroups.add(sgName, accessGroupName);
		output = simGroupAccessNumberGroups.add(sgName, accessGroupName);

		assertThat(output, containsString("already exists"));
	}

	@Test
	public void addSimGroupAccessNumberGroupWithAllValues() {
		String priority = "1";
		output = simGroupAccessNumberGroups.add(sgName, accessGroupName, priority);

		assertThat(output, containsString("successfully"));
	}

	@Test
	public void addSimGroupAccessNumberGroupWithInvalidPriority() {
		String priority = "a";
		output = simGroupAccessNumberGroups.add(sgName, accessGroupName, priority);
		assertThat(output, containsString("Enter a whole number"));
	}

	@Test
	public void modifySimGroupAccessNumberGroups() {
		String newSgName = "newSimGroup";
		simGroupAccessNumberGroups.add(sgName, accessGroupName);
		output = simGroupAccessNumberGroups.modifyBySimGroup(sgName, newSgName, accessGroupName, priority);

		assertThat(output, containsString("successfully"));
	}

	@Test
	public void deleteSimGroupAccessNumberGroups() {
		simGroupAccessNumberGroups.add(sgName, accessGroupName);
		output = simGroupAccessNumberGroups.deleteBySimGroup(sgName);
		assertThat(output, containsString("successfully"));
	}

	@AfterClass
	public void afterTest() {
		// Remove all sim groups
		simGroups.deleteAll();

		// Remove all access number groups
		accessNumberGroup.deleteAll();

	}

}
