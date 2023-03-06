package server.api;

import commons.Board;
import commons.Card;
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
     * @param cardService
     * @param listOfCardsService
     * @param boardService
     */
    public CardController(CardService cardService,
                       ListOfCardsService listOfCardsService,
                       BoardService boardService) {
        this.cardService = cardService;
        this.listOfCardsService = listOfCardsService;
        this.boardService = boardService;
    }

    /**
     * Get the cards within a given list
     * @param board_id
     * @param list_id
     * @return the list of lists
     */
    @GetMapping("/")
    private ResponseEntity<List<Card>> getCards
                            (@PathVariable("board_id") long board_id,
                             @PathVariable("list_id") long list_id) {
        try {
            // Get the board
            Board board = boardService.getBoardById(board_id);
            // Get the list
            ListOfCards list = listOfCardsService.getListById(list_id);
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
     * @param board_id
     * @param list_id
     * @param card_id
     * @return the list
     */
    @GetMapping("/{card_id}")
    private ResponseEntity<Card> getCardById(@PathVariable("board_id") long board_id,
                                                           @PathVariable("list_id") long list_id,
                                                           @PathVariable("card_id") long card_id) {
        try {
            if(!validPath(board_id, list_id, card_id)) {
                return ResponseEntity.badRequest().build();
            }
            // Get the card
            Card card = cardService.getCardById(card_id);
            // Return the card with an HTTP 200 OK status
            return ResponseEntity.status(HttpStatus.OK).body(card);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Create a new list of cards
     * @param card
     * @param board_id
     * @param list_id
     * @return the new list
     */
    @PostMapping("/")
    public ResponseEntity<Card> createCard(@RequestBody Card card,
                                           @PathVariable("board_id") long board_id,
                                           @PathVariable("list_id") long list_id) {
        try {
            // Get the board to which the card will be added
            Board board = boardService.getBoardById(board_id);
            // Get the list to which the card will be added
            ListOfCards list = listOfCardsService.getListById(list_id);
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
     * @param newTitle
     * @param board_id
     * @param list_id
     * @param card_id
     * @return the edited card
     */
    @PostMapping("{card_id}")
    public ResponseEntity<Card> editCardTitleById(@RequestBody String newTitle,
                                                  @PathVariable("board_id") long board_id,
                                                  @PathVariable("list_id") long list_id,
                                                  @PathVariable("card_id") long card_id) {

        try {
            if(!validPath(board_id, list_id, card_id)) {
                return ResponseEntity.badRequest().build();
            }
            // Edit the list and save it in the database
            Card card = cardService.editCardTitle(card_id, newTitle);
            // Return the edited board with an HTTP 200 OK status
            return ResponseEntity.ok().body(card);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Delete a card given its id
     * @param board_id
     * @param list_id
     * @param card_id
     * @return the deleted card
     */
    @DeleteMapping("/{card_id}")
    public ResponseEntity<Card> removeCardById(@PathVariable("board_id") long board_id,
                                               @PathVariable("list_id") long list_id,
                                               @PathVariable("card_id") long card_id) {
        try {
            if(!validPath(board_id, list_id, card_id)) {
                return ResponseEntity.badRequest().build();
            }
            // Delete the card
            cardService.deleteCardById(card_id);
            // Return the saved card with an HTTP 200 OK status
            return ResponseEntity.ok().build();
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    private boolean validPath(long board_id, long list_id, long card_id)
            throws Exception {
        // Get the board
        Board board = boardService.getBoardById(board_id);
        // Get the list
        ListOfCards list = listOfCardsService.getListById(list_id);
        // Get the card
        Card card = cardService.getCardById(card_id);
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
