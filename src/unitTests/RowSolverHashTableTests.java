package unitTests;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

import entities.Row;
import entities.Cell.CellStatus;
import solver.RowSolverHashTable;

public class RowSolverHashTableTests {

	@Test
	public void solveExactSizedRowPositive() {
		System.err.println("solveExactSizedRowPositivePositive");

		RowSolverHashTable solver = new RowSolverHashTable();

		// test 1
		Row testRowOne = new Row(20, new int[] { 5, 4, 4, 4 });

		Row testRowOneSolved = solver.solveExactSizedRow(testRowOne);
		printErrTestRow("one", testRowOne.getNumbers(), testRowOne, testRowOneSolved);
		assertEquals(testRowOneSolved.toString(), "XXXXX.XXXX.XXXX.XXXX");
		assertTrue(solver.tableHasKey(solver.rowToHashKey(testRowOne)));

		// test 2
		Row testRowTwo = new Row(13, new int[] { 1, 1, 1, 1, 1, 1, 1 });

		Row testRowTwoSolved = solver.solveExactSizedRow(testRowTwo);
		printErrTestRow("two", testRowTwo.getNumbers(), testRowTwo, testRowTwoSolved);
		assertEquals(testRowTwoSolved.toString(), "X.X.X.X.X.X.X");
		assertTrue(solver.tableHasKey(solver.rowToHashKey(testRowTwo)));

		// test 2
		Row testRowThree = new Row(7, new int[] { 7 });

		Row testRowThreeSolved = solver.solveExactSizedRow(testRowThree);
		printErrTestRow("two", testRowThree.getNumbers(), testRowThree, testRowThreeSolved);
		assertEquals(testRowThreeSolved.toString(), "XXXXXXX");
		assertTrue(solver.tableHasKey(solver.rowToHashKey(testRowThree)));
	}

	@Test
	public void solveExactSizedRowNegative() {
		System.err.println("solveExactSizedRowNegative");

		RowSolverHashTable solver = new RowSolverHashTable();

		// test 1
		Row testRowOne = new Row(19, new int[] { 5, 4, 4, 4 });

		Row testRowOneSolved = solver.solveExactSizedRow(testRowOne);
		printErrTestRow("one", testRowOne.getNumbers(), testRowOne, testRowOneSolved);
		assertNull(testRowOneSolved);

		// test 2
		Row testRowTwo = new Row(21, new int[] { 5, 4, 4, 4 });

		Row testRowTwoSolved = solver.solveExactSizedRow(testRowTwo);
		printErrTestRow("one", testRowTwo.getNumbers(), testRowTwo, testRowTwoSolved);
		System.err.println("Test row two: " + testRowTwoSolved);
		assertNull(testRowTwoSolved);
	}

	@Test
	public void solveNonExactRowOneNumberPositive() {
		System.err.println("solveNonExactRowOneNumberPositive");

		RowSolverHashTable solver = new RowSolverHashTable();

		// test 1
		Row testRowOne = new Row(10, new int[] { 5 });
		testRowOne.setCellStatus(3, CellStatus.UNMARKED);
		testRowOne.setCellStatus(4, CellStatus.MARKED);

		Row testRowOneSolved = solver.solveNonExactRowOneNumber(testRowOne);
		printErrTestRow("one", testRowOne.getNumbers(), testRowOne, testRowOneSolved);
		assertEquals(testRowOneSolved.toString(), "....XXXXX.");
		assertTrue(solver.tableHasKey(solver.rowToHashKey(testRowOne)));

		// test 2
		Row testRowTwo = new Row(10, new int[] { 5 });
		testRowTwo.setCellStatus(3, CellStatus.UNMARKED);
		testRowTwo.setCellStatus(5, CellStatus.MARKED);

		Row testRowTwoSolved = solver.solveNonExactRowOneNumber(testRowTwo);
		printErrTestRow("two", testRowTwo.getNumbers(), testRowTwo, testRowTwoSolved);
		assertEquals(testRowTwoSolved.toString(), "....?XXXX?");
		assertTrue(solver.tableHasKey(solver.rowToHashKey(testRowTwo)));

		// test 3
		Row testRowThree = new Row(10, new int[] { 6 });

		Row testRowThreeSolved = solver.solveNonExactRowOneNumber(testRowThree);
		printErrTestRow("three", testRowThree.getNumbers(), testRowThree, testRowThreeSolved);
		assertEquals(testRowThreeSolved.toString(), "????XX????");
		assertTrue(solver.tableHasKey(solver.rowToHashKey(testRowThree)));

		// test 4
		Row testRowFour = new Row(10, new int[] { 4 });
		testRowFour.setCellStatus(3, CellStatus.MARKED);
		testRowFour.setCellStatus(6, CellStatus.MARKED);

		Row testRowFourSolved = solver.solveNonExactRowOneNumber(testRowFour);
		printErrTestRow("four", testRowFour.getNumbers(), testRowFour, testRowFourSolved);
		assertEquals(testRowFourSolved.toString(), "...XXXX...");
		assertTrue(solver.tableHasKey(solver.rowToHashKey(testRowFour)));

		// test 5
		Row testRowFive = new Row(10, new int[] { 6 });
		testRowFive.setCellStatus(3, CellStatus.MARKED);
		testRowFive.setCellStatus(6, CellStatus.MARKED);

		Row testRowFiveSolved = solver.solveNonExactRowOneNumber(testRowFive);
		printErrTestRow("five", testRowFive.getNumbers(), testRowFive, testRowFiveSolved);
		assertEquals(testRowFiveSolved.toString(), ".??XXXX??.");
		assertTrue(solver.tableHasKey(solver.rowToHashKey(testRowFive)));

		// test 6
		Row testRowSix = new Row(10, new int[] { 5 });
		testRowSix.setCellStatus(5, CellStatus.MARKED);

		Row testRowSixSolved = solver.solveNonExactRowOneNumber(testRowSix);
		printErrTestRow("six", testRowSix.getNumbers(), testRowSix, testRowSixSolved);
		assertEquals(testRowSixSolved.toString(), ".????X????");
		assertTrue(solver.tableHasKey(solver.rowToHashKey(testRowSix)));
	}

