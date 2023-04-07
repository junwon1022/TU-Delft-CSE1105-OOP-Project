/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package server.api;

import commons.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import server.database.*;
import server.services.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@SuppressWarnings({"MissingJavadocMethod","JavadocMethod"})
@SpringBootTest
public class  CardControllerTest {

    private ListOfCardsRepository listRepo;


    private ListOfCardsService listService;

    private BoardRepository boardRepo;

    private BoardService boardService;

    private CardRepository repo;

    private TagRepository tagRepo;

    private PaletteRepository paletteRepository;

    private CardService service;

    private CardController controller;

    private TagService tagService;

    private PaletteService paletteService;

    private SimpMessagingTemplate simpMessagingTemplate;

    @BeforeEach
    public void setup() {

        repo = Mockito.mock(CardRepository.class);
        boardRepo = Mockito.mock(BoardRepository.class);
        listRepo = Mockito.mock(ListOfCardsRepository.class);
        tagRepo = Mockito.mock(TagRepository.class);
        paletteRepository = Mockito.mock(PaletteRepository.class);

        service = new CardService(repo);
        boardService = new BoardService(boardRepo);
        listService = new ListOfCardsService(listRepo);
        paletteService = new PaletteService(paletteRepository);
        simpMessagingTemplate = Mockito.mock(SimpMessagingTemplate.class);

        controller = new CardController(service,listService,
                boardService,tagService,paletteService, simpMessagingTemplate);

    }


    @Test
    public void addListOfCardsCorrect() {
        Board b = new Board("My Schedule", "#111111","#111111",
                "#111111","#111111", "pass", new ArrayList<>(),
                new HashSet<>(), new HashSet<>());
        ListOfCards l = new ListOfCards("List 1",b,new ArrayList<>());
        Card c = new Card("Card 1","Finish CG Study","#555555",l,new ArrayList<>(),
                new HashSet<>(), null);
        b.addList(l);
        l.addCard(c);
        when(boardRepo.findById(1L)).thenReturn(Optional.of(b));
        when(listRepo.findById(2L)).thenReturn(Optional.of(l));
        when(repo.findById(2L)).thenReturn(Optional.of(c));
        var actual = controller.createCard(c,1L,2L);
        assertEquals(HttpStatus.CREATED, actual.getStatusCode());
//        assertEquals(c, actual.getBody());
    }

