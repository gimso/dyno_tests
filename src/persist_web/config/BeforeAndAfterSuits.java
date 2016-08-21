package persist_web.config;

import org.openqa.selenium.WebDriver;
import org.testng.SkipException;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import global.PersistUtil;
import global.PropertiesUtil;
import global.TestUtil;
import jdbc.JDBCUtil;
import testrail.api.TestRailAPI;

/**
 * Initializing before suite and closing after suite
 * 
 * @author Yehuda Ginsburg
 *
 */
public class BeforeAndAfterSuits {

	private static final String CLOUD = "CLOUD";
	private static final String RUN_NAME = PropertiesUtil.getInstance().getProperty("TEST_RAIL_PERSIST_PLAN");
	private static final String BASE_RUN_URL = "https://gimso.testrail.com/index.php?/runs/view/";
	private static final String BASE_PLAN_URL = "https://gimso.testrail.com/index.php?/plans/view/";
	/**
	 * Validate that there is an Internet connection</br> Initializing the
	 * PersistUtil</br> Initializing the TestRailAPI</br>
	 */
	@BeforeSuite
	public void beforeSuite() {
		
		boolean isInternetConnected = TestUtil.isInternetReachable();
		if (!isInternetConnected) {
			String errorMessage = "Skipping tests because Internet Connection is not available.";
			System.err.println(errorMessage);
			System.exit(0);
			throw new SkipException(errorMessage);
		}
		
		boolean isThereReachabilityForDBConnection = new JDBCUtil().getConnection() != null;
		if (!isThereReachabilityForDBConnection) {
			String errorMessage = "Skipping tests because DB connection is not available.";
			System.err.println(errorMessage);
			System.exit(0);
			throw new SkipException(errorMessage);
		}

		PersistUtil.getInstance();
		
		boolean isDriverNotNull = PersistUtil.getInstance().getDriver() != null;
		if (!isDriverNotNull) {
			String errorMessage = "Skipping tests because couldn't create a WebDriver.";
			System.err.println(errorMessage);
			System.exit(0);
			throw new SkipException(errorMessage);
		}

		TestRailAPI testRailAPI = TestUtil.getTestRailAPIInstance(RUN_NAME, CLOUD);
		
		if (testRailAPI.isPlan) {
			System.out.println("The TestRail plan URL:");
			System.out.println(BASE_PLAN_URL + testRailAPI.getPlan().get("id"));
		} else {
			System.out.println("The TestRail run URL:");
			System.out.println(BASE_RUN_URL + testRailAPI.getRun().get("id"));
		}
	}

	/**
	 * Closing the web driver if it still open
	 * 
	 */
	@AfterSuite
	public void afterSuite() {
		try {
			WebDriver driver = PersistUtil.getInstance().getDriver();
			if (driver != null) {
				driver.close();
				// for some reason when the driver close immediately without
				// waiting
				Thread.sleep(1000);
			}
		} catch (Exception e) {}
	}

}
