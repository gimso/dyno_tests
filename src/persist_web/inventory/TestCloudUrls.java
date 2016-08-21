package persist_web.inventory;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import persist.inventory.CloudUrls;

public class TestCloudUrls {
	private CloudUrls cloudUrls;

	public String output = "";
	public String testName;

	@BeforeClass
	public void beforeClass() {
		cloudUrls = new CloudUrls();
	}

	@BeforeMethod
	public void beforeMethod() {
		cloudUrls.deleteAll();
	}

	@Test
	// add a valid url
	public void addCloudUrlWithValidFields() {
		String name = "Staging";
		String url = "http://o.staging.gimso.net:5350/admin/";
		output = cloudUrls.add(name, url);

		assertThat(output, containsString("successfully"));
	}

	@Test
	// add non valid url
	public void addCloudUrlWithCorruptUrl() {
		String name = "TestUrl";
		String url = "ftp://133";
		output = cloudUrls.add(name, url);

		assertThat(output, containsString("Enter a valid URL."));
	}

	@Test
	public void addCloudUrlWithAnInvalidUrlPort() {
		String name = "Test123";
		String url = "http://www.google.com";

		output = cloudUrls.add(name, url);

		assertThat(output, containsString("Enter a valid URL"));
	}

	@Test
	// add identical clouds with the same name
	public void addDuplicateCloudNames() {
		String name = "Staging";
		String url = "http://o.staging.gimso.net:5350/admin/";

		cloudUrls.add(name, url);
		output = cloudUrls.add(name, url);

		assertThat(output, containsString("already exists"));
	}

	@Test
	// modify cloud url with valid values
	public void modifyCloudUrl() {
		String name = "Staging";
		String url = "http://o.staging.gimso.net:5350/admin/";
		cloudUrls.add(name, url);

		output = cloudUrls.modifyByName(name, "TestUrl", "http://d.eos.simgo.me:5350/");

		assertThat(output, containsString("successfully"));
	}

	@Test
	// add a cloud url, send a JSON request and verify that reply contains the
	// url.
	public void getCloudUrlListViaJsonRequest() {
		String name = "Staging";
		String url = "http://d.qa.gimso.net:5350/cloud_settings";
		cloudUrls.add(name, url);
		output = cloudUrls.getJsonFromUrl(url);

		assertThat(output, containsString("http://d.qa.gimso.net:5350/cloud_settings"));
	}

	@Test
	public void deleteCloudUrl() {
		String name = "Staging";
		String url = "http://o.staging.gimso.net:5350/admin/";
		cloudUrls.add(name, url);
		output = cloudUrls.deleteByName(name);

		assertThat(output, containsString("successfully"));
	}

}
