package reader;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import entities.Board;
import interfaces.BoardReaderInterface;

public class BoardReaderText implements BoardReaderInterface {
	private Board currentBoard;
	
	@Override
	public Boolean ReadFile(File file) {
		boolean readSuccess;
	    Scanner in;
		try {
			in = new Scanner(file);
			Scanner dimensionsScanner = new Scanner(in.nextLine());
		    int rows = dimensionsScanner.nextInt();
		    int columns = dimensionsScanner.nextInt();
		    dimensionsScanner.close();
		    int[][] rowNumbers = new int[rows][];
		    int[][] columnNumbers = new int[columns][];
		    
		    for (int nextRow = 0; nextRow < rows; nextRow++) {
				Scanner lineScanner = new Scanner (in.nextLine());
				int numbersCount = lineScanner.nextInt();
		    	int[] newRow = new int[numbersCount];
				for (int nextNumber = 0; nextNumber < numbersCount; nextNumber++) {
					newRow[nextNumber] = lineScanner.nextInt();
				}
				rowNumbers[nextRow] = newRow;
				lineScanner.close();
			}

		    //Should remove this copy and paste
		    for (int nextColumn = 0; nextColumn < columns; nextColumn++) {
				Scanner lineScanner = new Scanner (in.nextLine());
				int numbersCount = lineScanner.nextInt();
		    	int[] newColumn = new int[numbersCount];
				for (int nextNumber = 0; nextNumber < numbersCount; nextNumber++) {
					newColumn[nextNumber] = lineScanner.nextInt();
				}
				columnNumbers[nextColumn] = newColumn;
				lineScanner.close();
			}
		    
		    currentBoard = new Board(rows, columns, rowNumbers, columnNumbers);
			readSuccess = true;
		} catch (FileNotFoundException e) {
			readSuccess = false;
			e.printStackTrace();
		}

		return readSuccess;
	}

	public Board getBoard() {
		return currentBoard;
	}
}