	@Test
	public void solveNonExactRowOneNumberNegative() {
		System.err.println("solveNonExactRowOneNumberNegative");

		RowSolverHashTable solver = new RowSolverHashTable();

		// test 1
		Row testRowOne = new Row(10, new int[] { 3 });
		testRowOne.setCellStatus(2, CellStatus.MARKED);
		testRowOne.setCellStatus(3, CellStatus.UNMARKED);
		testRowOne.setCellStatus(4, CellStatus.MARKED);

		Row testRowOneSolved = solver.solveNonExactRowOneNumber(testRowOne);
		printErrTestRow("one", testRowOne.getNumbers(), testRowOne, testRowOneSolved);
		assertNull(testRowOneSolved);
		assertTrue(solver.tableHasKey(solver.rowToHashKey(testRowOne)));

		// test 2
		Row testRowTwo = new Row(10, new int[] { 3 });
		testRowTwo.setCellStatus(3, CellStatus.MARKED);
		testRowTwo.setCellStatus(6, CellStatus.MARKED);

		Row testRowTwoSolved = solver.solveNonExactRowOneNumber(testRowTwo);
		printErrTestRow("two", testRowTwo.getNumbers(), testRowTwo, testRowTwoSolved);
		assertNull(testRowTwoSolved);
		assertTrue(solver.tableHasKey(solver.rowToHashKey(testRowTwo)));
	}

	@Test
	public void solveBlankRows() {
		System.err.println("solveBlankRows");
		
		RowSolverHashTable solver = new RowSolverHashTable();
		
		//test 1
		Row testRowOne = new Row(20, new int[] { 4, 3, 2, 1, 1, 1 });
		Row testRowOneSolved = solver.SolveRow(testRowOne);
		printErrTestRow("one", testRowOne.getNumbers(), testRowOne, testRowOneSolved);
		assertEquals(testRowOneSolved.toString(), "???X????????????????");
		assertTrue(solver.tableHasKey(solver.rowToHashKey(testRowOne)));
		
		//test 2
		Row testRowTwo = new Row(20, new int[] { 1, 3, 2, 2, 5 });
		Row testRowTwoSolved = solver.SolveRow(testRowTwo);
		printErrTestRow("two", testRowTwo.getNumbers(), testRowTwo, testRowTwoSolved);
		assertEquals(testRowTwoSolved.toString(), "???????????????XX???");
		assertTrue(solver.tableHasKey(solver.rowToHashKey(testRowTwo)));

	}

	private void printErrTestRow(String testNumber, int[] numbers, Row testRow, Row testRowSolved) {
		System.err.println(String.format("%-24s %s %s", "Test row " + testNumber + ":",
				Arrays.toString(testRow.getNumbers()), testRow));
		System.err.println(String.format("%-24s %s %s", "Test row " + testNumber + " solved:",
				Arrays.toString(testRow.getNumbers()), testRowSolved));
		System.err.println();
	}

}
