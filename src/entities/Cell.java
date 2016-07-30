package entities;

public class Cell {

	public enum CellStatus {
		UNSOLVED(0), MARKED(1), UNMARKED(2), BOTH(3);

		private int value;

		CellStatus(int value) {
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}

		public static CellStatus fromInt(int i) {
			for (CellStatus b : CellStatus.values()) {
				if (b.getValue() == i) {
					return b;
				}
			}
			return null;
		}
	}

	private CellStatus status;

	///////////////
	// Constructors
	///////////////

	public Cell(Cell cell) {
		this.status = cell.status;
	}

	public Cell() {
		this.status = CellStatus.UNSOLVED;
	}

	//////////////////////
	// Getters and setters
	//////////////////////

	public CellStatus getStatus() {
		return status;
	}

	public void setStatus(CellStatus status) {
		this.status = status;
	}

	////////////////
	// Public Methods
	////////////////

	// METHOD: toString
	// DEFINITION: Returns a string representation of this cell.
	// RETURNS: "." for an unmarked cell, "X" for a marked cell, "?" otherwise
	// THROWS: None
	@Override
	public String toString() {
		if (status == CellStatus.UNMARKED) {
			return ".";
		} else if (status == CellStatus.MARKED) {
			return "X";
		} else {
			return "?";
		}
	}
}
