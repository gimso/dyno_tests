package persist_api.verificationcode;

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
 * @author Dana
 * This class tests the Verification code API.
 */
public class VerificationCodeTest {
	private static final String URL = PropertiesUtil.getInstance().getProperty("VERIFICATION_CODE_URL");
	private static final String PHONE_NUMBER = PropertiesUtil.getInstance().getProperty("VER_CODE_PHONE_NUMBER");
	private String password;
	private JsonObject fullJson;	
	private String data;
	JsonParser jsonParser = new JsonParser();

	@BeforeClass
	public void beforeClass() {
		String name = PropertiesUtil.getInstance().getProperty("USAGE_USER");
		// get the correct password of the user
		int userId = new TelemetryQueries().getUserId(name);
		this.password = new TelemetryQueries().getPasswordByUserId(userId);
		data = FileUtil.readFromFile(new File("files/VerificationCodeTest/verCodeTest.json"));
	}
	
	@BeforeMethod
	public void beforeMethod() {
		this.fullJson = (JsonObject) jsonParser.parse(data);
	}

	/*
	 * Send a valid JSON, containing the correct user's password taken from DB, and the correct country
	 * The URL contains the correct Hnum of the user
	 */
	@Test
	public void sendAValidJson() {		
		//change the hard-coded password value in the json file to the current password 			
		this.fullJson.addProperty("pass", this.password);
		given().contentType(ContentType.JSON).body(this.fullJson.toString()).when().post(URL + PHONE_NUMBER).then()
				.assertThat().statusCode(200).and().body(containsString("user_name")).and().body(containsString("\"login_phase\": 2"));
		
		System.out.println("valid verification code sent-status 200");
	}
	
	/*
	 * Send a valid JSON to a non existing Hnum in the URL.
	 * The Hnum of user is +972551234567, incorrect is +9725512345
	 */
	@Test
	public void sendJsonToAUrlWithNonExistingHnum() {	
		//change the hard-coded password value in the json file to the current password 			
		this.fullJson.addProperty("pass", this.password);
							
		given().contentType(ContentType.JSON).body(this.fullJson.toString()).when().post(URL + TestParams.INVALID_PHONE_NUMBER).then()
				.assertThat().statusCode(412)
				.and().body(containsString("We can\'t find your number in our records. Simgo is available for registered users only and requires a special protective cover. Visit our website (www.simgo-mobile.com) or contact us at info@simgo-mobile.com to check availability in your country"));
				
				
		System.out.println("verification code with invalid hnum sent-status 412");
	}
	
	/*
	 * Send a valid JSON using a PUT method instead of POST, should trigger error message 400- BAD REQUEST
	 */
	@Test
	public void sendValidJsonUsingPutMethod() {
		//change the hard-coded password value in the json file to the current password
		this.fullJson.addProperty("pass", this.password);
		//send the Json using PUT instead of POST
		given().contentType(ContentType.JSON).body(this.fullJson.toString()).when().put(URL + PHONE_NUMBER).then()
		.assertThat().statusCode(400).and().body(containsString("{\"error\": \"Invalid User Cloud request\"}"));
		
		System.out.println("verification code with Put method sent-status 400");
	}
	
	/*
	 * send a Json with verification code that doesn't match the password of the user in Persist.
	 * expected response: UNAUTHORIZED
	 */
	@Test
	public void sendJsonWithNonMatchingVerificationCode() {	
		//send a hard coded verification code that doesn't match the actual one.
		given().contentType(ContentType.JSON).body(this.fullJson.toString()).when().post(URL + PHONE_NUMBER).then()
		.assertThat().statusCode(401).and().body(containsString("\"error\": \"Verification code mismatch\""));
		
		System.out.println("A non matching verification code sent - status 401");
	}

}
