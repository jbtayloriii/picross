package reader;

import java.io.File;

import entities.Board;

public interface BoardReaderInterface {

	public Boolean ReadFile(File file);

	public Board getBoard();
}
