package solver;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import entities.Board;
import entities.Row;
import interfaces.BoardSolverInterface;

public class BoardSolverQueue implements BoardSolverInterface {

	// CLASS: rowQueueObject
	// DEFINITION: Object representation of next row/column to solve for use in
	// queue
	private class rowQueueObject {
		public boolean isRow; // is a row or column?
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
		int dequeueCount = 0;

		// Initially add all rows and columns
		for (int i = 0; i < board.getRowCount(); i++) {
			rowQueue.add(new rowQueueObject(true, i));
			enqueueingCount++;
		}
		for (int i = 0; i < board.getColumnCount(); i++) {
			rowQueue.add(new rowQueueObject(false, i));
			enqueueingCount++;

		}

		// Dequeue until we are out of potential rows to solve. Relies on never
		// hitting infinite loop (we should only queue rows that have made
		// forward progress)
		while (!rowQueue.isEmpty()) {
			rowQueueObject nextRowQueueObject = rowQueue.remove();

			Row nextRow;
			if (nextRowQueueObject.isRow) {
				nextRow = board.getRow(nextRowQueueObject.rowNumber);
			} else {
				nextRow = board.getColumn(nextRowQueueObject.rowNumber);
			}

			System.err.println();
			System.err.println("Queue size:" + rowQueue.size());
			System.err.println("Dequeue step: " + ++dequeueCount);
			System.err.println("Pulled " + (nextRowQueueObject.isRow ? "row " : "column ")
					+ nextRowQueueObject.rowNumber + " from queue");
			System.err.println(
					"Numbers: " + Arrays.toString(nextRow.getNumbers()) + ", spaceNeeded: " + nextRow.SpacesNeeded());

			try {
				Row nextSolvedRow = rowSolver.SolveRow(nextRow);

				System.err.println("Next row:        " + nextRow);
				System.err.println("Next solved row: " + nextSolvedRow);

				// Queue rows/columns that have changed from this
				// column/row if we have made progress
				if (nextSolvedRow != null && !Row.isEqual(nextRow, nextSolvedRow)) {
					for (int i = 0; i < nextSolvedRow.rowSize(); i++) {
						if (nextRow.getCellStatus(i) != nextSolvedRow.getCellStatus(i)) {
							rowQueue.add(new rowQueueObject(!nextRowQueueObject.isRow, i));
							enqueueingCount++;

							// Update status on board
							if (nextRowQueueObject.isRow) {
								board.getRow(nextRowQueueObject.rowNumber).setCellStatus(i,
										nextSolvedRow.getCellStatus(i));
							} else {
								board.getColumn(nextRowQueueObject.rowNumber).setCellStatus(i,
										nextSolvedRow.getCellStatus(i));
							}

							System.err.println("Enqueuing " + (!nextRowQueueObject.isRow ? "row " : "column ") + i
									+ ", enqueue count: " + enqueueingCount);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println("Enqueueings:" + enqueueingCount);
		System.out.println("Hash table size:" + rowSolver.getHashTableCount());
		System.out.println("Hash table hits:" + rowSolver.getHashTableFinds());
		return board;
	}
}
