package persist_web.inventory;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;

import general_config.DataProviderInit;
import global.ExcelType;
import global.ExcelUtil;
import global.PersistException;
import global.TestUtil;
import persist.inventory.AccessNumberGroups;
import persist.inventory.AccessNumbers;
import persist.inventory.GeoLocations;
/**
 * 
 * @author Dana & Yehuda
 *
 */
public class TestAccessNumbers {
	
	private String accessNumberGroupName;		

	private AccessNumbers accessNumbers;
	private AccessNumberGroups accessNumberGroups;
	private GeoLocations geoLocations;

	private static final int NEW_PHONE_NUMBER_TYPE = 18;
	private static final int NEW_PHONE_NUMBER_FORMAT = 17;
	private static final int NEW_TYPE = 16;
	private static final int NEW_GROUP = 15;
	private static final int NEW_ALLOWED = 14;
	private static final int NEW_NUMBER_OF_CHANNELS = 13;
	private static final int NEW_GEOLOCATION = 12;
	private static final int NEW_NUMBER = 11;
	private static final int PHONE_NUMBER_TYPE = 10;
	private static final int PHONE_NUMBER_FORMAT = 9;
	private static final int TYPE = 8;
	private static final int GROUP = 7;
	private static final int ALLOWED = 6;
	private static final int NUMBER_OF_CHANNELS = 5;
	private static final int GEO_LOCATION = 4;
	private static final int NUMBER = 3;

	
	public String output;
	public String testName;
	
	private ExcelType excelType;
	private String expectedResult;
	
	private String number;
	private String geolocation;
	private String numberOfChannels;
	private Boolean allowed;
	private String group;
	private String type;
	private String phoneNumberFormat;
	private String phoneNumberType;

	private String newNumber;
	private String newGeolocation;
	private String newNumberOfChannels;
	private Boolean newAllowed;
	private String newGroup;
	private String newType;
	private String newPhoneNumberFormat;
	private String newPhoneNumberType;

	

	/**
	 * receive from the data provider the object[] and initialized it to the
	 * class attributes
	 * 
	 * @param objects
	 */
	@Factory(dataProvider = "ExcelData", dataProviderClass = DataProviderInit.class)
	public TestAccessNumbers(Object[] objects) {
		this.output = "";
		// Type
		this.excelType = 				TestUtil.getExcelType(objects[ExcelUtil.TYPE_COLUMN]);		
		// Test Name
		this.testName = 				TestUtil.getString(objects[ExcelUtil.TEST_NAME_COLUMN]);
		// Expected Result
		this.expectedResult = 			TestUtil.getString(objects[ExcelUtil.EXPECTED_RESULT_COLUMN]);

		// Initialized the values from the excel sheet as a String
		// default
		this.number = 					TestUtil.getString(objects[NUMBER]);
		this.geolocation = 				TestUtil.getString(objects[GEO_LOCATION]);
		
		this.numberOfChannels = 		TestUtil.getIntegerAsString(objects[NUMBER_OF_CHANNELS]);
		this.allowed =  				TestUtil.getBoolean(objects[ALLOWED]);
		this.group = 					TestUtil.getString(objects[GROUP]);
		this.type = 					TestUtil.getString(objects[TYPE]);
		this.phoneNumberFormat = 		TestUtil.getString(objects[PHONE_NUMBER_FORMAT]);
		this.phoneNumberType = 			TestUtil.getString(objects[PHONE_NUMBER_TYPE]);
		
		this.newNumber = 				TestUtil.getString(objects[NEW_NUMBER]);
		this.newGeolocation = 			TestUtil.getString(objects[NEW_GEOLOCATION]);
		this.newNumberOfChannels = 		TestUtil.getIntegerAsString(objects[NEW_NUMBER_OF_CHANNELS]);
		this.newAllowed =				TestUtil.getBoolean(objects[NEW_ALLOWED]);
		this.newGroup =					TestUtil.getString(objects[NEW_GROUP]);
		this.newType = 					TestUtil.getString(objects[NEW_TYPE]);
		this.newPhoneNumberFormat = 	TestUtil.getString(objects[NEW_PHONE_NUMBER_FORMAT]);
		this.newPhoneNumberType = 		TestUtil.getString(objects[NEW_PHONE_NUMBER_TYPE]);
		
		this.accessNumbers = new AccessNumbers();
	}
	
	@BeforeGroups(groups = "TestAccessNumbers")
	public void beforeGroup() {

		accessNumberGroups = new AccessNumberGroups();
		geoLocations = new GeoLocations();

		accessNumberGroupName = "SimgoQaTesting";
		accessNumberGroups.add(accessNumberGroupName);

		geoLocations.add("425", "Israel", "IL", "97235550002");
		geoLocations.add("234", "UK", "GB", "4412245550002");
		geoLocations.add("228", "Switzerland", "CH", "2282245550002");
	}

	/**
	 * Before the test: if its not testing 'add' (e.g when modify element), it
	 * will add an element for the test in this method
	 */
	@BeforeMethod(groups = "TestAccessNumbers")
	public void configBeforeMethod() {

		accessNumbers.deleteAll();

		if (excelType != ExcelType.ADD) {
			accessNumbers.add(number, geolocation, group);
		}
	}

	/***
	 * check what type it is and run the test accordingly
	 * 
	 * @throws PersistException
	 */
	@Test(groups="TestAccessNumbers")
	public void test() throws PersistException {
		
		switch (excelType) {
		case ADD:
			if (newNumber != null && newGeolocation != null && newGroup != null) {
				accessNumbers.add(newNumber, newGeolocation, newGroup);
			}
			output = accessNumbers.add(number, geolocation, numberOfChannels,
					allowed, group, type, phoneNumberFormat, phoneNumberType);
			break;
		case MODIFY:
			output = accessNumbers.modifyByNumber(number, newNumber, newGeolocation, newNumberOfChannels, newAllowed, newGroup, newType, newPhoneNumberFormat, newPhoneNumberType);
			break;
		case DELETE:
			output = accessNumbers.deleteByNumber(number);
			break;
		}
		
		assertThat(output, containsString(expectedResult));

		System.out.println(testName + " succeeded");
	}
	
	@AfterGroups("TestAccessNumbers")
	public void afterClass(){
		//delete access number groups
		accessNumberGroups.deleteAll();
	}

}
