package persist_web.usage;

import org.testng.annotations.Test;

import persist.inventory.GeoLocations;
import persist.usage.Plans;
import persist.usage.UserGroups;

import org.testng.annotations.BeforeMethod;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

/**
 * 
 * @author Dana
 *
 */
public class TestUserGroups {
	UserGroups userGroups;
	GeoLocations geoLocations;
	Plans plans;

	String name = "Simgo";
	boolean allowed = false;
	String homeGeoLocation = "unknown1 (435)";
	String plan = "Daily";
	String parentUserGroup = name;
	String creator = "Simgo";
	String owner = "Simgo";

	String newName = "Simgo Singapore";
	String newHomeGeoLocation = "unknown2 (820)";
	String newPlan = "Weekly";

	public String output = "";

	@BeforeClass
	public void beforeClass() {
		this.userGroups = new UserGroups();
		this.geoLocations = new GeoLocations();
		this.plans = new Plans();

		geoLocations.add("435", "unknown1", "SFG", "111");
		geoLocations.add("820", "unknown2", "MNB", "222");

		plans.add("Daily", "Simgo");
		plans.add("Weekly", "Simgo");
	}

	@BeforeMethod
	public void beforeMethod() {
		userGroups.deleteAll();
	}

	@Test
	public void addUserGroupWithDefaultValues() {
		output = userGroups.add(name, homeGeoLocation, owner, plan);
		assertThat(output, containsString("successfully"));
	}

	@Test
	public void addUserGroupWithAllValues() {
		// add a user group for parentUserGroup parameter
		userGroups.add(name, homeGeoLocation, owner, plan);

		// add user group with all values, including the added user group.
		output = userGroups.add(newName, allowed, newHomeGeoLocation, newPlan, parentUserGroup, creator, owner);

		// required here since "parentUserGroup" is not deleted at the same time
		// of the childUserGroup
		userGroups.deleteAll();
		assertThat(output, containsString("successfully"));
	}

	@Test
	public void addDuplicateUserGroup() {
		userGroups.add(name, homeGeoLocation, owner, plan);
		output = userGroups.add(name, homeGeoLocation, owner, plan);
		assertThat(output, containsString("already exists"));
	}

	@Test
	public void modifyUserGroup() {
		userGroups.add(name, homeGeoLocation, owner, plan);
		output = userGroups.modifyByName(name, newName, null, newHomeGeoLocation, newPlan, null, creator, owner);
		assertThat(output, containsString("successfully"));
	}

	@Test
	public void deleteUserGroup() {
		userGroups.add(name, homeGeoLocation, owner, plan);
		output = userGroups.deleteByName(name);
		assertThat(output, containsString("successfully"));
	}

	@AfterClass
	public void afterClass() {
		userGroups.deleteAll();
		geoLocations.deleteAll();
		plans.deleteAll();
	}

}
