package server.api;

import commons.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import server.services.*;

import java.util.Set;


@RestController
@RequestMapping("/api/boards/{board_id}/lists/{list_id}/cards/{card_id}/tags")
public class TagController {


    private final TagService tagService;
    private final CardService cardService;
    private final ListOfCardsService listOfCardsService;
    private final BoardService boardService;

    @Autowired
    private final SimpMessagingTemplate simpMessagingTemplate;

    /**
     * Constructor with parameters
     *
     * @param cardService
     * @param listOfCardsService
     * @param tagService
     * @param boardService
     * @param simpMessagingTemplate
     */
    @Autowired
    public TagController(CardService cardService,
                         ListOfCardsService listOfCardsService,
                         TagService tagService, BoardService boardService,
                         SimpMessagingTemplate simpMessagingTemplate) {
        this.cardService = cardService;
        this.listOfCardsService = listOfCardsService;
        this.tagService = tagService;
        this.boardService = boardService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    /**
     * Get the tags within a given card
     *
     * @param boardId
     * @param listId
     * @param cardId
     * @return the set of tags
     */
    @GetMapping(path = {"", "/"})
    private ResponseEntity<Set<Tag>> getTag(
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
            if(!listOfCardsService.listInBoard(list, board)
                    || !cardService.cardInList(card, list)) {
                return ResponseEntity.badRequest().build();
            }
            Set<Tag> tags = tagService.getTags(card);
            // Return the cards with an HTTP 200 OK status
            return ResponseEntity.status(HttpStatus.OK).body(tags);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }


    /**
     * Get a tag given its id
     *
     * @param boardId
     * @param listId
     * @param cardId
     * @param tagId
     * @return the tag
     */
    @GetMapping(path = {"/{tag_id}", "/{tag_id}/"})
    private ResponseEntity<Tag> getTagById(@PathVariable("board_id") long boardId,
                                           @PathVariable("list_id") long listId,
                                           @PathVariable("card_id") long cardId,
                                           @PathVariable("tag_id") long tagId) {
        try {
            if(!validPath(boardId, listId, cardId, tagId)) {
                return ResponseEntity.badRequest().build();
            }
            // Get the tag
            Tag tag = tagService.getTagById(tagId);
            // Return the tag with an HTTP 200 OK status
            return ResponseEntity.status(HttpStatus.OK).body(tag);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Create a new tag
     *
     * @param tag
     * @param boardId
     * @param listId
     * @param cardId
     * @return the new tag
     */
    @PostMapping(path = {"", "/"})
    public ResponseEntity<Tag> createTag(@RequestBody Tag tag,
                                         @PathVariable("board_id") long boardId,
                                         @PathVariable("list_id") long listId,
                                         @PathVariable("card_id") long cardId) {
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

            // Save the new tag to the database
            tagService.createTag(tag, card);
            // Send new data to all users in the board
            simpMessagingTemplate.convertAndSend("/topic/" + board.id, board);
            // Return the saved tag with an HTTP 201 Created status
            return ResponseEntity.status(HttpStatus.CREATED).body(tag);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Edit a tag's name
     *
     * @param newName
     * @param boardId
     * @param listId
     * @param cardId
     * @param tagId
     * @return the edited tag
     */
    @PostMapping(path = {"/{tag_id}/name", "/{tag_id}/name/"})
    public ResponseEntity<Tag> editTagName(@RequestBody String newName,
                                           @PathVariable("board_id") long boardId,
                                           @PathVariable("list_id") long listId,
                                           @PathVariable("card_id") long cardId,
                                           @PathVariable("tag_id") long tagId) {

        try {
            if(!validPath(boardId, listId, cardId, tagId)) {
                return ResponseEntity.badRequest().build();
            }
            // Get the board in which the tag will be updated
            Board board = boardService.getBoardById(boardId);
            // Edit the tag and save it in the database
            Tag tag = tagService.editTagName(tagId,newName);
            // Send new data to all users in the board
            simpMessagingTemplate.convertAndSend("/topic/" + board.id, board);
            // Return the edited tag with an HTTP 200 OK status
            return ResponseEntity.ok().body(tag);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Edit a tag's colour
     *
     * @param newColour
     * @param boardId
     * @param listId
     * @param cardId
     * @param tagId
     * @return the edited tag
     */
    @PostMapping(
            path = {
                    "/{tag_id}/colour",
                    "/{tag_id}/colour",
                    "/{tag_id}/color",
                    "/{tag_id}/color"
            }
    )
    public ResponseEntity<Tag> editColour(@RequestBody String newColour,
                                          @PathVariable("board_id") long boardId,
                                          @PathVariable("list_id") long listId,
                                          @PathVariable("card_id") long cardId,
                                          @PathVariable("tag_id") long tagId) {

        try {
            if(!validPath(boardId, listId, cardId, tagId)) {
                return ResponseEntity.badRequest().build();
            }
            // Get the board in which the colour will be updated
            Board board = boardService.getBoardById(boardId);
            // Edit the tag and save it in the database
            Tag tag = tagService.editTagColour(tagId,newColour);
            // Send new data to all users in the board
            simpMessagingTemplate.convertAndSend("/topic/" + board.id, board);
            // Return the edited tag with an HTTP 200 OK status
            return ResponseEntity.ok().body(tag);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Delete a tag given its id
     *
     * @param boardId
     * @param listId
     * @param cardId
     * @param tagId
     * @return the deleted tag
     */
    @DeleteMapping(path = {"/{tag_id}", "/{tag_id}/"})
    public ResponseEntity<Tag> removeTagById(@PathVariable("board_id") long boardId,
                                             @PathVariable("list_id") long listId,
                                             @PathVariable("card_id") long cardId,
                                             @PathVariable("tag_id") long tagId) {
        try {
            if(!validPath(boardId, listId, cardId, tagId)) {
                return ResponseEntity.badRequest().build();
            }
            // Get the board from which the tag will be deleted
            Board board = boardService.getBoardById(boardId);
            // Get the card
            Tag tag = tagService.getTagById(tagId);
            // Delete the tag
            tagService.deleteTagById(tagId);
            // Send new data to all users in the board
            simpMessagingTemplate.convertAndSend("/topic/" + board.id, board);
            // Return the saved tag with an HTTP 200 OK status
            return ResponseEntity.ok().build();
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Checks whether the path to the tag exists
     *
     * @param boardId
     * @param listId
     * @param cardId
     * @param tagId
     * @return true if path is valid
     * @throws Exception
     */
    private boolean validPath(long boardId, long listId, long cardId, long tagId)
            throws Exception {
        // Get the board
        Board board = boardService.getBoardById(boardId);
        // Get the list
        ListOfCards list = listOfCardsService.getListById(listId);
        // Get the card
        Card card = cardService.getCardById(cardId);
        // Get the tag
        Tag tag = tagService.getTagById(tagId);

        // Check if the list is in the board
        if(!listOfCardsService.listInBoard(list, board)) {
            return false;
        }
        // Check if the card is in the list
        if(!cardService.cardInList(card, list)) {
            return false;
        }
        // Check if the tag is applied to the card
        if(!tagService.tagInCard(tag, card)){
            return false;
        }
        return true;
    }
}


