package server.api;

import commons.Board;
import commons.Card;
import commons.Palette;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import server.services.BoardService;
import server.services.CardService;
import server.services.PaletteService;

import java.util.Set;

@RestController
@RequestMapping("/api/boards/{board_id}/palettes")
public class PaletteController {
    private final PaletteService paletteService;

    private final CardService cardService;

    private final BoardService boardService;

    @Autowired
    private final SimpMessagingTemplate simpMessagingTemplate;


    /**
     * Constructor for the palette
     * @param paletteService
     * @param cardService
     * @param boardService
     * @param simpMessagingTemplate
     */
    @Autowired
    public PaletteController(PaletteService paletteService, CardService cardService,
                             BoardService boardService,
                             SimpMessagingTemplate simpMessagingTemplate) {
        this.paletteService = paletteService;
        this.cardService = cardService;
        this.boardService = boardService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }


    /**
     * Getter for palette, by its id
     * @param boardId
     * @param paletteId
     * @return the requested palette
     */
    @GetMapping(path = {"/{palette_id}/","/{palette_id}"})
    public ResponseEntity<Palette> getPaletteById(@PathVariable("board_id") long boardId,
                                              @PathVariable("palette_id") long paletteId){
        try {
            // Get the board
            Board board = boardService.getBoardById(boardId);
            if(board == null) return ResponseEntity.badRequest().build();

            Palette palette = paletteService.getPaletteById(paletteId);
            // Return the board with an HTTP 200 OK status
            return ResponseEntity.status(HttpStatus.OK).body(palette);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Getter for all the palettes within a board
     * @param boardId
     * @return the palettes from a board
     */
    @GetMapping(path = {"/",""})
    public ResponseEntity<Set<Palette>> getPalettes(@PathVariable("board_id") long boardId){
        try {
            // Get the board
            Board board = boardService.getBoardById(boardId);
            if(board == null) return ResponseEntity.badRequest().build();

            Set<Palette> palettes = paletteService.getPalettes(board);
            // Return the board with an HTTP 200 OK status
            return ResponseEntity.status(HttpStatus.OK).body(palettes);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Method that removes the palette from the board
     * @param boardId
     * @param paletteId
     * @return the removed palette
     */
    @DeleteMapping(path = {"/{palette_id}/","/{palette_id}"})
    public ResponseEntity<Palette> removePalette(@PathVariable ("board_id") long boardId,
                                                 @PathVariable("palette_id") long paletteId){
        Board board = null;
        try {
            board = boardService.getBoardById(boardId);
            // Get the palette
            Palette palette = paletteService.getPaletteById(paletteId);
            // Delete the palette
            paletteService.removePaletteById(paletteId);
            // Send new data to all users in the board
            simpMessagingTemplate.convertAndSend("/topic/" + board.id, board);

            // Return the saved list with an HTTP 200 OK status
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Method that adds a palette to the given board
     * @param palette
     * @param boardId
     * @return the added palette
     */
    @PostMapping(path = {"/",""})
    public ResponseEntity<Palette> addPalette(@RequestBody Palette palette,
                                              @PathVariable ("board_id") long boardId){
        try {
            Board board = boardService.getBoardById(boardId);
            paletteService.createPalette(palette, board);

            // Send new data to all users in the board
            simpMessagingTemplate.convertAndSend("/topic/" + board.id, board);
            // Return the saved list with an HTTP 201 Created status
            return ResponseEntity.status(HttpStatus.CREATED).body(palette);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Method that renames a given palette
     * @param newTitle
     * @param boardId
     * @param paletteId
     * @return - the renamed palette
     */
    @PutMapping(path = {"/{palette_id}", "/{palette_id}/"})
    public ResponseEntity<Palette> renamePalette(@RequestBody String newTitle,
                                         @PathVariable("board_id") long boardId,
                                         @PathVariable("palette_id") long paletteId) {
        try {
            Board board = boardService.getBoardById(boardId);
            Palette palette = paletteService.getPaletteById(paletteId);

            paletteService.renamePalette(paletteId, newTitle);
            // Send new data to all users in the board
            simpMessagingTemplate.convertAndSend("/topic/" + board.id, board);
            // Return the edited list with an HTTP 200 OK status
            return ResponseEntity.ok().body(palette);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Method that changes the background of a given palette
     * @param colour
     * @param boardId
     * @param paletteId
     * @return - the edited palette
     */
    @PutMapping(path = {"/{palette_id}/background", "/{palette_id}/background/"})
    public ResponseEntity<Palette> changeBackground(@RequestBody String colour,
                                                 @PathVariable("board_id") long boardId,
                                                 @PathVariable("palette_id") long paletteId) {
        try {
            Board board = boardService.getBoardById(boardId);
            Palette palette = paletteService.getPaletteById(paletteId);

            paletteService.editPaletteBackground(paletteId, colour);
            // Send new data to all users in the board
            simpMessagingTemplate.convertAndSend("/topic/" + board.id, board);
            // Return the edited list with an HTTP 200 OK status
            return ResponseEntity.ok().body(palette);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Method that changes the font colour of a given palette
     * @param colour
     * @param boardId
     * @param paletteId
     * @return - the edited palette
     */
    @PutMapping(path = {"/{palette_id}/font", "/{palette_id}/font/"})
    public ResponseEntity<Palette> changeFont(@RequestBody String colour,
                                                 @PathVariable("board_id") long boardId,
                                                 @PathVariable("palette_id") long paletteId) {
        try {
            Board board = boardService.getBoardById(boardId);
            Palette palette = paletteService.getPaletteById(paletteId);

            paletteService.editPaletteFont(paletteId, colour);
            // Send new data to all users in the board
            simpMessagingTemplate.convertAndSend("/topic/" + board.id, board);
            // Return the edited list with an HTTP 200 OK status
            return ResponseEntity.ok().body(palette);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Method that gets the default palette from the board
     * @param boardId
     * @return - the default palette
     */
    @GetMapping(path = {"/default/","/default"})
    public ResponseEntity<Palette> getDefault(@PathVariable("board_id") long boardId){
        try {
            // Get the board
            Board board = boardService.getBoardById(boardId);
            if(board == null) return ResponseEntity.badRequest().build();

            Palette defaultPalette = paletteService.getDefault(board);
            // Return the board with an HTTP 200 OK status
            return ResponseEntity.status(HttpStatus.OK).body(defaultPalette);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Method that sets the palette as default
     * @param boardId
     * @param paletteId
     * @return the default palette
     */
    @PutMapping(path = {"/{palette_id}/default", "/{palette_id}/default/"})
    public ResponseEntity<Palette> setDefault(@PathVariable("board_id") long boardId,
                                              @PathVariable("palette_id") long paletteId) {
        try {
            Board board = boardService.getBoardById(boardId);
            Palette palette = paletteService.getPaletteById(paletteId);

            paletteService.setDefault(palette, board);
            // Send new data to all users in the board
            simpMessagingTemplate.convertAndSend("/topic/" + board.id, board);
            // Return the edited list with an HTTP 200 OK status
            return ResponseEntity.ok().body(palette);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Method that gets the cards of a palette
     * @param boardId
     * @param paletteId
     * @return - the cards
     */
    @GetMapping(path = {"/{palette_id}/cards", "/{palette_id}/cards/"})
    public ResponseEntity<Set<Card>> getCardsOfPalette(@PathVariable("board_id") long boardId,
        @PathVariable("palette_id") long paletteId){
        try {
            // Get the board
            Board board = boardService.getBoardById(boardId);
            if(board == null) return ResponseEntity.badRequest().build();

            Set<Card> cards = paletteService.getCardsOfPalette(paletteId);
            // Return the board with an HTTP 200 OK status
            return ResponseEntity.status(HttpStatus.OK).body(cards);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }



}
