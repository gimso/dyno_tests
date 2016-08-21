package persist_api.gcm;

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
import persist_api.config.TestParams;

/**
 * 
 * @author Dana
 * 
 */
public class GcmTest {
	
	private static final String URL = PropertiesUtil.getInstance().getProperty("GCM_API_URL");
	private String invalidUrl = "http://d.qa.gimso.net:5350/users/sessio";
	private TelemetryQueries mTelemetryQueries;
	private String cookie;
	private String sessionId;
	private String username;
	private JsonObject fullJson;
	private String data;
	JsonParser jsonParser = new JsonParser();

	/*
	 * Initialize variables
	 */
	@BeforeClass
	public void beforeClass() {
		// initialize variables
		this.mTelemetryQueries = new TelemetryQueries();
		this.username = PropertiesUtil.getInstance().getProperty("USAGE_USER");
		this.cookie = mTelemetryQueries.getCookieFromUser(username);
		this.sessionId = "sessionid=" + this.cookie;
		data = FileUtil.readFromFile(new File("files/GcmTest/gcmTest.json"));
	}
	
	/*
	 * Delete the gcm key before each test
	 */
	@BeforeMethod
	public void beforeMethod() {
		mTelemetryQueries.deleteGcmKeyByUser();
		this.fullJson = (JsonObject) jsonParser.parse(data);
		
	}

	/*
	 * Send valid values: hnum, cookie, gcm, url
	 * Expected: status 200
	 */
	@Test
	public void validValuesTest() {
		given().header(TestParams.COOKIE, this.sessionId).contentType(ContentType.JSON).body(fullJson.toString()).when()
				.put(URL).then().assertThat().statusCode(200);
		System.out.println("200 valid values");
	}
	
	/*
	 * Inject a GCM to user, then send a JSON containing a new GCM value.
	 * Expected: status 200
	 */
	@Test
	public void updateGcmValue() {
		// inject a gcm value
		mTelemetryQueries.injectGcmKeyIntoUser();
		// change the value on gcm key in the json file
		JsonObject gcmObj = fullJson.getAsJsonObject("object");
		gcmObj.addProperty("gcm_key", "JSQ9VJpg7TDLsn6vOkK5V_1q6cczV5");

		given().header(TestParams.COOKIE, this.sessionId).contentType(ContentType.JSON).body(fullJson.toString()).when()
				.put(URL).then().assertThat().statusCode(200);
		System.out.println("200 update GCM");
	}
	
	/*
	 * Send a Json with invalid cookie.
	 * Expected: status 401
	 */
	@Test
	public void invalidCookie() {
		given().header(TestParams.COOKIE, "sessionid=" + TestParams.INVALID_COOKIE).contentType(ContentType.JSON)
				.body(fullJson.toString()).when().put(URL).then().assertThat().statusCode(401).and()
				.body(containsString("\"error\": \"Session key mismatch\""));
		System.out.println("invalid cookie- status 401");
	}
	
	/*
	 * Send a Json with invalid hnum.
	 * Expected: status 412
	 */
	@Test
	public void invalidHnumValue() {
		// change the gcm key value in the json file: change the inner object
		// "hnum" to an invalid hnum
		JsonObject gcmObj = fullJson.getAsJsonObject("object");
		gcmObj.addProperty("hnum", TestParams.INVALID_PHONE_NUMBER);

		given().header(TestParams.COOKIE, this.sessionId).contentType(ContentType.JSON).body(fullJson.toString()).when()
				.put(URL).then().assertThat().statusCode(412).and()
				.body(containsString("\"error\": \"User not found\""));
		System.out.println("status 412- User not found");
	}
	
	/*
	 * Send a Json with invalid url.
	 * Expected: status 404
	 */
	@Test
	public void invalidUrl() {
		// send invalid url- missing the last letter
		given().header(TestParams.COOKIE, this.sessionId).contentType(ContentType.JSON).body(fullJson.toString()).when()
				.put(invalidUrl).then().assertThat().statusCode(404);
		System.out.println("status 404- Not found");

	}
	
	/*
	 * Send a Json using GET method.
	 * Expected: status 400
	 */
	@Test
	public void sendJsonUsingGetMethod() {
		given().header(TestParams.COOKIE, this.sessionId).when().get(URL).then().assertThat().statusCode(400).and()
				.body(containsString("\"error\": \"Invalid User Cloud request\""));
		System.out.println("Status 400 - invalid request");
	}

}
