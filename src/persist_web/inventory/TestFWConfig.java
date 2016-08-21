package persist_web.inventory;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import global.ExcelType;
import global.ExcelUtil;
import global.PersistException;
import global.TestUtil;
import persist.inventory.FwConfigs;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;

import general_config.DataProviderInit;

public class TestFWConfig {
	
	private static final Integer CONFIG_VALUE_COLUMN = 3;
	private static final Integer CONFIG_OPTIONAL_VALUE_COLUMN = 4;
	
	private FwConfigs fwConfigs;
	
	public String output;	
	public Boolean isSuccess;
	public String testName ;
	
	private ExcelType excelType ;
	private String expectedResult;
	private String value;
	private String optionalValue;
	
	
	/**
	 * receive from the data provider the object[] and initialized it to the class attributes
	 * 
	 * @param objects
	 */
	@Factory(dataProvider = "ExcelData", dataProviderClass = DataProviderInit.class)
	public  TestFWConfig (Object[] objects) {
		this.output = "";
		this.isSuccess = false;
		//Type	Test Name	Expected Result
		this.excelType = 		TestUtil.getExcelType(objects[ExcelUtil.TYPE_COLUMN]);
		//Initialized the values from the excel sheet as a String
		this.testName = 		TestUtil.getString(objects[ExcelUtil.TEST_NAME_COLUMN]);
		this.expectedResult = 	TestUtil.getString(objects[ExcelUtil.EXPECTED_RESULT_COLUMN]);
		//Initialized the fw-config values
		this.value = 			TestUtil.getString(objects[CONFIG_VALUE_COLUMN]);
		this.optionalValue = 	TestUtil.getString(objects[CONFIG_OPTIONAL_VALUE_COLUMN]);
		
		this.fwConfigs = new FwConfigs();
	}
	
	/**
	 * Before the test:  if its not testing 'add' (e.g when modify element), it will add an element for the test in this method
	 */
	@BeforeMethod
	public void configBeforeMethod(){	
		fwConfigs.deleteAll();
		if (excelType != ExcelType.ADD){
			fwConfigs.add(value);
		}
	}
	
	/***
	 *check what type it is and run the test accordingly  
	 * @throws PersistException
	 */
	@Test
	public void test() throws PersistException {
		switch (excelType) {
		case ADD:
			if (optionalValue != null)
				fwConfigs.add(optionalValue);
			output = fwConfigs.add(value);
			break;
		case MODIFY:
			output = fwConfigs.modifyByConfig(value, optionalValue);
			break;
		case DELETE:
			output = fwConfigs.deleteByConfig(value);
			break;
		}
		assertThat(output, containsString(expectedResult));

	}
	
}
