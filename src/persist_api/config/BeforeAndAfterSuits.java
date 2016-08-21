package persist_api.config;

import java.util.Date;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import cloudProtocol.GateKeeperInterface;
import cloudProtocol.MessageTypes.Challenge;
import global.PersistUtil;
import global.PropertiesUtil;
import global.TestUtil;
import jdbc.TelemetryQueries;
import persist.inventory.AccessNumberGroups;
import persist.inventory.AccessNumbers;
import persist.inventory.GeoLocations;
import persist.inventory.Sims;
import persist.trip.Trips;
import persist.usage.Allocations;
import persist.usage.Plans;
import persist.usage.Plugs;
import persist.usage.UserGroups;
import persist.usage.Users;
import teles_simulator.TelesHttpInterface;

/**
 * Initializing before suite and closing after suite
 */
public class BeforeAndAfterSuits {
	
	private static final String PLAN_NAME = PropertiesUtil.getInstance().getProperty("TEST_RAIL_RESTAPI_PLAN");
	private static final String IMSI = "425010451006375";
	private static final int MCC = 425;
	
	private Sims sims;
	private Plans plans;
	private GeoLocations geolocations;	
	private UserGroups userGroups;				
	private Plugs plugs;	
	private AccessNumberGroups accesNumGroups;
	private AccessNumbers accessNums;	
	private Users users;
	private Trips trips;
	private Allocations allocations;	
	private TelemetryQueries tq;
	private GateKeeperInterface gkInterface;
	private TelesHttpInterface teles;
	private String username = PropertiesUtil.getInstance().getProperty("USAGE_USER");
	
/**
 * Add a sim, create the minimum resources in db that are required for the tests.
 * create a plan, a user group, access number and a user </br> Create a trip and allocation for user
 */
	@BeforeSuite
	public void initParams(){
		sims = new Sims();
		plans = new Plans();
		geolocations = new GeoLocations();
		userGroups = new UserGroups();
		plugs = new Plugs();	
		accesNumGroups = new AccessNumberGroups();
		accessNums = new AccessNumbers();
		users = new Users();
		trips = new Trips();
		allocations = new Allocations();
		tq = new TelemetryQueries();
		gkInterface = new GateKeeperInterface();
		teles = new TelesHttpInterface();
		
		boolean isInternetConnected = TestUtil.isInternetReachable();
		if (!isInternetConnected) {
			String errorMessage = "Skipping tests because Internet Connection or db connection was not available.";
			System.err.println(errorMessage);
			System.exit(0);
		}
		TestUtil.getTestRailAPIInstanceRestApi(PLAN_NAME);
		
		//add sim and 'allow' it
		teles.addSimToSimUnit("3", "2", IMSI, "3g");
		sims.modifyByBoardAndOffset("teles_simulator:1", "1", null, null, null, null, null, null, true, null, null, null, null, null, null);		
		//create basic values in db: plan, geolocation,usergroup and plug
		plans.add("DailyPlanA", "50", "50", "", "", "", "", "0", "1", "0", "0", "1", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "USD", "Simgo", "Simgo");
		geolocations.add("425", "Israel", "IL", "972551122334");
		userGroups.add("Undefined", "Israel (425)", "Simgo", "DailyPlanA");				
		plugs.add(TestParams.PLUG_ID, "1234567891234567", "Undefined", "Simgo");		
		//add access number
		accesNumGroups.add("AccessNumGroup");
		accessNums.add("032222222", "Israel (425)", "1", true, "AccessNumGroup", "Incoming", "International", "Fixed Line");
		accessNums.add("1900000000", "Israel (425)", "1", true, "AccessNumGroup", "Outgoing", "International", "Premium");
		//create new user and inject cookie into the user
		users.add(username, "Undefined", "972551234567", new Date(), "Simgo");
		int userId = tq.getUserId(username);
		tq.injectCookieIntoUser(userId);		
		//create new trip, and new allocation, and inject it into the trip.		
		trips.add(username, TestParams.PLUG_ID, "032222222");
		Challenge challenge  = gkInterface.getChallengeForPlug(TestParams.PLUG_ID);
		gkInterface.createSession(TestParams.PLUG_ID, MCC, challenge.getChallengeResponse());	 
		tq.injectAllocationIntoTrip();
		}
						 	
	/**
	 * Close all resources.
	 * Closing the web driver if it still open
	 */
	@AfterSuite
	public void afterSuite() {				
		try {
			allocations.deleteAll();
			trips.deleteByUser(username);
			users.deleteByHomeNumber("972551234567");
			accessNums.deleteByNumber("032222222");
			accesNumGroups.deleteByName("AccessNumGroup");
			plugs.deleteById(TestParams.PLUG_ID);
			userGroups.deleteByName("Undefined");
			//delete trip history in db so that plan can be deleted.
			tq.deleteTripPlanHistory(tq.getPlanId("DailyPlanA"));
			plans.deleteByName("DailyPlanA");		
			teles.deleteSimFromSimUnit("1", "1", IMSI, "3g");
			
			WebDriver driver = PersistUtil.getInstance().getDriver();
			if (driver != null) {
				driver.close();
				// for some reason when the driver close immediately without
				// waiting
				Thread.sleep(1000);
			}
		} catch (Exception e) {}
	}
}
