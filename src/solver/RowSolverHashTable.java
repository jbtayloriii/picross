package solver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;

import entities.Row;
import entities.Cell.CellStatus;
import interfaces.RowSolverInterface;

public class RowSolverHashTable implements RowSolverInterface {

	// CLASS: RowWrapper
	// DEFINITION: Wrapper class for row hash table. Instantiated RowWrapper
	//  object with null row represents a found invalid solution. Null RowWrapper
	//  object represents solution not investigated yet.
	private class RowWrapper {
		public Row row;

		public RowWrapper(Row row) {
			this.row = row;
		}
	}

	// Because we have three states, we only need a base 3 number (converted to
	// decimal) to get a unique row
	public static final int CELL_HASH_POWER = 3;

	private Hashtable<String, RowWrapper> rowTable;
	private int hashTableFinds = 0;

	///////////////
	// Constructors
	///////////////

	public RowSolverHashTable() {
		rowTable = new Hashtable<String, RowWrapper>();
	}

	/////////////////////////////
	// RowSolverInterface methods
	/////////////////////////////

	public Row SolveRow(Row unsolvedRow) {
		return SolveRowBranch(unsolvedRow, 1);
	}

	//////////////////////
	// Getters and setters
	//////////////////////

	public int getHashTableFinds() {
		return hashTableFinds;
	}

	public boolean tableHasKey(String key) {
		return rowTable.containsKey(key);
	}

	public int getHashTableCount() {
		return rowTable.size();
	}

	/////////////////
	// Public methods
	/////////////////

	// These methods are all public so that they can be tested with unit tests

	// METHOD: SolveRowBranch
	// DEFINITION: Main branch for attempting to solve a row problem. Will try to pick the simplest of three methods if a solution for this row has not previously been found:
	//  1. If we have exactly as many spaces as the numbers need, see if they fit nicely
	//  2. If we have more than enough space and one number, find all the spots it can fit
	//  3. Otherwise, recursively solve subproblems taking out the first number and shifting around for valid solutions
	// RETURNS: A solved (as much as possible) row
	// THROWS: None
	public Row SolveRowBranch(Row unsolvedRow, int recursionLevel) {
		RowWrapper solvedRowObject = rowTable.get(rowToHashKey(unsolvedRow));
		if (solvedRowObject != null) {
			hashTableFinds++;
			return solvedRowObject.row;
		}

		int spacesNeeded = unsolvedRow.SpacesNeeded();
		if (spacesNeeded > unsolvedRow.rowSize()) {
			return null;
		} else if (unsolvedRow.rowSize() == spacesNeeded) {
			return solveExactSizedRow(unsolvedRow);
		} else if (unsolvedRow.NumbersCount() == 1) {
			return solveNonExactRowOneNumber(unsolvedRow);
		} else {
			return solveRowRecursiveIteration(unsolvedRow, recursionLevel);
		}
	}

