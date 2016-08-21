package persist_api.statusreport;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

import java.util.Date;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jayway.restassured.RestAssured;

import general_config.DataProviderInit;
import global.PropertiesUtil;
import jdbc.TelemetryQueries;
import persist.trip.Trips;
import persist.usage.Allocations;
import persist.usage.Users;
import persist_api.config.TestParams;

/**
 * @author Dana
 */

/*
 * In this class a Json is sent with either data or calls consumption, or both.
 * timestamp must be changed so that the delta will be counted.
 * 
 * The reply from the cloud updates the usage consumption on the App.
 * 
 * Expected a reply from the cloud that contains the max of the following: Data
 * usage: App usage report, or Telemetry DB usage registry Since data usage sent
 * in the report squashes the registry in Telemetry DB, expected are 'current'
 * and 'trip' with the same value.
 * 
 * Calls usage: max of Telemetry DB usage registry, or OpenSIPS usage registry.
 * Since no calls usage is registered in this test from OpenSips, expected is
 * value 0.
 */
public class ValidValuesTest {
	private static final String APPLICATION_JSON = "application/json";
	private static final String URL = PropertiesUtil.getInstance().getProperty("STATUS_REPORT_URL");	
	private TelemetryQueries mTelemetryQueries;
	private Users users;
	private Allocations allocations;
	private Trips trips;
	private String name;
	private String cookie;
	private String sessionId;
	private int tripId;

	/*
	 * initialize variables, get the correct cookie from user
	 */
	@BeforeClass
	public void beforeClass() {
		users = new Users();
		allocations = new Allocations();
		trips = new Trips();
		mTelemetryQueries = new TelemetryQueries();
		name = PropertiesUtil.getInstance().getProperty("USAGE_USER");
	}

	/*
	 * This method ensures that all user's records are cleared before data/calls are sent to Telemetry.
	 * it deletes allocation, trip and user so that user's usage record is clear before each test.
	 * creates a user and new trip. 
	 * after allocation is inserted into trip, the billing marker date is updated to be before the
	 * timestamp that is sent in the json.
	 */
	@BeforeMethod
	public void beforeMethod() {
		//delete allocation, trip if exists
		allocations.deleteByPlugId(TestParams.PLUG_ID);
		this.tripId = mTelemetryQueries.getTripIdByUserName(name);
		if (tripId != -1) {
			trips.deleteByUser(name);
			tripId = mTelemetryQueries.getTripIdByUserName(name);
		} 		
		//delete the user and create it again, insert cookie
		users.deleteByHomeNumber("972551234567");
		users.add(name, "Undefined", "972551234567", new Date(), "Simgo");
		int userId = mTelemetryQueries.getUserId(name);
		mTelemetryQueries.injectCookieIntoUser(userId);		
		// get the correct cookie from DB
		this.cookie = mTelemetryQueries.getCookieFromUser(name);
		this.sessionId = "sessionid=" + this.cookie;		
		//create a new trip for user
		trips.add(name, "000010002024", "032222222");
		mTelemetryQueries.injectAllocationIntoTrip();
		tripId = mTelemetryQueries.getTripIdByUserName(name);
		mTelemetryQueries.updateBillingMarkerDate(tripId);		
	}

