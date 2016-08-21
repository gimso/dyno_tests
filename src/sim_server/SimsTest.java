package sim_server;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import jdbc.JDBCUtil;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalToIgnoringCase;

import org.apache.commons.lang3.RandomStringUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;

import teles_simulator.TelesHttpInterface;


public class SimsTest {
  
  String imsi;
  String geolocation;
  String generation;
  String offset;
  String boardnumber;
  String geoLocation;
  JDBCUtil jdbc;
  TelesHttpInterface teles;
  
  String foundGeoLocation;
  String foundGeneration;
  int is_umts;
  int is_gsm;
  
  @Test(dataProvider = "addSimTestData", timeOut = 350000)

  public void insertSim(String boardnumber, String offset, String geoLocation, String imsi, String generation) {
	  
 
   
   this.imsi = imsi;
   this.generation = generation;
   this.offset = offset;
   this.boardnumber = boardnumber;
   this.geoLocation = geoLocation;
   String foundImsi = null;
   
   Assert.assertTrue(teles.addSimToSimUnit(boardnumber, offset, imsi, generation));
     
   Map <String, Object> sims;
   
   
   
  // Search the sim in the DB until foundImsi or fail if timeout 
   
   while (foundImsi == null)
   {
   
	   try {
		sims = (jdbc.getSimByImsi(imsi)); 
		
		
		if (sims.get("imsi") != null) 
			{
			 foundImsi = sims.get("imsi").toString();
			 foundGeoLocation = sims.get("geo_location").toString();
			 is_umts = (Integer) sims.get("is_umts");
			 is_gsm = (Integer) sims.get("is_gsm");
			};
		Thread.sleep(1000);
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	   
   }
   
   
   // Verify that the sim's imsi the same imsi that was set
   
   assertThat(foundImsi,equalToIgnoringCase(imsi));
   
   // Verify that the Sim's geolocation is correct
   
   assertThat(foundGeoLocation,equalToIgnoringCase(geoLocation));
   
   // Verify that the Sim's generation is correct - first match DB type with generation input
   
   if (is_umts == 1 && is_gsm ==1) {foundGeneration = "2g3g";}
   else if (is_umts == 0 && is_gsm == 1) {foundGeneration = "2g";}
   else if (is_umts == 1 && is_gsm == 0) {foundGeneration = "3g";}
   else {foundGeneration = "not found";}
   
   
   assertThat(foundGeneration, equalToIgnoringCase(generation));
   
  }
  @BeforeMethod
  public void beforeMethod() {
	  
	  
  }

  @AfterMethod
  public void afterMethod() {
	  teles.deleteSimFromSimUnit(boardnumber, offset, imsi, generation);
	  
	 
	  
  }
  
  @BeforeClass
  public void beforeClass()  {
	  jdbc = new JDBCUtil();
	  teles = new TelesHttpInterface();
	  
	  teles.deleteAllBoards();
	  
	 
  }
  

  @DataProvider
  public Iterator<Object[]> addSimTestData() {
	Random random = new Random();  
	List<String> generationList = new ArrayList<String>();
	// This is the list of sim generation types, for each one there will be a test iteration
	
	generationList.add("2g");
	generationList.add("2g3g");
	generationList.add("3g");
	List<Object[]> dataToBeReturned = new ArrayList<Object[]>();
	for (String generation : generationList) {
		
		// Per each iteration, a random Imsi, board, offset and Country Code are generated
		
		String randImsi = RandomStringUtils.randomNumeric(12);
		String randCountryCode = Integer.toString(random.nextInt((699 - 625) + 1) + 625);
		String randOffset = Integer.toString(random.nextInt(30));
		String randBoardNumber = Integer.toString(random.nextInt(10));			
		Object [] sim = new Object [] {randBoardNumber, randOffset, randCountryCode, randCountryCode + randImsi, generation};
		dataToBeReturned.add(sim);
			
						
	}
	
	
   return dataToBeReturned.iterator();
      			
      
    };
  }
  

  
  

