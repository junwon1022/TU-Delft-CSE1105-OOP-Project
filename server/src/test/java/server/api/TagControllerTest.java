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
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.OK;

@SuppressWarnings({"MissingJavadocMethod","JavadocMethod"})
@SpringBootTest
public class TagControllerTest {

    private BoardRepository boardRepo;
    private BoardService boardService;

    private ListOfCardsService listOfCardsService;

    private ListOfCardsRepository listRepo;

    private CardRepository cardRepo;
    private CardService cardService;

    private TagService service;

    private TagRepository repo;
    private TagController controller;

    private SimpMessagingTemplate simpMessagingTemplate;


    @BeforeEach
    public void setup() {
        repo = Mockito.mock(TagRepository.class);
        boardRepo = Mockito.mock(BoardRepository.class);
        listRepo = Mockito.mock(ListOfCardsRepository.class);
        cardRepo = Mockito.mock(CardRepository.class);
        cardService = new CardService(cardRepo);
        listOfCardsService = new ListOfCardsService(listRepo);

        service = new TagService(repo);
        boardService = new BoardService(boardRepo);
        simpMessagingTemplate = Mockito.mock(SimpMessagingTemplate.class);

        controller = new TagController(
                service,
                cardService,
                boardService,
                simpMessagingTemplate);

    }


    @Test
    public void addTagCorrect() {
        Board b = new Board("My Schedule", "#111111", "#111111",
                "#111111","#111111","pass", new ArrayList<>(),
                new HashSet<>(), new HashSet<>());
        Tag t = new Tag("S", "#555555","#555555", b, new HashSet<>());

        b.addTag(t);
        when(boardRepo.findById(1L)).thenReturn(Optional.of(b));
        var actual = controller.createTag(t, 1L);

        assertEquals(HttpStatus.CREATED, actual.getStatusCode());
        assertEquals(t, actual.getBody());
    }


    @Test
    public void addTagWrong1() {
        Board b = new Board("My Schedule", "#111111","#111111",
                "#111111","#111111", "pass", new ArrayList<>(),
                new HashSet<>(), new HashSet<>());
        Tag t = new Tag("","#555555", "#555555", b, new HashSet<>());

        b.addTag(t);
        when(boardRepo.findById(1L)).thenReturn(Optional.of(b));
        var actual = controller.createTag(t,1L);
        assertEquals(HttpStatus.BAD_REQUEST, actual.getStatusCode());

    }


