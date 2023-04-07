package server.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import commons.Board;
import org.springframework.web.context.request.async.DeferredResult;
import server.services.BoardService;

import java.util.*;
import java.util.function.Consumer;


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

    private Map<String, Map<Object, Consumer<Board>>> listeners = new HashMap<>();

    /**
     * @return the updates
     */
    @GetMapping(path = {"/{board_key}/updates", "/{board_key}/updates/"})
    public DeferredResult<ResponseEntity<Board>> getUpdates(@PathVariable("board_key") String boardKey) {
        var noContent = ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        var res = new DeferredResult<ResponseEntity<Board>>(500L, noContent);

        var key = new Object();
        if (!listeners.containsKey(boardKey))
            listeners.put(boardKey, new HashMap<>());
        listeners.get(boardKey).put(key, b -> res.setResult(ResponseEntity.ok(b)));
        res.onCompletion(() -> listeners.get(boardKey).remove(key));

        return res;
    }

    /**
     * Get the boards
     *
     * @return the list of lists
     */
    @GetMapping(path = {"", "/"})
    ResponseEntity<List<Board>> getBoards() {
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
            simpMessagingTemplate.convertAndSend("/topic/" + board.id, newBoard);
            // Send new data to all users who have board in preferences
            listeners.getOrDefault(board.key, Collections.emptyMap()).forEach((k, l) -> l.accept(newBoard));
            // Return the edited board with an HTTP 200 OK status
            return ResponseEntity.ok().body(newBoard);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Edit a board's password
     *
     * @param password
     * @param boardId
     * @return the edited board
     */
    @PutMapping(path = {"/{board_id}/password/","/{board_id}/password"})
    public ResponseEntity<Board> editBoardPasswordById(@RequestBody String password,
                                                    @PathVariable("board_id") long boardId) {
        try {
            // Get the initial board
            Board board = boardService.getBoardById(boardId);
            // Edit the board and save it in the database
            Board newBoard = boardService.editBoardPassword(boardId, password);
            // Send new data to all users in the board
            simpMessagingTemplate.convertAndSend("/topic/" + board.id, newBoard);
            // Send new data to all users who have board in preferences
            listeners.getOrDefault(board.key, Collections.emptyMap()).forEach((k, l) -> l.accept(newBoard));
            System.out.println("Sent out password update for board " + board.title);
            // Return the edited board with an HTTP 200 OK status
            return ResponseEntity.ok().body(newBoard);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Delete a board's password
     *
     * @param boardId
     * @return the edited board
     */
    @DeleteMapping(path = {"/{board_id}/password/","/{board_id}/password"})
    public ResponseEntity<Board> editBoardPasswordById(@PathVariable("board_id") long boardId) {
        try {
            // Get the initial board
            Board board = boardService.getBoardById(boardId);
            // Edit the board and save it in the database
            Board newBoard = boardService.removeBoardPassword(boardId);
            // Send new data to all users in the board
            simpMessagingTemplate.convertAndSend("/topic/" + board.id, newBoard);
            // Send new data to all users who have board in preferences
            listeners.getOrDefault(board.key, Collections.emptyMap()).forEach((k, l) -> l.accept(newBoard));
            System.out.println("Sent out password removed updates for board " + board.title);
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
            // Send new data to all users who have board in preferences
            board.title = "REMOVED";
            listeners.getOrDefault(board.key, Collections.emptyMap()).forEach((k, l) -> l.accept(board));
            System.out.println("Sent out board removal updates for board " + board.id);
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
        if(colour == null) return ResponseEntity.badRequest().build();
        try{
            // Get the initial board
            Board board = boardService.getBoardById(boardId);

            Board newBoard = boardService.editBoardBackground(boardId, colour);
            // Send new data to all users in the board
            simpMessagingTemplate.convertAndSend("/topic/" + board.id, newBoard);
            // Send new data to all users who have board in preferences
            listeners.getOrDefault(board.key, Collections.emptyMap()).forEach((k, l) -> l.accept(newBoard));
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

            Board newBoard = boardService.editBoardFont(boardId, colour);
            // Send new data to all users in the board
            simpMessagingTemplate.convertAndSend("/topic/" + board.id, newBoard);
            // Send new data to all users who have board in preferences
            listeners.getOrDefault(board.key, Collections.emptyMap()).forEach((k, l) -> l.accept(newBoard));
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

            Board newBoard = boardService.editListsBackground(boardId, colour);
            // Send new data to all users in the board
            simpMessagingTemplate.convertAndSend("/topic/" + board.id, newBoard);

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

            Board newBoard = boardService.editListsFont(boardId, colour);
            // Send new data to all users in the board
            simpMessagingTemplate.convertAndSend("/topic/" + board.id, newBoard);

            return ResponseEntity.ok(board);
        }
        catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
}
