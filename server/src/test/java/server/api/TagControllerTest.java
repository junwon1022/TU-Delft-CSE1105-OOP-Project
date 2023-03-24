///*
// * Copyright 2021 Delft University of Technology
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *    http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package server.api;
//
//import commons.*;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.HttpStatus;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import server.database.*;
//import server.services.*;
//
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.Optional;
//import java.util.Set;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.Mockito.when;
//import static org.springframework.http.HttpStatus.OK;
//
//@SuppressWarnings({"MissingJavadocMethod","JavadocMethod"})
//@SpringBootTest
//public class TagControllerTest {
//
//    private ListOfCardsRepository listRepo;
//
//    private ListOfCardsService listService;
//
//    private BoardRepository boardRepo;
//    private BoardService boardService;
//
//    private CardRepository cardRepo;
//
//    private CardService cardService;
//
//    private TagService service;
//
//    private TagRepository repo;
//    private TagController controller;
//
//    private SimpMessagingTemplate simpMessagingTemplate;
//
//
//    @BeforeEach
//    public void setup() {
//
//        repo = Mockito.mock(TagRepository.class);
//        boardRepo = Mockito.mock(BoardRepository.class);
//        listRepo = Mockito.mock(ListOfCardsRepository.class);
//        cardRepo = Mockito.mock(CardRepository.class);
//
//
//        service = new TagService(repo);
//        boardService = new BoardService(boardRepo);
//        listService = new ListOfCardsService(listRepo);
//        cardService = new CardService(cardRepo);
//        simpMessagingTemplate = Mockito.mock(SimpMessagingTemplate.class);
//
//        controller = new TagController(cardService,
//                listService,
//                service,
//                boardService,
//                simpMessagingTemplate);
//
//    }
//
//
//    @Test
//    public void addTagCorrect() {
//        Board b = new Board("My Schedule", "#111111", "#111111",
//                "#111111","#111111","pass", new ArrayList<>());
//        ListOfCards l = new ListOfCards("List 1", b, new ArrayList<>());
//        Card c = new Card("Card 1", "F", "#555555", l, new ArrayList<>(), new HashSet<>());
//        Tag t = new Tag("S", "#555555", new HashSet<>());
//
//        b.addList(l);
//        l.addCard(c);
//        c.addTag(t);
//        when(boardRepo.findById(1L)).thenReturn(Optional.of(b));
//        when(listRepo.findById(2L)).thenReturn(Optional.of(l));
//        when(cardRepo.findById(3L)).thenReturn(Optional.of(c));
//        var actual = controller.createTag(t, 1L, 2L, 3L);
//
//        assertEquals(HttpStatus.CREATED, actual.getStatusCode());
//        assertEquals(t, actual.getBody());
//    }
//
//
//    @Test
//    public void addTagWrong1() {
//        Board b = new Board("My Schedule", "#111111","#111111",
//                "#111111","#111111", "pass", new ArrayList<>());
//        ListOfCards l = new ListOfCards("List 1",b,new ArrayList<>());
//        Card c = new Card("Card 1","F","#555555",l,new ArrayList<>(),new HashSet<>());
//        Tag t = new Tag("","#555555",new HashSet<>());
//
//        b.addList(l);
//        l.addCard(c);
//        c.addTag(t);
//        when(boardRepo.findById(1L)).thenReturn(Optional.of(b));
//        when(listRepo.findById(2L)).thenReturn(Optional.of(l));
//        when(cardRepo.findById(3L)).thenReturn(Optional.of(c));
//        var actual = controller.createTag(t,1L,2L,3L);
//        assertEquals(HttpStatus.BAD_REQUEST, actual.getStatusCode());
//
//    }
//
//
//    @Test
//    public void addTagWrong2() {
//        Board b = new Board("My Schedule", "#111111","#111111",
//                "#111111","#111111", "pass", new ArrayList<>());
//        ListOfCards l = new ListOfCards("List 1",b,new ArrayList<>());
//        Card c = new Card("Card 1","F","#555555",l,new ArrayList<>(),new HashSet<>());
//        Tag t = new Tag(null,"#555555",new HashSet<>());
//
//        b.addList(l);
//        l.addCard(c);
//        c.addTag(t);
//        when(boardRepo.findById(1L)).thenReturn(Optional.of(b));
//        when(listRepo.findById(2L)).thenReturn(Optional.of(l));
//        when(cardRepo.findById(3L)).thenReturn(Optional.of(c));
//        var actual = controller.createTag(t,1L,2L,3L);
//        assertEquals(HttpStatus.BAD_REQUEST, actual.getStatusCode());
//    }
//
//    @Test
//    public void addTagWrongBoardInList() {
//        Board b = new Board("My Schedule", "#111111","#111111",
//                "#111111","#111111", "pass", new ArrayList<>());
//        Board b2 = new Board("My Schedule", "#111111", "#111111",
//                "#111111","#111111","pass", new ArrayList<>());
//        ListOfCards l = new ListOfCards("List 1",b2,new ArrayList<>());
//        Card c = new Card("Card 1","F","#555555",l,new ArrayList<>(),new HashSet<>());
//        Tag t = new Tag(null,"#555555",new HashSet<>());
//
//        l.addCard(c);
//        c.addTag(t);
//        when(boardRepo.findById(1L)).thenReturn(Optional.of(b));
//        when(listRepo.findById(2L)).thenReturn(Optional.of(l));
//        when(cardRepo.findById(3L)).thenReturn(Optional.of(c));
//        var actual = controller.createTag(t,1L,2L,3L);
//        assertEquals(HttpStatus.BAD_REQUEST, actual.getStatusCode());
//    }
//    @Test
//    public void addTagWrongListInCards() {
//        Board b = new Board("My Schedule", "#111111","#111111",
//                "#111111","#111111", "pass", new ArrayList<>());
//        ListOfCards l = new ListOfCards("List 1",b,new ArrayList<>());
//        ListOfCards l2 = new ListOfCards("List 1",b,new ArrayList<>());
//        Card c = new Card("Card 1","F","#555555",l2,new ArrayList<>(),new HashSet<>());
//        Tag t = new Tag(null,"#555555",new HashSet<>());
//        b.addList(l);
//        c.addTag(t);
//        when(boardRepo.findById(1L)).thenReturn(Optional.of(b));
//        when(listRepo.findById(2L)).thenReturn(Optional.of(l));
//        when(cardRepo.findById(3L)).thenReturn(Optional.of(c));
//        var actual = controller.createTag(t,1L,2L,3L);
//        assertEquals(HttpStatus.BAD_REQUEST, actual.getStatusCode());
//    }
//
//
//    @Test
//    public void addTagWrongCardTags() {
//        Board b = new Board("My Schedule", "#111111","#111111",
//                "#111111","#111111", "pass", new ArrayList<>());
//        ListOfCards l = new ListOfCards("List 1",b,new ArrayList<>());
//        Card c = new Card("Card 1","Finish CG Study","#555555",l,new ArrayList<>(),new HashSet<>());
//        Tag t = new Tag(null,"#555555",new HashSet<>());
//        b.addList(l);
//        c.addTag(t);
//        when(boardRepo.findById(1L)).thenReturn(Optional.of(b));
//        when(listRepo.findById(2L)).thenReturn(Optional.of(l));
//        when(cardRepo.findById(3L)).thenReturn(Optional.of(c));
//        var actual = controller.createTag(t,1L,2L,3L);
//        assertEquals(HttpStatus.BAD_REQUEST, actual.getStatusCode());
//    }
//
//
//    @Test
//    public void editTagCorrect() {
//        Board b = new Board("My Schedule", "#111111", "#111111",
//                "#111111","#111111","pass", new ArrayList<>());
//        ListOfCards l = new ListOfCards("List 1",b,new ArrayList<>());
//        Set<Tag> m = new HashSet<>();
//
//        Card c = new Card("Card 1","Finish CG Study","#555555",l,new ArrayList<>(),m);
//
//        Set<Card> s = new HashSet<>();
//        s.add(c);
//
//        Tag t = new Tag("Tag 2","#555555",s);
//
//        b.id = 0L;
//        l.id = 1L;
//        c.id = 2L;
//        t.id = 8L;
//
//
//        b.addList(l);
//        l.addCard(c);
//        c.addTag(t);
//
//        System.out.println("Tag     " + t + "------------------");
//
//        System.out.println("Tag size " + c.tags.size());
//        System.out.println("All tags     " + c.tags);
//
//        when(boardRepo.findById(b.id)).thenReturn(Optional.of(b));
//        when(listRepo.findById(l.id)).thenReturn(Optional.of(l));
//        when(cardRepo.findById(c.id)).thenReturn(Optional.of(c));
//        when(repo.findById(t.id)).thenReturn(Optional.of(t));
//
//        when(repo.save(Mockito.any(Tag.class))).thenAnswer(I -> I.getArguments()[0]);
//
//        var actual = controller.editTagName("New tag",b.id,l.id,c.id,t.id);
//        //System.out.println(actual.getBody());
//        assertEquals(HttpStatus.OK, actual.getStatusCode());
//        assertEquals(t, actual.getBody());
//    }
//
//    @Test
//    public void editTagWrongNull() {
//        Board b = new Board("My Schedule", "#111111","#111111",
//                "#111111","#111111", "pass", new ArrayList<>());
//        ListOfCards l = new ListOfCards("List 1",b,new ArrayList<>());
//        Card c = new Card("Card 1","Finish CG Study","#555555",l,new ArrayList<>(),new HashSet<>());
//        Tag t = new Tag(null,"#555555",new HashSet<>());
//
//        b.addList(l);
//        l.addCard(c);
//        c.addTag(t);
//        when(boardRepo.findById(1L)).thenReturn(Optional.of(b));
//        when(listRepo.findById(2L)).thenReturn(Optional.of(l));
//        when(cardRepo.findById(3L)).thenReturn(Optional.of(c));
//        when(repo.findById(t.id)).thenReturn(Optional.of(t));
//
//        when(repo.save(Mockito.any(Tag.class))).thenAnswer(I -> I.getArguments()[0]);
//
//        var actual = controller.editTagName(null,1L,2L,3L,t.id);
//        assertEquals(HttpStatus.BAD_REQUEST, actual.getStatusCode());
//
//    }
//    @Test
//    public void editTagWrongEmpty() {
//        Board b = new Board("My Schedule", "#111111", "#111111",
//                "#111111","#111111","pass", new ArrayList<>());
//        ListOfCards l = new ListOfCards("List 1",b,new ArrayList<>());
//        Card c = new Card("Card 1","Finish CG Study","#555555",
//                l,new ArrayList<>(),new HashSet<>());
//        Tag t = new Tag("","#555555",new HashSet<>());
//
//        b.addList(l);
//        l.addCard(c);
//        c.addTag(t);
//        when(boardRepo.findById(1L)).thenReturn(Optional.of(b));
//        when(listRepo.findById(2L)).thenReturn(Optional.of(l));
//        when(cardRepo.findById(3L)).thenReturn(Optional.of(c));
//        when(repo.findById(t.id)).thenReturn(Optional.of(t));
//
//        when(repo.save(Mockito.any(Tag.class))).thenAnswer(I -> I.getArguments()[0]);
//        var actual = controller.editTagName("",1L,2L,3L,t.id);
//        assertEquals(HttpStatus.BAD_REQUEST, actual.getStatusCode());
//
//    }
//    @Test
//    public void editTagWrong3() {
//        Board b = new Board("My Schedule", "#111111","#111111",
//                "#111111","#111111", "pass", new ArrayList<>());
//        ListOfCards l = new ListOfCards("List 1",b,new ArrayList<>());
//        Card c = new Card("Card 1","F","#555555",l,new ArrayList<>(),new HashSet<>());
//        Tag t = new Tag(null,"#555555",new HashSet<>());
//        b.addList(l);
//        c.addTag(t);
//        when(boardRepo.findById(1L)).thenReturn(Optional.of(b));
//        when(listRepo.findById(2L)).thenReturn(Optional.of(l));
//        when(repo.findById(t.id)).thenReturn(Optional.of(t));
//
//        when(repo.save(Mockito.any(Tag.class))).thenAnswer(I -> I.getArguments()[0]);
//        var actual = controller.editTagName("Solve CG Questions"
//                ,1L,2L,3L,t.id);
//        assertEquals(HttpStatus.BAD_REQUEST, actual.getStatusCode());
//
//    }
//    @Test
//    public void editTagColorCorrect() {
//        Board b = new Board("My Schedule", "#111111","#111111",
//                "#111111","#111111", "pass", new ArrayList<>());
//        ListOfCards l = new ListOfCards("List 1",b,new ArrayList<>());
//        Card c = new Card("Card 1","F","#555555",l,new ArrayList<>(),new HashSet<>());
//        Tag t = new Tag("Solve Phong Shading Questions","#555555",new HashSet<>());
//
//        b.addList(l);
//        l.addCard(c);
//        c.addTag(t);
//        when(boardRepo.findById(1L)).thenReturn(Optional.of(b));
//        when(listRepo.findById(2L)).thenReturn(Optional.of(l));
//        when(cardRepo.findById(3L)).thenReturn(Optional.of(c));
//        when(repo.findById(t.id)).thenReturn(Optional.of(t));
//        when(repo.save(Mockito.any(Tag.class))).thenAnswer(I -> I.getArguments()[0]);
//        var actual = controller.editColour("#333333",1L,2L,3L,t.id);
//        assertEquals(HttpStatus.OK, actual.getStatusCode());
//        assertEquals(t, actual.getBody());
//    }
//
//
//    @Test
//    public void deleteTagByIdCorrect() {
//        Board b = new Board("My Schedule", "#111111","#111111",
//                "#111111","#111111", "pass", new ArrayList<>());
//        ListOfCards l = new ListOfCards("List 1",b,new ArrayList<>());
//        Card c = new Card("Card 1","Finish CG Study","#555555",l,new ArrayList<>(),new HashSet<>());
//        Tag t = new Tag("Solve Phong Shading Questions","#555555",new HashSet<>());
//
//        b.addList(l);
//        l.addCard(c);
//        c.addTag(t);
//
//        when(boardRepo.findById(1L)).thenReturn((Optional.of(b)));
//        when(listRepo.findById(2L)).thenReturn((Optional.of(l)));
//        when(cardRepo.findById(3L)).thenReturn((Optional.of(c)));
//        when(repo.findById(t.id)).thenReturn(Optional.of(t));
//        var actual = controller.removeTagById(1L,2L,3L,t.id);
//
//        assertEquals(OK, actual.getStatusCode());
//
//    }
//
//
//
//}