    @Test
    public void addListOfCardsWrongNull() {
        Board b = new Board("My Schedule", "#111111","#111111",
                "#111111","#111111", "pass", new ArrayList<>(),
                new HashSet<>(), new HashSet<>());
        ListOfCards l = new ListOfCards("List 1",b,new ArrayList<>());
        Card c = new Card(null,"Finish CG Study","#555555",l,new ArrayList<>(),
                new HashSet<>(), null);
        b.addList(l);
        l.addCard(c);
        when(boardRepo.findById(1L)).thenReturn(Optional.of(b));
        when(listRepo.findById(2L)).thenReturn(Optional.of(l));
        when(repo.findById(2L)).thenReturn(Optional.of(c));
        var actual = controller.createCard(c,1L,2L);
        assertEquals(HttpStatus.BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void addListOfCardsWrongEmpty() {
        Board b = new Board("My Schedule", "#111111", "#111111",
                "#111111","#111111","pass", new ArrayList<>(),
                new HashSet<>(), new HashSet<>());
        ListOfCards l = new ListOfCards("List 1",b,new ArrayList<>());
        Card c = new Card("","Finish CG Study","#555555",l,new ArrayList<>(),
                new HashSet<>(), null);
        b.addList(l);
        l.addCard(c);
        when(boardRepo.findById(1L)).thenReturn(Optional.of(b));
        when(listRepo.findById(2L)).thenReturn(Optional.of(l));
        when(repo.findById(2L)).thenReturn(Optional.of(c));
        var actual = controller.createCard(c,1L,2L);
        assertEquals(HttpStatus.BAD_REQUEST, actual.getStatusCode());
    }
    @Test
    public void addListOfCardsWrongBoardListUnrelate() {
        Board b = new Board("My Schedule", "#111111","#111111",
                "#111111","#111111", "pass", new ArrayList<>(),
                new HashSet<>(), new HashSet<>());
        Board b2 = new Board("My Schedule 2", "#111111","#111111",
                "#111111","#111111", "pass", new ArrayList<>(),
                new HashSet<>(), new HashSet<>());
        ListOfCards l = new ListOfCards("List 1", b2,new ArrayList<>());
        Card c = new Card("","Finish CG Study","#555555",l,new ArrayList<>(),
                new HashSet<>(), null);

        l.addCard(c);
        when(boardRepo.findById(1L)).thenReturn(Optional.of(b));
        when(listRepo.findById(2L)).thenReturn(Optional.of(l));
        when(repo.findById(2L)).thenReturn(Optional.of(c));
        var actual = controller.createCard(c,1L,2L);
        assertEquals(HttpStatus.BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void editListOfCardsCorrect() {
        Board b = new Board("My Schedule", "#111111","#111111",
                "#111111","#111111", "pass", new ArrayList<>(),
                new HashSet<>(), new HashSet<>());
        ListOfCards l = new ListOfCards("List 1",b,new ArrayList<>());
        Card c = new Card("CG","Finish CG Study","#555555",l,new ArrayList<>(),
                new HashSet<>(), null);
        b.addList(l);
        l.addCard(c);
        when(boardRepo.findById(1L)).thenReturn(Optional.of(b));
        when(listRepo.findById(2L)).thenReturn(Optional.of(l));
        when(repo.findById(c.id)).thenReturn(Optional.of(c));
        var actual = controller.editCardTitleById("Finish ADS Study",1L,2L,c.id);
        assertEquals(OK, actual.getStatusCode());
    }

    @Test
    public void editListOfCardsWrongEmpty() {
        Board b = new Board("My Schedule", "#111111", "#111111",
                "#111111","#111111","pass", new ArrayList<>(),
                new HashSet<>(), new HashSet<>());
        ListOfCards l = new ListOfCards("List 1", b, new ArrayList<>());
        Card c = new Card("","Finish CG Study","#555555", l,
                new ArrayList<>(),new HashSet<>(), null);
        b.addList(l);
        l.addCard(c);
        when(boardRepo.findById(1L)).thenReturn(Optional.of(b));
        when(listRepo.findById(2L)).thenReturn(Optional.of(l));
        when(repo.findById(c.id)).thenReturn(Optional.of(c));
        var actual = controller.editCardTitleById("Finish ADS Study",1L,2L,c.id);
        assertEquals(OK, actual.getStatusCode());
    }
    @Test
    public void editListOfCardsWrongNull() {
        Board b = new Board("My Schedule", "#111111","#111111",
                "#111111","#111111", "pass", new ArrayList<>(),
                new HashSet<>(), new HashSet<>());
        ListOfCards l = new ListOfCards("List 1",b,new ArrayList<>());
        Card c = new Card(null,"Finish CG Study","#555555",l,new ArrayList<>(),
                new HashSet<>(), null);
        b.addList(l);
        l.addCard(c);
        when(boardRepo.findById(1L)).thenReturn(Optional.of(b));
        when(listRepo.findById(2L)).thenReturn(Optional.of(l));
        when(repo.findById(c.id)).thenReturn(Optional.of(c));
        var actual = controller.editCardTitleById("Finish ADS Study",
                1L,2L,c.id);
        assertEquals(OK, actual.getStatusCode());
    }

    @Test
    public void editListOfCardsWrongBoardListUnrelate() {
        Board b = new Board("My Schedule", "#111111", "#111111",
                "#111111","#111111","pass", new ArrayList<>(),
                new HashSet(), new HashSet<>());
        Board b2 = new Board("My Schedule 2", "#111111", "#111111",
                "#111111","#111111", "pass", new ArrayList<>(),
                new HashSet<>(), new HashSet<>());
        ListOfCards l = new ListOfCards("List 1", b2,new ArrayList<>());
        Card c = new Card("CG","Finish CG Study","#555555",l,new ArrayList<>(),
                new HashSet<>(), null);

        l.addCard(c);
        when(boardRepo.findById(1L)).thenReturn(Optional.of(b));
        when(listRepo.findById(2L)).thenReturn(Optional.of(l));
        when(repo.findById(c.id)).thenReturn(Optional.of(c));
        var actual = controller.editCardTitleById("Finish ADS Study",
                1L,2L,c.id);
        assertEquals(HttpStatus.BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void editListOfCardsWrongBoardListUnrelateCard() {
        Board b = new Board("My Schedule", "#111111","#111111",
                "#111111","#111111", "pass", new ArrayList<>(),
                new HashSet<>(), new HashSet<>());
        Board b2 = new Board("My Schedule 2", "#111111", "#111111",
                "#111111","#111111","pass", new ArrayList<>(),
                new HashSet<>(), new HashSet<>());
        ListOfCards l = new ListOfCards("List 1",b2,new ArrayList<>());
        ListOfCards l2 = new ListOfCards("List 1",b2,new ArrayList<>());
        Card c = new Card("CG","Finish CG Study","#555555",l2,new ArrayList<>(),
                new HashSet<>(), null);

        b.addList(l);
        l2.addCard(c);
        when(boardRepo.findById(1L)).thenReturn(Optional.of(b));
        when(listRepo.findById(2L)).thenReturn(Optional.of(l));
        when(repo.findById(c.id)).thenReturn(Optional.of(c));
        var actual = controller.editCardTitleById("Finish ADS Study",1L,2L,c.id);
        assertEquals(HttpStatus.BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void deleteListOfCardByIdCorrect() {
        Board b = new Board("My Board", "#111111", "#111111",
                "#111111","#111111","pass", new ArrayList<>(),
                new HashSet<>(), new HashSet<>());
        ListOfCards l = new ListOfCards("My List",b,new ArrayList<>());
        Card c = new Card("CG","Finish CG Study","#555555",l,
                new ArrayList<>(),new HashSet<>(), null);
        b.addList(l);
        l.addCard(c);

        when(boardRepo.findById(b.id)).thenReturn((Optional.of(b)));
        when(listRepo.findById(l.id)).thenReturn((Optional.of(l)));
        when(repo.findById(c.id)).thenReturn((Optional.of(c)));
        var actual = controller.removeCardById(b.id, l.id, c.id);

        assertEquals(OK, actual.getStatusCode());

    }

    /**
     * Get all cards in a list
     */
    @Test
    public void getAllCardsInListCorrect() {
        Board b = new Board("My Board", "#111111", "#111111",
                "#111111","#111111","pass", new ArrayList<>(),
                new HashSet<>(), new HashSet<>());
        ListOfCards l = new ListOfCards("My List",b,new ArrayList<>());
        Card c = new Card("CG","Finish CG Study","#555555",l,
                new ArrayList<>(),new HashSet<>(), null);
        Card c2 = new Card("ADS","Finish ADS Study","#555555",l,
                new ArrayList<>(),new HashSet<>(), null);
        b.addList(l);
        l.addCard(c);
        l.addCard(c2);

        when(boardRepo.findById(b.id)).thenReturn((Optional.of(b)));
        when(listRepo.findById(l.id)).thenReturn((Optional.of(l)));
        var actual = controller.getCards(b.id, l.id);

        assertEquals(OK, actual.getStatusCode());
        assertEquals(2, actual.getBody().size());
    }

    /**
     * Test getCardById with correct input
     */
    @Test
    public void getCardByIdTest() {
        Board b = new Board("My Board", "#111111", "#111111",
                "#111111","#111111","pass", new ArrayList<>(),
                new HashSet<>(), new HashSet<>());
        ListOfCards l = new ListOfCards("My List",b,new ArrayList<>());
        Card c = new Card("CG","Finish CG Study","#555555",l,
                new ArrayList<>(),new HashSet<>(), null);
        b.addList(l);
        l.addCard(c);

        when(boardRepo.findById(b.id)).thenReturn((Optional.of(b)));
        when(listRepo.findById(l.id)).thenReturn((Optional.of(l)));
        when(repo.findById(c.id)).thenReturn((Optional.of(c)));
        var actual = controller.getCardById(b.id, l.id, c.id);

        assertEquals(OK, actual.getStatusCode());
        assertEquals(c, actual.getBody());
    }

    /**
     * Test updateCardDescription method
     */
    @Test
    public void updateCardDescriptionTest() {
        Board b = new Board("My Board", "#111111", "#111111",
                "#111111","#111111","pass", new ArrayList<>(),
                new HashSet<>(), new HashSet<>());
        ListOfCards l = new ListOfCards("My List",b,new ArrayList<>());
        Card c = new Card("CG","Finish CG Study","#555555",l,
                new ArrayList<>(),new HashSet<>(), null);
        b.addList(l);
        l.addCard(c);

        when(boardRepo.findById(b.id)).thenReturn((Optional.of(b)));
        when(listRepo.findById(l.id)).thenReturn((Optional.of(l)));
        when(repo.findById(c.id)).thenReturn((Optional.of(c)));
        var actual = controller.updateCardDescription(
                "Updated", b.id, l.id, c.id);

        assertEquals(OK, actual.getStatusCode());
        assertEquals("Updated", c.description);
    }

    /**
     * Test getCardDescription method
     */
    @Test
    public void getCardDescriptionTest() {
        Board b = new Board("My Board", "#111111", "#111111",
                "#111111","#111111","pass", new ArrayList<>(),
                new HashSet<>(), new HashSet<>());
        ListOfCards l = new ListOfCards("My List",b,new ArrayList<>());
        Card c = new Card("CG","Finish CG Study","#555555",l,
                new ArrayList<>(),new HashSet<>(), null);
        b.addList(l);
        l.addCard(c);

        when(boardRepo.findById(b.id)).thenReturn((Optional.of(b)));
        when(listRepo.findById(l.id)).thenReturn((Optional.of(l)));
        when(repo.findById(c.id)).thenReturn((Optional.of(c)));
        var actual = controller.getCardDescription(b.id, l.id, c.id);

        assertEquals(OK, actual.getStatusCode());
        assertEquals(c.description, actual.getBody());
    }


    /**
     * Test addTagToCard method
     */
    @Test
    public void addTagToCardTest() {
        Board b = new Board("My Board", "#111111", "#111111",
                "#111111","#111111","pass", new ArrayList<>(),
                new HashSet<>(), new HashSet<>());
        ListOfCards l = new ListOfCards("My List",b,new ArrayList<>());
        Card c = new Card("CG","Finish CG Study","#555555",l,
                new ArrayList<>(),new HashSet<>(), null);
        Tag t = new Tag("name", "color", "description", b, new HashSet<>());
        b.addList(l);
        l.addCard(c);
        b.addTag(t);

        when(boardRepo.findById(b.id)).thenReturn((Optional.of(b)));
        when(listRepo.findById(l.id)).thenReturn((Optional.of(l)));
        when(repo.findById(c.id)).thenReturn((Optional.of(c)));
        when(tagRepo.findById(t.id)).thenReturn((Optional.of(t)));
        var actual = controller.addTagToCard(b.id, l.id, c.id, t.id);

        assertEquals(0, c.tags.size());
    }


    /**
     * Test removeTagFromCard method
     */
    @Test
    public void removeTagFromCardTest() {
        Board b = new Board("My Board", "#111111", "#111111",
                "#111111","#111111","pass", new ArrayList<>(),
                new HashSet<>(), new HashSet<>());
        ListOfCards l = new ListOfCards("My List",b,new ArrayList<>());
        Card c = new Card("CG","Finish CG Study","#555555",l,
                new ArrayList<>(),new HashSet<>(), null);
        Tag t = new Tag("name", "color", "description", b, new HashSet<>());
        b.addList(l);
        l.addCard(c);
        b.addTag(t);
        c.addTag(t);

        when(boardRepo.findById(b.id)).thenReturn((Optional.of(b)));
        when(listRepo.findById(l.id)).thenReturn((Optional.of(l)));
        when(repo.findById(c.id)).thenReturn((Optional.of(c)));
        when(tagRepo.findById(t.id)).thenReturn((Optional.of(t)));
        var actual = controller.removeTagFromCard(b.id, l.id, c.id, t.id);

        assertEquals(1, c.tags.size());
    }


    /**
     * Test addPaletteToCard method
     */
    @Test
    public void addPaletteToCardTest() {
        Board b = new Board("My Board", "#111111", "#111111",
                "#111111","#111111","pass", new ArrayList<>(),
                new HashSet<>(), new HashSet<>());
        ListOfCards l = new ListOfCards("My List",b,new ArrayList<>());
        Card c = new Card("CG","Finish CG Study","#555555",l,
                new ArrayList<>(),new HashSet<>(), null);
        b.addList(l);
        l.addCard(c);
        Palette p = new Palette("palette", "#111111", "#111111",
                true ,b, new HashSet<>());

        when(boardRepo.findById(b.id)).thenReturn((Optional.of(b)));
        when(listRepo.findById(l.id)).thenReturn((Optional.of(l)));
        when(repo.findById(c.id)).thenReturn((Optional.of(c)));
        when(paletteRepository.findById(c.id)).thenReturn((Optional.of(p)));
        var actual = controller.addPaletteToCard(b.id, l.id, c.id, p);

        assertEquals(OK, actual.getStatusCode());
        assertEquals(0, c.palette.id);
    }

    /**
     * Test deletePaletteFromCard method
     */
    @Test
    public void deletePaletteFromCardTest() {
        Board b = new Board("My Board", "#111111", "#111111",
                "#111111","#111111","pass", new ArrayList<>(),
                new HashSet<>(), new HashSet<>());
        ListOfCards l = new ListOfCards("My List",b,new ArrayList<>());
        Card c = new Card("CG","Finish CG Study","#555555",l,
                new ArrayList<>(),new HashSet<>(), null);
        b.addList(l);
        l.addCard(c);
        Palette p = new Palette("palette", "#111111", "#111111",
                true ,b, new HashSet<>());

        when(boardRepo.findById(b.id)).thenReturn((Optional.of(b)));
        when(listRepo.findById(l.id)).thenReturn((Optional.of(l)));
        when(repo.findById(c.id)).thenReturn((Optional.of(c)));
        when(paletteRepository.findById(c.id)).thenReturn((Optional.of(p)));
        var actual = controller.addPaletteToCard(b.id, l.id, c.id, p);

        assertEquals(OK, actual.getStatusCode());
        assertEquals(0, c.palette.id);

        var actual2 = controller.deletePaletteFromCard(b.id, l.id, c.id, 1);
        assertEquals(BAD_REQUEST, actual2.getStatusCode());
    }

    /**
     * Test getCardTags method
     */
    @Test
    public void getCardTagsTest() {
        Board b = new Board("My Board", "#111111", "#111111",
                "#111111","#111111","pass", new ArrayList<>(),
                new HashSet<>(), new HashSet<>());
        ListOfCards l = new ListOfCards("My List",b,new ArrayList<>());
        Card c = new Card("CG","Finish CG Study","#555555",l,
                new ArrayList<>(),new HashSet<>(), null);
        Tag t = new Tag("name", "color", "description", b, new HashSet<>());
        b.addList(l);
        l.addCard(c);
        b.addTag(t);
        c.addTag(t);

        when(boardRepo.findById(b.id)).thenReturn((Optional.of(b)));
        when(listRepo.findById(l.id)).thenReturn((Optional.of(l)));
        when(repo.findById(c.id)).thenReturn((Optional.of(c)));
        when(tagRepo.findById(t.id)).thenReturn((Optional.of(t)));
        var actual = controller.getCardTags(b.id, l.id, c.id);

        assertEquals(OK, actual.getStatusCode());
        assertEquals(1, actual.getBody().size());
    }
}