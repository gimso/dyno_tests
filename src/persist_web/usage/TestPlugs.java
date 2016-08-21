package persist_web.usage;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.apache.log4j.Logger;

import persist.inventory.SimBoards;
import persist.inventory.SimUnits;
import persist.inventory.Sims;
import persist.usage.Plans;
import persist.usage.PlugTypes;
import persist.usage.Plugs;
import persist.usage.UserGroups;


public class TestPlugs {
	public String output = "";
	public Logger log = Logger.getLogger(this.getClass().getName());
	String userGroup = "userGroupTest";
	String userGroupOwner = "Simgo";
	String planName = "Daily";
	String geoLocation = "Israel (425)";
	String simUnit = "tempSimUnit";
	String simSlot = "0";
	String plugid = "000010002185";
	String validPlugKey = "1234567891234567";
	String invalidPlugKey = "333451112";
	String owner = "Simgo";
	String creator = "Simgo";
	String plugtype = "iPhone";
	Plans plans;
	UserGroups usergroups;
	PlugTypes plugtypes;
	Plugs plugs;
	SimUnits simunits;
	SimBoards simboards;
	Sims sims;

 


  @Test
  public void addPlugWithAllParameters() {
	  output = plugs.add(plugid, validPlugKey, userGroup, true, true, true, null, null, null, true, creator, owner, plugtype, null);
	  assertThat(output, containsString("successfully"));
	  
  }
  
  @Test
  public void addPlugWithInvalidParameters() {
	  // Plug with invalid Key length
	  output = plugs.add(plugid, invalidPlugKey, userGroup, owner);
	  assertThat(output, containsString("Key length must be 16"));
	  
	  // Plug with no plug ID
	  output = plugs.add(null, validPlugKey, userGroup, owner);
	  assertThat(output, containsString("This field is required"));
	  
	  // Plug with no userGroup
	  output = plugs.add(plugid, validPlugKey, null, owner);
	  assertThat(output, containsString("This field is required"));
  }
  
  @Test
  public void modifyAllPlugParameters() {
	  // Add plug and modify it's parameters
	  
	  plugs.add(plugid, validPlugKey, userGroup, owner);
	  output = plugs.modifyById(plugid, plugid, validPlugKey, userGroup, false, false, false, null, null, null, null, creator, owner, plugtype, null);
	  assertThat(output, containsString("successfully"));
	  
  }
  
  @Test
  public void deletePlugById() {
	  plugs.add(plugid, validPlugKey, userGroup, owner);
	  output = plugs.deleteById(plugid);
	  assertThat(output, containsString("was deleted successfully"));
	  
  }
 
  @Test
  public void addDuplicatedPlug () {
	  plugs.add(plugid, validPlugKey, userGroup, owner);
	  output = plugs.add(plugid, validPlugKey, userGroup, owner);		
	  assertThat(output, containsString("Plug with this Id already exists"));
  }

  @BeforeMethod
  public void beforeMethod() {
	  plugs.deleteAll();
  }

  @AfterMethod
  public void afterMethod() {
  }

  
  @BeforeClass
  public void beforeClass() {
	  
	  plans = new Plans();
	  usergroups = new UserGroups(); 
	  simunits = new SimUnits();
	  simboards = new SimBoards();
	  sims = new Sims();
	  plugtypes = new PlugTypes();
	  plugs = new Plugs();
	  
	  plans.add(planName, owner);
	  usergroups.add(userGroup, geoLocation, owner, planName);
	  simunits.add(simUnit, "9.9.9.9");
	  simboards.add(simUnit, simSlot);
	  plugtypes.add(plugtype);
	  sims.add(simUnit + ":0", "0");
	  
  }
  
  

  @AfterClass
  public void afterClass() {
	  
	  plugs.deleteAll();
	  sims.deleteAll();
	  plugtypes.deleteAll();
	  simboards.deleteAll();
	  simunits.deleteAll();
	  usergroups.deleteAll();
	  plans.deleteAll();
	  
  }

}
