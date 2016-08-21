package persist_web.inventory;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

import org.openqa.selenium.NoSuchElementException;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import persist.inventory.GeoLocations;

/**
 * 
 * @author Dana
 *
 */



public class TestGeoLocations {
	GeoLocations geoLocations;

	String mcc1 = "435";
	String mcc2 = "820";
	String invalidMcc = "a";
	String name1 = "unknown1";
	String name2 = "unknown2";
	String countryCode1 = "SFG";
	String countryCode2 = "MNB";
	String junkNumber1 = "7777777";
	String junkNumber2 = "8888888";
	String billingZone1 = "1";
	String billingZone2 = "2";

	public String output = "";
	
	@BeforeClass
	public void beforeClass(){
		geoLocations = new GeoLocations();
	}
	@Test
	public void addGeoLocation() {
		output = geoLocations.add(mcc1, name1, countryCode1, junkNumber1);
		assertThat(output, containsString("successfully"));
	}
	
	@Test
	public void addGeoLocationWithAllParams() {
		output = geoLocations.add(mcc1, name1, countryCode1, junkNumber1, billingZone1);
		assertThat(output, containsString("successfully"));
	}
	

	@Test
	// add duplicate geo location with the same Mcc
	public void addDuplicateGeoLocation() {
		geoLocations.add(mcc1, name1, countryCode1, junkNumber1);
		output = geoLocations.add(mcc1, name2, countryCode2, junkNumber2);
		assertThat(output, containsString("already exists"));
	}

	@Test
	public void addGeoLocationWithInvalidMcc() {
		output = geoLocations.add(invalidMcc, name1, countryCode1, junkNumber1);
		assertThat(output, containsString("Enter a whole number"));
	}

	@Test
	public void modifyGeoLocation() {
		geoLocations.add(mcc1, name1, countryCode1, junkNumber1);
		output = geoLocations.modifyByMcc(mcc1, name2, countryCode2, junkNumber2, billingZone2);
		assertThat(output, containsString("changed successfully"));
	}

	@Test
	// delete
	public void deleteGeoLocation() {
		geoLocations.add(mcc1, name1, countryCode1, junkNumber1);
		output = geoLocations.deleteByMcc(mcc1);
		assertThat(output, containsString("successfully"));
	}
	
	@AfterMethod
	public void afterMethod(ITestResult iTestResult) {
		String methodName = iTestResult.getMethod().getMethodName();
		switch (methodName) {
		case "deleteGeoLocation":
			break;
		case "addGeoLocationWithInvalidMcc":
			break;
		default:
			try {
				geoLocations.deleteByMcc(mcc1);
			} catch (NoSuchElementException e) {
			}
			break;
		}
	}
}
