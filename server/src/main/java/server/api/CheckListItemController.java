package server.api;

import commons.Board;
import commons.Card;
import commons.CheckListItem;
import commons.ListOfCards;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.services.BoardService;
import server.services.CardService;
import server.services.CheckListItemService;
import server.services.ListOfCardsService;

import java.util.List;


@RestController
@RequestMapping("/api/boards/{board_id}/lists/{list_id}/cards/{card_id}/checks")
public class CheckListItemController {


    private final CheckListItemService checkListItemService;
    private final CardService cardService;
    private final ListOfCardsService listOfCardsService;
    private final BoardService boardService;

    /**
     * Constructor with parameters
     *
     * @param checkListItemService
     * @param cardService
     * @param listOfCardsService
     * @param boardService
     */
    public CheckListItemController(CardService cardService,
                                   ListOfCardsService listOfCardsService,
                                   CheckListItemService checkListItemService, BoardService boardService) {
        this.cardService = cardService;
        this.checkListItemService = checkListItemService;
        this.listOfCardsService = listOfCardsService;
        this.boardService = boardService;
    }

    /**
     * Get the checklists within a given card
     * @param boardId
     * @param listId
     * @param cardId
     * @return the list of lists
     */
    @GetMapping("/")
    private ResponseEntity<List<CheckListItem>> getChecks
    (@PathVariable("board_id") long boardId,
        @PathVariable("list_id") long listId,
        @PathVariable("card_id") long cardId ) {
        try {
            // Get the board
            Board board = boardService.getBoardById(boardId);
            // Get the list
            ListOfCards list = listOfCardsService.getListById(listId);

            // Get the card
            Card card = cardService.getCardById(cardId);

            // Check if the list is in the board and the card is in the list
            if(!listOfCardsService.listInBoard(list, board) || !cardService.cardInList(card,list)) {
                return ResponseEntity.badRequest().build();
            }
            List<CheckListItem> checks = checkListItemService.getChecklists(card);
            // Return the cards with an HTTP 200 OK status
            return ResponseEntity.status(HttpStatus.OK).body(checks);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }


    /**
     * Get a card given its id
     * @param boardId
     * @param listId
     * @param cardId
     * @param checkId
     * @return the list
     */
    @GetMapping("/{check_id}")
    private ResponseEntity<CheckListItem> getCheckById(@PathVariable("board_id") long boardId,
                                                           @PathVariable("list_id") long listId,
                                                           @PathVariable("card_id") long cardId,
                                                           @PathVariable("check_id") long checkId) {
        try {
            if(!validPath(boardId, listId, cardId,checkId)) {
                return ResponseEntity.badRequest().build();
            }
            // Get the card
            CheckListItem checkListItem = checkListItemService.getCheckById(checkId);
            // Return the card with an HTTP 200 OK status
            return ResponseEntity.status(HttpStatus.OK).body(checkListItem);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Create a new check
     * @param check
     * @param boardId
     * @param listId
     * @param cardId
     * @return the new check
     */
    @PostMapping("/")
    public ResponseEntity<CheckListItem> createCheck(@RequestBody CheckListItem check,
                                           @PathVariable("board_id") long boardId,
                                           @PathVariable("list_id") long listId,
                                            @PathVariable("card_id") long cardId ) {
        try {
            // Get the board
            Board board = boardService.getBoardById(boardId);
            // Get the list
            ListOfCards list = listOfCardsService.getListById(listId);

            // Get the card
            Card card = cardService.getCardById(cardId);

            // Check if the list is in the board and the card is in the list
            if(!listOfCardsService.listInBoard(list, board) || !cardService.cardInList(card,list)) {
                return ResponseEntity.badRequest().build();
            }

            // Save the new card to the database
            checkListItemService.createChecklistItem(check,card);
            // Return the saved card with an HTTP 201 Created status
            return ResponseEntity.status(HttpStatus.CREATED).body(check);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Edit a check's text
     * @param newText
     * @param boardId
     * @param listId
     * @param cardId
     * @return the edited card
     */
    @PostMapping("{check_id}/")
    public ResponseEntity<CheckListItem> editChecktextById(@RequestBody String newText,
                                                  @PathVariable("board_id") long boardId,
                                                  @PathVariable("list_id") long listId,
                                                  @PathVariable("card_id") long cardId,
                                                  @PathVariable("check_id") long checkId) {

        try {
            if(!validPath(boardId, listId, cardId, checkId)) {
                return ResponseEntity.badRequest().build();
            }

            // Edit the list and save it in the database
            CheckListItem check = checkListItemService.editCheckText(checkId,newText);
            // Return the edited board with an HTTP 200 OK status
            return ResponseEntity.ok().body(check);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }




    /**
     * Edit a check's completion
     * @param boardId
     * @param listId
     * @param cardId
     * @return the edited card
     */
    @PostMapping("{check_id}")
    public ResponseEntity<CheckListItem> editCompletion(
                                                           @PathVariable("board_id") long boardId,
                                                           @PathVariable("list_id") long listId,
                                                           @PathVariable("card_id") long cardId,
                                                           @PathVariable("check_id") long checkId) {

        try {
            if(!validPath(boardId, listId, cardId, checkId)) {
                return ResponseEntity.badRequest().build();
            }
            // Edit the list and save it in the database
            CheckListItem check = checkListItemService.getCheckById(checkId);
            checkListItemService.editCompletion(check);
            // Return the edited board with an HTTP 200 OK status
            return ResponseEntity.ok().body(check);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }


    /**
     * Delete a card given its id
     * @param boardId
     * @param listId
     * @param cardId
     * @return the deleted card
     */
    @DeleteMapping("/{check_id}")
    public ResponseEntity<Card> removeCheckById(@PathVariable("board_id") long boardId,
                                               @PathVariable("list_id") long listId,
                                               @PathVariable("card_id") long cardId,
                                               @PathVariable("check_id") long checkId) {
        try {
            if(!validPath(boardId, listId, cardId, checkId)) {
                return ResponseEntity.badRequest().build();
            }
            // Delete the card
            checkListItemService.deleteChecklistItemById(checkId);
            // Return the saved card with an HTTP 200 OK status
            return ResponseEntity.ok().build();
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    private boolean validPath(long boardId, long listId, long cardId, long checkId)
            throws Exception {
        // Get the board
        Board board = boardService.getBoardById(boardId);
        // Get the list
        ListOfCards list = listOfCardsService.getListById(listId);
        // Get the card
        Card card = cardService.getCardById(cardId);

        // Get the check
        CheckListItem check = checkListItemService.getCheckById(checkId);

        // Check if the list is in the board
        if(!listOfCardsService.listInBoard(list, board)) {
            return false;
        }
        // Check if the card is in the list
        if(!cardService.cardInList(card, list)) {
            return false;
        }

        if(!checkListItemService.checkInCard(check,card)){
            return false;
        }
        return true;
    }
}
