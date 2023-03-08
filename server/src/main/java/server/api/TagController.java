package server.api;

import commons.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.database.TagRepository;
import server.services.*;

import java.util.Set;


@RestController
@RequestMapping("/api/boards/{board_id}/lists/{list_id}/cards/{card_id}/tags")
public class TagController {


    private final TagService tagService;
    private final CardService cardService;
    private final ListOfCardsService listOfCardsService;
    private final BoardService boardService;
    private final TagRepository tagRepository;

    /**
     * Constructor with parameters
     *
     * @param cardService
     * @param listOfCardsService
     * @param tagService
     * @param boardService
     */
    public TagController(CardService cardService,
                         ListOfCardsService listOfCardsService,
                         TagService tagService, BoardService boardService,
                         TagRepository tagRepository) {
        this.cardService = cardService;
        this.listOfCardsService = listOfCardsService;
        this.tagService = tagService;
        this.boardService = boardService;
        this.tagRepository = tagRepository;
    }

    /**
     * Get the tags within a given card
     * @param boardId
     * @param listId
     * @param cardId
     * @return the list of tags
     */
    @GetMapping("/")
    private ResponseEntity<Set<Tag>> getTags
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
     * @param boardId
     * @param listId
     * @param cardId
     * @param tagId
     * @return the list
     */
    @GetMapping("/{tag_id}")
    private ResponseEntity<Tag> getCheckById(@PathVariable("board_id") long boardId,
                                                           @PathVariable("list_id") long listId,
                                                           @PathVariable("card_id") long cardId,
                                                           @PathVariable("tag_id") long tagId) {
        try {
            if(!validPath(boardId, listId, cardId,tagId)) {
                return ResponseEntity.badRequest().build();
            }
            // Get the card
            Tag tag = tagService.getTagById(tagId);
            // Return the card with an HTTP 200 OK status
            return ResponseEntity.status(HttpStatus.OK).body(tag);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Create a new tag
     * @param tag
     * @param boardId
     * @param listId
     * @param cardId
     * @return the new tag
     */
    @PostMapping("/")
    public ResponseEntity<Tag> createTag(@RequestBody Tag tag,
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
            tagService.createTag(tag,card);
            // Return the saved tag with an HTTP 201 Created status
            return ResponseEntity.status(HttpStatus.CREATED).body(tag);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Edit a tag's name
     * @param newName
     * @param boardId
     * @param listId
     * @param cardId
     * @param tagId
     * @return the edited tag
     */
    @PostMapping("{tag_id}/name")
    public ResponseEntity<Tag> editName(@RequestBody String newName,
                                                  @PathVariable("board_id") long boardId,
                                                  @PathVariable("list_id") long listId,
                                                  @PathVariable("card_id") long cardId,
                                                  @PathVariable("tag_id") long tagId) {

        try {
            if(!validPath(boardId, listId, cardId, tagId)) {
                return ResponseEntity.badRequest().build();
            }

            // Edit the tag and save it in the database
            Tag tag = tagService.editTagName(tagId,newName);
            // Return the edited tag with an HTTP 200 OK status
            return ResponseEntity.ok().body(tag);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Edit a tag's name
     * @param newColour
     * @param boardId
     * @param listId
     * @param cardId
     * @param tagId
     * @return the edited tag
     */
    @PostMapping("{tag_id}/color")
    public ResponseEntity<Tag> editColour(@RequestBody String newColour,
                                        @PathVariable("board_id") long boardId,
                                        @PathVariable("list_id") long listId,
                                        @PathVariable("card_id") long cardId,
                                        @PathVariable("tag_id") long tagId) {

        try {
            if(!validPath(boardId, listId, cardId, tagId)) {
                return ResponseEntity.badRequest().build();
            }

            // Edit the tag and save it in the database
            Tag tag = tagService.editTagColour(tagId,newColour);
            // Return the edited tag with an HTTP 200 OK status
            return ResponseEntity.ok().body(tag);
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
    @DeleteMapping("/{tag_id}/remove")
    public ResponseEntity<Tag> removeTagById(@PathVariable("board_id") long boardId,
                                               @PathVariable("list_id") long listId,
                                               @PathVariable("card_id") long cardId,
                                               @PathVariable("tag_id") long tagId) {
        try {
            if(!validPath(boardId, listId, cardId, tagId)) {
                return ResponseEntity.badRequest().build();
            }
            // Delete the card
            tagService.deleteTagById(tagId);
            // Return the saved card with an HTTP 200 OK status
            return ResponseEntity.ok().build();
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    private boolean validPath(long boardId, long listId, long cardId, long tagId)
            throws Exception {
        // Get the board
        Board board = boardService.getBoardById(boardId);
        // Get the list
        ListOfCards list = listOfCardsService.getListById(listId);
        // Get the card
        Card card = cardService.getCardById(cardId);

        // Get the check
        Tag tag = tagService.getTagById(tagId);

        // Check if the list is in the board
        if(!listOfCardsService.listInBoard(list, board)) {
            return false;
        }
        // Check if the card is in the list
        if(!cardService.cardInList(card, list)) {
            return false;
        }

        if(!tagService.tagInCard(tag,card)){
            return false;
        }
        return true;
    }
}
