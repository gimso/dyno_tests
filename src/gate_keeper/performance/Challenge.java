package gate_keeper.performance;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import cloudProtocol.GateKeeperInterface;

public class Challenge {
  @Test(dataProvider = "dp")
  public void TestMultipleChallengeRequests(String plugid) {
	  GateKeeperInterface gk = new GateKeeperInterface();
	  gk.getChallengeForPlug(plugid);
  }

  @DataProvider (parallel = true)
  public Object[][] dp() {
    return new Object[][] {
      new Object[] {"000010002185"},
      new Object[] {"000010002185"},
      new Object[] {"000010002185"},
    };
  }


}
