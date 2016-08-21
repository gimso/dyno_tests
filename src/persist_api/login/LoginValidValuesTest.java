package persist_api.login;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

import org.testng.annotations.Test;

import com.google.gson.JsonObject;
import com.jayway.restassured.http.ContentType;

import general_config.DataProviderInit;
import global.PropertiesUtil;

/**
 * @author Dana
 * This class tests the Login API- valid values
 */
public class LoginValidValuesTest {
	private static final String PHONE_NUMBER = PropertiesUtil.getInstance().getProperty("LOGIN_PHONE_NUMBER");
	private static final String PARSE_PHONE_NUMBER = "0551234567";
	private static final String URL = PropertiesUtil.getInstance().getProperty("LOGIN_API_URL");

	/*
	 * Send valid JSON with valid phone number in the URL.
	 * The response contains the number sent in the url
	 */
	@Test(dataProvider="jsonDp",dataProviderClass=DataProviderInit.class)
	public void sendValidJson(JsonObject fullJson) {
		
		given().contentType(ContentType.JSON).
		body(fullJson.toString()).when().post(URL + PHONE_NUMBER).
		then().
		assertThat().statusCode(200).and().
		body(containsString("+972551234567")).and().body(containsString("\"login_phase\": 1"));
		System.out.println("status code 200 OK");

	}
	
	/*
	 * Checks the parsing of the number in the cloud
	 */
	@Test(dataProvider="jsonDp",dataProviderClass=DataProviderInit.class)
	public void parseNumInCloud(JsonObject fullJson) {
		
		given().contentType(ContentType.JSON).
		body(fullJson.toString()).when().post(URL + PARSE_PHONE_NUMBER).
		then().
		assertThat().statusCode(200).and().
		body(containsString("0551234567")).and().body(containsString("\"login_phase\": 1"));
		System.out.println("status code 200 OK");

	}

}
