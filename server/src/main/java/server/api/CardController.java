package server.api;

import commons.Board;
import commons.Card;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import commons.ListOfCards;
import server.services.BoardService;
import server.services.CardService;
import server.services.ListOfCardsService;

import java.util.List;


@RestController
@RequestMapping("/api/boards/{board_id}/lists/{list_id}/cards")
public class CardController {

    private final CardService cardService;
    private final ListOfCardsService listOfCardsService;

    private final BoardService boardService;

    /**
     * Constructor with parameters
     *
     * @param cardService
     * @param listOfCardsService
     * @param boardService
     */
    @Autowired
    public CardController(CardService cardService,
                       ListOfCardsService listOfCardsService,
                       BoardService boardService) {
        this.cardService = cardService;
        this.listOfCardsService = listOfCardsService;
        this.boardService = boardService;
    }

    /**
     * Get the cards within a given list
     *
     * @param boardId
     * @param listId
     * @return the list of lists
     */
    @GetMapping(path = {"", "/"})
    private ResponseEntity<List<Card>> getCards
    (@PathVariable("board_id") long boardId,
        @PathVariable("list_id") long listId) {
        try {
            // Get the board
            Board board = boardService.getBoardById(boardId);
            // Get the list
            ListOfCards list = listOfCardsService.getListById(listId);
            // Check if the list is in the board
            if(!listOfCardsService.listInBoard(list, board)) {
                return ResponseEntity.badRequest().build();
            }
            List<Card> cards = cardService.getCards(list);
            // Return the cards with an HTTP 200 OK status
            return ResponseEntity.status(HttpStatus.OK).body(cards);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get a card given its id
     *
     * @param boardId
     * @param listId
     * @param cardId
     * @return the list
     */
    @GetMapping(path = {"/{card_id}/","/{card_id}"})
    private ResponseEntity<Card> getCardById(@PathVariable("board_id") long boardId,
                                                           @PathVariable("list_id") long listId,
                                                           @PathVariable("card_id") long cardId) {
        try {
            if(!validPath(boardId, listId, cardId)) {
                return ResponseEntity.badRequest().build();
            }
            // Get the card
            Card card = cardService.getCardById(cardId);
            // Return the card with an HTTP 200 OK status
            return ResponseEntity.status(HttpStatus.OK).body(card);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Create a new list of cards
     *
     * @param card
     * @param boardId
     * @param listId
     * @return the new list
     */
    @PostMapping(path = {"","/"})
    public ResponseEntity<Card> createCard(@RequestBody Card card,
                                           @PathVariable("board_id") long boardId,
                                           @PathVariable("list_id") long listId) {
        try {
            // Get the board to which the card will be added
            Board board = boardService.getBoardById(boardId);
            // Get the list to which the card will be added
            ListOfCards list = listOfCardsService.getListById(listId);
            // Check if the list is in the board
            if(!listOfCardsService.listInBoard(list, board)) {
                return ResponseEntity.badRequest().build();
            }
            // Save the new card to the database
            cardService.createCard(card, list, board);
            // Return the saved card with an HTTP 201 Created status
            return ResponseEntity.status(HttpStatus.CREATED).body(card);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Edit a card's title
     *
     * @param newTitle
     * @param boardId
     * @param listId
     * @param cardId
     * @return the edited card
     */
    @PutMapping(path = {"/{card_id}/","/{card_id}"})
    public ResponseEntity<Card> editCardTitleById(@RequestBody String newTitle,
                                                  @PathVariable("board_id") long boardId,
                                                  @PathVariable("list_id") long listId,
                                                  @PathVariable("card_id") long cardId) {

        try {
            if(!validPath(boardId, listId, cardId)) {
                return ResponseEntity.badRequest().build();
            }
            // Edit the list and save it in the database
            Card card = cardService.editCardTitle(cardId, newTitle);
            // Return the edited board with an HTTP 200 OK status
            return ResponseEntity.ok().body(card);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Delete a card given its id
     *
     * @param boardId
     * @param listId
     * @param cardId
     * @return the deleted card
     */
    @DeleteMapping(path = {"/{card_id}/","/{card_id}"})
    public ResponseEntity<Card> removeCardById(@PathVariable("board_id") long boardId,
                                               @PathVariable("list_id") long listId,
                                               @PathVariable("card_id") long cardId) {
        try {
            if(!validPath(boardId, listId, cardId)) {
                return ResponseEntity.badRequest().build();
            }
            // Get the card
            Card card = cardService.getCardById(cardId);
            // Delete the card
            cardService.deleteCardById(cardId);
            // Return the saved card with an HTTP 200 OK status
            return ResponseEntity.ok().build();
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Checks whether the path to a card is valid
     *
     * @param boardId
     * @param listId
     * @param cardId
     * @return true if the given path is valid
     * @throws Exception
     */
    private boolean validPath(long boardId, long listId, long cardId)
            throws Exception {
        // Get the board
        Board board = boardService.getBoardById(boardId);
        // Get the list
        ListOfCards list = listOfCardsService.getListById(listId);
        // Get the card
        Card card = cardService.getCardById(cardId);
        // Check if the list is in the board
        if(!listOfCardsService.listInBoard(list, board)) {
            return false;
        }
        // Check if the card is in the list
        if(!cardService.cardInList(card, list)) {
            return false;
        }
        return true;
    }
}
