package gate_keeper;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import cloudProtocol.GateKeeperInterface;
import cloudProtocol.MessageTypes.Challenge;
import cloudProtocol.MessageTypes.Session;

public class Echo {
	
	
	String plugId=BeforeAndAfterGateKeeperTests.plugid;
	int countryCode = 425;
	byte [] challengeResponse;
	byte [] sessionId;
	GateKeeperInterface gk = new GateKeeperInterface();	


  @Test
  public void echoReqestValid() {
	   			
 	 assertThat(gk.echoRequest(sessionId), containsString("echo request was received"));
		 				 		
		 		
  }
  @BeforeClass
  public void beforeClass() {
	
	  
	  

	
    // Send a challenge request for the plug
    
		
	Challenge challenge = gk.getChallengeForPlug(plugId);	
	challengeResponse = challenge.getChallengeResponse();			
	Session session= gk.createSession(plugId, 425,"1","1",challengeResponse);	
	sessionId = session.getSessionId();	
	String status = session.getStatus();		
	int sessionInt = session.getSessionIdAsInt();						
	System.out.println(status);				
	System.out.println(sessionInt);		
	  
  }

  @AfterClass
  public void afterClass() {
  }

}
