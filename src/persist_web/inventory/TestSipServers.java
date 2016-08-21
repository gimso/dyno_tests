package persist_web.inventory;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import persist.inventory.SipServers;

/**
 * 
 * @author Dana
 *
 */
public class TestSipServers {
	public String output = "";
	public String testName;
	
	SipServers sipServers;
	String sipName = "MySipServer";
	String sipIdentifier = "0";
	
	@BeforeClass
	public void beforeClass(){
		this.sipServers = new SipServers();
	}
	
	@Test
	public void modifySipServerName() {
		output = sipServers.modifyByIdentifier(sipIdentifier, sipName);
		
		assertThat(output, containsString("successfully"));
	}

	@AfterMethod
	public void afterMethod() {
		String sipName = "";
		sipServers.modifyByIdentifier(sipIdentifier, sipName);
	}
}
