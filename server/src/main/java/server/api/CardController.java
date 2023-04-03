package server.api;

import commons.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import server.services.*;

import java.util.List;
import java.util.Set;


@RestController
@RequestMapping("/api/boards/{board_id}/lists/{list_id}/cards")
public class CardController {

    private final CardService cardService;
    private final ListOfCardsService listOfCardsService;
    private final BoardService boardService;

    private final TagService tagService;

    private final PaletteService paletteService;

    @Autowired
    private final SimpMessagingTemplate simpMessagingTemplate;

    /**
     * Constructor with parameters
     *
     * @param cardService
     * @param listOfCardsService
     * @param boardService
     * @param tagService
     * @param paletteService
     * @param simpMessagingTemplate
     */
    @Autowired
    public CardController(CardService cardService,
                       ListOfCardsService listOfCardsService,
                       BoardService boardService,
                       TagService tagService,
                       PaletteService paletteService,
                       SimpMessagingTemplate simpMessagingTemplate) {
        this.cardService = cardService;
        this.listOfCardsService = listOfCardsService;
        this.boardService = boardService;
        this.tagService = tagService;
        this.paletteService = paletteService;
        this.simpMessagingTemplate = simpMessagingTemplate;
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

            // Send new data to all users in the board
            simpMessagingTemplate.convertAndSend("/topic/" + board.id, board);

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
            // Get the board in which the card will be updated
            Board board = boardService.getBoardById(boardId);
            // Edit the list and save it in the database
            Card card = cardService.editCardTitle(cardId, newTitle);
            // Send new data to all users in the board
            simpMessagingTemplate.convertAndSend("/topic/" + board.id, board);
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
            // Get the board from which the card will be deleted
            Board board = boardService.getBoardById(boardId);
            // Get the card
            Card card = cardService.getCardById(cardId);
            // Delete the card
            cardService.deleteCardById(cardId);
            // Send new data to all users in the board
            simpMessagingTemplate.convertAndSend("/topic/" + board.id, board);
            // Return the saved card with an HTTP 200 OK status
            return ResponseEntity.ok().build();
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

   /**
     * retrieves the Put messages sent to the specific path
     * updates the description of the required card
     * @param newDescription the new description
     * @param boardId the boardId of the card
     * @param listId the listId of the card
     * @param cardId the id of the card
     * @return returns the updated Card entity
     */
    @PutMapping(path = {"/{card_id}/description"})
    public ResponseEntity<Card> updateCardDescription(@RequestBody String newDescription,
                                                      @PathVariable("board_id") long boardId,
                                                      @PathVariable("list_id") long listId,
                                                      @PathVariable("card_id") long cardId) {

        try {
            if (!validPath(boardId, listId, cardId)) {
                return ResponseEntity.badRequest().build();
            }
            Card card = cardService.editCardDescription(cardId, newDescription);
            Board board = boardService.getBoardById(boardId);

            simpMessagingTemplate.convertAndSend("/topic/" + board.id, board);

            return ResponseEntity.ok().body(card);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Gets the description of the specified card
     * @param boardId
     * @param listId
     * @param cardId
     * @return the description
     */
    @GetMapping(path = {"/{card_id}/description"})
    public ResponseEntity<String> getCardDescription(@PathVariable("board_id") long boardId,
                                                      @PathVariable("list_id") long listId,
                                                      @PathVariable("card_id") long cardId) {
        try {
            if (!validPath(boardId, listId, cardId)) {
                return ResponseEntity.badRequest().build();
            }
            String description = cardService.getCardDescription(cardId);
            return ResponseEntity.ok().body(description);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get all the tags of a card
     *
     * @param boardId
     * @param listId
     * @param cardId
     * @return the tags
     */
    @GetMapping(path = {"/{card_id}/tags","/{card_id}/tags/"})
    private ResponseEntity<Set<Tag>> getCardTags(@PathVariable("board_id") long boardId,
                                                  @PathVariable("list_id") long listId,
                                                  @PathVariable("card_id") long cardId) {
        try {
            if(!validPath(boardId, listId, cardId)) {
                return ResponseEntity.badRequest().build();
            }
            // Get the card
            Card card = cardService.getCardById(cardId);
            // Get the tags
            Set<Tag> tags = cardService.getTags(card);
            // Return the tags with an HTTP 200 OK status
            return ResponseEntity.status(HttpStatus.OK).body(tags);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Add a tag to a card
     *
     * @param boardId
     * @param listId
     * @param cardId
     * @param tagId
     * @return the card
     */
    @PostMapping(path = {"/{card_id}/tags/{tag_id}/","/{card_id}/tags/{tag_id}"})
    private ResponseEntity<Card> addTagToCard(@PathVariable("board_id") long boardId,
                                              @PathVariable("list_id") long listId,
                                              @PathVariable("card_id") long cardId,
                                              @PathVariable("tag_id") long tagId) {
        try {
            if(!validPath(boardId, listId, cardId)) {
                return ResponseEntity.badRequest().build();
            }
            // Get the board
            Board board = boardService.getBoardById(boardId);
            // Get the card
            Card card = cardService.getCardById(cardId);
            // Get the tag
            Tag tag = tagService.getTagById(tagId);
            // Add the tag to the card
            cardService.addTagToCard(card, tag);
            // Send new data to all users in the board
            simpMessagingTemplate.convertAndSend("/topic/" + board.id, board);
            // Return the card with an HTTP 200 OK status
            return ResponseEntity.status(HttpStatus.OK).body(card);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Delete a tag from a card
     *
     * @param boardId
     * @param listId
     * @param cardId
     * @param tagId
     * @return the card
     */
    @DeleteMapping(path = {"/{card_id}/tags/{tag_id}/","/{card_id}/tags/{tag_id}"})
    private ResponseEntity<Card> removeTagFromCard(@PathVariable("board_id") long boardId,
                                                   @PathVariable("list_id") long listId,
                                                   @PathVariable("card_id") long cardId,
                                                   @PathVariable("tag_id") long tagId) {
        try {
            if(!validPath(boardId, listId, cardId)) {
                return ResponseEntity.badRequest().build();
            }
            // Get the board
            Board board = boardService.getBoardById(boardId);
            // Get the card
            Card card = cardService.getCardById(cardId);
            // Get the tag
            Tag tag = tagService.getTagById(tagId);
            // Remove the tag from the card
            cardService.removeTagFromCard(card, tag);
            // Send new data to all users in the board
            simpMessagingTemplate.convertAndSend("/topic/" + board.id, board);
            // Return the card with an HTTP 200 OK status
            return ResponseEntity.status(HttpStatus.OK).body(card);
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

    /**
     * Add a palette to a card
     *
     * @param boardId
     * @param listId
     * @param cardId
     * @param palette
     * @return the card with the palette
     */
    @PostMapping(path = {"/{card_id}/palette/", "/{card_id}/palette"})
    private ResponseEntity<Card> addPaletteToCard(@PathVariable("board_id") long boardId,
                                              @PathVariable("list_id") long listId,
                                              @PathVariable("card_id") long cardId,
                                              @RequestBody() Palette palette) {
        try {
            if(!validPath(boardId, listId, cardId)) {
                return ResponseEntity.badRequest().build();
            }

            Board board = boardService.getBoardById(boardId);
            Card card = cardService.getCardById(cardId);

            cardService.addPaletteToCard(card, palette);
            // Send new data to all users in the board
            simpMessagingTemplate.convertAndSend("/topic/" + board.id, board);
            // Return the card with an HTTP 200 OK status
            return ResponseEntity.status(HttpStatus.OK).body(card);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Removes a palette from the card
     *
     * @param boardId
     * @param listId
     * @param cardId
     * @param paletteId
     * @return the card with the palette
     */
    @DeleteMapping(path = {"/{card_id}/palette/{palette_id}", "/{card_id}/palette/{palette_id}/"})
    private ResponseEntity<Card> deletePaletteFromCard(@PathVariable("board_id") long boardId,
                                                  @PathVariable("list_id") long listId,
                                                  @PathVariable("card_id") long cardId,
                                                  @PathVariable("palette_id") long paletteId) {
        try {
            if(!validPath(boardId, listId, cardId)) {
                return ResponseEntity.badRequest().build();
            }

            Board board = boardService.getBoardById(boardId);
            Card card = cardService.getCardById(cardId);
            Palette palette = paletteService.getPaletteById(paletteId);

            cardService.removePaletteFromCard(card, palette);
            // Send new data to all users in the board
            simpMessagingTemplate.convertAndSend("/topic/" + board.id, board);
            // Return the card with an HTTP 200 OK status
            return ResponseEntity.status(HttpStatus.OK).body(card);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

}
