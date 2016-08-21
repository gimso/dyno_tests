package persist_api.config;


import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import global.TestUtil;

public class TestNGListeners implements ITestListener {

	@Override
	public void onTestSuccess(ITestResult result) {			
			updateTestRail(result);
	}
	
	@Override
	public void onTestFailure(ITestResult result) {
		updateTestRail(result);
	}

	@Override
	public void onTestSkipped(ITestResult result) {
		updateTestRail(result);
	}

	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
		updateTestRail(result);	
	}

	
	/**
	 * Get the test name, result and the output from the test class fields</br> if
	 * the attribute using excel is null the test name will come from the method
	 * name
	 * 
	 * @param ITestResult iTestResult
	 */
	
	private void updateTestRail(ITestResult iTestResult) {
		try {			
			//update without the test name - using the method name as the test name
			TestUtil.updateTestRail(null, iTestResult, "");
		} catch (IllegalArgumentException | SecurityException e) {
			e.printStackTrace();
		}
	}
			
		
	@Override
	public void onFinish(ITestContext arg0) {
		
	}

	@Override
	public void onStart(ITestContext arg0) {
		
	}

	@Override
	public void onTestStart(ITestResult result) {
		// TODO Auto-generated method stub
		
	}


	
}
