package persist_web.usage;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import java.util.Date;
import org.testng.annotations.Test;

import persist.inventory.GeoLocations;
import persist.usage.Plans;
import persist.usage.UserGroups;
import persist.usage.Users;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;

public class TestUsers {

	public String output = "";
	Users users;
	String username = "userNameTest";
	String usernameModify = "userNameLongDifferent";
	String userGroup = "userGroupTest";
	String userGroupOwner = "Simgo";
	String userHomeNumber = "972526143889";
	String userPassword = "1234";
	String userGcmKey = "aaa";
	String userApnsToken;
	String planName = "Daily";
	String geoLocation = "unknown (435)";
	String owner = "Simgo";
	String creator = "Simgo";
	String plugtype = "iPhone";
	Date regDate = new Date();
	Plans plans;
	UserGroups usergroups;
	GeoLocations geolocations;

	@BeforeClass
	public void beforeClass() {

		users = new Users();
		plans = new Plans();
		geolocations = new GeoLocations();
		usergroups = new UserGroups();
		
		plans.add(planName, owner);
		geolocations.add("435", "unknown", "SFG", "7777777");
		usergroups.add(userGroup, geoLocation, owner, planName);
	}

	@BeforeMethod
	public void beforeMethod() {
		users.deleteAll();
	}

	@Test
	public void AddValidUser() {
		output = users.add(username, userGroup, true, userHomeNumber, userPassword, userGcmKey, userApnsToken, null,
				creator, owner, null, regDate);
		assertThat(output, containsString("successfully"));

	}

	@Test
	public void addUserWithMissingParameters() {
		output = users.add(username, userGroup, true, userHomeNumber, userPassword, userGcmKey, userApnsToken, null,
				creator, owner, null, null);
		assertThat(output, containsString("This field is required"));

	}

	@Test
	public void modifyUser() {
		users.add(username, userGroup, userHomeNumber, regDate, owner);
		output = users.modifyByName(username, usernameModify, null, null, null, null, null, null, null, null, null,
				null, null);
		assertThat(output, containsString("successfully"));

	}

	@Test
	public void deleteUser() {
		users.add(username, userGroup, userHomeNumber, regDate, owner);
		output = users.deleteByName(username);
		assertThat(output, containsString("successfully"));

	}

	@AfterClass
	public void afterClass() {
		users.deleteAll();
		usergroups.deleteAll();
		plans.deleteAll();
		geolocations.deleteByMcc("435");

	}

}
