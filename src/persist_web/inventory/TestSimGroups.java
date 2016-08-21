package persist_web.inventory;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.core.IsNot.not;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import persist.inventory.Apns;
import persist.inventory.SimGroups;

public class TestSimGroups {
	SimGroups simGroups;
	Apns apns;

	public String output = "";
	public String testName;

	String apnName = "testQA";
	String name = "SimsQA";
	String apn = "testAPN";

	@BeforeClass
	// adds apn for method "addSimGroupWithAllValues"
	public void beforeTest() {
		this.simGroups = new SimGroups();
		this.apns = new Apns();

		apns.add(apnName, apn);
	}

	@BeforeMethod
	public void beforeMethod() {
		simGroups.deleteAll();
	}

	@Test
	public void addSimGroup() {
		output = simGroups.add(name);

		assertThat(output, containsString("successfully"));
	}

	@Test
	public void addSimGroupWithAllValues() {

		String priority = "1";
		String apnName = "testQA";
		String smsc = "972558700785";
		String roamingMode = "Allow Roaming";
		String digestAugmentation = "0000047FFF6F06020008AABBCCDDEEFF00110000026F070200080849520000000001";

		output = simGroups.add(name, priority, apnName, smsc, roamingMode, digestAugmentation);

		assertThat(output, containsString("successfully"));
	}

	@Test
	public void addSimGroupWithInvalidPriorityValue() {
		String priority = "a";
		String apnName = "testQA";
		String smsc = "972558700785";
		String roamingMode = "Allow Roaming";
		String digestAugmentation = "0000047FFF6F06020008AABBCCDDEEFF00110000026F070200080849520000000001";

		output = simGroups.add(name, priority, apnName, smsc, roamingMode, digestAugmentation);
		assertThat(output, containsString("Enter a whole number"));

	}

	@Test
	public void addSimGroupWithInvalidSmscValue() {
		String priority = "1";
		String apnName = "testQA";
		String smsc = "a";
		String roamingMode = "Allow Roaming";
		String digestAugmentation = "0000047FFF6F06020008AABBCCDDEEFF00110000026F070200080849520000000001";

		output = simGroups.add(name, priority, apnName, smsc, roamingMode, digestAugmentation);

		assertThat(output, containsString("should contain only numeric characters"));
	}

	@Test
	public void addSimGroupWithInvalidDigestValue() {
		String priority = "1";
		String apnName = "testQA";
		String smsc = "972558700785";
		String roamingMode = "Allow Roaming";
		String digestAugmentation = "a";

		output = simGroups.add(name, priority, apnName, smsc, roamingMode, digestAugmentation);

		assertThat(output, not(containsString("successfully")));
	}

	@Test
	public void addDuplicateSimGroup() {
		simGroups.add(name);
		output = simGroups.add(name);

		assertThat(output, not(containsString("successfully")));
	}

	@Test
	// modify all params
	public void modifySimGroup() {
		String newName = "newTest1";
		String priority = "1";
		String apnName = "testQA";
		String smsc = "972558700785";
		String roamingMode = "Allow Roaming";
		String digestAugmentation = "0000047FFF6F06020008AABBCCDDEEFF00110000026F070200080849520000000001";

		simGroups.add(name, priority, apnName, smsc, roamingMode, digestAugmentation);
		output = simGroups.modifyByName(name, newName, "3", null, null, "Prohibit Roaming", "");

		assertThat(output, containsString("successfully"));
	}

	@Test
	public void deleteSimGroup() {
		simGroups.add(name);
		output = simGroups.deleteByName(name);

		assertThat(output, containsString("successfully"));
	}

	@AfterTest
	// deletes the apn that was added for method "addSimGroupWithAllValues"
	public void afterClass() {
		apns.deleteByName(apnName);
		simGroups.deleteAll();
	}
}
