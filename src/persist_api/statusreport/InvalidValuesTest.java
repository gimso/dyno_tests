package persist_api.statusreport;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jayway.restassured.RestAssured;

import general_config.DataProviderInit;
import global.PropertiesUtil;
import jdbc.TelemetryQueries;
import persist_api.config.TestParams;

/**
 * @author Dana
 */

public class InvalidValuesTest {
	private static final String APPLICATION_JSON = "application/json";
	private static final String URL = PropertiesUtil.getInstance().getProperty("STATUS_REPORT_URL");
	private String cookie;
	private String sessionId;
	private int tripId;

	@BeforeClass
	public void beforeClass() {
		String name = PropertiesUtil.getInstance().getProperty("USAGE_USER");
		// get the correct cookie and trip id from DB
		this.cookie = new TelemetryQueries().getCookieFromUser(name);
		this.sessionId = "sessionid=" + this.cookie;
		this.tripId = new TelemetryQueries().getTripIdByUserName(name);
	}
	
	/* 
	 * A Json is sent with trip id value=null.
	 */
	@Test(dataProvider="jsonDp",dataProviderClass=DataProviderInit.class)
	public void sendValidJsonWithoutTripId(JsonObject fullJson) {
		RestAssured.baseURI = URL;

		// get current timestamp
		long date = System.currentTimeMillis() / 1000L;

		// get the inner object "timestamp" and change it to current time.
		JsonArray statusArray = fullJson.getAsJsonObject().getAsJsonArray("status");
		JsonObject innerStatus = statusArray.get(0).getAsJsonObject();
		innerStatus.addProperty("timestamp", date);

		given().headers(TestParams.CONTENT_TYPE, APPLICATION_JSON, TestParams.COOKIE, this.sessionId).body(fullJson.toString()).when().put("")
				.then().assertThat().statusCode(500);
		System.out.println("Null trip id cannot be sent");
	}
	
	/*
	 * A JSON with negative values is sent:
	 * phone_data- battery: -100, charging: -1.
	 * plug_data- battery: -5, fw_status: -3, plug_status: -1, sim_status: -3.
	 */
	@Test(dataProvider="jsonDp",dataProviderClass=DataProviderInit.class)
	public void negativeNumbers(JsonObject fullJson) {
		RestAssured.baseURI = URL;
		// get current timestamp
		long date = System.currentTimeMillis() / 1000L;

		// get the inner object "timestamp" and change it to current time.
		JsonArray statusArray = fullJson.getAsJsonObject().getAsJsonArray("status");
		JsonObject innerStatus = statusArray.get(0).getAsJsonObject();
		innerStatus.addProperty("timestamp", date);
		// change trip id to the correct one
		innerStatus.addProperty("trip_id", this.tripId);

		given().headers(TestParams.CONTENT_TYPE, APPLICATION_JSON, TestParams.COOKIE, this.sessionId).body(fullJson.toString()).when().put("")
				.then().assertThat().statusCode(500);
		System.out.println("Negative numbers cannot be sent");
	}
	
	/*
	 * A Json is sent with mismatching MCC and MNC values:
	 * "mcc": 425, "mnc": 425
	 */
	@Test(dataProvider="jsonDp",dataProviderClass=DataProviderInit.class)
	public void sendNonMatchingFields(JsonObject fullJson) {
		RestAssured.baseURI = URL;

		// get current timestamp
		long date = System.currentTimeMillis() / 1000L;

		// get the inner object "timestamp" and change it to current time.
		JsonArray statusArray = fullJson.getAsJsonObject().getAsJsonArray("status");
		JsonObject innerStatus = statusArray.get(0).getAsJsonObject();
		innerStatus.addProperty("timestamp", date);
		// change trip id to the correct one
		innerStatus.addProperty("trip_id", this.tripId);

		given().headers(TestParams.CONTENT_TYPE, APPLICATION_JSON, TestParams.COOKIE, this.sessionId).body(fullJson.toString()).when().put("")
				.then().assertThat().statusCode(500);
		System.out.println("Mismatching fields cannot be sent");
	}
	
	/*
	 * A Json is sent with mismatching plug id and trip id:
	 * The correct trip id is sent, and a plug id that doesn't exist in Persist.
	 * "plug_id": "000010002000"
	 */
	@Test(dataProvider="jsonDp",dataProviderClass=DataProviderInit.class)
	public void crossInformation(JsonObject fullJson) {
		RestAssured.baseURI = URL;

		// get current timestamp
		long date = System.currentTimeMillis() / 1000L;

		// get the inner object "timestamp" and change it to current time.
		JsonArray statusArray = fullJson.getAsJsonObject().getAsJsonArray("status");
		JsonObject innerStatus = statusArray.get(0).getAsJsonObject();
		innerStatus.addProperty("timestamp", date);
		// change trip id to the correct one
		innerStatus.addProperty("trip_id", this.tripId);

		given().headers(TestParams.CONTENT_TYPE, APPLICATION_JSON, TestParams.COOKIE, this.sessionId).body(fullJson.toString()).when().put("")
				.then().assertThat().statusCode(412).and().body(containsString("{\"error\""));
		System.out.println("TripID and PlugID don't match");
	}

	/*
	 * checks that data/calls consumption is as per: current =< trip =< total
	 * "calls":{"current":100,"total":0,"trip":10},"data":{"current":90,"total":10,"trip":50}.
	 * expected error.
	 */
	@Test(dataProvider="jsonDp",dataProviderClass=DataProviderInit.class)
	public void consumptionMismatch(JsonObject fullJson) {
		RestAssured.baseURI = URL;

		// get current timestamp
		long date = System.currentTimeMillis() / 1000L;

		// get the inner object "timestamp" and change it to current time.
		JsonArray statusArray = fullJson.getAsJsonObject().getAsJsonArray("status");
		JsonObject innerStatus = statusArray.get(0).getAsJsonObject();
		innerStatus.addProperty("timestamp", date);
		// change trip id to the correct one
		innerStatus.addProperty("trip_id", this.tripId);

		given().headers(TestParams.CONTENT_TYPE, APPLICATION_JSON, TestParams.COOKIE, this.sessionId).body(fullJson.toString()).when().put("")
				.then().assertThat().statusCode(500);
		System.out.println("Consumption details mismatch");
	}
}
