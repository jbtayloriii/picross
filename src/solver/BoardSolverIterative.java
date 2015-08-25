package solver;

import java.util.Queue;

import reader.*;
import entities.*;

public class BoardSolverIterative {
	
	public static Board currentBoard;
	public static Queue<Integer> nextRowQueue;
	

	
	public static void main(String[] args) {
		 BoardReaderInterface boardReader = new BoardReaderText();
		 
		 boardReader.ReadFile(null);
		 
		 currentBoard = boardReader.getBoard();
		 nextRowQueue.clear();
		 
		 for (int i = 0; i < currentBoard.getColumnCount() + currentBoard.getRowCount(); i++) {
			 //add all rows and columns
			nextRowQueue.add(Integer.valueOf(i));
		}
		 
		 
	}
}
