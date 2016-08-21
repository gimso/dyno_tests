package persist_web.config;

import global.PersistUtil;
import global.ScreenshotUtil;
import global.TestUtil;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestNGListeners implements ITestListener  {

	public static final String RESULT_BOOLEAN_FIELD = "isSuccess";
	public static final String OUTPUT_STRING_FIELD = "output";
	public static final String TEST_NAME_STRING_FIELD = "testName";


	@Override
	public void onTestSuccess(ITestResult result) {
		updateTestRail(result);
		
		String testName = getTestName(result);
		System.out.println("Test " + testName + " completed successfully");
	}
	

	@Override
	public void onTestFailure(ITestResult result) {
		ScreenshotUtil.takeScreenShot(result, TEST_NAME_STRING_FIELD);
		updateTestRail(result);
		
		String testName = getTestName(result);
		System.out.println("Test " + testName + " has Failed");
	}

	@Override
	public void onTestSkipped(ITestResult result) {
		updateTestRail(result);
		
		String testName = getTestName(result);
		System.out.println("Test " + testName + " was Skipped");
	}

	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
		updateTestRail(result);	
	
		String message = getTestName(result);
		System.out.println("Test " + message + " was Failed but with in success percentage");
	}

	@Override
	public void onTestStart(ITestResult result) {}

	@Override
	public void onStart(ITestContext context) {}

	@Override
	public void onFinish(ITestContext context) {
		try {
			WebDriver driver = PersistUtil.getInstance().getDriver();
			if (driver != null) {
				driver.close();
				// for some reason, the compiler should wait until the web driver closing.
				Thread.sleep(1000);
			}
		} catch (Exception e) {}
	}
	
	/**
	 * Get the test name, result and the output from the test class fields</br> if
	 * the attribute using excel is null the test name will come from the method
	 * name
	 * 
	 * @param ITestResult iTestResult
	 */
	private void updateTestRail(ITestResult iTestResult) {
		// check if using excel
		boolean usingExcel = isTestingWithExcel(iTestResult);
		// get the instance of the current running class
		Object testObject = iTestResult.getInstance();
		
		try {			
			// get the output result of the test from class field
			String output = (String) testObject.getClass().getDeclaredField(OUTPUT_STRING_FIELD).get(testObject);
			String testName = null;
			if (usingExcel) {
				
				// get the test name from the testName attribute (initialized
				// from excel)
				testName = (String) testObject.getClass().getDeclaredField(TEST_NAME_STRING_FIELD).get(testObject);

				// update with the test name
				TestUtil.updateTestRail(testName, iTestResult, output);

			} else {
				// update without the test name - using the method name as the
				// test name
				TestUtil.updateTestRail(null, iTestResult, output);
			}

		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			System.err.println("from class "+iTestResult.getMethod().getTestClass().getName());
			e.printStackTrace();
		}
	}

	/**
	 * Check if testing with excel
	 * @param iTestResult
	 * @return boolean
	 */
	public boolean isTestingWithExcel(ITestResult iTestResult) {
		//class name
		String classFullName = iTestResult.getTestClass().getName();
		// check the context attributes "usingExcel" and "classFullName"
		@SuppressWarnings("unchecked")
		List<String> usingExcelList = (List<String>) iTestResult.getTestContext().getAttribute("usingExcelList");
		//get excel attribute
		String excelFlag = String.valueOf(iTestResult.getTestContext().getAttribute("usingExcel"));
				
		// if using excel set with the correct class name,
		// the test name will will be taken from the excel,
		// else the test name will be taken from the method name
		if (usingExcelList != null) {
			for (String className : usingExcelList) {
				if (classFullName.equalsIgnoreCase(className)) {
					return Boolean.valueOf(excelFlag);
				}
			} 
		}
		return false;
	}
	
	/**
	 * get the test name
	 * @param result
	 * @return String
	 */
	private String getTestName(ITestResult result) {
		Object testObject = result.getInstance();
		if (!isTestingWithExcel(result))
			return TestUtil.getTestMethodName();
		
		try {
			return (String) testObject.getClass().getDeclaredField(TEST_NAME_STRING_FIELD).get(testObject);
		} catch (Exception e) {}
		
		return "";
	}
}