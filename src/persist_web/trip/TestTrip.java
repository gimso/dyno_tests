/**
 * @author Or Shachar
 */
package persist_web.trip;

import java.util.Date;

import org.apache.log4j.Logger;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import persist.inventory.AccessNumberGroups;
import persist.inventory.AccessNumbers;
import persist.trip.Trips;
import persist.usage.Plans;
import persist.usage.Plugs;
import persist.usage.UserGroups;
import persist.usage.Users;



public class TestTrip {
	
	public String output = "";
	public Logger log = Logger.getLogger(this.getClass().getName());
	String userGroup = "UserGroup";
	String userGroupOwner = "Simgo";
	String planName = "Daily";
	String geoLocation = "Israel (425)";
	String plugid = "000010002185";
	String plugKey = "1234567891234567";
	String owner = "Simgo";
	String userName = "Frank Sinatra";
	String homeNumber = "97226132519";
	Date registrationDate;
	String incomingAccessNumber = "97226135419";
	String accessNumberGroupName = "AccessNumberGroup300";
	Trips trips;
	Plans plans;
	Users users;
	UserGroups usergroups;
	Plugs plugs;
	AccessNumbers accessNumbers;
	AccessNumberGroups accessNumberGroups;
	
    @BeforeGroups (groups="TestTrip")
	public void beforeGroups() {
		trips = new Trips();
		users = new Users();
		usergroups = new UserGroups();
		plugs = new Plugs();
		plans = new Plans();
		accessNumbers = new AccessNumbers();
		accessNumberGroups = new AccessNumberGroups();

		registrationDate = new Date();
    	
    	plans.add(planName, owner);
    	
    	
    	// Add a user group to be used by the plug and the user 
    	
		usergroups.add(userGroup, geoLocation,userGroupOwner,planName);
		log.info("Added a userGroup");
		
		
    // Add a plug to be used by the trip   
		plugs.add(plugid, plugKey, userGroup, owner);

    // Add a user to be used by the trip
   		users.add(userName, userGroup, homeNumber, registrationDate,owner);
		
    // Add an access Number group
   		accessNumberGroups.add(accessNumberGroupName);
   		
    // Add an Incoming Access Number
		accessNumbers.add(incomingAccessNumber, geoLocation, accessNumberGroupName);

    }
    
	
    
    @BeforeMethod
    public void BeforeMethod(){
    	// delete all existing trips
    	
    		trips.deleteAll();
    }
    
    
    @Test(groups="TestTrip")
    
	// add a trip with a plug and user
	public void addValidTrip(){
		output = trips.add(userName, plugid, incomingAccessNumber);
		
	// Verify that the trip was created successfully
	
	assertThat(output, containsString("successfully"));
	
	}
    
    @Test(groups="TestTrip") 
    
    // add a trip without a plug and verify that it is prevented
    
    public void addTripWithoutAPlug(){    
		output = trips.add(userName, null, incomingAccessNumber);
		
    // Verify that the a validation message was received
       
       assertThat(output, containsString("Plug field is required"));
       
       
    
       
    }
    @Test(groups="TestTrip")    
 // add a trip without a user and verify that it is prevented
    
    public void addTripWithoutAUser(){
        
 		output = trips.add(null, plugid, incomingAccessNumber); 
        assertThat(output, containsString("User field is required"));
        
        
     
        
     }
    
    
    @Test(groups="TestTrip")
    // Delete a trip which has no allocation successfully
    public void canDeleteTripWithNoAllocation(){
      
        trips.add(userName, plugid, incomingAccessNumber);	
 		output = trips.deleteByPlugID(plugid);    
        assertThat(output, containsString("1 entries deleted"));
        
        
     
        
     }
    
    
    
    
    @Test(groups="TestTrip",enabled = false)
    
    public void cannotDeleteTripWithAllocation(){
        
    
 		output = trips.add(userName, null, incomingAccessNumber);
 
    
     // Verify that the a validation message was received
        
        assertThat(output, containsString("cannot delete")); 
        
     }
	
  @AfterGroups(groups="TestTrip")
  public void afterGroups() {
	  
	  trips.deleteAll();
	  accessNumbers.deleteAll();
	  accessNumberGroups.deleteAll();  
	  users.deleteAll();
	  usergroups.deleteAll();
	  plugs.deleteAll();
	  
  }
  
	
	
	}
	

	

		
		
	


