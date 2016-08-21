package persist_web.inventory;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import persist.inventory.FwVersions;

public class TestFWVersion {

	private FwVersions fwVersions;

	public String output = "";
	public String testName;

	@BeforeClass
	private void beforeClassInit() {
		this.fwVersions = new FwVersions();
	}

	@BeforeMethod
	public void configBeforeMethod() {
		this.fwVersions.deleteAll();
	}

	@Test
	public void addVersion() {/* true */

		String fwVersion = "99.99.99";
		output = fwVersions.add(fwVersion);

		assertThat(output, containsString("successfully"));

	}

	@Test
	public void modifyVersion() {/* true */

		String version = "88.88.88";
		String newVersion = "99.99.91";
		// add
		fwVersions.add(version);

		output = fwVersions.modifyByVersion(version, newVersion);

		assertThat(output, containsString("successfully"));
	}

	@Test
	public void deleteVersion() {/* true */

		String version = "77.77.77";

		// add
		fwVersions.add(version);

		output = fwVersions.deleteByVersion(version);

		assertThat(output, containsString("successfully"));

	}

	@Test
	public void addDuplicateVersion() {/* false */

		String version = "66.66.66";

		// add
		fwVersions.add(version);

		output = fwVersions.add(version);
		
		assertThat(output, containsString("Fw version with this FW update version already exists"));
	}

	@Test
	public void addInvalidVersion() {/* false */

		String versionInvalid = "A.g$^G|`~";
		output = fwVersions.add(versionInvalid);
		assertThat(output, containsString("Only numeric characters delimited in 2 dot format are allowed"));
	}

}