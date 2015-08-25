package entities;

public class Cell {
	
	public enum cellStatus {
		statusUnsolved, statusUnmarked, statusMarked
	}
	
	public enum potentialCellStatus {
		potentialStatusUnmarked, potentialStatusMarked, potentialStatusBoth, potentialStatusNothing
	}
	

	
	private cellStatus status;
	private potentialCellStatus potentialStatus;

	public Cell() {
		this.status = cellStatus.statusUnsolved;
		this.potentialStatus = potentialCellStatus.potentialStatusNothing;
	}
	
	public potentialCellStatus getPotentialStatus() {
		return potentialStatus;
	}
	
	public void setPotentialStatus(potentialCellStatus newStatus) {
		potentialStatus = newStatus;
	}

	public cellStatus getStatus() {
		return status;
	}


	public void setStatus(cellStatus status) {
		this.status = status;
	}
	
	
	public String toString() {
		if (status == cellStatus.statusUnmarked) {
			return ".";
		} else if (status == cellStatus.statusMarked) {
			return "X";
		} else {
			return "?";
		}
	}
}
