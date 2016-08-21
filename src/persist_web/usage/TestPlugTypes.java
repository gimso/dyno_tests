package persist_web.usage;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import persist.usage.PlugTypes;

/**
 * 
 * @author Dana
 *
 */
public class TestPlugTypes {
	PlugTypes plugTypes;
	String plugTypeName1 = "iphone6";
	String plugTypeName2 = "SGS6";
	String erpInfo = "aaa";
	
	public String output = "";

	@BeforeClass
	public void beforeClass() {
		this.plugTypes = new PlugTypes();
	}

	@BeforeMethod
	public void beforeMethod() {
		plugTypes.deleteAll();
	}

	@Test
	public void addPlugType() {
		output = plugTypes.add(plugTypeName1);
		assertThat(output, containsString("successfully"));
	}

	@Test
	public void addDuplicatePlugType() {
		plugTypes.add(plugTypeName1);
		output = plugTypes.add(plugTypeName1);
		assertThat(output, containsString("already exists"));
	}

	@Test
	public void addPlugTypeWithAllValues() {
		output = plugTypes.add(plugTypeName1, erpInfo);
		assertThat(output, containsString("successfully"));
	}

	@Test
	public void modifyPlugType() {
		plugTypes.add(plugTypeName1);
		output = plugTypes.modifyByName(plugTypeName1, plugTypeName2, erpInfo);
		assertThat(output, containsString("successfully"));
	}

	@Test
	public void deletePlugType() {
		plugTypes.add(plugTypeName1);
		output = plugTypes.delete(plugTypeName1);
		assertThat(output, containsString("successfully"));
	}

	@AfterClass
	public void afterClass() {
		plugTypes.deleteAll();
	}

}
