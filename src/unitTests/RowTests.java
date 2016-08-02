package unitTests;

import static org.junit.Assert.*;

import org.junit.Test;

import entities.Cell.CellStatus;
import entities.Row;

public class RowTests {

	@Test
	public void testRowCreation() {
		int rowSize = 20;
		int[] numbers = new int[2];
		numbers[0] = 5;
		numbers[1] = 6;

		Row testRow = new Row(rowSize, numbers);
		assertTrue("Testing correct row size", testRow.rowSize() == rowSize);
		assertEquals("Testing spacesNeeded", 12, testRow.SpacesNeeded());

		Row testRowCopy = new Row(testRow);
		assertTrue("Testing correct row size", testRow.rowSize() == rowSize);
		assertFalse("Testing for different objects", testRow == testRowCopy);
		assertFalse("Testing for different cell array objects", testRow.getCells() == testRowCopy.getCells());
		assertFalse("Testing for different number array objects", testRow.getNumbers() == testRowCopy.getNumbers());
		assertEquals("Testing spacesNeeded", 12, testRowCopy.SpacesNeeded());

	}

	@Test
	public void testIsEqual() {
		int rowSize = 20;
		int[] numbers = new int[2];
		numbers[0] = 5;
		numbers[1] = 6;
		Row testRow = new Row(rowSize, numbers);
		Row testRowCopy = new Row(testRow);
		Row testBlankRow = new Row(rowSize);
		Row testBlankRowTwo = new Row(rowSize);

		assertTrue("Testing copy", Row.isEqual(testRow, testRowCopy));
		assertFalse("Testing blank row not equal to row with numbers", Row.isEqual(testRow, testBlankRow));
		assertTrue("Testing two rows without numbers are equal", Row.isEqual(testBlankRow, testBlankRowTwo));

		testRow.setCellStatus(0, CellStatus.MARKED);
		assertFalse("Testing with changed cell status", Row.isEqual(testRow, testRowCopy));
		testRowCopy.setCellStatus(0, CellStatus.MARKED);
		assertTrue("Testing with additional changed cell status", Row.isEqual(testRow, testRowCopy));
	}

	@Test
	public void testSubRow() {

		Row testRow = new Row(20);
		testRow.setCellStatus(2, CellStatus.MARKED);
		testRow.setCellStatus(5, CellStatus.UNMARKED);
		Row subRow = testRow.getSubRow(3, 10, testRow.getNumbers());

		assertNotNull(subRow);
		assertTrue("Testing correct subrow size", subRow.rowSize() == 8);
		assertTrue("Testing set cell status carries over", subRow.getCellStatus(2) == CellStatus.UNMARKED);
		assertTrue("Testing default cell status carries over", subRow.getCellStatus(0) == CellStatus.UNSOLVED);
	}

}
