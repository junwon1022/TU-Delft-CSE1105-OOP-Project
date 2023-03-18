package server.api;

import commons.Board;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    public ListOfCardsController(ListOfCardsService listOfCardsService,
                                 BoardService boardService) {
        this.listOfCardsService = listOfCardsService;
        this.boardService = boardService;
    }

    /**
     * Get the lists within a given board
     *
     * @param boardId
     * @return the list of lists
     */
    @GetMapping(path = {"", "/"})
    private ResponseEntity<List<ListOfCards>> getListsOfCards
    (@PathVariable("board_id") long boardId) {
        try {
            // Get the board
            Board board = boardService.getBoardById(boardId);
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
     *
     * @param boardId
     * @param listId
     * @return the list
     */
    @GetMapping(path = {"/{list_id}/","/{list_id}"})
    private ResponseEntity<ListOfCards> getListOfCardsById(@PathVariable("board_id") long boardId,
                                                           @PathVariable("list_id") long listId) {
        try {
            if(!validPath(boardId, listId)) {
                return ResponseEntity.badRequest().build();
            }
            // Get the list
            ListOfCards list = listOfCardsService.getListById(listId);
            // Return the list with an HTTP 200 OK status
            return ResponseEntity.status(HttpStatus.OK).body(list);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Create a new list of cards
     *
     * @param list
     * @param boardId
     * @return the new list
     */
    @PostMapping(path = {"", "/"})
    public ResponseEntity<ListOfCards> createListOfCards(@RequestBody ListOfCards list,
                                                         @PathVariable("board_id") long boardId) {
        try {
            // Get the board to which the list will be added
            Board board = boardService.getBoardById(boardId);
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
     *
     * @param newTitle
     * @param boardId
     * @param listId
     * @return the edited list
     */
    @PutMapping(path = {"/{list_id}/","/{list_id}"})
    public ResponseEntity<ListOfCards> editListOfCardsTitleById(@RequestBody String newTitle,
                                                    @PathVariable("board_id") long boardId,
                                                    @PathVariable("list_id") long listId) {

        try {
            if(!validPath(boardId, listId)) {
                return ResponseEntity.badRequest().build();
            }
            // Edit the list and save it in the database
            ListOfCards list = listOfCardsService.editListOfCardsTitle(listId, newTitle);
            // Return the edited list with an HTTP 200 OK status
            return ResponseEntity.ok().body(list);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Delete a list given its id
     *
     * @param boardId
     * @param listId
     * @return the deleted list
     */
    @DeleteMapping(path = {"/{list_id}/","/{list_id}"})
    public ResponseEntity<ListOfCards> removeListOfCardsById(@PathVariable("board_id") long boardId,
                                                             @PathVariable("list_id") long listId) {
        try {
            if(!validPath(boardId, listId)) {
                return ResponseEntity.badRequest().build();
            }
            // Get the list
            ListOfCards list = listOfCardsService.getListById(listId);
            // Delete the list
            listOfCardsService.deleteListOfCardsById(listId);
            // Return the saved list with an HTTP 200 OK status
            return ResponseEntity.ok().build();
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Checks if there is a valid path between a board and a list
     * @param boardId
     * @param listId
     * @return true if the path is valid
     * @throws Exception
     */
    private boolean validPath(long boardId, long listId)
            throws Exception {
        // Get the board
        Board board = boardService.getBoardById(boardId);
        // Get the list
        ListOfCards list = listOfCardsService.getListById(listId);
        // Check if the list is in the board
        if(!listOfCardsService.listInBoard(list, board)) {
            return false;
        }
        return true;
    }
}
