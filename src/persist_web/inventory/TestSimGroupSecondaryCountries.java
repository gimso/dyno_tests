package persist_web.inventory;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import persist.inventory.GeoLocations;
import persist.inventory.SimGroupSecondaryCountries;
import persist.inventory.SimGroups;

/**
 * 
 * @author Dana
 *
 */
public class TestSimGroupSecondaryCountries {
	public String output = "";
	public String testName;
	
	SimGroupSecondaryCountries simGroupSecondaryCountries;
	GeoLocations geoLocations;
	SimGroups simGroups;
	
	String simGroupName1 = "SimGroupOne";
	String simGroupName2 = "SimGroupTwo";
	
	String group = simGroupName1;
	String priority = "0";
	String geolocation = "unknown1 (435)";
	
	String newGroup = simGroupName2;
	String newPriority = "2";
	String newGeolocation = "unknown2 (820)";
	
	
	//attributes for geolocation establishing at @beforeClass
	String mcc1 = "435";
	String mcc2 = "820";
	String name1 = "unknown1";
	String name2 = "unknown2";
	String countryCode1 = "SFG";
	String countryCode2 = "MNB";
	String junkNumber1 = "7777777";
	String junkNumber2 = "8888888";

	@BeforeClass
	public void beforeClass() {
		this.simGroups = new SimGroups();
		this.simGroupSecondaryCountries = new SimGroupSecondaryCountries();
		this.geoLocations = new GeoLocations();
		//add 2 geoLocations
		geoLocations.add(mcc1, name1, countryCode1, junkNumber1);
		geoLocations.add(mcc2, name2, countryCode2, junkNumber2);
		//add 2 simGroups
		simGroups.add(simGroupName1);
		simGroups.add(simGroupName2);

	}

	@BeforeMethod
	public void beforeMetod() {
		simGroupSecondaryCountries.deleteAll();

	}

	@Test
	public void addSimGroupSecondaryCountry() {
		output = simGroupSecondaryCountries.add(group, priority, geolocation);

		assertThat(output, containsString("successfully"));
	}

	@Test
	public void addDuplicateSimGroupSecondaryCountry() {
		simGroupSecondaryCountries.add(group, priority, geolocation);
		output = simGroupSecondaryCountries.add(group, priority, geolocation);
		assertThat(output, containsString("already exists"));
	}

	@Test
	public void addSimGroupSecondaryCountryWithInvalidPriority() {
		// accepts numbers only
		String invalidPriority = "a";
		output = simGroupSecondaryCountries.add(group, invalidPriority, geolocation);
		assertThat(output, containsString("whole number"));
	}

	@Test
	public void modifySimGroupSecondaryCountry() {
		simGroupSecondaryCountries.add(group, priority, geolocation);
		output = simGroupSecondaryCountries.modifyByGroupAndGeoLocation(group, geolocation, newGroup, newPriority,
				newGeolocation);
		assertThat(output, containsString("successfully"));
	}

	@Test
	public void deleteSimGroupSecondaryCountry() {
		simGroupSecondaryCountries.add(group, priority, geolocation);
		output = simGroupSecondaryCountries.deleteByGroupAndGeoLocation(group, geolocation);
		assertThat(output, containsString("successfully"));
	}

	@AfterClass
	public void afterClass() {
		simGroupSecondaryCountries.deleteAll();
		simGroups.deleteAll();
		geoLocations.deleteByMcc(mcc1);
		geoLocations.deleteByMcc(mcc2);
	}

}
