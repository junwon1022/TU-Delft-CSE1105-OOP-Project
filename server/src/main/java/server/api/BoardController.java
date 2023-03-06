package server.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import commons.Board;
import server.services.BoardService;


@RestController
@RequestMapping("/api/boards")
public class BoardController {

    private final BoardService boardService;

    /**
     * Constructor with parameters
     * @param boardService
     */
    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    /**
     * Get a board given its id
     * @param board_id
     * @return the board
     */
    @GetMapping("/{board_id}")
    private ResponseEntity<Board> getBoardById(@PathVariable("board_id") long board_id){
        try {
            // Get the board
            Board board = boardService.getBoardById(board_id);
            // Return the board with an HTTP 200 OK status
            return ResponseEntity.status(HttpStatus.OK).body(board);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Create a new board
     * @param board
     * @return the new board
     */
    @PostMapping("/")
    public ResponseEntity<Board> createBoard(@RequestBody Board board) {
        try {
            // Save the new board to the database
            boardService.createBoard(board);
            // Return the saved board with an HTTP 201 Created status
            return ResponseEntity.status(HttpStatus.CREATED).body(board);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Edit a board's title
     * @param newTitle
     * @param board_id
     * @return the edited board
     */
    @PostMapping("/{board_id}")
    public ResponseEntity<Board> editBoardTitleById(@RequestBody String newTitle,
                                                  @PathVariable("board_id") long board_id) {

        try {
            // Edit the board and save it in the database
            Board board = boardService.editBoardTitle(board_id, newTitle);
            // Return the edited board with an HTTP 200 OK status
            return ResponseEntity.ok().body(board);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Delete a board given its id
     * @param board_id
     * @return the deleted board
     */
    @DeleteMapping("/{board_id}")
    public ResponseEntity<Board> removeBoardById(@PathVariable("board_id") long board_id){
        try {
            // Check if a board with the given id exists
            boardService.getBoardById(board_id);
            // Delete the board
            boardService.deleteBoardById(board_id);
            // Return the saved board with an HTTP 200 OK status
            return ResponseEntity.ok().build();
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
