package gate_keeper;

import java.util.Date;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import cloudProtocol.GateKeeperInterface;
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

public class BeforeAndAfterGateKeeperTests {
	
  GateKeeperInterface gkInterface;
  protected static final String plugid = "000010002185";
  String plugIdNotDefined = "999999999";
  String plugKey = "1234567891234567";
  String userGroupName = "TestGroup";  
  String owner = "Simgo";
  String planName = "Daily";
  String geoLocation = "Israel (425)";
  String usergroup = "usergroup Test";
  String username = "tester";
  String homeNumber = "972526141122";
  String incomingAccessNumber = "972036134555";
  String accessGroupName = "AccessNumberGroup";
  String imsi="425000000000009";
  String simBoardId = "5";
  String simOffsetId = "1";
  String simMSISDN = "9725551325";
  Date regDate = new Date();
  GeoLocations geolocations;
  TelesHttpInterface teles;
  Sims sims;
  Plans plans;
  Users users;
  Plugs plug;
  AccessNumbers accessnumbers;
  AccessNumberGroups accessnumbergroups;
  UserGroups userGroups;
  Allocations allocations;  
  Trips trips;

  @BeforeSuite
  public void beforeSuite() {
	  // Create all required persist items required for an allocation	  
	  gkInterface = new GateKeeperInterface();
	  users = new Users();
	  plans = new Plans();
	  plug = new Plugs();
	  trips = new Trips();
	  allocations = new Allocations();
	  geolocations = new GeoLocations();
	  userGroups = new UserGroups();
	  accessnumbers = new AccessNumbers();
	  accessnumbergroups = new AccessNumberGroups();
	  
	  plans.add(planName, owner);
	  geolocations.add("425", "Israel", "IL", "7777777");
	  userGroups.add(usergroup, geoLocation, owner, planName);
	  users.add(username, usergroup, homeNumber, regDate, owner);
	  accessnumbergroups.add(accessGroupName);
	  accessnumbers.add(incomingAccessNumber, geoLocation, accessGroupName);
	  accessnumbers.add("1900000000", "Israel (425)", "1", true, "AccessNumGroup", "Outgoing", "International", "Premium");
	  plug.add(plugid, plugKey, usergroup, owner);
	  trips.add(username, plugid, incomingAccessNumber);
	  
	  
	//Create a sim on the teles simulator
    teles = new TelesHttpInterface();
    System.out.println(teles.addSimToSimUnit(simBoardId,simOffsetId,imsi,"2g3g"));
    sims = new Sims();
    try {
		Thread.sleep(15000);
	} catch (InterruptedException e) {
		e.printStackTrace();
	}
    sims.modifyByBoardAndOffset("teles_simulator:" + simBoardId, simOffsetId, null, null, null, null, null, null, true, simMSISDN, null, null, null, null, null);
    sims.allowAllSims();
	
    
	 
  }

  @AfterSuite
  public void afterSuite() {
	  allocations.deleteAll();
	  teles.deleteBoard(simBoardId);
	  trips.deleteAll();
	  plug.deleteAll();
	  accessnumbers.deleteAll();
	  accessnumbergroups.deleteAll();
	  users.deleteAll();
	  userGroups.deleteAll();
	  plans.deleteAll();
	  
  }

}
