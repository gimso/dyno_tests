package persist_api.login;

import static com.jayway.restassured.RestAssured.get;
import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

import org.testng.annotations.Test;

import com.google.gson.JsonObject;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;

import general_config.DataProviderInit;
import global.PropertiesUtil;
import persist_api.config.TestParams;

/**
 * @author Dana
 * This class tests the Login API- invalid values
 *
 */
public class LoginInvalidValuesTest {
	private static final String PHONE_NUMBER = PropertiesUtil.getInstance().getProperty("LOGIN_PHONE_NUMBER");
	private static final String URL = PropertiesUtil.getInstance().getProperty("LOGIN_API_URL");

	/*
	 * Send a JSON with URL that contains a non-valid hnum (too short), or a non existing hnum in Persist
	 * The invalid value of phone number sent: +9725512345
	 */
	@Test(dataProvider="jsonDp",dataProviderClass=DataProviderInit.class)
	public void sendJsonWithNonExistingNumber(JsonObject fullJson) {
		
		given().contentType(ContentType.JSON).
		body(fullJson.toString()).when().post(URL + TestParams.INVALID_PHONE_NUMBER).
		then().
		assertThat().statusCode(412).
		and().body(containsString("We can\'t find your number in our records. Simgo is available for registered users only and requires a special protective cover. Visit our website (www.simgo-mobile.com) or contact us at info@simgo-mobile.com to check availability in your country"));
		System.out.println("Number doesn't exist");

	}
	
	/*
	 * Send a JSON using PUT method
	 * Response from the cloud should be 400- BAD REQUEST
	 */
	@Test(dataProvider="jsonDp",dataProviderClass=DataProviderInit.class)
	public void sendJsonUsingPutMethod(JsonObject fullJson) {
	
		given().contentType(ContentType.JSON).
		body(fullJson.toString()).
		when().put(URL + PHONE_NUMBER).
		then().
		assertThat().statusCode(400).
		and().body(containsString("Invalid User Cloud request"));
		
		System.out.println("Send using PUT: Invalid User Cloud request error");
	}
	
	
	/*
	 * Send a request using GET method
	 * Response from the cloud should be 400- BAD REQUEST
	 */
	
	/*
	 * IMPORTANT NOTE:
	 * Currently there's a problem with this method:
	 * Using a RESTClient (DHC), method:GET, URL: http://d.dev.gimso.net:5350/users/session?hnum=+972551234567, status 200 OK is received.
	 * When running the method, status 401 is received.
	 */
	@Test
	public void useGetMethod() {
		
		RestAssured.baseURI = "http://d.dev.gimso.net:5350";
		get("/users/session?hnum=+972551234567").then().assertThat().statusCode(400).
		and().body(containsString("Invalid User Cloud request")).log().all();
		
		System.out.println("Send using GET: Invalid User Cloud request error");
	}
	
	/*
	 * Send a JSON with a different country than the hnum
	 * Should trigger error, the error code is currently unknown
	 */
	@Test(dataProvider="jsonDp",dataProviderClass=DataProviderInit.class)
	public void sendDifferentCountry(JsonObject fullJson) {

		given().contentType(ContentType.JSON).
		body(fullJson.toString()).when().post(URL + PHONE_NUMBER).
		then().
		assertThat().statusCode(409).
		and().body(containsString("error"));																				
		System.out.println("Different country - status code 400 Conflict");
	}



}
