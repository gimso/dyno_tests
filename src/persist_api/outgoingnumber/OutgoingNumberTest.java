package persist_api.outgoingnumber;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

import java.io.File;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jayway.restassured.http.ContentType;

import global.FileUtil;
import global.PropertiesUtil;
import jdbc.TelemetryQueries;
import persist.trip.Trips;
import persist.usage.Allocations;
import persist_api.config.TestParams;

/**
 * 
 * @author Dana
 * This class tests the Outgoing Number API.
 * A single JSON file is sent to outgoing number URL along with trip id, and user's cookie.
 * Allocation must exist in the trip.
 */
public class OutgoingNumberTest {
	
	private static final String URI = PropertiesUtil.getInstance().getProperty("OUTGOING_NUMBER_API_URI");
	private JsonParser jsonParser = new JsonParser();
	private TelemetryQueries mTelemetryQueries;
	private Trips trips;	
	private String cookie;
	private String sessionId;
	private String username;
	private int tripId;
	private JsonObject fullJson;
	private String url;	
	private String data;
	
	/*
	 * get username, trip id, cookie
	 * injects allocation into trip if doesn't exist
	 * parses the relevant JSON file 
	 */
	@BeforeClass
	public void beforeClass() {
		mTelemetryQueries = new TelemetryQueries();
		trips = new Trips();	
		this.username = PropertiesUtil.getInstance().getProperty("USAGE_USER");
		this.cookie = mTelemetryQueries.getCookieFromUser(username);
		this.sessionId = "sessionid=" + this.cookie;
		data = FileUtil.readFromFile(new File("files/OutgoingNumberTest/OutgoingNumber.json"));		
		// get the current trip id, if doesn't exist create new
		this.tripId = mTelemetryQueries.getTripIdByUserName(username);
		if (tripId == -1) {
			trips.add(username, TestParams.PLUG_ID, "032222222");
			tripId = mTelemetryQueries.getTripIdByUserName(username);
		}
		// inject allocation into trip, if doesn't exist create new
		mTelemetryQueries.injectAllocationIntoTrip();
	}
	
	/*
	 * before each method get the correct trip id and insert to url.
	 */
	@BeforeMethod
	public void beforeMethod() {
		// get the correct url per trip id
		this.url = URI + this.tripId + "/outgoing_call";
		this.fullJson = (JsonObject) jsonParser.parse(data);
	}
	
	/*
	 * send correct trip id in the url, cookie, POST and valid JSON.
	 */
	@Test
	public void sendValidRequest() {
		given().header(TestParams.COOKIE, this.sessionId).contentType(ContentType.JSON).body(this.fullJson.toString()).when()
				.post(this.url).then().assertThat().statusCode(200);
		
		System.out.println("200: valid request sent");
	}
	
	/*
	 * delete allocation, send a request with valid parameters and restore the allocation.
	 * expect: 409
	 */
	@Test
	public void sendRequestWhenAllocationNotExist() {
		// delete allocation
		new Allocations().deleteByPlugId(TestParams.PLUG_ID);

		// send request without an existing allocation in the trip
		given().header(TestParams.COOKIE, this.sessionId).contentType(ContentType.JSON).body(this.fullJson.toString()).when()
				.post(this.url).then().assertThat().statusCode(409);

		// create allocation and insert into the trip
		mTelemetryQueries.injectAllocationIntoTrip();
		
		System.out.println("409: no allocation");
	}
	
	/*
	 * send a request with valid parameters, except for invalid cookie parameter.
	 * expect: 401 unauthorized.
	 */
	@Test
	public void sendRequestWithNonValidCookie() {
		// insert invalid cookie id
		given().header(TestParams.COOKIE, TestParams.INVALID_COOKIE).contentType(ContentType.JSON).body(this.fullJson.toString()).when()
				.post(this.url).then().assertThat().statusCode(401);
		
		System.out.println("401: invalid cookie");
	}
	
	/*
	 * send all valid parameters, use GET method 
	 * JSON is not required in this case
	 * expect: 400- Only POST is supported
	 */
	@Test
	public void sendRequestUsingGet() {
		given().header(TestParams.COOKIE, this.sessionId).contentType(ContentType.JSON).when().get(this.url).then().assertThat()
				.statusCode(400).and().body(containsString("Only POST is supported"));
		
		System.out.println("400: Get method");
	}
	
	/*
	 * send all valid parameters, except for trip id.
	 * trip id sent is hard coded (1).
	 * expect: 412
	 */
	@Test
	public void sendRequestWithNonValidTripId() {
		given().header(TestParams.COOKIE, this.sessionId).contentType(ContentType.JSON).body(this.fullJson.toString()).when()
				.post(URI + 1 + "/outgoing_call").then().assertThat().statusCode(412);
		
		System.out.println("412: trip doesn't exist");
	}
	
	/*
	 * send valid parameters, change the JSON file to be invalid.
	 * correct key: "destination_number", changed to: "dest_number"
	 * expected: 400 Bad Request
	 */
	@Test
	public void sendRequestWithInvalidJson() {
		// change the "destination_number" key in the json file to be invalid ("dest_number").
		String invalidJson = this.fullJson.toString().replace("destination_number", "dest_number");

		given().header(TestParams.COOKIE, this.sessionId).contentType(ContentType.JSON).body(invalidJson).when().post(this.url)
				.then().assertThat().statusCode(400);
		
		System.out.println("400: invalid Json key");
	}

}
