package persist_web.inventory;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * 
 * @author Dana
 *
 */
public class TestSimUnits {

	SimUnits simUnit;

	public String simUnitName1 = "TestQa";
	public String simUnitName2 = "NewTestQa1";
	public String simUnitAdress1 = "1.1.1.1";
	public String simUnitAdress2 = "hq.gimso.net:10";

	public String output = "";

	
	@BeforeClass
	public void beforeClass(){
		simUnit = new SimUnits();
	}
	
	@BeforeMethod
	public void beforeMethod() {
		simUnit.deleteAll();
	}

	@Test(enabled=false)
	public void addSimUnit() {
		output = simUnit.add(simUnitName1, simUnitAdress1);
		assertThat(output, containsString("successfully"));
	}

	@Test(enabled=false)
	// duplicate name
	public void addDuplicateSimUnitWithTheSameName() {
		simUnit.add(simUnitName1, simUnitAdress1);
		output = simUnit.add(simUnitName1, simUnitAdress2);
		assertThat(output, containsString("Name already exists"));
	}

	@Test (enabled=false)
	// duplicate address
	public void addDuplicateSimUnitWithTheSameAddress() {
		simUnit.add(simUnitName1, simUnitAdress1);
		output = simUnit.add(simUnitName2, simUnitAdress1);
		assertThat(output, containsString("Address already exists"));
	}

	@Test (enabled=false)
	public void modifySimUnit() {
		simUnit.add(simUnitName1, simUnitAdress1);
		output = simUnit.modifyByName(simUnitName1, simUnitName2, simUnitAdress2);
		assertThat(output, containsString("changed successfully"));
	}

	@Test (enabled=false)
	public void deleteSimUnit() {
		simUnit.add(simUnitName1, simUnitAdress1);
		output = simUnit.deleteByName(simUnitName1);
		assertThat(output, containsString("deleted successfully"));
	}

}
