package persist_web.usage;

import org.testng.annotations.Test;

import persist.usage.AccessControls;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

/**
 * 
 * @author Dana
 *
 */
public class TestAccessControls {

	AccessControls accessControls;
	String cidr = "0.0.0.0";
	String type = "Dyno Gatekeeper";
	String newCidr = "http://www.simgo-mobile.com/";

	public String output = "";

	@BeforeClass
	public void beforeClass() {
		this.accessControls = new AccessControls();
	}

	@BeforeMethod
	public void beforeMethod() {
		accessControls.deleteAll();
	}

	@Test(enabled=false)
	public void addAccessControlWithDefaultType() {
		output = accessControls.add(cidr);
		assertThat(output, containsString("successfully"));
	}

	@Test(enabled=false)
	// currently there is only one type, but this might change to different ip
	// addresses.
	public void addAccessControlWithAllValues() {
		output = accessControls.add(cidr, type);
		assertThat(output, containsString("successfully"));
	}

	@Test(enabled=false)
	public void addDuplicateAccessControl() {
		accessControls.add(cidr);
		output = accessControls.add(cidr);
		assertThat(output, containsString("already exists"));
	}

	@Test(enabled=false)
	public void modifyAccessControl() {
		accessControls.add(cidr);
		output = accessControls.modifyByCidr(cidr, newCidr, type);
		assertThat(output, containsString("successfully"));
	}

	@Test(enabled=false)
	public void deleteAccessControl() {
		accessControls.add(cidr);
		output = accessControls.deleteByCidr(cidr);
		assertThat(output, containsString("successfully"));
	}
	
	@AfterClass
	public void afterClass(){
		accessControls.deleteAll();
	}

}
