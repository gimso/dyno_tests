package persist_api.trip;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import global.PropertiesUtil;
import jdbc.TelemetryQueries;
import persist.trip.Trips;
import persist_api.config.TestParams;
/**
 * 
 * @author Dana
 * This class is checking "delete trip api". No JSON files are required
 */
public class TripDeleteTest {
	private static final String URI = PropertiesUtil.getInstance().getProperty("TRIP_API_URL");
	private String invalidURL = URI + "/" + "17";	
	private TelemetryQueries mTelemetryQueries;
	private String cookie;
	private String sessionId;
	private String username;
	private int tripId;
	private String url;
	
	/*
	 * initialize telemetry queries
	 */
	@BeforeClass
	public void beforeClass() {
		mTelemetryQueries = new TelemetryQueries();
	}
	
	/*
	 * check if trip per user exists, if not create new.
	 * get the users's cookie
	 * URL should be: d.qa.gimso.net:5350/trips/{tripId}
	 * get the current trip id and insert into URL 
	 */
	@BeforeMethod
	public void beforeMethod() {		
		// get user name
		this.username = PropertiesUtil.getInstance().getProperty("USAGE_USER");
		// get trip id
		this.tripId = mTelemetryQueries.getTripIdByUserName(this.username);
		// if trip doesn't exist- create new
		if (this.tripId == -1) {
			new Trips().add(username, TestParams.PLUG_ID, "032222222");
			this.tripId = mTelemetryQueries.getTripIdByUserName(this.username);
		}
		// get user's cookie and insert to sessionid format
		this.cookie = mTelemetryQueries.getCookieFromUser(this.username);
		this.sessionId = "sessionid=" + this.cookie;
		
		// url for delete contains the trip id
		this.url = URI + "/" + this.tripId;

	}
	
	/*
	 * Send DELETE request, using correct trip id and cookie.
	 * Expect: status code 200
	 */
	@Test
	public void sendValidValuesRequset() {
		given().header(TestParams.COOKIE, this.sessionId).delete(this.url).then().assertThat().statusCode(200).and()
				.body(containsString("{}"));
		System.out.println("status code 200");
	}

	/*
	 * Send DELETE request, using invalid cookie.
	 * Expect: status code 401
	 */
	@Test
	public void sendRequestWithInvalidCookie() {
		given().header(TestParams.COOKIE, TestParams.INVALID_COOKIE).delete(this.url).then().assertThat().statusCode(401).and()
				.body(containsString("\"error\": \"Session code mismatch\""));
		System.out.println("status code 401: invalid cookie");
	}
	
	/*
	 * Send the same request using PUT.
	 * PUT method is updating a trip and sending back usage information
	 * Expect: status code 200
	 */
	@Test
	public void sendRequestUsingPutMethod() {
		given().header(TestParams.COOKIE, this.sessionId).put(this.url).then().assertThat().statusCode(200).and()
				.body(containsString("\"allocation\"")).and().body(containsString("\"usage\""));
		System.out.println("status code 200- put method");
	}
	
	/*
	 * Send the same request using GET.
	 * GET method is not valid
	 * Expect: status code 400
	 */
	@Test
	public void sendRequestUsingGetMethod() {
		given().header(TestParams.COOKIE, this.sessionId).get(this.url).then().assertThat().statusCode(400).and()
				.body(containsString("Only POST/PUT/DELETE are supported"));
		System.out.println("status code 400- get method");
	}
	
	/*
	 * Send DELETE request, using incorrect trip id
	 * Expect: status code 412
	 */
	@Test
	public void sendDeleteRequestForNonExistingTrip() {
		given().header(TestParams.COOKIE, this.sessionId).put(invalidURL).then().assertThat().statusCode(412).and()
				.body(containsString("\"error\": \"Trip doesn't exist\""));
		System.out.println("status code 412: trip doesn't exist");
	}
}
