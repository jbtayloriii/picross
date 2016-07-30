package solver;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import entities.Board;
import entities.Row;
import entities.Cell.CellStatus;
import interfaces.BoardSolverInterface;

public class BoardSolverQueue implements BoardSolverInterface {

	private class rowQueueObject {
		public boolean isRow; // determine if we are using a row or column
		public int rowNumber;

		public rowQueueObject(boolean isRow, int rowNumber) {
			this.isRow = isRow;
			this.rowNumber = rowNumber;
		}
	}

	@Override
	public Board SolveBoard(Board board) {

		RowSolverHashTable rowSolver = new RowSolverHashTable();
		Queue<rowQueueObject> rowQueue = new LinkedList<rowQueueObject>();
		int enqueueingCount = 0;

		// Initially add all rows and columns
		for (int i = 0; i < board.getRowCount(); i++) {
			rowQueue.add(new rowQueueObject(true, i));
			enqueueingCount++;
		}

		for (int i = 0; i < board.getColumnCount(); i++) {
			rowQueue.add(new rowQueueObject(false, i));
			enqueueingCount++;

		}

		while (!rowQueue.isEmpty()) {
			rowQueueObject nextRowQueueObject = rowQueue.remove();
			System.err.println();
			// System.err.println("Queue size:" + rowQueue.size());
			System.err.println("Pulled " + (nextRowQueueObject.isRow ? "row " : "column ")
					+ nextRowQueueObject.rowNumber + " from queue");
			Row nextRow;
			if (nextRowQueueObject.isRow) {
				nextRow = board.getRow(nextRowQueueObject.rowNumber);
			} else {
				nextRow = board.getColumn(nextRowQueueObject.rowNumber);
			}

			System.err.println("Numbers: " + Arrays.toString(nextRow.getNumbers()));

			try {
				System.err.println("Next row:        " + nextRow);

				Row nextSolvedRow = rowSolver.SolveRow(nextRow);
				System.err.println("Next solved row: " + nextSolvedRow);

				if (nextSolvedRow != null && !Row.isEqual(nextRow, nextSolvedRow)) {
					// Add new rows/columns that have changed from this
					// column/row and continue solving
					for (int i = 0; i < nextSolvedRow.rowSize(); i++) {
						if (nextRow.getCellStatus(i) != nextSolvedRow.getCellStatus(i)) {
							rowQueue.add(new rowQueueObject(!nextRowQueueObject.isRow, i));
							enqueueingCount++;

							// Update status
							if (nextRowQueueObject.isRow) {
								board.getRow(nextRowQueueObject.rowNumber).setCellStatus(i,
										nextSolvedRow.getCellStatus(i));
							} else {
								board.getColumn(nextRowQueueObject.rowNumber).setCellStatus(i,
										nextSolvedRow.getCellStatus(i));
							}

							// Debug
							System.err.println("Enqueuing " + (!nextRowQueueObject.isRow ? "row " : "column ") + i
									+ ", enqueue count: " + enqueueingCount);
						}
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// test
		System.out.println("Enqueueings:" + enqueueingCount);
		System.out.println("Hash table size:" + rowSolver.getHashTableCount());

		return board;
	}

}
