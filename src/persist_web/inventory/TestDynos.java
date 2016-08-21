package persist_web.inventory;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import persist.inventory.Dynos;

/**
 * 
 * @author Dana
 *
 */
public class TestDynos {
	private Dynos dynos;

	private String dynoName = "Dyno16";
	private String dynoIdentifier = "0";

	public String output = "";
	public String testName;

	@BeforeClass 
	public void beforeClass() {
		dynos = new Dynos();
	}

	@Test(enabled=false)
	public void modifyDynoName() {
		output = dynos.modifYByIdentifier(dynoIdentifier, dynoName);
		assertThat(output, containsString("changed successfully"));
	}

	@AfterMethod
	public void afterMethod() {
		String dynoName = "Dyno0";
		dynos.modifYByIdentifier(dynoIdentifier, dynoName);
	}
}
