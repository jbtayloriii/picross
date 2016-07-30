package entities;

import java.util.Arrays;

import entities.Cell.CellStatus;

public class Row {

	public enum RowStatus {
		UNSOLVED, SOLVED
	}

	private Cell[] cells;
	private int[] numbers;

	// private rowStatus status;
	private boolean updated;

	///////////////
	// Constructors
	///////////////

	public Row(Row row) {
		// Make deep copies, cannot simply use other constructor

		if (row.cells != null) {
			this.cells = new Cell[row.getCells().length];
			for (int i = 0; i < this.cells.length; i++) {
				this.cells[i] = new Cell(row.cells[i]);
			}
		}

		if (row.getNumbers() != null) {
			this.numbers = new int[row.getNumbers().length];
			for (int i = 0; i < this.numbers.length; i++) {
				this.numbers[i] = row.numbers[i];
			}
		}
	}

	public Row(Cell[] cells, int[] numbers) {
		this.cells = cells;
		this.numbers = numbers;
	}

	public Row(int size, int[] numbers) {
		this(size);
		this.numbers = numbers;
	}

	public Row(int size) {
		super();
		cells = new Cell[size];
		for (int i = 0; i < cells.length; i++) {
			cells[i] = new Cell();
		}
	}

	//////////////////////
	// Getters and setters
	//////////////////////

	public boolean isUpdated() {
		return updated;
	}

	public void setUpdated(boolean updated) {
		this.updated = updated;
	}

	public Cell[] getCells() {
		return cells;
	}

	public int rowSize() {
		return cells.length;
	}

	public int[] getNumbers() {
		return numbers;
	}

	public int NumbersCount() {
		return numbers.length;
	}

	public void setCellStatus(int cellIndex, CellStatus newStatus) {
		cells[cellIndex].setStatus(newStatus);
	}

	public CellStatus getCellStatus(int cellIndex) {
		return cells[cellIndex].getStatus();
	}

	////////////////
	// Public Methods
	////////////////

	// METHOD: getSubRow
	// DEFINITION: Creates a row of size (end - beginning) using this row.
	// Beginning and end are inclusive cell indices
	// RETURNS: A subrow created from this row
	// THROWS: IndexOutOfBoundsException: If beginning is less than 0, if end is
	// greater than the last cell's index, or if end is less than or equal to
	// beginning
	public Row getSubRow(int beginning, int end, int[] numbers) {
		if (beginning < 0 || end >= cells.length || end <= beginning) {
			throw new IndexOutOfBoundsException(
					"Beginning (" + beginning + ") or end (" + end + ") out of bounds for row size " + cells.length);
		}
		Cell[] newCells = new Cell[end - beginning + 1];
		for (int i = 0; i < newCells.length; i++) {
			newCells[i] = this.cells[i + beginning];
		}

		return new Row(newCells, numbers);
	}

	// METHOD: toString
	// DEFINITION: Returns a string representation of this row. Concatenates
	// Cell's toString()
	// RETURNS: The smallest number of cells that can contain this row's set of
	// numbers. For example, 2 4 3 will return 2 + 1 + 4 + 1 + 3 = 11
	// THROWS: None
	public int SpacesNeeded() {
		if (numbers == null || numbers.length == 0) {
			return 0;
		}
		int count = numbers.length - 1; // 1 space between each number
		for (int i = 0; i < numbers.length; i++) {
			count = count + numbers[i];
		}

		return count;
	}

	// METHOD: toString
	// DEFINITION: Returns a string representation of this row. Concatenates
	// cells' toString().
	// RETURNS: A string representation of this row
	// THROWS: None

	@Override
	public String toString() {
		char[] charArray = new char[cells.length];
		for (int i = 0; i < cells.length; i++) {
			charArray[i] = cells[i].toString().charAt(0);
		}
		return new String(charArray);
	}

	////////////////
	// Static Methods
	////////////////
	public static boolean isEqual(Row firstRow, Row secondRow) {
		if (firstRow.rowSize() != secondRow.rowSize()) {
			return false;
		}

		if (!Arrays.equals(firstRow.numbers, secondRow.numbers)) {
			return false;
		}

		for (int i = 0; i < firstRow.rowSize(); i++) {
			if (firstRow.getCellStatus(i) != secondRow.getCellStatus(i)) {
				return false;
			}
		}

		return true;
	}
}
