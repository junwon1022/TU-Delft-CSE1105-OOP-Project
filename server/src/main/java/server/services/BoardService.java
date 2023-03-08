package server.services;

import commons.Board;
import org.springframework.stereotype.Service;
import server.database.BoardRepository;

@Service
public class BoardService {

    private BoardRepository boardRepository;

    /**
     * Constructor with parameters
     * @param boardRepository
     */
    public BoardService(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    /**
     * Retrieve a board given its id
     * @param id
     * @return a board
     */
    public Board getBoardById(Long id) throws Exception {
        return boardRepository.findById(id)
                .orElseThrow(() -> new Exception("Board not found with id " + id));
    }

    /**
     * Create a new board
     * @param board
     * @return boardRepository
     */
    public Board createBoard(Board board) throws Exception {
        if(board.title == null || board.title.isEmpty()) {
            throw new Exception("Board cannot be created without a title.");
        }
        return boardRepository.save(board);
    }

    /**
     * Delete a board given its id
     * @param id
     */
    public void deleteBoardById(Long id) {
        boardRepository.deleteById(id);
    }

    /**
     * Edit the title of a board and store the edited board in the database
     * @param id
     * @param newTitle
     * @return the edited board
     */
    public Board editBoardTitle(Long id, String newTitle) throws Exception {
        if(newTitle == null || newTitle.isEmpty()) {
            throw new Exception("Title should not be null or empty.");
        }
        Board board = getBoardById(id);
        board.title = newTitle;
        return boardRepository.save(board);
    }
}
