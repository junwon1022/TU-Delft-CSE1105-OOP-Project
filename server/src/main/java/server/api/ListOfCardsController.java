package server.api;

import commons.Board;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import commons.ListOfCards;
import server.services.BoardService;
import server.services.ListOfCardsService;

import java.util.List;


@RestController
@RequestMapping("/api/boards/{board_id}/lists")
public class ListOfCardsController {

    private final ListOfCardsService listOfCardsService;

    private final BoardService boardService;

    /**
     * Constructor with parameters
     *
     * @param listOfCardsService
     * @param boardService
     */
    public ListOfCardsController(ListOfCardsService listOfCardsService,
                                 BoardService boardService) {
        this.listOfCardsService = listOfCardsService;
        this.boardService = boardService;
    }

    /**
     * Get the lists within a given board
     * @param board_id
     * @return the list of lists
     */
    @GetMapping("/")
    private ResponseEntity<List<ListOfCards>> getListsOfCards
            (@PathVariable("board_id") long board_id) {
        try {
            // Get the board
            Board board = boardService.getBoardById(board_id);
            List<ListOfCards> lists = listOfCardsService.getListsOfCards(board);
            // Return the lists with an HTTP 200 OK status
            return ResponseEntity.status(HttpStatus.OK).body(lists);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get a list given its id
     * @param board_id
     * @param list_id
     * @return the list
     */
    @GetMapping("/{list_id}")
    private ResponseEntity<ListOfCards> getListOfCardsById(@PathVariable("board_id") long board_id,
                                                           @PathVariable("list_id") long list_id) {
        try {
            if(!validPath(board_id, list_id)) {
                return ResponseEntity.badRequest().build();
            }
            // Get the list
            ListOfCards list = listOfCardsService.getListById(list_id);
            // Return the list with an HTTP 200 OK status
            return ResponseEntity.status(HttpStatus.OK).body(list);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Create a new list of cards
     * @param list
     * @param board_id
     * @return the new list
     */
    @PostMapping("/")
    public ResponseEntity<ListOfCards> createListOfCards(@RequestBody ListOfCards list,
                                                         @PathVariable("board_id") long board_id) {
        try {
            // Get the board to which the list will be added
            Board board = boardService.getBoardById(board_id);
            // Save the new list of cards to the database
            listOfCardsService.createListOfCards(list, board);
            // Return the saved list with an HTTP 201 Created status
            return ResponseEntity.status(HttpStatus.CREATED).body(list);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Edit a list's title
     * @param newTitle
     * @param board_id
     * @param list_id
     * @return the edited list
     */
    @PostMapping("{list_id}")
    public ResponseEntity<ListOfCards> editListOfCardsTitleById(@RequestBody String newTitle,
                                                    @PathVariable("board_id") long board_id,
                                                    @PathVariable("list_id") long list_id) {

        try {
            if(!validPath(board_id, list_id)) {
                return ResponseEntity.badRequest().build();
            }
            // Edit the list and save it in the database
            ListOfCards list = listOfCardsService.editListOfCardsTitle(list_id, newTitle);
            // Return the edited list with an HTTP 200 OK status
            return ResponseEntity.ok().body(list);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Delete a list given its id
     * @param board_id
     * @param list_id
     * @return the deleted list
     */
    @DeleteMapping("/{list_id}")
    public ResponseEntity<ListOfCards> removeListOfCardsById(@PathVariable("board_id") long board_id,
                                                             @PathVariable("list_id") long list_id) {
        try {
            if(!validPath(board_id, list_id)) {
                return ResponseEntity.badRequest().build();
            }
            // Delete the list
            listOfCardsService.deleteListOfCardsById(list_id);
            // Return the saved list with an HTTP 200 OK status
            return ResponseEntity.ok().build();
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    private boolean validPath(long board_id, long list_id)
            throws Exception {
        // Get the board
        Board board = boardService.getBoardById(board_id);
        // Get the list
        ListOfCards list = listOfCardsService.getListById(list_id);
        // Check if the list is in the board
        if(!listOfCardsService.listInBoard(list, board)) {
            return false;
        }
        return true;
    }
}
