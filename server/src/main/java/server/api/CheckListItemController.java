package server.api;

import commons.Board;
import commons.Card;
import commons.CheckListItem;
import commons.ListOfCards;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import server.services.BoardService;
import server.services.CardService;
import server.services.CheckListItemService;
import server.services.ListOfCardsService;

import java.util.List;


@RestController
@RequestMapping("/api/boards/{board_id}/lists/{list_id}/cards/{card_id}/checklists")
public class CheckListItemController {


    private final CheckListItemService checkListItemService;
    private final CardService cardService;
    private final ListOfCardsService listOfCardsService;
    private final BoardService boardService;

    @Autowired
    private final SimpMessagingTemplate simpMessagingTemplate;

    /**
     * Constructor with parameters
     *
     * @param checkListItemService
     * @param cardService
     * @param listOfCardsService
     * @param boardService
     * @param simpMessagingTemplate
     */
    @Autowired
    public CheckListItemController(CardService cardService,
                                   ListOfCardsService listOfCardsService,
                                   CheckListItemService checkListItemService,
                                   BoardService boardService,
                                   SimpMessagingTemplate simpMessagingTemplate) {
        this.cardService = cardService;
        this.checkListItemService = checkListItemService;
        this.listOfCardsService = listOfCardsService;
        this.boardService = boardService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    /**
     * Get the checklist of a given card
     *
     * @param boardId
     * @param listId
     * @param cardId
     * @return the checklist
     */
    @GetMapping(path = {"", "/"})
    ResponseEntity<List<CheckListItem>> getChecks(
            @PathVariable("board_id") long boardId,
            @PathVariable("list_id") long listId,
            @PathVariable("card_id") long cardId
    ) {
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
            List<CheckListItem> checks = checkListItemService.getChecklist(card);
            // Return the cards with an HTTP 200 OK status
            return ResponseEntity.status(HttpStatus.OK).body(checks);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }


    /**
     * Get a checklist item given its id
     *
     * @param boardId
     * @param listId
     * @param cardId
     * @param checklistId
     * @return the checklist item
     */
    @GetMapping(path = {"/{checklist_id}/","/{checklist_id}"})
    ResponseEntity<CheckListItem> getCheckById(@PathVariable("board_id") long boardId,
                                               @PathVariable("list_id") long listId,
                                               @PathVariable("card_id") long cardId,
                                               @PathVariable("checklist_id")
                                               long checklistId) {
        try {
            if(!validPath(boardId, listId, cardId, checklistId)) {
                return ResponseEntity.badRequest().build();
            }
            // Get the checklist item
            CheckListItem checkListItem = checkListItemService.getCheckById(checklistId);
            // Return the checklist item with an HTTP 200 OK status
            return ResponseEntity.status(HttpStatus.OK).body(checkListItem);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Create a new check
     *
     * @param check
     * @param boardId
     * @param listId
     * @param cardId
     * @return the new check
     */
    @PostMapping(path = {"","/"})
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

            if(!listOfCardsService.listInBoard(list, board) || !cardService.cardInList(card,list)) {
                return ResponseEntity.badRequest().build();
            }

            // Save the new check to the database
            checkListItemService.createCheckListItem(check, card);
            // Send new data to all users in the board
            simpMessagingTemplate.convertAndSend("/topic/" + board.id, board);
            // Return the saved checklist item with an HTTP 201 Created status
            return ResponseEntity.status(HttpStatus.CREATED).body(check);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Edit a check's text
     *
     * @param newText
     * @param boardId
     * @param listId
     * @param cardId
     * @param checklistId
     * @return the edited checklist item
     */
    @PutMapping(path = {"/{checklist_id}/text","/{checklist_id}/text/"})
    public ResponseEntity<CheckListItem> editCheckTextById(@RequestBody String newText,
                                                           @PathVariable("board_id") long boardId,
                                                           @PathVariable("list_id") long listId,
                                                           @PathVariable("card_id") long cardId,
                                                           @PathVariable("checklist_id")
                                                               long checklistId) {

        try {
            if(!validPath(boardId, listId, cardId, checklistId)) {
                return ResponseEntity.badRequest().build();
            }
            // Get the board in which the check will be updated
            Board board = boardService.getBoardById(boardId);
            // Edit the check and save it in the database
            CheckListItem check = checkListItemService.editCheckText(checklistId,newText);
            // Send new data to all users in the board
            simpMessagingTemplate.convertAndSend("/topic/" + board.id, board);
            // Return the edited checklist item with an HTTP 200 OK status
            return ResponseEntity.ok().body(check);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }




    /**
     * Edit a check's completion
     *
     * @param completed
     * @param boardId
     * @param listId
     * @param cardId
     * @param checklistId
     * @return the edited checklist item
     */
    @PutMapping(path = {"/{checklist_id}/completion", "/{checklist_id}/completion/"})
    public ResponseEntity<CheckListItem> editCheckCompletion(@RequestBody Boolean completed,
                                                             @PathVariable("board_id") long boardId,
                                                             @PathVariable("list_id") long listId,
                                                             @PathVariable("card_id") long cardId,
                                                             @PathVariable("checklist_id")
                                                                 long checklistId) {

        try {
            if(!validPath(boardId, listId, cardId, checklistId)) {
                return ResponseEntity.badRequest().build();
            }
            // Get the board in which the checklist item will be updated
            Board board = boardService.getBoardById(boardId);
            // Edit the checklist item and save it in the database
            CheckListItem check = checkListItemService.getCheckById(checklistId);
            checkListItemService.editCompletion(check);
            // Send new data to all users in the board
            simpMessagingTemplate.convertAndSend("/topic/" + board.id, board);
            // Return the edited checklist item with an HTTP 200 OK status
            return ResponseEntity.ok().body(check);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }


    /**
     * Delete a checklist item given its id
     *
     * @param boardId
     * @param listId
     * @param cardId
     * @param checklistId
     * @return the deleted checklist item
     */
    @DeleteMapping(path = {"/{checklist_id}","/{checklist_id}/"})
    public ResponseEntity<CheckListItem> removeCheckById(@PathVariable("board_id") long boardId,
                                                         @PathVariable("list_id") long listId,
                                                         @PathVariable("card_id") long cardId,
                                                         @PathVariable("checklist_id")
                                                             long checklistId) {
        try {
            if(!validPath(boardId, listId, cardId, checklistId)) {
                return ResponseEntity.badRequest().build();
            }
            // Get the board from which the check will be removed
            Board board = boardService.getBoardById(boardId);
            // Get the item
            CheckListItem checkListItem = checkListItemService.getCheckById(checklistId);
            // Delete the checklist item
            checkListItemService.deleteCheckListItemById(checklistId);
            // Send new data to all users in the board
            simpMessagingTemplate.convertAndSend("/topic/" + board.id, board);
            // Return the saved checklist item with an HTTP 200 OK status
            return ResponseEntity.ok(checkListItem);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Checks whether the path to a checklist item is valid
     *
     * @param boardId
     * @param listId
     * @param cardId
     * @param checklistId
     * @return true if the path is valid
     * @throws Exception
     */
    private boolean validPath(long boardId, long listId, long cardId, long checklistId)
            throws Exception {
        // Get the board
        Board board = boardService.getBoardById(boardId);
        // Get the list
        ListOfCards list = listOfCardsService.getListById(listId);
        // Get the card
        Card card = cardService.getCardById(cardId);
        // Get the check
        CheckListItem check = checkListItemService.getCheckById(checklistId);

        // Check if the list is in the board
        if(!listOfCardsService.listInBoard(list, board)) {
            return false;
        }
        // Check if the card is in the list
        if(!cardService.cardInList(card, list)) {
            return false;
        }
        // Check if the checklist item is in the card
        if(!checkListItemService.checkInCard(check,card)){
            return false;
        }
        return true;
    }
}

