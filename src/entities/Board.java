package entities;

public class Board {
	
	
	private int[][] rowNumbers; // first array is which row, second array is array of numbers
	private int[][] columnNumbers;
	private Cell[][] board;  //board[1][2] is row 1, column 2
	private int columns;
	private int rows;

	public Board (int rows, int columns, int[][] rowNumbers, int[][] columnNumbers) {
		this.rowNumbers = rowNumbers;
		this.columnNumbers = columnNumbers;
		board = new Cell[rows][columns];
		this.columns = columns;
		this.rows = rows;
	}
	
	public int getRowCount() {
		return rows;
	}
	
	public int getColumnCount() {
		return columns;
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
	
	public void replaceRow(int rowIndex, Row newRow) throws Exception {
		if(newRow.rowSize() != columns) {
			throw new Exception("The row you're trying to replace has a different size");
		}
		board[rowIndex] = newRow.toCells();
		
	}
	
	public void replaceColumn(int columnIndex, Row newColumn) throws Exception {
		if(newColumn.rowSize() != rows) {
			throw new Exception ("The column you're trying to replace has a different size");
		}
		Cell[] columnArray = newColumn.toCells();
		for (int i = 0; i < columnArray.length; i++) {
			board[i][columnIndex] = columnArray[i];
		}
	}
}
