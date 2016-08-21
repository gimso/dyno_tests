package persist_web.trip;

import java.util.Date;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import persist.inventory.AccessNumberGroups;
import persist.inventory.AccessNumbers;
import persist.trip.ArchivedTrips;
import persist.trip.Trips;
import persist.usage.Plans;
import persist.usage.Plugs;
import persist.usage.UserGroups;
import persist.usage.Users;

public class ArchivedTripsTest {

	public String output;

	private String userGroup = "UserGroup";
	private String userGroupOwner = "Simgo";
	private String planName = "Daily";
	private String geoLocation = "Israel (425)";
	private String plugId = "000010009999";
	private String plugKey = "9999888877776666";
	private String owner = "Simgo";
	private String userName = "2nd Frank Sinatra";
	private String homeNumber = "97225555555";
	private String incomingAccessNumber = "97222222222";
	private String accessNumberGroupName = "AccessNumberGroupTest";
	private Date registrationDate;

	private ArchivedTrips archivedTrips;
	private Trips trips;
	private Plans plans;
	private Users users;
	private UserGroups usergroups;
	private Plugs plugs;
	private AccessNumbers accessNumbers;
	private AccessNumberGroups accessNumberGroups;

	@BeforeClass
	public void beforeClass() {

		this.archivedTrips = new ArchivedTrips();
		this.trips = new Trips();
		this.users = new Users();
		this.usergroups = new UserGroups();
		this.plugs = new Plugs();
		this.plans = new Plans();
		this.accessNumbers = new AccessNumbers();
		this.accessNumberGroups = new AccessNumberGroups();
		this.registrationDate = new Date();
		
		//Add plan
		this.plans.add(planName, owner);

		// Add a user group to be used by the plug and the user
		this.usergroups.add(userGroup, geoLocation, userGroupOwner, planName);

		// Add a plug to be used by the trip
		this.plugs.add(plugId, plugKey, userGroup, owner);

		// Add a user to be used by the trip
		this.users.add(userName, userGroup, homeNumber, registrationDate, owner);

		// Add an access Number group
		this.accessNumberGroups.add(accessNumberGroupName);

		// Add an Incoming Access Number
		this.accessNumbers.add(incomingAccessNumber, geoLocation, accessNumberGroupName);
		
		// Add a Trip 
		this.trips.add(userName, plugId, incomingAccessNumber);
		
		// Delete The Trip (and it's now moving on to archived trips)
		this.trips.deleteByPlugID(plugId);
	}

	@Test
	public void checkArchivedTripPage() {
		Date date = new Date();
		
		boolean findArchivedTrip = archivedTrips.findArchivedTripByPlugIdAndDate(plugId, date);
		
		output = "find an archived Trip by Plug id and Date - " + findArchivedTrip;
		
		Assert.assertTrue(findArchivedTrip, output);
	}

	@AfterClass
	public void afterClass() {

		trips.deleteAll();
		accessNumbers.deleteAll();
		accessNumberGroups.deleteAll();
		users.deleteAll();
		usergroups.deleteAll();
		plugs.deleteAll();

	}
}
