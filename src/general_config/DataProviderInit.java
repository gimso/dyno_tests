package general_config;

import global.ExcelUtil;
import global.FileUtil;
import global.JSONReader;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.annotations.DataProvider;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import beans.FromTo;
import beans.PhoneType;

/**
 * this class provide a easy way to add a list as data_provider instead of string array of arrays
 * @author Yehuda Ginsburg
 *
 */
public class DataProviderInit {

	private static List<String> usingExcelList = new ArrayList<String>();

	/**
	 * data_provider for the dialed digit list getting from test_data.json
	 * 
	 * @return
	 */
	public static List<FromTo> fromto() {
		List<PhoneType> list = JSONReader.phoneTypes();
		List<FromTo> fromTos = new ArrayList<>();

		for (PhoneType p : list) {
			for (String s : p.getTestData().getDialedDigits()) {
				String from = p.getTestData().getHomeNumber();
				String to = s;
				FromTo fromTo = new FromTo(from, to);
				fromTos.add(fromTo);
			}
		}
		return fromTos;
	}


	/**
	 * get phone types, test data keys and dialed digits list convert them into
	 * PhoneType beans,
	 * 
	 * then injecting them into List of Object array, thats the way the testng
	 * API receive the data_provider
	 * 
	 * @return
	 */
	@DataProvider(name = "test_data")
	public static Iterator<Object[]> initalizedDataProviderFromTestDataFile() {
		List<PhoneType> phoneTypes = JSONReader.phoneTypes();
		List<Object[]> dataToBeReturned = new ArrayList<Object[]>();
		for (PhoneType phone : phoneTypes) {
			dataToBeReturned.add(new Object[] { phone });
		}
		return dataToBeReturned.iterator();
	}
	@DataProvider(name="ExcelData")
	public static Iterator<Object[]> dp (ITestNGMethod iTestNGMethod, ITestContext context){
		
		// save as attribute value if using excel 
		context.setAttribute("usingExcel", true);
		
		// get the fully qualified name (e.g. persist_web.inventory.ClassName)
		String classFullName = iTestNGMethod.getTestClass().getName();
		
		//add it to list of file names and add the list as context attribute
		usingExcelList.add(classFullName);
		context.setAttribute("usingExcelList", usingExcelList);
		
		// get the class name (with no packages)
		String objectName = classFullName.split("\\.")[2];

		// if the class name is more then 28 chars it won't save as sheet name
		// in excel +the two first chars of the package and the 'dot' after
		if (objectName.length() > 28)
			throw new RuntimeException("class name length is more then 31 char");

		// The sheet name is equals
		// the first two letters of inner package name
		// and after that the class name (e.g. in.ClassName for
		// persist_web.inventory.ClassName)
		String sheetName = classFullName.split("\\.")[1].substring(0, 2) + "." + classFullName.split("\\.")[2];
		List<Object[]> objectsList = ExcelUtil.getAllSheetCells(sheetName);

		// Remove the headers from lists
		objectsList.remove(0);
		
		//Initialized the data provider with List<Object> that represent row
		List<Object[]> rv = new ArrayList<Object[]>();
		for (Object[] objectArray : objectsList){
			rv.add(new Object[] { objectArray });
		}
		return rv.iterator();
		
	}
	
	@DataProvider
	public static Object[][] jsonDp(ITestNGMethod iTestNGMethod) {
		//get the test name
		String testName = iTestNGMethod.getMethodName();
		String simpleName = iTestNGMethod.getTestClass().getName().split("\\.")[2];
		//find the matching .json file and take its content
		File jsonFile = new File(simpleName + "/" + testName + ".json");
		String data = FileUtil.readFromFile(new File("files/" + jsonFile));
		//parse it into a fullJson that will be injected into the different Tests.
		JsonParser jsonParser = new JsonParser();
		JsonObject fullJson = (JsonObject) jsonParser.parse(data);
		return (new Object[][] { { fullJson } });
	}
}
