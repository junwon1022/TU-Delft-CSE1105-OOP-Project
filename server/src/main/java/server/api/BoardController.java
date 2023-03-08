package server.api;

import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    /**
     * Get a board given its id
     * @param boardId
     * @return the board
     */
    @GetMapping(path = {"/{board_id}/","/{board_id}"})
    public ResponseEntity<Board> getBoardById(@PathVariable("board_id") long boardId){
        try {
            // Get the board
            Board board = boardService.getBoardById(boardId);
            if(board == null) return ResponseEntity.badRequest().build();
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

    @PostMapping(path={"","/"})
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
     * @param boardId
     * @return the edited board
     */
    @PostMapping(path = {"/{board_id}/","/{board_id}"})
    public ResponseEntity<Board> editBoardTitleById(@RequestBody String newTitle,
                                                  @PathVariable("board_id") long boardId) {

        try {
            // Edit the board and save it in the database
            Board board = boardService.editBoardTitle(boardId, newTitle);
            // Return the edited board with an HTTP 200 OK status
            return ResponseEntity.ok().body(board);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Delete a board given its id
     * @param boardId
     * @return the deleted board
     */
    @DeleteMapping(path = {"/{board_id}/","/{board_id}"})
    public ResponseEntity<Board> removeBoardById(@PathVariable("board_id") long boardId){
        try {
            // Check if a board with the given id exists
            Board board = boardService.getBoardById(boardId);
            // Delete the board
            boardService.deleteBoardById(boardId);
            // Return the saved board with an HTTP 200 OK status
            return ResponseEntity.ok(board);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
