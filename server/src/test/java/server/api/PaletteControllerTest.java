package server.api;

import commons.Board;

import commons.Palette;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import server.database.BoardRepository;
import server.database.PaletteRepository;
import server.services.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.OK;

@SuppressWarnings({"MissingJavadocMethod","JavadocMethod"})
@SpringBootTest
public class PaletteControllerTest {
    private BoardRepository boardRepo;
    private BoardService boardService;

    private ListOfCardsService listOfCardsService;

    private CardService cardService;

    private PaletteService service;

    private PaletteRepository repo;
    private PaletteController controller;

    private SimpMessagingTemplate simpMessagingTemplate;

    @BeforeEach
    void initialize(){
        repo = Mockito.mock(PaletteRepository.class);
        boardRepo = Mockito.mock(BoardRepository.class);



        service = new PaletteService(repo);
        boardService = new BoardService(boardRepo);
        simpMessagingTemplate = Mockito.mock(SimpMessagingTemplate.class);

        controller = new PaletteController(
                service,
                cardService,
                boardService,
                simpMessagingTemplate);
    }

    @Test
    void addPaletteTest(){
        Board b = new Board("test board", "#0000000", "#0000000",
                "#0000000","#0000000","password", new ArrayList<>(),
                new HashSet<>(), new HashSet<>());
        Palette p = new Palette("default", "#0000000", "#0000000",
                true, b, new HashSet<>());

        b.addPalette(p);

        when(boardRepo.findById(1L)).thenReturn(Optional.of(b));

        var actual = controller.addPalette(p, 1L);
        assertEquals(HttpStatus.CREATED, actual.getStatusCode());
        assertEquals(p, actual.getBody());
    }

    @Test
    void removePaletteTest(){
        Board b = new Board("test board", "#0000000", "#0000000",
                "#0000000","#0000000","password", new ArrayList<>(),
                new HashSet<>(), new HashSet<>());
        Palette p = new Palette("default", "#0000000", "#0000000",
                true, b, new HashSet<>());

        b.addPalette(p);

        when(boardRepo.findById(1L)).thenReturn((Optional.of(b)));
        when(repo.findById(p.id)).thenReturn(Optional.of(p));

        var actual = controller.removePalette(1L, p.id);
        assertEquals(OK, actual.getStatusCode());
    }

    @Test
    void renamePaletteTest(){
        Board b = new Board("test board", "#0000000", "#0000000",
                "#0000000","#0000000","password", new ArrayList<>(),
                new HashSet<>(), new HashSet<>());
        Palette p = new Palette("default", "#0000000", "#0000000",
                true, b, new HashSet<>());

        b.addPalette(p);

        when(boardRepo.findById(1L)).thenReturn((Optional.of(b)));
        when(repo.findById(p.id)).thenReturn(Optional.of(p));

        var actual = controller.renamePalette("Renamed",1L,p.id);
        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertEquals(p, actual.getBody());
        assertEquals("Renamed", actual.getBody().title);
    }

    @Test
    void setDefaultPaletteTest(){
        Board b = new Board("test board", "#0000000", "#0000000",
                "#0000000","#0000000","password", new ArrayList<>(),
                new HashSet<>(), new HashSet<>());
        Palette p = new Palette("palette", "#0000000", "#0000000",
                false, b, new HashSet<>());

        Palette def = new Palette("default", "#0000000", "#0000000",
                true, b, new HashSet<>());

        b.addPalette(p);
        b.addPalette(def);

        when(boardRepo.findById(1L)).thenReturn((Optional.of(b)));
        when(repo.findById(p.id)).thenReturn(Optional.of(p));

        var actual = controller.setDefault(1L,p.id);
        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertEquals(p, actual.getBody());
        assertEquals(true, actual.getBody().isDefault);
    }

    @Test
    void changeBackgroundTest(){
        Board b = new Board("test board", "#0000000", "#0000000",
                "#0000000","#0000000","password", new ArrayList<>(),
                new HashSet<>(), new HashSet<>());
        Palette p = new Palette("default", "#0000000", "#0000000",
                true, b, new HashSet<>());

        b.addPalette(p);

        when(boardRepo.findById(1L)).thenReturn((Optional.of(b)));
        when(repo.findById(p.id)).thenReturn(Optional.of(p));

        var actual = controller.changeBackground("#123456",1L,p.id);
        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertEquals(p, actual.getBody());
        assertEquals("#123456", actual.getBody().background);
    }

