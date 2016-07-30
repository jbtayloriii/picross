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
		if (unsolvedRow.rowSize() == spacesNeeded) {

			int numberCount = 0;
			int counter = unsolvedRow.getNumbers()[numberCount];
			for (int i = 0; i < spacesNeeded; i++) {
				if (counter > 0) {
					// Should be marked
					if (unsolvedRow.getCellStatus(i) == CellStatus.UNMARKED) {
						solvedRowObject.row = null;
						rowTable.put(rowKey, solvedRowObject);
						return null;
					}
					solvedRowObject.row.setCellStatus(i, CellStatus.MARKED);
					counter--;
				} else {
					// Should be unmarked
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

		if (unsolvedRow.NumbersCount() == 1) {
			// If we only have one number, we can solve this problem by
			// iterating over the cells
			int number = unsolvedRow.getNumbers()[0];
			int counter = 0;
			ArrayList<Integer> tempBucket = new ArrayList<Integer>();
			ArrayList<Integer> canFitIndices = new ArrayList<Integer>();
			ArrayList<Integer> cannotFitIndices = new ArrayList<Integer>();
			for (int i = 0; i < unsolvedRow.rowSize(); i++) {
				CellStatus status = unsolvedRow.getCellStatus(i);
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

		} else {
			// Solve subproblem of taking the first number and then solving
			// second through last number subproblem
			solvedRowObject.row = null; // Start without a row in case we can't
										// solve any problems
			int firstNumber = unsolvedRow.getNumbers()[0];
			Row fullSolvedRow = null;
			boolean foundGoodSolution = false;

			outerIteration: for (int index = 0; index < unsolvedRow.rowSize() - spacesNeeded; index++) {
				Row solvedRowIteration = new Row(unsolvedRow.rowSize());

				// Check that we can fit the first number in here before solving
				// subproblems
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

				// Solve subproblem now and attempt to add on
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

	public int getHashTableCount() {
		return rowTable.size();
	}

	/////////////////
	// Private methods
	/////////////////

	private String rowToHashKey(Row row) {
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
