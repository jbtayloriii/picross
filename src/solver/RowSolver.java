package solver;

import entities.Row;

public class RowSolver {

	
	public static Row SolveRow(Row unsolvedRow) {
		int spacesNeeded = unsolvedRow.SpacesNeeded();
		Row solvedRow = new Row(unsolvedRow.rowSize(), unsolvedRow.getNumbers());
		
		for (int index = 0; index < array.length; index++) {
			Row solvedSubRow;
			if (spacesNeeded > unsolvedRow.rowSize()) {
				//error, we need too many spaces
				throw new Exception("You've given a row to the solver that can't fit its numbers");
			}
			if (spacesNeeded == unsolvedRow.rowSize()) {
				//We have exactly as many spaces as we need
				solvedSubRow = fitNumbersIntoRow(unsolvedRow);
				
			} else if (unsolvedRow.NumbersCount() == 1){
				//We have one number left, try to pack it in if it fits somewhere
			} else {
				//recursively try smaller solutions
			}
			
			
			
		}
		
		
		
		
		
		return null; // invalid set
		
		
	}
	
	public Row addSolvedSubRowToRow(Row )
	
	public static Row fitNumbersIntoRow(Row row) {
		return null;
	}
}
