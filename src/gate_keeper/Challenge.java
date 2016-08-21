package gate_keeper;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import cloudProtocol.GateKeeperInterface;
import cloudProtocol.MessageTypes;
import persist.inventory.GeoLocations;
import persist.trip.Trips;
import persist.usage.Plans;
import persist.usage.Plugs;
import persist.usage.UserGroups;
import persist.usage.Users;

public class Challenge {
  
  GateKeeperInterface gkInterface;
  String plugid = BeforeAndAfterGateKeeperTests.plugid;
  String plugIdNotDefined = "999999999";
  String plugKey = "1234567891234567";
  String userGroupName = "TestGroup";  
  String owner = "Simgo";
  String planName = "Daily";
  String geoLocation = "unknown (435)";
  String usergroup = "usergroup Test";
  GeoLocations geolocations;
  Plans plan;
  Users user;
  Plugs plug;
  UserGroups userGroups;
  
  Trips trip;
  
  @BeforeClass
  public void beforeClass() {
	  
	  gkInterface = new GateKeeperInterface();
	  /*
	  user = new Users();
	  plans = new Plans();
	  plug = new Plugs();
	  geolocations = new GeoLocations();
	  userGroups = new UserGroups(); 
	  
	  plans.add(planName, owner);
	  geolocations.add("435", "unknown", "SFG", "7777777");
	  userGroups.add(usergroup, geoLocation, owner, planName);
	  plug.add(plugid, plugKey, usergroup, owner);
	  */
  }

 
 
  
  @Test
  public void ChallengeValidPlug() {
	  
	 
	  
	  MessageTypes.Challenge challenge = gkInterface.getChallengeForPlug(plugid);
	  assertThat (challenge.getStatus(), containsString("NO_ERROR"));
	  
	  
  }
  
  

  @Test
  public void ChallengeNonExistingPlug() {
	  
	  
	  MessageTypes.Challenge challenge = gkInterface.getChallengeForPlug(plugIdNotDefined);
	  assertThat (challenge.getStatus(), containsString("RESOURCE_UNAVAILABLE"));
	  
	  
  }
  
  


  @AfterClass
  public void afterClass() {
	  
	  //plug.deleteAll();
  }

}
