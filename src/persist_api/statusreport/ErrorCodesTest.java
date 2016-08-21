package persist_api.statusreport;
/**
 * @author Dana
 */

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
import persist.usage.Users;
import persist_api.config.TestParams;

/**
 * 
 * @author Dana
 *
 */
public class ErrorCodesTest {
	private static final String APPLICATION_JSON = "application/json";
	private static final String URL = PropertiesUtil.getInstance().getProperty("STATUS_REPORT_URL");
	private String cookie;
	private String sessionId;
	private int tripId;	
	private long date; 
	private String name;
	private TelemetryQueries mTelemetryQueries;
	private Users users;
	private Trips trips;

	@BeforeClass
	public void beforeClass() {
		mTelemetryQueries = new TelemetryQueries();
		trips = new Trips();		
		name = PropertiesUtil.getInstance().getProperty("USAGE_USER");
		int userId = mTelemetryQueries.getUserId(name);
		if (userId == -1){
			users = new Users();
			users.add(name, "Undefined", "972551234567", new Date(), "Simgo");
			userId = mTelemetryQueries.getUserId(name);
		}
		// get the correct cookie and trip id from DB
		this.cookie = mTelemetryQueries.getCookieFromUser(name);
		this.sessionId = "sessionid=" + this.cookie;
		this.tripId = mTelemetryQueries.getTripIdByUserName(name);
		//if trip doesn't exist- create new
		if (tripId == -1) {
			trips.add(name, TestParams.PLUG_ID, "032222222");
			this.tripId = mTelemetryQueries.getTripIdByUserName(name);
		}
		// inject allocation into trip, if doesn't exist create new
		mTelemetryQueries.injectAllocationIntoTrip();
	}
	
	@BeforeMethod
	public void beforeMethod(){
		// get current timestamp
		date = System.currentTimeMillis() / 1000L;
	}
	
	/*
	 * A valid JSON is sent with current timestamp and a valid trip id.
	 */
	@Test(dataProvider="jsonDp",dataProviderClass=DataProviderInit.class)
	public void sendValidJsonToReceiveStatus200(JsonObject fullJson) {
		RestAssured.baseURI = URL;
		// get the inner object "timestamp" and change it to current time.
		JsonArray statusArray = fullJson.getAsJsonObject().getAsJsonArray("status");
		JsonObject innerStatus = statusArray.get(0).getAsJsonObject();
		innerStatus.addProperty("timestamp", date);
		// change trip id to the correct one
		innerStatus.addProperty("trip_id", this.tripId);
		
		given().headers(TestParams.CONTENT_TYPE, APPLICATION_JSON, TestParams.COOKIE, this.sessionId).body(fullJson.toString()).when().put("").then()
				.assertThat().statusCode(200).and()
				.body(containsString("\"overall_allocations\": 1"));
		System.out.println("status code 200 success");
	}
	
	/*
	 * A valid JSON is sent, with a hard-coded trip id value in it.
	 * Trip id = 3, and does not match the actual trip id.
	 */
	@Test(dataProvider="jsonDp",dataProviderClass=DataProviderInit.class)
	public void sendValidJsonWithNonValidTripId(JsonObject fullJson) {
		RestAssured.baseURI = URL;
		// get the inner object "timestamp" and change it to current time.
		JsonArray statusArray = fullJson.getAsJsonObject().getAsJsonArray("status");
		JsonObject innerStatus = statusArray.get(0).getAsJsonObject();
		innerStatus.addProperty("timestamp", date);

		given().headers(TestParams.CONTENT_TYPE, APPLICATION_JSON, TestParams.COOKIE, this.sessionId).body(fullJson.toString()).when()
				.put("").then().assertThat().statusCode(412).and()
				.body(containsString("\"error\": \"Trip doesn't exist\""));
		System.out.println("status code 412 trip doesn't exist");
	}
	
	/*
	 * A valid JSON is sent, with a mismatching cookie parameter (invalidCookie).
	 */
	@Test(dataProvider="jsonDp",dataProviderClass=DataProviderInit.class)
	public void sendValidJsonWithInvalidCookieSessionId(JsonObject fullJson) {
		RestAssured.baseURI = URL;
		// get the inner object "timestamp" and change it to current time.
		JsonArray statusArray = fullJson.getAsJsonObject().getAsJsonArray("status");
		JsonObject innerStatus = statusArray.get(0).getAsJsonObject();
		innerStatus.addProperty("timestamp", date);
		// change trip id to the correct one
		innerStatus.addProperty("trip_id", this.tripId);

		given().headers(TestParams.CONTENT_TYPE, APPLICATION_JSON, TestParams.COOKIE, TestParams.INVALID_COOKIE).body(fullJson.toString()).when()
				.put("").then().assertThat().statusCode(401).and()
				.body(containsString("\"error\": \"Session code mismatch\""));
		System.out.println("status code 401 session code mismatch");
	}
	
	/*
	 * A valid JSON is sent, with a non existing imsi value in it ("imsi":"12345")
	 */
	@Test(dataProvider="jsonDp",dataProviderClass=DataProviderInit.class)
	public void sendValidJsonWithInvalidRsimImsi(JsonObject fullJson) {
		RestAssured.baseURI = URL;
		// get the inner object "timestamp" and change it to current time.
		JsonArray statusArray = fullJson.getAsJsonObject().getAsJsonArray("status");
		JsonObject innerStatus = statusArray.get(0).getAsJsonObject();
		innerStatus.addProperty("timestamp", date);
		// change trip id to the correct one
		innerStatus.addProperty("trip_id", this.tripId);

		given().headers(TestParams.CONTENT_TYPE, APPLICATION_JSON, TestParams.COOKIE, this.sessionId).body(fullJson.toString()).when()
				.put("").then().assertThat().statusCode(409).and()
				.body(containsString("\"error\": \"SIM allocation mismatch\""));
		System.out.println("status code 409 sim allocation mismatch");
	}
	
	
	/*
	 * Using a GET method, no JSON is sent, in order to get error code 500.
	 */
	@Test
	public void sendRequestUsingGetToReceiveStatus500() {
		RestAssured.baseURI = URL;
		given().headers(TestParams.CONTENT_TYPE, APPLICATION_JSON, TestParams.COOKIE, this.sessionId).when().get().then().assertThat()
				.statusCode(500).and().body(containsString("\"error\": \"Internal server error\""));
		System.out.println("status code 500 internal server error");
	}

}
