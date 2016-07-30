import java.io.File;

import entities.Board;
import interfaces.BoardReaderInterface;
import interfaces.BoardSolverInterface;
import reader.*;
import solver.*;

public class runClass {

	public static BoardReaderInterface boardReader;
	public static BoardSolverInterface boardSolver;
	
	public static void main(String[] args) {
		//Change board reader here
		boardReader = new BoardReaderText();
		boardReader.ReadFile(new File("spiderweb.txt"));
		Board nextBoard = boardReader.getBoard();
		
		//Change board solver here
		boardSolver = new BoardSolverQueue();
		Board solvedBoard = boardSolver.SolveBoard(nextBoard);
		
		//Change output here
		System.out.print(solvedBoard);
		
	}
}
