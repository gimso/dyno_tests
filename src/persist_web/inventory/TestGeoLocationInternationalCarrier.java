package persist_web.inventory;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import persist.inventory.GeoLocationInternaionalCarriers;
import persist.inventory.GeoLocations;
import persist.inventory.InternationalCarriers;

/**
 * 
 * @author Dana
 *
 */
public class TestGeoLocationInternationalCarrier {
	GeoLocationInternaionalCarriers geoLocationInternationalCarrier;
	InternationalCarriers internationalCarrier;
	GeoLocations geoLocations;

	String geoLocation1 = "unknown1 (435)";
	String geoLocation2 = "unknown2 (820)";
	String internationalCarrierName1 = "InternationalTest1";
	String internationalCarrierName2 = "InternationalTest2";
	String internationalCarrierUrl = "1.1.1.1";
	String priority = "100";
	
	//attributes for geolocation establishing at @beforeClass
	String mcc1 = "435";
	String mcc2 = "820";
	String name1 = "unknown1";
	String name2 = "unknown2";
	String countryCode1 = "SFG";
	String countryCode2 = "MNB";
	String junkNumber1 = "7777777";
	String junkNumber2 = "8888888";

	public String output = "";

	@BeforeClass
	public void beforeClass() {
		geoLocationInternationalCarrier = new GeoLocationInternaionalCarriers();
		internationalCarrier = new InternationalCarriers();
		geoLocations = new GeoLocations();
		
		//add 2 geoLocations
		geoLocations.add(mcc1, name1, countryCode1, junkNumber1);
		geoLocations.add(mcc2, name2, countryCode2, junkNumber2);
		
		// add 2 international carriers
		internationalCarrier.add(internationalCarrierName1, internationalCarrierUrl);
		internationalCarrier.add(internationalCarrierName2, internationalCarrierUrl);
	}

	@BeforeMethod
	public void beforeMethod() {
		geoLocationInternationalCarrier.deleteAll();
	}

	@Test
	public void addGeoLocationInternationalCarrierDefaultValues() {
		output = geoLocationInternationalCarrier.add(geoLocation1, internationalCarrierName1);
		assertThat(output, containsString("successfully"));
	}

	@Test
	public void addGeoLocationInternationalCarrierAllValues() {
		output = geoLocationInternationalCarrier.add(geoLocation1, internationalCarrierName1, priority);
		assertThat(output, containsString("successfully"));
	}

	@Test
	public void addDuplicateGeoLocationInternationalCarrier() {
		geoLocationInternationalCarrier.add(geoLocation1, internationalCarrierName1);
		output = geoLocationInternationalCarrier.add(geoLocation1, internationalCarrierName1);
		assertThat(output, containsString("already exist"));
	}

	@Test
	public void modifyGeoLocationInternationalCarrier() {
		geoLocationInternationalCarrier.add(geoLocation1, internationalCarrierName1);
		output = geoLocationInternationalCarrier.modifyByGeolocation(geoLocation1, geoLocation2,
				internationalCarrierName2, priority);
		assertThat(output, containsString("successfully"));
	}

	@Test
	public void deleteGeoLocationInternationalCarrier() {
		geoLocationInternationalCarrier.add(geoLocation1, internationalCarrierName1);
		output = geoLocationInternationalCarrier.deleteByGeolocation(geoLocation1);
		assertThat(output, containsString("successfully"));

	}

	@AfterClass
	public void afterClass() {	
		geoLocationInternationalCarrier.deleteAll();
		geoLocations.deleteByMcc(mcc1);
		geoLocations.deleteByMcc(mcc2);
		internationalCarrier.deleteAll();		
	}

}