    @Test
    public void addTagWrong2() {
        Board b = new Board("My Schedule", "#111111","#111111",
                "#111111","#111111", "pass", new ArrayList<>(),
                new HashSet<>(), new HashSet<>());
        Tag t = new Tag(null,"#555555", "#555555", b, new HashSet<>());

        b.addTag(t);
        when(boardRepo.findById(1L)).thenReturn(Optional.of(b));
        var actual = controller.createTag(t,1L);
        assertEquals(HttpStatus.BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void addTagWrongBoard() {
        Board b = new Board("My Schedule", "#111111","#111111",
                "#111111","#111111", "pass", new ArrayList<>(),
                new HashSet<>(), new HashSet<>());
        Board b2 = new Board("My Schedule", "#111111", "#111111",
                "#111111","#111111","pass", new ArrayList<>(),
                new HashSet<>(), new HashSet<>());

        Tag t = new Tag(null,"#555555", "#555555", b, new HashSet<>());

        b.addTag(t);
        when(boardRepo.findById(1L)).thenReturn(Optional.of(b2));
        var actual = controller.createTag(t,1L);
        assertEquals(HttpStatus.BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void editTagCorrect() {
        Board b = new Board("My Schedule", "#111111","#111111",
                "#111111","#111111", "pass", new ArrayList<>(),
                new HashSet<>(), new HashSet<>());
        Tag t = new Tag("Solve CG Questions","#555555","#555555", b, new HashSet<>());
        b.addTag(t);

        when(boardRepo.findById(1L)).thenReturn(Optional.of(b));
        when(repo.findById(t.id)).thenReturn(Optional.of(t));

        when(repo.save(Mockito.any(Tag.class))).thenAnswer(I -> I.getArguments()[0]);
        var actual = controller.updateTag(new Tag("Solve CG Questions","#555555",
                        "#555555", b, new HashSet<>()), 1L, t.id);
        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertEquals(t, actual.getBody());
    }

    @Test
    public void editTagWrong3() {
        Board b = new Board("My Schedule", "#111111", "#111111",
                "#111111","#111111","pass", new ArrayList<>(),
                new HashSet<>(), new HashSet<>());
        Set<Card> s = new HashSet<>();

        Tag t = new Tag("Tag 2","#555555", "#555555", b , s);

        b.addTag(t);

        when(boardRepo.findById(b.id)).thenReturn(Optional.of(b));
        when(repo.findById(t.id)).thenReturn(Optional.of(t));
        when(repo.save(Mockito.any(Tag.class))).thenAnswer(I -> I.getArguments()[0]);

        var actual = controller.updateTag(new Tag("New tag",
                "#555555", "#555555", b , s), 1L, t.id);
        assertEquals(HttpStatus.BAD_REQUEST, actual.getStatusCode());

    }

    @Test
    public void deleteTagByIdCorrect() {
        Board b = new Board("My Schedule", "#111111","#111111",
                "#111111","#111111", "pass", new ArrayList<>(),
                new HashSet<>(), new HashSet<>());
        Tag t = new Tag("Solve Phong Shading Questions","#555555", "#555555", b,new HashSet<>());

        b.addTag(t);

        when(boardRepo.findById(1L)).thenReturn((Optional.of(b)));
        when(repo.findById(t.id)).thenReturn(Optional.of(t));
        var actual = controller.removeTagById(1L,t.id);

        assertEquals(OK, actual.getStatusCode());
    }

    /**
     * Test getTags method.
     */
    @Test
    public void getTags() {
        Board b = new Board("My Schedule", "#111111","#111111",
                "#111111","#111111", "pass", new ArrayList<>(),
                new HashSet<>(), new HashSet<>());
        Tag t = new Tag("Solve Phong Shading Questions","#555555", "#555555", b,new HashSet<>());

        b.addTag(t);

        when(boardRepo.findById(1L)).thenReturn((Optional.of(b)));
        when(repo.findById(t.id)).thenReturn(Optional.of(t));
        var actual = controller.getTags(1L);

        assertEquals(OK, actual.getStatusCode());
    }

    /**
     * Test getTagsByCardId method
     */
    @Test
    public void getTagsByCardId() {
        Board b = new Board("My Schedule", "#111111","#111111",
                "#111111","#111111", "pass", new ArrayList<>(),
                new HashSet<>(), new HashSet<>());
        ListOfCards l = new ListOfCards("List 1",b,new ArrayList<>());
        Card c = new Card(null,"Finish CG Study","#555555",l,new ArrayList<>(),
                new HashSet<>(), null);
        Tag t = new Tag("Solve Phong Shading Questions","#555555", "#555555", b,new HashSet<>());

        b.addList(l);
        b.addTag(t);
        l.addCard(c);
        c.addTag(t);

        when(boardRepo.findById(b.id)).thenReturn((Optional.of(b)));
        when(listRepo.findById(l.id)).thenReturn(Optional.of(l));
        when(cardRepo.findById(c.id)).thenReturn(Optional.of(c));
        when(repo.findById(t.id)).thenReturn(Optional.of(t));

        var actual = controller.getTagsByCardId(b.id, l.id, c.id);

        assertEquals(OK, actual.getStatusCode());
    }
    /**
     * Test getTagById method.
     */
    @Test
    public void getTagByIdTest() {
        Board b = new Board("My Schedule", "#111111","#111111",
                "#111111","#111111", "pass", new ArrayList<>(),
                new HashSet<>(), new HashSet<>());
        Tag t = new Tag("Solve Phong Shading Questions","#555555", "#555555", b,new HashSet<>());

        b.addTag(t);

        when(boardRepo.findById(1L)).thenReturn((Optional.of(b)));
        when(repo.findById(t.id)).thenReturn(Optional.of(t));
        var actual = controller.getTagById(1L, t.id);

        assertEquals(OK, actual.getStatusCode());
    }

    /**
     * Test updateTagColor method.
     */
    @Test
    public void updateTagColorTest() throws Exception {
        Board b = new Board("My Schedule", "#111111","#111111",
                "#111111","#111111", "pass", new ArrayList<>(),
                new HashSet<>(), new HashSet<>());
        Tag t = new Tag("Solve Phong Shading Questions","#555555", "#555555", b,new HashSet<>());

        b.addTag(t);

        when(boardRepo.findById(1L)).thenReturn((Optional.of(b)));
        when(repo.findById(t.id)).thenReturn(Optional.of(t));
        when(repo.save(Mockito.any(Tag.class))).thenAnswer(I -> I.getArguments()[0]);
        var actual = controller.updateTagColor("#142857", t.id);

        assertEquals("#142857", t.colour);
    }
}