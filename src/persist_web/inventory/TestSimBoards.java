package persist_web.inventory;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import persist.inventory.SimBoards;
import persist.inventory.SimUnits;

public class TestSimBoards {
	public String output = "";
	public String testName;

	private SimUnits simUnit = new SimUnits();
	private String unitName1 = "QaSimUnit";
	private String unitName2 = "TestingUnit";
	private String unitAddress1 = "212.199.181.170";
	private String unitAddress2 = "212.199.181.300";

	private SimBoards simBoards;
	private String unit = "QaSimUnit";
	private String slot = "1";

	boolean isSuccess = false;

	@BeforeClass
	public void beforeClass() {
		this.simBoards = new SimBoards();
		simUnit.add(unitName1, unitAddress1);
		simUnit.add(unitName2, unitAddress2);
	}

	@BeforeMethod
	public void beforeMethod() {
		simBoards.deleteAll();
	}

	@Test
	public void addSimBoard() {
		output = simBoards.add(unit, slot);

		assertThat(output, containsString("successfully"));
	}

	@Test
	public void addDuplicateSimBoard() {
		simBoards.add(unit, slot);
		output = simBoards.add(unit, slot);

		assertThat(output, containsString("Sim board with this Unit and Slot already exists"));
	}

	@Test
	public void modifySimBoard() {
		String newUnit = "TestingUnit";
		String newSlot = "5";
		simBoards.add(unit, slot);
		output = simBoards.modifyByUnitAndSlot(unit, slot, newUnit, newSlot);
		assertThat(output, containsString("successfully"));
	}

	@Test
	public void deleteSimBoard() {
		simBoards.add(unit, slot);
		output = simBoards.deleteByUnitAndSlot(unit, slot);
		assertThat(output, containsString("successfully"));
	}

	@AfterClass
	public void afterClass() {
		// remove sim unit
		simUnit.deleteAll();
	}

}
