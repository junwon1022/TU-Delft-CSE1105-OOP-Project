package server.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import commons.Board;
import server.services.BoardService;

import java.util.List;


@RestController
@RequestMapping("/api/boards")
public class BoardController {

    private final BoardService boardService;

    @Autowired
    private final SimpMessagingTemplate simpMessagingTemplate;

    /**
     * Constructor with parameters
     *
     * @param boardService
     * @param simpMessagingTemplate
     */
    @Autowired
    public BoardController(BoardService boardService, SimpMessagingTemplate simpMessagingTemplate) {
        this.boardService = boardService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }






    /**
     * Get the boards
     *
     * @return the list of lists
     */
    @GetMapping(path = {"", "/"})
    private ResponseEntity<List<Board>> getBoards() {
        try {
            List<Board> boards = boardService.getBoards();
            return ResponseEntity.status(HttpStatus.OK).body(boards);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }



    /**
     * Get a board given its id
     *
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
     * Get a board given its key
     *
     * @param key
     * @return the board
     */
    @GetMapping(path = {"/getByKey/{key}/","/getByKey/{key}"})
    public ResponseEntity<Board> getBoardByKey(@PathVariable("key") String key){
        try {
            // Get the board
            Board board = boardService.getBoardByKey(key);
            System.out.println("The board , as we get it would be " + board);
            if(board == null) return ResponseEntity.internalServerError().build();
            // Return the board with an HTTP 200 OK status
            return ResponseEntity.status(HttpStatus.OK).body(board);
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }





    /**
     * Create a new board
     *
     * @param board
     * @return the new board
     */

    @PostMapping(path={"","/"})
    public ResponseEntity<Board> createBoard(@RequestBody Board board) {
        try {
            // Save the new board to the database
            boardService.createBoard(board);
            // Send new data to all users in the board
            simpMessagingTemplate.convertAndSend("/topic/" + board.id, board);
            // Return the saved board with an HTTP 201 Created status
            return ResponseEntity.status(HttpStatus.CREATED).body(board);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }


    /**
     * Edit a board's title
     *
     * @param newTitle
     * @param boardId
     * @return the edited board
     */
    @PutMapping(path = {"/{board_id}/","/{board_id}"})
    public ResponseEntity<Board> editBoardTitleById(@RequestBody String newTitle,
                                                    @PathVariable("board_id") long boardId) {
        try {
            // Get the initial board
            Board board = boardService.getBoardById(boardId);
            // Edit the board and save it in the database
            Board newBoard = boardService.editBoardTitle(boardId, newTitle);
            // Send new data to all users in the board
            simpMessagingTemplate.convertAndSend("/topic/" + board.id, board);
            // Return the edited board with an HTTP 200 OK status
            return ResponseEntity.ok().body(newBoard);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Delete a board given its id
     *
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
            // Send new data to all users in the board
            simpMessagingTemplate.convertAndSend("/topic/" + board.id, board);
            // Return the saved board with an HTTP 200 OK status
            return ResponseEntity.ok(board);
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }




    /**
     * Method that changes the board's background color
     * @param boardId
     * @param colour
     * @return the edited board
     */
    @PutMapping(path =  "{board_id}/background")
    public ResponseEntity<Board> changeBoardBackground(@PathVariable("board_id") long boardId,
                                                       @RequestBody String colour){
        try{
            // Get the initial board
            Board board = boardService.getBoardById(boardId);

            boardService.editBoardBackground(boardId, colour);
            // Send new data to all users in the board
            simpMessagingTemplate.convertAndSend("/topic/" + board.id, board);

            return ResponseEntity.ok(board);
        }
        catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Method that changes the board's font color
     * @param boardId
     * @param colour
     * @return the edited board
     */
    @PutMapping(path =  "{board_id}/font")
    public ResponseEntity<Board> changeBoardFont(@PathVariable("board_id") long boardId,
                                                       @RequestBody String colour){
        try{
            // Get the initial board
            Board board = boardService.getBoardById(boardId);

            boardService.editBoardFont(boardId, colour);
            // Send new data to all users in the board
            simpMessagingTemplate.convertAndSend("/topic/" + board.id, board);

            return ResponseEntity.ok(board);
        }
        catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
    /**
     * Method that changes the lists' background color
     * @param boardId
     * @param colour
     * @return the edited board
     */
    @PutMapping(path =  "{board_id}/listsCol")
    public ResponseEntity<Board> changeListsBackground(@PathVariable("board_id") long boardId,
                                                       @RequestBody String colour){
        try{
            // Get the initial board
            Board board = boardService.getBoardById(boardId);

            boardService.editListsBackground(boardId, colour);
            // Send new data to all users in the board
            simpMessagingTemplate.convertAndSend("/topic/" + board.id, board);

            return ResponseEntity.ok(board);
        }
        catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Method that changes the board's font color
     * @param boardId
     * @param colour
     * @return the edited board
     */
    @PutMapping(path =  "{board_id}/listsFontCol")
    public ResponseEntity<Board> changeListsFont(@PathVariable("board_id") long boardId,
                                                 @RequestBody String colour){
        try{
            // Get the initial board
            Board board = boardService.getBoardById(boardId);

            boardService.editListsFont(boardId, colour);
            // Send new data to all users in the board
            simpMessagingTemplate.convertAndSend("/topic/" + board.id, board);

            return ResponseEntity.ok(board);
        }
        catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
}