	// METHOD: solveRowRecursiveIteration
	// DEFINITION: Recursive solver that will iterate all possible positions for the first number in this row and then solve the subproblems after that number
	// RETURNS: A solved (as much as possible) row
	// THROWS: None
	public Row solveRowRecursiveIteration(Row unsolvedRow, int recursionLevel) {
		int firstNumber = unsolvedRow.getNumbers()[0];
		Row fullSolvedRow = null; // row that will contain all possible
									// solutions
		String rowKey = rowToHashKey(unsolvedRow);
		RowWrapper solvedRowObject = new RowWrapper(new Row(unsolvedRow));
		boolean foundGoodSolution = false;

		// Iterate over each shifted potential first number. We can get an
		// invalid solution (and hit a continue statement) in one of two
		// ways: either the first number's position is invalid with marked
		// or unmarked cells, or the subproblem solved on the right half is
		// invalid

		// If we hit a valid solution, update the full solution over all
		// iterations
		outerIteration: for (int index = 0; index <= unsolvedRow.rowSize() - unsolvedRow.SpacesNeeded(); index++) {
			Row solvedRowIteration = new Row(unsolvedRow.rowSize(), unsolvedRow.getNumbers());
			int partialRowBeginIndex = index + firstNumber + 1;

			// Check that we can fit the first number in here before solving
			// subproblems, add in cell statuses as necessary
			for (int i = 0; i < partialRowBeginIndex; i++) {
				if (i < index || i == index + firstNumber) {
					// Unmarked
					if (unsolvedRow.getCellStatus(i) == CellStatus.MARKED) {
						continue outerIteration;
					} else {
						solvedRowIteration.setCellStatus(i, CellStatus.UNMARKED);
					}
				} else if (i < index + firstNumber) {
					// Marked
					if (unsolvedRow.getCellStatus(i) == CellStatus.UNMARKED) {
						continue outerIteration;
					} else {
						solvedRowIteration.setCellStatus(i, CellStatus.MARKED);
					}
				}
			}

			Row partialRow = unsolvedRow.getSubRow(partialRowBeginIndex, unsolvedRow.rowSize() - 1,
					Arrays.copyOfRange(unsolvedRow.getNumbers(), 1, unsolvedRow.NumbersCount()));

			Row partialSolvedRow = SolveRowBranch(partialRow, recursionLevel + 1);

			if (partialSolvedRow != null) {
				for (int i = partialRowBeginIndex; i < unsolvedRow.rowSize(); i++) {
					solvedRowIteration.setCellStatus(i, partialSolvedRow.getCellStatus(i - partialRowBeginIndex));
				}
			} else {
				continue outerIteration;
			}

			foundGoodSolution = true;

			if (fullSolvedRow == null) {
				fullSolvedRow = solvedRowIteration;
			} else {
				for (int i = 0; i < fullSolvedRow.rowSize(); i++) {
					//If two valid solutions differ, the only possible result is that this space is still unsolved
					if (fullSolvedRow.getCellStatus(i) != solvedRowIteration.getCellStatus(i)) {
						fullSolvedRow.setCellStatus(i, CellStatus.UNSOLVED);
					}
				}
			}
		}

		if (!foundGoodSolution) {
			solvedRowObject.row = null; //since we've initialized fullSolvedRow, we want to save null if there is no solution
		} else {
			solvedRowObject.row = fullSolvedRow;
		}
		rowTable.put(rowKey, solvedRowObject);
		return solvedRowObject.row;
	}

	// METHOD: solveNonExactRowOneNumber
	// DEFINITION: Solver that will look at first (and hopefully only) number in this row and try to iterate it over all possible spots. Will perform a number of likely
	//  inefficient checks to validate solution, but should still run in O(n) time where n = unsolvedRow.RowSize(), with a high multiplier on n.
	// RETURNS: A solved (as much as possible) row
	// THROWS: None
	public Row solveNonExactRowOneNumber(Row unsolvedRow) {
		int number = unsolvedRow.getNumbers()[0];
		int counter = 0;
		int minPossibleIndex = 0;
		int maxPossibleIndex = unsolvedRow.rowSize() - 1;
		Integer smallestMarkedIndex = null;
		Integer largestMarkedIndex = null;

		String rowKey = rowToHashKey(unsolvedRow);
		RowWrapper solvedRowObject = new RowWrapper(new Row(unsolvedRow));

		ArrayList<Integer> alreadyMarkedBucked = new ArrayList<Integer>();

		// get marked things
		for (int i = 0; i < unsolvedRow.rowSize(); i++) {
			CellStatus status = unsolvedRow.getCellStatus(i);
			if (status == CellStatus.MARKED) {
				alreadyMarkedBucked.add(i);
			}
		}

		if (!alreadyMarkedBucked.isEmpty()) {
			Collections.sort(alreadyMarkedBucked);
			smallestMarkedIndex = alreadyMarkedBucked.get(0);
			largestMarkedIndex = alreadyMarkedBucked.get(alreadyMarkedBucked.size() - 1);

			// make sure we can fit number in range
			if (largestMarkedIndex - smallestMarkedIndex > number - 1) {
				solvedRowObject.row = null;
				rowTable.put(rowKey, solvedRowObject);
				return solvedRowObject.row;
			}

			// make sure there are no unmarked cells in between what we have
			for (int i = smallestMarkedIndex + 1; i < largestMarkedIndex; i++) {
				if (unsolvedRow.getCellStatus(i) == CellStatus.UNMARKED) {
					solvedRowObject.row = null;
					rowTable.put(rowKey, solvedRowObject);
					return solvedRowObject.row;
				}
			}

			minPossibleIndex = Math.max(minPossibleIndex, largestMarkedIndex - number + 1);
			maxPossibleIndex = Math.min(maxPossibleIndex, smallestMarkedIndex + number - 1);

		}

		ArrayList<Integer> tempBucket = new ArrayList<Integer>();
		ArrayList<Integer> canFitIndices = new ArrayList<Integer>();
		ArrayList<Integer> cannotFitIndices = new ArrayList<Integer>();
		for (int i = 0; i < unsolvedRow.rowSize(); i++) {
			CellStatus status = unsolvedRow.getCellStatus(i);

			// cells outside possible range are unmarked, cannot be marked
			if (i < minPossibleIndex || i > maxPossibleIndex) {
				if (status == CellStatus.MARKED) {
					solvedRowObject.row = null;
					rowTable.put(rowKey, solvedRowObject);
					return solvedRowObject.row;
				} else {
					solvedRowObject.row.setCellStatus(i, CellStatus.UNMARKED);
				}
			} else {
				// within possible range for being marked
				if (status == CellStatus.MARKED) {
					counter++;
				} else if (status == CellStatus.UNMARKED) {
					if (counter >= number) {
						canFitIndices.addAll(tempBucket);
					} else {
						cannotFitIndices.addAll(tempBucket);
					}
					tempBucket.clear();
					counter = 0;
				} else if (status == CellStatus.UNSOLVED) {
					counter++;
					tempBucket.add(i);
				}
			}
		}

		// Add any extra indices if we finish looping over
		if (!tempBucket.isEmpty()) {
			if (counter >= number) {
				canFitIndices.addAll(tempBucket);
			} else {
				cannotFitIndices.addAll(tempBucket);
			}
		}

		for (Integer index : cannotFitIndices) {
			solvedRowObject.row.setCellStatus(index, CellStatus.UNMARKED);
		}

		// for marked cells, we have to see if a cell fits within the low
		// and high for coloring
		if (!canFitIndices.isEmpty()) {
			Collections.sort(canFitIndices);
			int low = canFitIndices.get(0);
			int high = canFitIndices.get(canFitIndices.size() - 1);
			for (Integer index : canFitIndices) {
				if (index < low + number && index > high - number) {
					solvedRowObject.row.setCellStatus(index, CellStatus.MARKED);
				} else {
					solvedRowObject.row.setCellStatus(index, CellStatus.UNSOLVED);
				}
			}
		}
		rowTable.put(rowKey, solvedRowObject);
		return solvedRowObject.row;
	}

