package persist_web.usage;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import persist.usage.Plans;

public class PlansTest {

	private static final String SIMGO = "Simgo";

	public String output = "";

	private Plans plans;

	@BeforeClass
	public void beforeClass() {

		plans = new Plans();

	}

	@BeforeMethod
	public void beforeMethod() {

		plans.deleteAll();
	}

	@Test
	public void addPlanWithAllParameters() {

		String planName = "PlansTest";
		String planPeriodDays = "7";
		String chargeCurrency = "EUR";
		String creator = SIMGO;
		String owner = SIMGO;

		output = plans.add(planName, "3", "3", null, "3", "3", null, "3", planPeriodDays, "3", "3", "3", "3", "3", "3",
				"3", null, null, "3", "3", "3", "3", null, null, chargeCurrency, creator, owner);

		String expectedOutput = "was added successfully";

		assertThat(output, containsString(expectedOutput));
	}

	@Test
	public void modifyAllPlanParameters() {
		String planName = "PlansTest";
		String planPeriodDays = "7";
		String chargeCurrency = "EUR";
		String creator = SIMGO;
		String owner = SIMGO;

		plans.add(planName, "3", "3", null, "3", "3", null, "3", planPeriodDays, "3", "3", "3", "3", "3", "3", "3",
				null, null, "3", "3", "3", "3", null, null, chargeCurrency, creator, owner);

		output = plans.modifyByName(planName, "PlansTest2", "2", "2", null, "2", "2", null, "2", "1", "2", "2", "2",
				"2", "2", "2", "2", null, null, "2", "2", "2", "2", null, null, "USD", creator, owner);

		String expectedOutput = "was changed successfully";

		assertThat(output, containsString(expectedOutput));
	}

	@Test
	public void deletePlanByName() {
		
		String planName = "deleteThisPlan";
		
		plans.add(planName, SIMGO);
		
		output = plans.deleteByName(planName);

		String expectedOutput = "was deleted successfully";

		assertThat(output, containsString(expectedOutput));
	}

	@Test
	public void addDuplicatedPlan() {
		
		String planName = "Duplicated Plan";
		
		plans.add(planName, SIMGO);
		
		output = plans.add(planName, SIMGO);

		String expectedOutput = "Plan with this Name already exists";

		assertThat(output, containsString(expectedOutput));
	}

	@Test
	public void addPlanWithInvalidValues() {
		String planPeriodDays = "7";
		String chargeCurrency = "EUR";
		String creator = SIMGO;
		String owner = SIMGO;
		
		output = plans.add("planName", "a", "a", null, "a", "a", null, "a", planPeriodDays, "a", "a", "a", "a", "a", "a", "a",
		null, null, "a", "a", "a", "a", null, null, chargeCurrency, creator, owner);

		String expectedOutput = "Enter a whole number";

		assertThat(output, containsString(expectedOutput));
	}

	@Test
	public void addLimitToUnitAndCallsAndData() {
		String planName = "PlansTest";
		String planPeriodDays = "7";
		String chargeCurrency = "EUR";
		String creator = SIMGO;
		String owner = SIMGO;

		output = plans.add(planName, "3", "3", "3", "3", "3", null, "3", planPeriodDays, "3", "3", "3", "3", "3", "3",
				"3", null, null, "3", "3", "3", "3", null, null, chargeCurrency, creator, owner);
		
		String expectedOutput = "Please remove the limit from unit or from calls and data";
		
		assertThat(output, containsString(expectedOutput));
	}

	@AfterClass
	public void afterClass() {

		plans.deleteAll();

	}
}
