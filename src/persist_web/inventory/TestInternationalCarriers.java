package persist_web.inventory;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import global.PersistException;
import persist.inventory.InternationalCarriers;

public class TestInternationalCarriers {
	
	InternationalCarriers internationalCarrier;
	
	public String output = "";

	boolean allowed = true;
	String internationalCarrierName = "test_qa";
	String internationalCarrierName1 = "test_qa1";
	String url = "1.1.1.1";
	String url1 = "2.2.2.2";
	String prefix = "aaa";
	String carrierSpecfic = "aaa";

	@BeforeClass
	private void beforeClassInit() {
		this.internationalCarrier = new InternationalCarriers();
	}

	@BeforeMethod
	public void configBeforeMethod() {
		this.internationalCarrier.deleteAll();
	}

	@Test
	// add
	public void addInternationalCarrier() throws PersistException {
		output = internationalCarrier.add(internationalCarrierName, url);
		assertThat(output, containsString("successfully"));

	}

	@Test
	// modify
	public void modifyInternationalCarrier() throws PersistException {
		internationalCarrier.add(internationalCarrierName, url);
		output = internationalCarrier.modifyByName(internationalCarrierName, internationalCarrierName1, url1, allowed,
				prefix, carrierSpecfic);
		assertThat(output, containsString("successfully"));

	}

	@Test
	// delete
	public void deleteInternationalCarrier() throws PersistException {
		internationalCarrier.add(internationalCarrierName, url);
		output = internationalCarrier.deleteByName(internationalCarrierName);
		
		assertThat(output, containsString("was deleted successfully"));

	}

	@Test
	// create international carrier with the same name as an existing one
	public void createDupInternationalCarrierSameName() throws PersistException {
		internationalCarrier.add(internationalCarrierName, url);
		output = internationalCarrier.add(internationalCarrierName, url1);

		assertThat(output, containsString("already exists"));

	}

	@Test
	// create international carrier with the same url as an existing one
	public void createDupInternationalCarrierSameUrl() throws PersistException {
		internationalCarrier.add(internationalCarrierName, url);
		output = internationalCarrier.add(internationalCarrierName1, url);

		assertThat(output, containsString("already exists"));
	}

	@Test
	// modify international carrier name parameter to an existing one
	public void modifyInternationalCarrierSameName() throws PersistException {		
		internationalCarrier.add(internationalCarrierName, url);
		internationalCarrier.add(internationalCarrierName1, url1);		
		output = internationalCarrier.modifyByName(internationalCarrierName, internationalCarrierName1, url, allowed, prefix, carrierSpecfic);	

		assertThat(output, containsString("already exists"));
	}

	@Test
	// modify international carrier url parameter to an existing one
	public void modifyInternationalCarrierSameUrl() throws PersistException {
		internationalCarrier.add(internationalCarrierName, url);
		internationalCarrier.add(internationalCarrierName1, url1);
		output = internationalCarrier.modifyByName(internationalCarrierName, internationalCarrierName, url1, allowed, prefix, carrierSpecfic);

		assertThat(output, containsString("already exists"));
	}

}