	// METHOD: solveExactSizedRow
	// DEFINITION: Solver that will iterate over row and check that each cell is not incorrectly marked/unmarked (while also marking correctly)
	// RETURNS: A solved (as much as possible) row
	// THROWS: None
	public Row solveExactSizedRow(Row unsolvedRow) {
		// Should be checking before entering here, but a good check to use
		if (unsolvedRow.SpacesNeeded() != unsolvedRow.rowSize()) {
			return null;
		}

		String rowKey = rowToHashKey(unsolvedRow);
		RowWrapper solvedRowObject = new RowWrapper(new Row(unsolvedRow.rowSize(), unsolvedRow.getNumbers()));

		int numberCount = 0;
		int counter = unsolvedRow.getNumbers()[numberCount];
		for (int i = 0; i < unsolvedRow.rowSize(); i++) {
			if (counter > 0) {
				// Should be marked or unsolved
				if (unsolvedRow.getCellStatus(i) == CellStatus.UNMARKED) {
					solvedRowObject.row = null;
					rowTable.put(rowKey, solvedRowObject);
					return null;
				}
				solvedRowObject.row.setCellStatus(i, CellStatus.MARKED);
				counter--;
			} else {
				// Should be unmarked or unsolved
				if (unsolvedRow.getCellStatus(i) == CellStatus.MARKED) {
					solvedRowObject.row = null;
					rowTable.put(rowKey, solvedRowObject);
					return null;
				}
				solvedRowObject.row.setCellStatus(i, CellStatus.UNMARKED);
				counter = unsolvedRow.getNumbers()[++numberCount];
			}
		}

		rowTable.put(rowKey, solvedRowObject);
		return solvedRowObject.row;
	}

	// METHOD: rowToHashKey
	// DEFINITION: Determines a unique key for a row given its size, numbers, and current cell statuses. Cell statuses will be turned into a number using base 3,
	//  where everything else will simply be written and separated by spaces.
	// RETURNS: A unique string key for a given row in the form [rowSize + " " + [list of row numbers separated by spaces] + " " + base 3 value of cell statuses
	// THROWS: Will probably throw an overflow exception if the calculated cell values gets too high. At row size 20 this is not currently a problem.
	public String rowToHashKey(Row row) {
		String rowNumbers = "";
		for (int i = 0; i < row.NumbersCount(); i++) {
			rowNumbers += row.getNumbers()[i] + " ";
		}

		long numberRepresentation = 0;
		long power = 1;
		for (int i = 0; i < row.rowSize(); i++) {
			int cellValue = row.getCellStatus(i).getValue();
			numberRepresentation += (cellValue * power);
			power = power * CELL_HASH_POWER;
		}

		return row.rowSize() + " " + rowNumbers + numberRepresentation;
	}
}
