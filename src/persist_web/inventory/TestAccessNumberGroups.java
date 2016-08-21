package persist_web.inventory;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import persist.inventory.AccessNumberGroups;

public class TestAccessNumberGroups {
	public String output = "";
	public String testName;
	private String accessGroupName = "testQa";

	private AccessNumberGroups accessNumberGroup;

	private boolean allowed = false;

	@BeforeClass
	public void beforeClass() {
		accessNumberGroup = new AccessNumberGroups();
	}

	@BeforeMethod
	public void beforeMethod() {
		accessNumberGroup.deleteAll();
	}

	@Test
	public void addAccessNumberGroup() {
		output = accessNumberGroup.add(accessGroupName);

		assertThat(output, containsString("successfully"));
	}

	@Test
	// add access number group with "allowed" removed
	public void addAccessNumberGroupWithAllParameters() {

		output = accessNumberGroup.add(accessGroupName, allowed);

		assertThat(output, containsString("successfully"));
	}

	@Test
	// add duplicate
	public void addDuplicateAccessNumberGroup() {
		accessNumberGroup.add(accessGroupName);

		output = accessNumberGroup.add(accessGroupName);

		assertThat(output, containsString("already exists"));
	}

	@Test
	// modify both parameters
	public void modifyAccessNumberGroup() {

		accessNumberGroup.add(accessGroupName);
		output = accessNumberGroup.modifyByName(accessGroupName, "accessTestQa", allowed);

		assertThat(output, containsString("changed successfully"));
	}

	@Test
	// delete
	public void deleteAccessNumberGroup() {
		accessNumberGroup.add(accessGroupName);

		output = accessNumberGroup.deleteByName(accessGroupName);
		assertThat(output, containsString("successfully"));
	}

}
