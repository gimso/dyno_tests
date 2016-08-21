package persist_web.inventory;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import global.ExcelType;
import global.ExcelUtil;
import global.PersistException;
import global.TestUtil;
import persist.inventory.Apns;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;

import general_config.DataProviderInit;

public class TestAPN {
	
	private Apns apns;

	public String output;
	public String testName;
	
	private ExcelType excelType;
	private String expectedResult;
	
	private String apnName;
	private String apn;
	private String userName;
	private String password;
	private String authenticationType;
	private String apnSmsUpdate;
	private String newApnName;
	private String newApn;
	private String newUserName;
	private String newPassword;
	private String newAuthenticationType;
	private String newApnSmsUpdate;
	
	private static final int APN_NAME = 3;
	private static final int APN = 4;
	private static final int USER_NAME = 5;
	private static final int PASSWORD = 6;
	private static final int AUTHENTICATION_TYPE = 7;
	private static final int APN_SMS_UPDATE = 8;

	private static final int NEW_APN_NAME = 9;
	private static final int NEW_APN = 10;
	private static final int NEW_USER_NAME = 11;
	private static final int NEW_PASSWORD = 12;
	private static final int NEW_AUTHENTICATION_TYPE = 13;
	private static final int NEW_APN_SMS_UPDATE = 14;
	
	/**
	 * receive from the data provider the object[] and initialized it to the
	 * class attributes
	 * 
	 * @param objects
	 */
	@Factory(dataProvider = "ExcelData", dataProviderClass = DataProviderInit.class)
	public TestAPN(Object[] objects) {
		
		this.output = "";
		// Type
		this.excelType = 			TestUtil.getExcelType(objects[ExcelUtil.TYPE_COLUMN]);		
		// Test Name
		this.testName = 			TestUtil.getString(objects[ExcelUtil.TEST_NAME_COLUMN]);
		// Expected Result
		this.expectedResult = 		TestUtil.getString(objects[ExcelUtil.EXPECTED_RESULT_COLUMN]);
		
		// Initialized the values from the excel sheet as a String
		// default
		this.apnName = 				TestUtil.getString(objects[APN_NAME]);
		this.apn = 					TestUtil.getString(objects[APN]);	
		this.userName = 			TestUtil.getString(objects[USER_NAME]);
		this.password =  			TestUtil.getString(objects[PASSWORD]);
		this.authenticationType = 	TestUtil.getString(objects[AUTHENTICATION_TYPE]);
		this.apnSmsUpdate = 		TestUtil.getString(objects[APN_SMS_UPDATE]);
		
		this.newApnName= 			TestUtil.getString(objects[NEW_APN_NAME]);
		this.newApn = 				TestUtil.getString(objects[NEW_APN]);
		this.newUserName= 			TestUtil.getString(objects[NEW_USER_NAME]);
		this.newPassword =			TestUtil.getString(objects[NEW_PASSWORD]);
		this.newAuthenticationType =TestUtil.getString(objects[NEW_AUTHENTICATION_TYPE]);
		this.newApnSmsUpdate = 		TestUtil.getString(objects[NEW_APN_SMS_UPDATE]);
		
		this.apns = new Apns();
	}
	
	
	/**
	 * Before the test: if its not testing 'add' (e.g when modify element), it
	 * will add an element for the test in this method
	 */
	@BeforeMethod
	public void configBeforeMethod() {
		apns.deleteAll();

		if (excelType != ExcelType.ADD) {
			apns.add(apnName, apn);
		}
	}

	/***
	 * check what type it is and run the test accordingly
	 * 
	 * @throws PersistException
	 */
	@Test
	public void test() throws PersistException {
		switch (excelType) {
		case ADD:
			if (newApn != null && newApnName != null) {
				apns.add(newApnName, newApn, newUserName, newPassword, newAuthenticationType, newApnSmsUpdate);
			}
			output = apns.add(apnName, apn, userName, password, authenticationType, apnSmsUpdate);
					
			break;
		case MODIFY:
			output = apns.modifyByName(apnName, newApnName, apn, userName, password, authenticationType, apnSmsUpdate);
			break;
		case DELETE:
			output = apns.deleteByName(apnName);
			break;
		}
		
		assertThat(output, containsString(expectedResult));
		System.out.println(testName + " succeeded");
	}
	


}
