package server.api;

import commons.Board;
import commons.Palette;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import server.services.BoardService;
import server.services.CardService;
import server.services.ListOfCardsService;
import server.services.PaletteService;

import java.util.Set;

@RestController
@RequestMapping("/api/boards/{board_id}/palettes")
public class PaletteController {
    private final PaletteService paletteService;

    private final CardService cardService;
    private final ListOfCardsService listOfCardsService;
    private final BoardService boardService;

    @Autowired
    private final SimpMessagingTemplate simpMessagingTemplate;


    @Autowired
    public PaletteController(PaletteService paletteService, CardService cardService,
                             ListOfCardsService listOfCardsService, BoardService boardService,
                             SimpMessagingTemplate simpMessagingTemplate) {
        this.paletteService = paletteService;
        this.cardService = cardService;
        this.listOfCardsService = listOfCardsService;
        this.boardService = boardService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }



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

    @PostMapping(path = {"/",""})
    public ResponseEntity<Palette> addPalette(@RequestBody Palette palette,
                                              @PathVariable ("board_id") long boardId){
        try {
            // Get the board to which the list will be added
            Board board = boardService.getBoardById(boardId);
            // Save the new list of cards to the database
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


}
