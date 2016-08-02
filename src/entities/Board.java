package entities;

public class Board {

	private int[][] rowNumbers; // first array is row index, second array is
								// list of numbers
	private int[][] columnNumbers;
	private Cell[][] board; // board[1][2] is row 1, column 2
	private int columns;
	private int rows;

	///////////////
	// Constructors
	///////////////

	public Board(int rows, int columns, int[][] rowNumbers, int[][] columnNumbers) {
		this.rowNumbers = rowNumbers;
		this.columnNumbers = columnNumbers;
		board = new Cell[rows][columns];
		for (int i = 0; i < this.rowNumbers.length; i++) {
			for (int j = 0; j < this.columnNumbers.length; j++) {
				board[i][j] = new Cell();
			}
		}
		this.columns = columns;
		this.rows = rows;
	}

	//////////////////////
	// Getters and setters
	//////////////////////

	public int getRowCount() {
		return rows;
	}

	public int getColumnCount() {
		return columns;
	}

	public int[][] getColumnNumbers() {
		return columnNumbers;
	}

	public int[][] getRowNumbers() {
		return rowNumbers;
	}

	public Row getRow(int rowIndex) {
		return new Row(board[rowIndex], rowNumbers[rowIndex]);
	}

	public Row getColumn(int columnIndex) {
		Cell[] retColumn = new Cell[board.length];
		for (int i = 0; i < board.length; i++) {
			retColumn[i] = board[i][columnIndex];
		}
		return new Row(retColumn, columnNumbers[columnIndex]);
	}

	////////////////
	// Public Methods
	////////////////

	// METHODs: replaceRow and replaceColumn
	// DEFINITION: Replaces a given row/column in this board with a new
	// row/column.
	// RETURNS: VOID
	// THROWS: IllegalArgumentException: If the new row/column is not the same
	// size as the existing row/column. Passing in a smaller row/column will not
	// overwrite a partial row/column

	public void replaceRow(int rowIndex, Row newRow) throws IllegalArgumentException {
		if (newRow.rowSize() != columns) {
			throw new IllegalArgumentException("The row you're trying to replace has a different number of cells");
		}
		board[rowIndex] = newRow.getCells();
	}

	public void replaceColumn(int columnIndex, Row newColumn) throws IllegalArgumentException {
		if (newColumn.rowSize() != rows) {
			throw new IllegalArgumentException("The column you're trying to replace has a different number of cells");
		}
		Cell[] columnArray = newColumn.getCells();
		for (int i = 0; i < columnArray.length; i++) {
			board[i][columnIndex] = columnArray[i];
		}
	}

	// METHOD: toString
	// DEFINITION: Returns a string representation of this board. Rows will use
	// their toString() value and will be broken by newlines.
	// RETURNS: A string representation of this board
	// THROWS: None

	@Override
	public String toString() {
		String returnedString = "";
		for (int i = 0; i < rows; i++) {
			Row nextRow = getRow(i);
			returnedString += nextRow.toString() + "\n";
		}
		return returnedString;
	}
}
