package persist_api.oldstatusreport;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import java.io.File;
import java.util.Date;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jayway.restassured.http.ContentType;
import global.FileUtil;
import global.PropertiesUtil;
import jdbc.TelemetryQueries;
import persist.trip.Trips;
import persist.usage.Allocations;
import persist.usage.Users;
import persist_api.config.TestParams;

/**
 * @author Dana This class tests the old version of the Status Code sent to the
 *         cloud. It checks the expected error codes.
 */
public class OldStatusReportTest {
	private static final String URL = PropertiesUtil.getInstance().getProperty("OLD_STATUS_REPORT_API_URL");
	private TelemetryQueries mTelemetryQueries;
	private Allocations allocations;
	private Users users;
	private Trips trips;
	private String cookie;
	private String sessionId;
	private String username;
	private int tripId;
	private JsonObject fullJson;
	private int userId;

	/*
	 * Read from DB and Json file the required parameters to send a request
	 */
	@BeforeClass
	public void beforeClass() {
		mTelemetryQueries = new TelemetryQueries();	
		allocations = new Allocations();
		users = new Users();
		trips = new Trips();		
		this.username = PropertiesUtil.getInstance().getProperty("USAGE_USER");
		this.tripId = mTelemetryQueries.getTripIdByUserName(username);
		if (tripId == -1) {
			trips.add(username, TestParams.PLUG_ID, "032222222");
			tripId = mTelemetryQueries.getTripIdByUserName(username);
		}		
		// inject allocation into trip, if doesn't exist create new
		mTelemetryQueries.injectAllocationIntoTrip();		
		this.cookie = mTelemetryQueries.getCookieFromUser(username);
		this.sessionId = "sessionid=" + this.cookie;	
		// get the json file to read from.
		String data = FileUtil.readFromFile(new File("files/OldStatusReportTest/oldStatusReportErrorCodes.json"));
		// parse it into a fullJson that will be injected into the Tests.
		JsonParser jsonParser = new JsonParser();
		this.fullJson = (JsonObject) jsonParser.parse(data);
	}
	
	/*
	 * This method ensures that all user's records are cleared before data/calls are sent to Telemetry.
	 * it deletes allocation, trip and user so that user's usage record is clear before each test.
	 * Then it creates new user and trip. 
	 * after allocation is inserted into trip, the billing marker date is updated to be before the
	 * timestamp that is sent in the json.
	 */
	@BeforeGroups(groups = "dataValidation")
	public void beforeGroup() {
		//delete allocation, trip if exists
		allocations.deleteByPlugId(TestParams.PLUG_ID);
		this.tripId = mTelemetryQueries.getTripIdByUserName(username);
		if (tripId != -1) {
			trips.deleteByUser(username);
			tripId = mTelemetryQueries.getTripIdByUserName(username);
		} 		
		//delete the user and create it again, insert cookie
		users.deleteByHomeNumber("972551234567");
		users.add(username, "Undefined", "972551234567", new Date(), "Simgo");
		userId = mTelemetryQueries.getUserId(username);
		mTelemetryQueries.injectCookieIntoUser(userId);		
		// get the correct cookie from DB
		this.cookie = mTelemetryQueries.getCookieFromUser(username);
		this.sessionId = "sessionid=" + this.cookie;		
		//create a new trip for user
		trips.add(username, TestParams.PLUG_ID, "032222222");
		mTelemetryQueries.injectAllocationIntoTrip();
		tripId = mTelemetryQueries.getTripIdByUserName(username);
		mTelemetryQueries.updateBillingMarkerDate(tripId);		
	}

	/*
	 * Send all values valid: trip id, cookie, json file, PUT method.
	 */
	@Test
	public void errorCodesValidRequest() {
		given().header(TestParams.COOKIE, this.sessionId).contentType(ContentType.JSON).body(fullJson.toString()).when()
				.put(URL + this.tripId).then().assertThat().statusCode(200).and()
				.body(containsString("\"overall_allocations\": 1"));
		System.out.println("200- valid request");
	}

	/*
	 * Send all valid values, and invalid trip id in the URL (trip id = 1)
	 */
	@Test
	public void sendJsonWithInvalidTripId() {
		given().header(TestParams.COOKIE, this.sessionId).contentType(ContentType.JSON).body(fullJson.toString()).when()
				.put(URL + 1).then().assertThat().statusCode(412).and()
				.body(containsString("\"error\": \"Trip doesn't exist\""));
		System.out.println("412- invalid trip");
	}

	/*
	 * Send all valid values, and invalid cookie in the header
	 */
	@Test
	public void sendJsonWithNonValidCookie() {
		given().header(TestParams.COOKIE, TestParams.INVALID_COOKIE).contentType(ContentType.JSON).body(fullJson.toString()).when()
				.put(URL + this.tripId).then().assertThat().statusCode(401).and()
				.body(containsString("\"error\": \"Session code mismatch\""));
		System.out.println("401- invalid cookie");
	}

	/*
	 * Send a request using GET method to receive: 400 Bad request
	 */
	@Test
	public void sendGetRequest() {
		given().header(TestParams.COOKIE, this.sessionId).when().get(URL + this.tripId).then().assertThat().statusCode(400).and()
				.body(containsString("Only POST/PUT/DELETE are supported"));
		System.out.println("400- invalid request: GET");
	}

	/*
	 * Send a request using POST method to receive: 400 Bad request
	 */
	@Test
	public void sendPostRequest() {
		given().header(TestParams.COOKIE, this.sessionId).contentType(ContentType.JSON).body(fullJson.toString()).when()
				.post(URL + this.tripId).then().assertThat().statusCode(400).and()
				.body(containsString("\"error\": \"Invalid Start Trip request\""));
		System.out.println("400- invalid request: POST");
	}
	
	/*
	 * Send request and get the data sent in the reply from the cloud.
	 * Data in the json file is 110.00MB, Expected: return "total_data_usage": 110.0
	 * timestamp (event id) must be changed so that data usage will return correctly.
	 */
	@Test(groups = "dataValidation")
	public void dataValidation() {
		// get the inner object "status" and change the event id to current time
		JsonObject object = fullJson.getAsJsonObject("object");
		String eventId = System.currentTimeMillis() +"";
		object.addProperty("status", "Phone: iPhone 6/9.1, MCC/NAME: 42501/orange Israel, CallState: IDLE, DataState: CONNECTED, Battery: 40%, Charging: No, Trip Data Usage: 110.00MB, Event ID: " + eventId);

		given().header(TestParams.COOKIE, this.sessionId).contentType(ContentType.JSON).body(fullJson.toString()).when()
		.put(URL + this.tripId).then().assertThat().statusCode(200).and()
		.body(containsString("\"total_data_usage\": 110.0"));	
		System.out.println("data usage updated successfully");
	}
}
