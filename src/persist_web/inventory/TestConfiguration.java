package persist_web.inventory;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import persist.inventory.Configurations;
import persist.inventory.InternationalCarriers;

public class TestConfiguration {

	Configurations configuration;
	InternationalCarriers internationalCarrier;

	public String output = "";

	String internationalCarrierName1 = "intlname1";
	String internationalCarrierName2 = "intlname2";
	String internationalCarrierName3 = "intlname3";
	String internationalCarrierNameUrl = "aaaa";
	String id = "1";
	String supportPhoneNumber = "97231112222";
	String supportEmail = "support@test.com";
	String supportSkype = "supportSkype";
	String supportWhatsapp = "972501112222";
	String apnsMode = "Production";
	String lowChargingThreshold = null;
	String highChargingThreshold = null;
	String nightTimeStart = null;
	String nightTimeEnd = null;

	@BeforeClass
	// adding 3 international carriers
	public void addInternationalCarriers() {
		configuration = new Configurations();
		internationalCarrier = new InternationalCarriers();
		internationalCarrier.add(internationalCarrierName1, internationalCarrierNameUrl);
		internationalCarrier.add(internationalCarrierName2, internationalCarrierNameUrl);
		internationalCarrier.add(internationalCarrierName3, internationalCarrierNameUrl);
	}

	@Test	  
	public void modifyConfigurationParameters() {
		output = configuration.modifyById(id, internationalCarrierName1, internationalCarrierName2,
				internationalCarrierName3, supportPhoneNumber, supportEmail, supportSkype, supportWhatsapp, apnsMode,
				lowChargingThreshold, highChargingThreshold, nightTimeStart, nightTimeEnd);
		
		assertThat(output, containsString("changed successfully"));
	}

	@Test
	public void replaceConfigurationParameters() {
		
		configuration.modifyById(id, internationalCarrierName1, internationalCarrierName2, internationalCarrierName3,
				supportPhoneNumber, supportEmail, supportSkype, supportWhatsapp, apnsMode, lowChargingThreshold,
				highChargingThreshold, nightTimeStart, nightTimeEnd);
		
		output = configuration.modifyById(id, internationalCarrierName2, internationalCarrierName1,
				internationalCarrierName3, supportPhoneNumber, supportEmail, supportSkype, supportWhatsapp, apnsMode,
				lowChargingThreshold, highChargingThreshold, nightTimeStart, nightTimeEnd);
		
		assertThat(output, containsString("changed successfully"));
	}

	@AfterClass
	// deleting 3 international carriers
	public void deleteInternationalCarriers() {
		internationalCarrier.deleteAll();
	}
}