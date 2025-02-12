package server.api;

import commons.Board;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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

    @Autowired
    private final SimpMessagingTemplate simpMessagingTemplate;

    /**
     * Constructor with parameters
     *
     * @param listOfCardsService
     * @param boardService
     * @param simpMessagingTemplate
     */
    @Autowired
    public ListOfCardsController(ListOfCardsService listOfCardsService,
                                 BoardService boardService,
                                 SimpMessagingTemplate simpMessagingTemplate) {

        this.listOfCardsService = listOfCardsService;
        this.boardService = boardService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    /**
     * Get the lists within a given board
     *
     * @param boardId
     * @return the list of lists
     */
    @GetMapping(path = {"", "/"})
    ResponseEntity<List<ListOfCards>> getListsOfCards
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
    ResponseEntity<ListOfCards> getListOfCardsById(@PathVariable("board_id") long boardId,
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
            // Send new data to all users in the board
            simpMessagingTemplate.convertAndSend("/topic/" + board.id, board);
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
            // Get the board to which the list of cards will be edited
            Board board = boardService.getBoardById(boardId);
            // Edit the list and save it in the database
            ListOfCards list = listOfCardsService.editListOfCardsTitle(listId, newTitle);
            // Send new data to all users in the board
            simpMessagingTemplate.convertAndSend("/topic/" + board.id, board);
            // Return the edited list with an HTTP 200 OK status
            return ResponseEntity.ok().body(list);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }


    /**
     * Move two cards' positions given the list they belong to and their ids
     * @param boardId the id of the board
     * @param listId the id of the list
     * @param fromId the id of the first card
     * @param toId the id of the second card
     * @return the updated list
     */
    @PutMapping(path = {"/{list_id}/from/{from}/to/{to}/","/{list_id}/from/{from}/to/{to}"})
    public ResponseEntity<ListOfCards> moveCards(@PathVariable("board_id") long boardId,
                                                 @PathVariable("list_id") long listId,
                                                 @PathVariable("from") long fromId,
                                                 @PathVariable("to") long toId) {

        try {
            if(!validPath(boardId, listId)
                    || !validIndex(boardId, listId, fromId)
                    || !validIndex(boardId, listId, toId)) {
                return ResponseEntity.badRequest().build();
            }
            // Get the board in which the cards will be moved
            Board board = boardService.getBoardById(boardId);
            // Edit the list and save it in the database
            ListOfCards list = listOfCardsService.moveCardsInListOfCards(listId, fromId, toId);
            // Send new data to all users in the board
            simpMessagingTemplate.convertAndSend("/topic/" + board.id, board);
            // Return the edited list with an HTTP 200 OK status
            return ResponseEntity.ok().body(list);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Delete a list given its id
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
            // Get the board from which the list of cards will be removed
            Board board = boardService.getBoardById(boardId);
            // Get the list
            ListOfCards list = listOfCardsService.getListById(listId);
            // Delete the list
            listOfCardsService.deleteListOfCardsById(listId);
            // Send new data to all users in the board
            simpMessagingTemplate.convertAndSend("/topic/" + board.id, board);
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

    /**
     * Checks if the index exists
     * @param boardId the id of the board
     * @param listId the id of the list
     * @param cardIndex the index of the card
     * @return true if the index is valid
     * @throws Exception
     */
    private boolean validIndex(long boardId, long listId, long cardIndex)
            throws Exception {
        // Get the board
        Board board = boardService.getBoardById(boardId);
        // Get the list
        ListOfCards list = listOfCardsService.getListById(listId);
        return list.cards.size() > cardIndex;
    }
}
