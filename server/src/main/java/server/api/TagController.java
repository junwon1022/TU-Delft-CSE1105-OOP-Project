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
@RequestMapping("/api/boards/{board_id}/tags")
public class TagController {


    private final TagService tagService;
    private final BoardService boardService;

    @Autowired
    private final SimpMessagingTemplate simpMessagingTemplate;

    /**
     * Constructor with parameters
     *
     * @param tagService
     * @param boardService
     * @param simpMessagingTemplate
     */
    @Autowired
    public TagController(TagService tagService, BoardService boardService,
                         SimpMessagingTemplate simpMessagingTemplate) {
        this.tagService = tagService;
        this.boardService = boardService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    /**
     * Get the tags within a given board
     *
     * @param boardId
     * @return the set of tags
     */
    @GetMapping(path = {"", "/"})
    private ResponseEntity<Set<Tag>> getTags
    (@PathVariable("board_id") long boardId) {
        try {
            // Get the board
            Board board = boardService.getBoardById(boardId);
            Set<Tag> tags = tagService.getTags(board);
                    //listOfCardsService.getListsOfCards(board);
            // Return the lists with an HTTP 200 OK status
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
     * @param tagId
     */
    @GetMapping(path = {"/{tag_id}", "/{tag_id}/"})
    private ResponseEntity<Tag> getTagById(@PathVariable("board_id") long boardId,
                                           @PathVariable("tag_id") long tagId) {
        try {
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
     */
    @PostMapping(path = {"", "/"})
    public ResponseEntity<Tag> createTag(@RequestBody Tag tag,
                                          @PathVariable("board_id") long boardId) {
        try {
            // Get the board
            Board board = boardService.getBoardById(boardId);

            TagService.createTag(tag, board);

            simpMessagingTemplate.convertAndSend("/topic/" + board.id, board);

            return ResponseEntity.status(HttpStatus.CREATED).body(tag);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Update a tag's name
     *
     * @param newName
     * @param boardId
     * @param tagId
     *
     * @return the updated tag
     */
    @PutMapping(path = {"/{tag_id}", "/{tag_id}/"})
    public ResponseEntity<Tag> updateTag(@RequestBody String newName,
                                          @PathVariable("board_id") long boardId,
                                          @PathVariable("tag_id") long tagId) {
        try {
            if(!validPath(boardId, tagId)) {
                return ResponseEntity.badRequest().build();
            }
            // Get the board to which the tag will be edited
            Board board = boardService.getBoardById(boardId);
            // Edit the tag and save it in the database
            Tag tag = TagService.editTagName(tagId, newName);
            // Send new data to all users in the board
            simpMessagingTemplate.convertAndSend("/topic/" + board.id, board);
            // Return the edited list with an HTTP 200 OK status
            return ResponseEntity.ok().body(tag);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Delete a tag
     *
     * @param boardId
     * @param tagId
     */
    @DeleteMapping(path = {"/{tag_id}", "/{tag_id}/"})
    public ResponseEntity<Tag> removeTagById(@PathVariable("board_id") long boardId,
                                                             @PathVariable("tag_id") long tagId) {
        try {
            if(!validPath(boardId, tagId)) {
                return ResponseEntity.badRequest().build();
            }
            // Get the board from which the list of tag will be removed
            Board board = boardService.getBoardById(boardId);
            // Get the tag
            Tag tag = tagService.getTagById(tagId);
            // Delete the tag
            tagService.deleteTagById(tagId);
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
     * Check if there's a valid path between the board and tag
     *
     * @param boardId
     * @param tagId
     *
     * @return true if there's a valid path, false otherwise
     */
    private boolean validPath(long boardId, long tagId) throws Exception {
        // Get the board
        Board board = boardService.getBoardById(boardId);
        // Get the tag
        Tag tag = TagService.getTagById(tagId);
        // Check if the tag is in the board
        if(!TagService.tagInBoard(tag, board)) {
            return false;
        }
        return true;
    }
}

