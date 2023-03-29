package server.services;

import commons.Board;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import server.database.BoardRepository;

import java.util.List;

@Service
public class BoardService {

    private BoardRepository boardRepository;

    /**
     * Constructor with parameters
     * @param boardRepository
     */
    @Autowired
    public BoardService(@Qualifier("board") BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    /**
     * Retrieve all boards
     * @return a list of boards
     */
    public List<Board> getBoards() {
        return boardRepository.findAll();
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
     * Retrieve a board given its key
     * @param key
     * @return a board
     */
    public Board getBoardByKey(String key) throws Exception {

        System.out.println("Board Repository " + boardRepository.findAll().toString());

        for(Board b : boardRepository.findAll()){
            System.out.println("Board to string" + b.toString());
            if(b.key.equals(key)) return b;
        }

        throw new Exception("There is no board with that key");
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

    /**
     * Method that changes the board's background colour in the database
     * @param id
     * @param colour
     * @return the edited board
     * @throws Exception
     */
    public Board editBoardBackground(Long id, String colour) throws Exception{
        Board board = getBoardById(id);
        board.colour = colour;
        return boardRepository.save(board);
    }

    /**
     * Method that changes the board's font colour in the database
     * @param id
     * @param colour
     * @return the edited board
     * @throws Exception
     */
    public Board editBoardFont(Long id, String colour) throws Exception{
        Board board = getBoardById(id);
        board.font = colour;
        return boardRepository.save(board);
    }

    /**
     * Method that changes the lists' font color in the database
     * @param id
     * @param colour
     * @return the edited board
     * @throws Exception
     */
    public Board editListsFont(Long id, String colour) throws Exception{
        Board board = getBoardById(id);
        board.listFont = colour;
        return boardRepository.save(board);
    }

    /**
     * Method that changes the lists' background color in the database
     * @param id
     * @param colour
     * @return the edited board
     * @throws Exception
     */
    public Board editListsBackground(Long id, String colour) throws Exception{
        Board board = getBoardById(id);
        board.listColour = colour;
        return boardRepository.save(board);
    }
}