	/*
	 * A Json is sent with both data and calls consumption. Expected: calls:
	 * 0.0, data: current-110, total-110
	 */
	@Test(dataProvider = "jsonDp", dataProviderClass = DataProviderInit.class)
	public void sendDataAndCallsUsage(JsonObject fullJson) {
		RestAssured.baseURI = URL;
		// get current timestamp
		long date = System.currentTimeMillis() / 1000L;
		// get the inner object "timestamp" and change it to current time.
		JsonArray statusArray = fullJson.getAsJsonObject().getAsJsonArray("status");
		JsonObject innerStatus = statusArray.get(0).getAsJsonObject();
		innerStatus.addProperty("timestamp", date);
		// change trip id to the correct one
		innerStatus.addProperty("trip_id", tripId);
		// get the inner object "usage", and its inner objects: data & calls.
		JsonObject usageArray = (JsonObject) innerStatus.get("usage");
		JsonObject calls = (JsonObject) usageArray.get("calls");
		JsonObject dataUsage = (JsonObject) usageArray.get("data");
		// change the primitives of data and calls
		calls.addProperty("current", 10);
		calls.addProperty("total", 110);
		calls.addProperty("trip", 110);
		dataUsage.addProperty("current", 10);
		dataUsage.addProperty("total", 110);
		dataUsage.addProperty("trip", 110);

		given().headers(TestParams.CONTENT_TYPE, APPLICATION_JSON, TestParams.COOKIE, this.sessionId).body(fullJson.toString()).when().put("")
				.then().assertThat().statusCode(200).and()
				.body(containsString("\"data\": {\"current\": 110.0, \"total\": 110.0, \"trip\": 110.0")).and()
				.body(containsString("\"calls\": {\"current\": 0.0, \"total\": 0.0, \"trip\": 0.0}"));
		System.out.println("data and calls usage updated successfully");
	}

	/*
	 * A Json is sent with data usage only (actual usage is taken from the App).
	 * Expected: current & trip values- 120
	 */
	@Test(dataProvider = "jsonDp", dataProviderClass = DataProviderInit.class)
	public void sendDataOnly(JsonObject fullJson) {
		RestAssured.baseURI = URL;
		// get current timestamp
		long date = System.currentTimeMillis() / 1000L;
		// get the inner object "timestamp" and change it to current time.
		JsonArray statusArray = fullJson.getAsJsonObject().getAsJsonArray("status");
		JsonObject innerStatus = statusArray.get(0).getAsJsonObject();
		innerStatus.addProperty("timestamp", date);
		// change trip id to the correct one
		innerStatus.addProperty("trip_id", this.tripId);
		// get the inner object "usage", and its inner object: data
		JsonObject usageArray = (JsonObject) innerStatus.get("usage");
		JsonObject dataUsage = (JsonObject) usageArray.get("data");
		// change the primitives of data
		dataUsage.addProperty("current", 10);
		dataUsage.addProperty("total", 120);
		dataUsage.addProperty("trip", 120);

		given().headers(TestParams.CONTENT_TYPE, APPLICATION_JSON, TestParams.COOKIE, this.sessionId).body(fullJson.toString()).when().put("")
				.then().assertThat().statusCode(200).and()
				.body(containsString("\"data\": {\"current\": 120.0, \"total\": 120.0, \"trip\": 120.0"));
		System.out.println("data usage updated successfully");
	}

	/*
	 * A Json is sent with calls usage only (actual usage is taken from
	 * openSips) Expected calls usage: 0
	 */
	@Test(dataProvider = "jsonDp", dataProviderClass = DataProviderInit.class)
	public void sendCallsOnly(JsonObject fullJson) {
		RestAssured.baseURI = URL;
		// get current timestamp
		long date = System.currentTimeMillis() / 1000L;
		// get the inner object "timestamp" and change it to current time.
		JsonArray statusArray = fullJson.getAsJsonObject().getAsJsonArray("status");
		JsonObject innerStatus = statusArray.get(0).getAsJsonObject();
		innerStatus.addProperty("timestamp", date);
		// change trip id to the correct one
		innerStatus.addProperty("trip_id", this.tripId);
		// get the inner object "usage", and its inner object calls.
		JsonObject usageArray = (JsonObject) innerStatus.get("usage");
		JsonObject calls = (JsonObject) usageArray.get("calls");
		// change the primitives of calls
		calls.addProperty("current", 10);
		calls.addProperty("total", 120);
		calls.addProperty("trip", 120);

		given().headers(TestParams.CONTENT_TYPE, APPLICATION_JSON, TestParams.COOKIE, this.sessionId).body(fullJson.toString()).when().put("")
				.then().assertThat().statusCode(200).and()
				.body(containsString("\"calls\": {\"current\": 0.0, \"total\": 0.0, \"trip\": 0.0}"));
		System.out.println("calls usage updated successfully");
	}
}
