package persist_web.inventory;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import global.FTPUtil;
import global.PersistException;
//import testing_utils.TestFWInit;
import jdbc.JDBCUtil;
import persist.inventory.FirmwareVersionURL;
import persist.inventory.FwConfigs;
import persist.inventory.FwVersions;

/**
 * 
 * @author Yehuda Ginsburg
 *
 */
public class TestFirmwareVersionURL {
	public String output = "";
	public String testName;
	
	private static final String FTP_INNER_FOLDER = "ftp_simgo";
	private static final String FTP_URL_01 = "test_ftp_01.sgo";
	private static final String FTP_URL_02 = "test_ftp_02.sgo";
	private static final String FULL_PATH_FTP_URL_01 = 
			FTPUtil.FTP_USER + ":" +
			FTPUtil.FTP_PASSWORD + "@" + 
			FTPUtil.FTP_SERVER + "/" +
			FTP_INNER_FOLDER + "/" + 
			FTP_URL_01;
	private static final String FULL_PATH_FTP_URL_02 = 
					FTPUtil.FTP_USER + ":" +
					FTPUtil.FTP_PASSWORD + "@" + 
					FTPUtil.FTP_SERVER + "/" +
					FTP_INNER_FOLDER + "/" + 
					FTP_URL_02;

	private FirmwareVersionURL firmwareVersionURL;
	private FwVersions fwVersions;
	private FwConfigs fwConfigs;
	private JDBCUtil jdbc;
	
	private String version = "00.00.00";
	private String config = "test_qa_firmwareVersionURLTest";	
	
	private String newConfig = "test_qa_sgs6";
	private String newVersion = "0.1.2";	

	@BeforeClass
	private void beforeClassInit() {
		FTPUtil ftpUtil = new FTPUtil();

		if (ftpUtil.isFileExistInFtp(FTP_URL_01) == false)
			ftpUtil.uploadFileFromLocal(FTP_URL_01);
		if (ftpUtil.isFileExistInFtp(FTP_URL_02) == false)
			ftpUtil.uploadFileFromLocal(FTP_URL_02);

		this.firmwareVersionURL = new FirmwareVersionURL();
		this.fwConfigs = new FwConfigs();
		this.fwVersions = new FwVersions();
		// delete all where equal null
		this.jdbc = new JDBCUtil();

		jdbc.deleteAllFirmwareVersionsWhereIsNull();
		firmwareVersionURL.deleteAll();
		fwConfigs.deleteAll();
		fwVersions.deleteAll();

		fwConfigs.add(this.config);
		fwConfigs.add(this.newConfig);
		fwVersions.add(this.version);
		fwVersions.add(this.newVersion);

	}
	
	@BeforeMethod
	public void beforeMethod(){
		jdbc.deleteAllFirmwareVersionsWhereIsNull();
		firmwareVersionURL.deleteAll();
	}
	
	/**
	 * add firmware version with all parameters
	 * @throws PersistException
	 */
	@Test
	public void addFirmwareVersionWithAllParameters() {

		String comment = "A Comment";
		String url = FULL_PATH_FTP_URL_01;

		// add
		output = firmwareVersionURL.add(this.config, this.version, url, comment);

		assertThat(output, containsString("successfully"));
	}
	
	@Test
	public void modifyFirmwareVersionUrl() {

		String url = FULL_PATH_FTP_URL_01;
		String newUrl = FULL_PATH_FTP_URL_02;
		String comment = "";
		addUrlAndComment(url, comment);

		output = firmwareVersionURL.modifyByUrl(url, this.config, this.version, newUrl, comment);
		
		assertThat(output, containsString("successfully"));
	}

	@Test
	public void deleteFirmwareVersionUrl() {
		String url = FULL_PATH_FTP_URL_01;
		String comment = "";
		addUrlAndComment(url, comment);
		
		output = firmwareVersionURL.deleteByUrl(url);
		assertThat(output, containsString("successfully"));
	}
	
	@Test(priority = 1)
	public void addFirmwareVersionWithoutNecessaryParameters() {
		
		String emptyConfig = "---------";
		String emptyVersion = "---------";
		String emptyUrl = "";
		String comment = "A Comment";
		String url = FULL_PATH_FTP_URL_01;

		
		// add with empty config
		output = firmwareVersionURL.add(emptyConfig, this.version, url, comment);

		assertThat(output, containsString("This field is required"));

		// add with empty version
		output = firmwareVersionURL.add(this.config, emptyVersion, url, comment);

		assertThat(output, containsString("This field is required"));

		// add with empty url
		output = firmwareVersionURL.add(this.config, this.version, emptyUrl, comment);

		assertThat(output, containsString("This field is required"));
		
	}


	// add duplicate url with different config and version
	@Test
	public void addDuplicateUrlWithDifferentConfigAndVersion() {
		String url = FULL_PATH_FTP_URL_01;
		String comment = "A Comment";
		String newConfig = "test_qa_sgs6";
		String newVersion = "0.1.2";
		String newComment = "A new Comment";
		
		addUrlAndComment(url, comment);
				
		output = firmwareVersionURL.add(newConfig, newVersion, url, newComment);
		
		assertThat(output, containsString("Firmware version with this FW update URL already exists."));
	}


	// Add url to config and version that already have a different url assigned
	// to them
	@Test(priority = 1)
	public void addUrlToConfigAndVersionThatAlreadyHaveADifferentUrlAssignedToThem() {
		// add a fw-version
		String url = FULL_PATH_FTP_URL_01;
		String newUrl = FULL_PATH_FTP_URL_02;
		String comment = " ";
		String newComment = "A Comment";
		addUrlAndComment(url, comment);

		output = firmwareVersionURL.add(this.config, this.version, newUrl, newComment);
		
		assertThat(output, containsString("Firmware version with this FW update configuration and FW update version already exists"));

	}
	
	//	Add firmware version with invalid URL 
	@Test(priority = 1)
	public void addFirmwareVersionWithInvalidUrl() {
		String invalidUrl = "invalid Url";
		String comment = " ";

		output = firmwareVersionURL.add(this.config, this.version, invalidUrl, comment);
		
		assertThat(output, containsString("File String is not in a correct format"));

	}

	// ************
	//  utilities
	// ************
	
	/**
	 * @param config
	 * @param version
	 */
	private void addUrlAndComment(String url, String comment) {
		firmwareVersionURL.add(this.config, this.version, url, comment!=null?comment:"");
	}
	
	
	
	@AfterClass
	public void afterClass() {
		firmwareVersionURL.deleteAll();
		fwConfigs.deleteAll();
		fwVersions.deleteAll();
	}
}
