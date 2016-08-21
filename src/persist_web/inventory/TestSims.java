package persist_web.inventory;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import persist.inventory.Apns;
import persist.inventory.SimBoards;
import persist.inventory.SimUnits;
import persist.inventory.Sims;

/**
 * 
 * @author Dana
 *
 */
public class TestSims {
	
	Sims sims;
	SimUnits simUnit;
	SimBoards simBoard;
	Apns apns;
	
	String simUnitName1 = "SimUnitOne";
	String simUnitAddress1 = "212.199.181.170";
	String simUnitName2 = "SimUnitTwo";
	String simUnitAddress2 = "212.199.181.300";

	String apnName = "TestApn";
	String apnApn = "internet";

	String simName = "SimUnitOne:1:3";
	String group = "Unassigned";
	String board = "SimUnitOne:1";
	String offset = "3";
	String mncLength = "2";
	String balance = "95";
	String paytype = "Prepaid";
	Boolean allowed = false;
	String number = "032444204";
	String incomingNumber = "039725501";
	String outgoingNumber = "039725522";
	String simGroupApnOverride = apnName;
	String roamingModeOverride = "Allow Roaming";
	String comments = "aaa";
	
	String newBoard = "SimUnitTwo:5";
	String newOffset = "0";
	String newMncLength = "3";
	String newBalance = "70";
	String newPaytype = "Postpaid";
	String newNumber = "97232444204";
	String newIncomingNumber = "97239725501";
	String newOutgoingNumber = "97239725522";
	String newRoamingModeOverride = "Prohibit Roaming";
	String newComments = "sim modifier";
	
	public String output = "";

	@BeforeClass
	public void beforeClass() {

		sims = new Sims();
		simUnit = new SimUnits();
		simBoard = new SimBoards();
		apns = new Apns();
		
		// add 2 sim units (for sim board)
		simUnit.add(simUnitName1, simUnitAddress1);
		simUnit.add(simUnitName2, simUnitAddress2);

		// add 2 sim boards (for sim)
		simBoard.add(simUnitName1, "1");
		simBoard.add(simUnitName2, "5");

		// add APN
		apns.add(apnName, apnApn);
	}

	@BeforeMethod
	public void beforeMethod() {
		sims.deleteAll();
	}

	@Test
	public void addSimUsingDefaultValues() {
		output = sims.add(board, offset);
		assertThat(output, containsString("successfully"));
	}

	@Test
	public void addSimUsingAllValues() {
		output = sims.add(group, board, offset, mncLength, balance, paytype, allowed, number, incomingNumber,
				outgoingNumber, simGroupApnOverride, roamingModeOverride, comments);
		assertThat(output, containsString("successfully"));
	}

	@Test
	public void addDuplicateSim() {
		sims.add(board, offset);
		output = sims.add(board, offset);
		assertThat(output, containsString("already exists"));
	}

	@Test
	public void addSimUsingInvalidBalanceValue() {
		// add invalid balance value: 101
		output = sims.add(group, board, offset, null, "101", null, null, null, null, null, null, null, null);
		assertThat(output, containsString("less than or equal to 100"));
	}

	@Test
	public void addSimUsingInvalidNumberValue() {
		// add invalid number value: a
		output = sims.add(group, board, offset, null, null, null, null, "a", null, null, null, null, null);
		assertThat(output, containsString("contain only numeric characters"));
	}

	@Test
	public void addSimUsingInvalidIncomingNumberValue() {
		// add invalid incoming number value: a
		output = sims.add(group, board, offset, null, null, null, null, null, "a", null, null, null, null);
		assertThat(output, containsString("contain only numeric characters"));
	}

	@Test
	public void addSimUsingInvalidOutgoingNumberValue() {
		// add invalid outgoing number value: a
		output = sims.add(group, board, offset, null, null, null, null, null, null, "a", null, null, null);
		assertThat(output, containsString("contain only numeric characters"));
	}

	@Test
	public void modifySimUsingDefaultValues() {
		sims.add(board, offset);
		output = sims.modifyByBoardAndOffset(board, offset, newBoard, newOffset, group, null, null, null, null, null,
				null, null, null, null, null);
		assertThat(output, containsString("successfully"));
	}

	@Test
	public void modifySimUsingAllValues() {
		sims.add(group, board, offset, mncLength, balance, paytype, allowed, number, incomingNumber, outgoingNumber,
				simGroupApnOverride, roamingModeOverride, comments);

		output = sims.modifyByBoardAndOffset(board, offset, newBoard, newOffset, group, newMncLength, newBalance,
				newPaytype, null, newNumber, newIncomingNumber, newOutgoingNumber, simGroupApnOverride,
				newRoamingModeOverride, newComments);
		assertThat(output, containsString("successfully"));
	}

	@Test
	public void deleteSim() {
		sims.add(board, offset);	
		output = sims.deleteByBoardAndOffset(board, offset);
		assertThat(output, containsString("successfully"));
	}

	@AfterClass
	public void afterClass() {
		// delete 2 sim boards
		simBoard.deleteAll();
		// delete 2 sim units	
		simUnit.deleteAll();
		// delete apn		
		apns.deleteAll();
		
	}
}