    @Test
    void changeFontTest(){
        Board b = new Board("test board", "#0000000", "#0000000",
                "#0000000","#0000000","password", new ArrayList<>(),
                new HashSet<>(), new HashSet<>());
        Palette p = new Palette("default", "#0000000", "#0000000",
                true, b, new HashSet<>());

        b.addPalette(p);

        when(boardRepo.findById(1L)).thenReturn((Optional.of(b)));
        when(repo.findById(p.id)).thenReturn(Optional.of(p));

        var actual = controller.changeFont("#123456",1L,p.id);
        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertEquals(p, actual.getBody());
        assertEquals("#123456", actual.getBody().font);
    }

    /**
     * Test getPaletteById method
     */
    @Test
    void getPaletteByIdTest(){
        Board b = new Board("test board", "#0000000", "#0000000",
                "#0000000","#0000000","password", new ArrayList<>(),
                new HashSet<>(), new HashSet<>());
        Palette p = new Palette("default", "#0000000", "#0000000",
                true, b, new HashSet<>());

        b.addPalette(p);

        when(boardRepo.findById(1L)).thenReturn((Optional.of(b)));
        when(repo.findById(p.id)).thenReturn(Optional.of(p));

        var actual = controller.getPaletteById(1L, p.id);
        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertEquals(p, actual.getBody());
    }

    /**
     * Test getPalettes method
     */
    @Test
    void getPalettesTest() {
        Board b = new Board("test board", "#0000000", "#0000000",
                "#0000000", "#0000000", "password", new ArrayList<>(),
                new HashSet<>(), new HashSet<>());
        Palette p = new Palette("default", "#0000000", "#0000000",
                true, b, new HashSet<>());

        b.addPalette(p);

        when(boardRepo.findById(1L)).thenReturn((Optional.of(b)));
        when(repo.findById(p.id)).thenReturn(Optional.of(p));

        var actual = controller.getPalettes(1L);
        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertEquals(b.getPalettes(), actual.getBody());
    }

    /**
     * Test getDefault method
     */
    @Test
    void getDefaultTest() {
        Board b = new Board("test board", "#0000000", "#0000000",
                "#0000000", "#0000000", "password", new ArrayList<>(),
                new HashSet<>(), new HashSet<>());
        Palette p = new Palette("default", "#0000000", "#0000000",
                true, b, new HashSet<>());

        b.addPalette(p);

        when(boardRepo.findById(1L)).thenReturn((Optional.of(b)));
        when(repo.findById(p.id)).thenReturn(Optional.of(p));

        var actual = controller.getDefault(1L);
        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertEquals(p, actual.getBody());
    }


    /**
     * Test getCardsOfPalette method
     */
    @Test
    void getCardsOfPaletteTest() {
        Board b = new Board("test board", "#0000000", "#0000000",
                "#0000000", "#0000000", "password", new ArrayList<>(),
                new HashSet<>(), new HashSet<>());
        Palette p = new Palette("default", "#0000000", "#0000000",
                true, b, new HashSet<>());

        b.addPalette(p);

        when(boardRepo.findById(1L)).thenReturn((Optional.of(b)));
        when(repo.findById(p.id)).thenReturn(Optional.of(p));

        var actual = controller.getCardsOfPalette(1L, p.id);
        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertEquals(p.cards, actual.getBody());
    }

    /**
     * Test paletteInBoard method
     */
    @Test
    void paletteInBoardTest() {
        Board b = new Board("test board", "#0000000", "#0000000",
                "#0000000", "#0000000", "password", new ArrayList<>(),
                new HashSet<>(), new HashSet<>());
        Palette p = new Palette("default", "#0000000", "#0000000",
                true, b, new HashSet<>());

        b.addPalette(p);

        when(boardRepo.findById(1L)).thenReturn((Optional.of(b)));
        when(repo.findById(p.id)).thenReturn(Optional.of(p));

        var actual = service.paletteInBoard(p, b);
        assertEquals(true, actual);
    }

}
