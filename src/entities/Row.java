package entities;

import java.util.Arrays;

import entities.Cell.CellStatus;

public class Row {
	private Cell[] cells;
	private int[] numbers;

	///////////////
	// Constructors
	///////////////

	// Makes deep copy
	public Row(Row row) {
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
		cells = new Cell[size];
		for (int i = 0; i < cells.length; i++) {
			cells[i] = new Cell();
		}
	}

	//////////////////////
	// Getters and setters
	//////////////////////

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
	// DEFINITION: Creates a row of size (end - beginning + 1) using this row.
	// Beginning and end are inclusive cell indices
	// RETURNS: A subrow created from this row
	// THROWS: IndexOutOfBoundsException: If beginning is less than 0, if end is
	// greater than the last cell's index, or if end is less than beginning
	public Row getSubRow(int beginning, int end, int[] numbers) {
		if (beginning < 0 || end >= cells.length || end < beginning) {
			throw new IndexOutOfBoundsException(
					"Beginning (" + beginning + ") or end (" + end + ") out of bounds for row of size " + cells.length);
		}

		Cell[] newCells = new Cell[end - beginning + 1];
		for (int i = 0; i < newCells.length; i++) {
			newCells[i] = this.cells[i + beginning];
		}
		return new Row(newCells, numbers);
	}

	// METHOD: SpacesNeeded
	// DEFINITION: Calculates the smallest number of cells needed for this row's
	// set of numbers with a single space between each number
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
	// cells' toString() value.
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
		if (firstRow == null && secondRow == null) {
			return true;
		}

		if (firstRow == null || secondRow == null) {
			return false;
		}

		if (firstRow.rowSize() != secondRow.rowSize()) {
			return false;
		}

		if (!Arrays.equals(firstRow.numbers, secondRow.numbers)) {
			return false;
		}

		for (int i = 0; i < firstRow.rowSize(); i++) {
			if (firstRow.getCellStatus(i) != secondRow.getCellStatus(i))
				return false;
		}
		return true;
	}
}
