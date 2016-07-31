package solver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;

import entities.Row;
import entities.Cell.CellStatus;
import interfaces.RowSolverInterface;

public class RowSolverHashTable implements RowSolverInterface {

	private class RowWithUnsolvability {
		public Row row;

		public RowWithUnsolvability(Row row) {
			this.row = row;
		}
	}

	public static final int CELL_HASH_POWER = 4;

	private Hashtable<String, RowWithUnsolvability> rowTable;

	public RowSolverHashTable() {
		rowTable = new Hashtable<String, RowWithUnsolvability>();
	}

	/////////////////////////////
	// RowSolverInterface methods
	/////////////////////////////

	public Row SolveRow(Row unsolvedRow) {

		// Look up previous solved rows
		String rowKey = rowToHashKey(unsolvedRow);
		RowWithUnsolvability solvedRowObject = rowTable.get(rowKey);
		if (solvedRowObject != null) {
			return solvedRowObject.row;
		} else {
			solvedRowObject = new RowWithUnsolvability(new Row(unsolvedRow.rowSize()));
		}

		// If we have an exact fit of numbers and open spaces, make sure we can
		// add it and then return that row
		int spacesNeeded = unsolvedRow.SpacesNeeded();
		if (spacesNeeded > unsolvedRow.rowSize()) {
			return null;
		} else if (unsolvedRow.rowSize() == spacesNeeded) {
			return solveExactSizedRow(unsolvedRow);
		} else if (unsolvedRow.NumbersCount() == 1) {
			return solveNonExactRowOneNumber(unsolvedRow);
		} else {
			// Solve subproblem of taking the first number and then solving
			// second through last number subproblem
			int firstNumber = unsolvedRow.getNumbers()[0];
			Row fullSolvedRow = null; // row that will contain all possible
										// solutions
			boolean foundGoodSolution = false;

			outerIteration: for (int index = 0; index <= unsolvedRow.rowSize() - spacesNeeded; index++) {
				Row solvedRowIteration = new Row(unsolvedRow.rowSize());

				// Check that we can fit the first number in here before solving
				// subproblems, add in cell statuses as necessary
				for (int i = 0; i < index + firstNumber + 1; i++) {
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

				if (index + firstNumber + 1 >= unsolvedRow.rowSize() - 1) {
					continue outerIteration;
				}

				Row partialRow = unsolvedRow.getSubRow(index + firstNumber + 1, unsolvedRow.rowSize() - 1,
						Arrays.copyOfRange(unsolvedRow.getNumbers(), 1, unsolvedRow.NumbersCount()));

				Row partialSolvedRow = SolveRow(partialRow);

				if (partialSolvedRow != null) {
					for (int i = index + firstNumber + 1; i < unsolvedRow.rowSize(); i++) {
						solvedRowIteration.setCellStatus(i,
								partialSolvedRow.getCellStatus(i - firstNumber - 1 - index));
					}
				} else {
					continue outerIteration;
				}

				foundGoodSolution = true;

				// Add results to solvedRowObject
				if (fullSolvedRow == null) {
					fullSolvedRow = solvedRowIteration;
				} else {
					for (int i = 0; i < solvedRowIteration.rowSize(); i++) {
						if (fullSolvedRow.getCellStatus(i) != solvedRowIteration.getCellStatus(i)) {
							fullSolvedRow.setCellStatus(i, CellStatus.UNSOLVED);
						}
					}
				}
			}

			if (!foundGoodSolution) {
				solvedRowObject.row = null;
			} else {
				solvedRowObject.row = fullSolvedRow;
			}
			rowTable.put(rowKey, solvedRowObject);
			return solvedRowObject.row;
		}

	}

	/////////////////
	// Public methods
	/////////////////

	public boolean tableHasKey(String key) {
		return rowTable.containsKey(key);
	}

	public int getHashTableCount() {
		return rowTable.size();
	}

	public Row solveNonExactRowOneNumber(Row unsolvedRow) {
		int number = unsolvedRow.getNumbers()[0];
		int counter = 0;
		int minPossibleIndex = 0;
		int maxPossibleIndex = unsolvedRow.rowSize() - 1;
		Integer smallestMarkedIndex = null;
		Integer largestMarkedIndex = null;

		String rowKey = rowToHashKey(unsolvedRow);
		RowWithUnsolvability solvedRowObject = new RowWithUnsolvability(new Row(unsolvedRow));

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

	public Row solveExactSizedRow(Row unsolvedRow) {
		// Should be checking before entering here, but a good check to use
		if (unsolvedRow.SpacesNeeded() != unsolvedRow.rowSize()) {
			return null;
		}

		String rowKey = rowToHashKey(unsolvedRow);
		RowWithUnsolvability solvedRowObject = new RowWithUnsolvability(new Row(unsolvedRow.rowSize()));

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

	/////////////////
	// Private methods
	/////////////////

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
