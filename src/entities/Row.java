package entities;

import entities.Cell.cellStatus;

public class Row {
	
	public enum rowStatus {
		unsolved, solved
	}
	private Cell[] cells;
	private int[] numbers;
	
	private rowStatus status;
	private boolean updated;
	
	
	//Constructor
	public Row(Cell[] cells, int[] numbers) {
		super();
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
	
	//Public methods
	public boolean isUpdated() {
		return updated;
	}
	
	public void setUpdated(boolean updated) {
		this.updated = updated;
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
	
	public void setCellStatus(int cellIndex, cellStatus newStatus) {
		cells[cellIndex].setStatus(newStatus);
	}
	
	public cellStatus getCellStatus(int cellIndex) {
		return cells[cellIndex].getStatus();
	}
	
	public Row getSubRow(int beginning, int end, int[] numbers) {
		if (beginning < 0 || end >= cells.length) {
			throw new IndexOutOfBoundsException();
		}
		Cell[] newCells = new Cell[end - beginning];
		for (int i = 0; i < newCells.length; i++) {
			newCells[i] = this.cells[i + beginning];
		}
		
		return new Row(newCells, numbers);
	}
	
	public int SpacesNeeded() {
		//returns the minimum number of cells needed for this set of numbers.
		if(numbers == null) {
			return 0;
		}
		int count = numbers.length - 1; // 1 space between each number
		for (int i = 0; i < numbers.length; i++) {
			count = count + numbers[i];
		}
		
		return count;
	}
	
	public Cell[] toCells() {
		return null;
	}
	
	public String toString() {
		char[] charArray = new char[cells.length];
		for (int i = 0; i < cells.length; i++) {
			charArray[i] = cells[i].toString().charAt(0);
		}
		return charArray.toString();
	}
}
