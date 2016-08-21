package persist_api.trip;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


import java.io.File;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.ValidatableResponse;

import global.FileUtil;
import global.PropertiesUtil;
import jdbc.TelemetryQueries;
import persist.trip.Trips;
import persist_api.config.TestParams;
/**
 * @author Dana
 *	This class is checking "create trip api" using a single JSON file: files/TripCreateTest/tripCreate.json
 */
public class TripCreateTest {
	private static final String URL = PropertiesUtil.getInstance().getProperty("TRIP_API_URL");
	private String cookie;
	private String sessionId;
	private int tripId;
	private JsonObject fullJson;
	private String name;	
	private TelemetryQueries mTelemetryQueries;
	private String data;
	JsonParser jsonParser = new JsonParser();
	

	@BeforeClass
	public void beforeClass() {
		mTelemetryQueries = new TelemetryQueries();
		this.name = PropertiesUtil.getInstance().getProperty("USAGE_USER");
		// get the correct cookie and trip id from DB
		this.cookie = mTelemetryQueries.getCookieFromUser(name);
		this.sessionId = "sessionid=" + this.cookie;
		data = FileUtil.readFromFile(new File("files/TripCreateTest/tripCreate.json"));
	}

	/*
	 * delete trip if exists, to test the Create Trip API
	 * to create a new trip: delete existing one
	 */	
	@BeforeMethod
	public void beforeMethod() {	
		this.tripId = mTelemetryQueries.getTripIdByUserName(name);
		if ((this.tripId != -1)) {
			mTelemetryQueries.deleteTripById(this.tripId);
			tripId=-1;
		}
		this.fullJson = (JsonObject) jsonParser.parse(data);
	}
	
	/*
	 * Send a valid JSON 
	 */
	@Test
	public void sendJsonWithValidValues() {
		//send valid values JSON, with valid cookie
		given().contentType(ContentType.JSON).header(TestParams.COOKIE, this.sessionId).body(fullJson.toString()).when().post(URL)
				.then().assertThat().statusCode(200).and().body(containsString("\"trip_id\""));

		System.out.println("valid values: 200 create trip succeeded");
	}

	/*
	 * Send a valid JSON when access numbers are dis-allowed
	 */
	@Test
	public void sendJsonWhenNoAccessNumberExists() {
		//in access numbers table: mark all as "allowed = false"
		mTelemetryQueries.disallowAllAccessNumbers();
		given().contentType(ContentType.JSON).header(TestParams.COOKIE, this.sessionId).body(this.fullJson.toString()).when().post(URL)
		.then().assertThat().statusCode(500).and()
		.body(containsString("\"error\": \"Can't start trip. Local Access Number not available.\""));

		System.out.println("No access number: 500 Local Access Number not available");
		
		//return access numbers table to "allowed = true"
		mTelemetryQueries.allowAllAccessNumbers();
	}
	
	/*
	 * Send a valid JSON with invalid cookie
	 */
	@Test
	public void startTripRequestWithInvalidCookie() {		
		given().contentType(ContentType.JSON).header(TestParams.COOKIE, TestParams.INVALID_COOKIE).body(this.fullJson.toString()).when().post(URL)
				.then().assertThat().statusCode(401).and()
				.body(containsString("\"error\": \"Session code mismatch\""));

		System.out.println("Invalid cookie: 401 session code mismatch");
	}
	
	/*
	 * Set Json plug id value to empty and send a request
	 */
	@Test
	public void startTripWhenPlugIdIsMissing() {		
		//insert empty plug_id value to JSON file
		this.fullJson.addProperty("plug_id", "");

		given().contentType(ContentType.JSON).header(TestParams.COOKIE, this.sessionId).body(this.fullJson.toString()).when().post(URL)
				.then().assertThat().statusCode(412).and()
				.body(containsString("\"error\": \"Simgo device ID not found\""));

		System.out.println("Plug id missing: 412 account not found");
	}
	
	/*
	 * Set Json Hnum value to empty and send a request
	 */
	@Test
	public void startTripWhenHnumIsMissing() {		
		//insert empty hnum value to JSON file
		this.fullJson.addProperty("hnum", "");

		given().contentType(ContentType.JSON).header(TestParams.COOKIE, this.sessionId).body(this.fullJson.toString()).when().post(URL)
				.then().assertThat().statusCode(412).and().body(containsString("\"error\": \"Account not found\""));

		System.out.println("Hnum missing: 412 account not found");
	}
	
	/*
	 * Send JSON when trip for that user already exists.
	 * Expected: response contains existing trip id
	 */
	@Test
	public void sendJsonWhenTripExists() {
		//create a new trip
		new Trips().add(name, TestParams.PLUG_ID, "032222222");
		this.tripId= mTelemetryQueries.getTripIdByUserName(name);
			
		//do not delete an existing trip.
		given().contentType(ContentType.JSON).header(TestParams.COOKIE, this.sessionId).body(this.fullJson.toString()).when().post(URL)
				.then().assertThat().statusCode(200).and().body(containsString("\"trip_id\": " + this.tripId));

		System.out.println("Trip exists: 200 create trip succeeded");
	}
	
	
	/*
	 * checks db for current trip id (deleted before the test, therefore will be -1)
	 * Sends create trip request, and checks db that a new trip id is inserted.
	 */
	@Test
	public void createTripAndCheckDb(){	
		//send trip create request
		ValidatableResponse response = given().contentType("application/json").header(TestParams.COOKIE, this.sessionId).
		body(this.fullJson.toString()).when().post(URL).then().statusCode(200);			
		//extract the trip id from the response 
		JsonObject responseBody = (JsonObject) new JsonParser().parse(response.extract().asString());
		JsonElement responseTripId = responseBody.getAsJsonObject("object").get("trip_id");		
		//check db for trip id
		tripId = mTelemetryQueries.getTripIdByUserName(name);		
		//assert that actual trip id (from response body) equals the one taken from db				
		assertThat(String.valueOf(tripId),equalToIgnoringCase(responseTripId.toString()));		
		System.out.println("trip id from body response after creation = " + responseTripId + ", trip id in db = " + tripId);		
	}
}
