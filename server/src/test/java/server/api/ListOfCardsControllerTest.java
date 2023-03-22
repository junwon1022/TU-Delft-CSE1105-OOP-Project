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

import commons.Board;
import commons.ListOfCards;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import server.database.BoardRepository;
import server.database.ListOfCardsRepository;
import server.services.BoardService;
import server.services.ListOfCardsService;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.OK;

@SuppressWarnings({"MissingJavadocMethod","JavadocMethod"})
@SpringBootTest
public class ListOfCardsControllerTest {

    private ListOfCardsRepository repo;

    private ListOfCardsController controller;

    private ListOfCardsService service;

    private BoardRepository boardRepo;

    private BoardService boardService;

    private SimpMessagingTemplate simpMessagingTemplate;

    @BeforeEach
    public void setup() {

        repo = Mockito.mock(ListOfCardsRepository.class);
        boardRepo = Mockito.mock(BoardRepository.class);
        service = new ListOfCardsService(repo);
        boardService = new BoardService(boardRepo);
        simpMessagingTemplate = Mockito.mock(SimpMessagingTemplate.class);
        controller = new ListOfCardsController(service,boardService, simpMessagingTemplate);
    }


    @Test
    public void addListOfCardsCorrect() {
        Board b = new Board("My Schedule", "#111111", "pass", new ArrayList<>());
        ListOfCards l = new ListOfCards("List 1","#555555",b,new ArrayList<>());
        b.addList(l);
        when(boardRepo.findById(b.id)).thenReturn(Optional.of(b));
        var actual = controller.createListOfCards(l,b.id);
        assertEquals(HttpStatus.CREATED, actual.getStatusCode());
        assertEquals(l, actual.getBody());
    }

    @Test
    public void addListOfCardsWrong() {
        Board b = new Board("My Schedule", "#111111", "pass", new ArrayList<>());
        ListOfCards l = new ListOfCards(null,"#555555",b,new ArrayList<>());
        b.addList(l);
        when(boardRepo.findById(b.id)).thenReturn(Optional.of(b));
        var actual = controller.createListOfCards(l,b.id);
        assertEquals(HttpStatus.BAD_REQUEST, actual.getStatusCode());

    }
    @Test
    public void addListOfCardsWrongEmpty() {
        Board b = new Board("My Schedule", "#111111", "pass", new ArrayList<>());
        ListOfCards l = new ListOfCards("","#555555",b,new ArrayList<>());
        b.addList(l);
        when(boardRepo.findById(b.id)).thenReturn(Optional.of(b));
        var actual = controller.createListOfCards(l,b.id);
        assertEquals(HttpStatus.BAD_REQUEST, actual.getStatusCode());

    }



    @Test
    public void editListOfCardTitleByIdCorrect() {
        Board b = new Board("My Board", "#111111", "pass", new ArrayList<>());
        ListOfCards l = new ListOfCards("My List","#555555",b,new ArrayList<>());
        b.addList(l);
        when(boardRepo.findById(b.id)).thenReturn((Optional.of(b)));
        when(repo.findById(l.id)).thenReturn((Optional.of(l)));
        var actual = controller.editListOfCardsTitleById("My New List", l.id,b.id);

        assertEquals(OK, actual.getStatusCode());

    }

    @Test
    public void editListOfCardTitleByIdWrongNull1() {
        Board b = new Board("My Board", "#111111", "pass", new ArrayList<>());
        ListOfCards l = new ListOfCards("My List", "#555555", b, new ArrayList<>());
        b.addList(l);
        when(boardRepo.findById(b.id)).thenReturn((Optional.of(b)));
        when(repo.findById(l.id)).thenReturn((Optional.of(l)));
        var actual = controller.editListOfCardsTitleById(null, l.id, b.id);

        assertEquals(HttpStatus.BAD_REQUEST, actual.getStatusCode());

    }
    @Test
    public void editListOfCardTitleByIdWrongEmpty() {
        Board b = new Board("My Board", "#111111", "pass", new ArrayList<>());
        ListOfCards l = new ListOfCards("My List","#555555",b,new ArrayList<>());
        b.addList(l);
        when(boardRepo.findById(b.id)).thenReturn((Optional.of(b)));
        when(repo.findById(l.id)).thenReturn((Optional.of(l)));
        var actual = controller.editListOfCardsTitleById("", l.id,b.id);

        assertEquals(HttpStatus.BAD_REQUEST, actual.getStatusCode());

    }
    @Test
    public void editListOfCardTitleByIdWrongNoListInBoard() {
        Board b = new Board("My Board 2", "#111111", "pass", new ArrayList<>());
        Board b2 = new Board("My Board 3", "#111111", "pass", new ArrayList<>());
        ListOfCards l = new ListOfCards("My List","#555555",b2,new ArrayList<>());

        when(boardRepo.findById(3L)).thenReturn((Optional.of(b)));
        when(boardRepo.findById(5L)).thenReturn((Optional.of(b2)));
        when(repo.findById(l.id)).thenReturn((Optional.of(l)));
        var actual = controller.editListOfCardsTitleById("My New List", l.id,3L);

        assertEquals(HttpStatus.BAD_REQUEST, actual.getStatusCode());

    }

    @Test
    public void deleteListOfCardByIdWrong() {
        Board b = new Board("My Board", "#111111", "pass", new ArrayList<>());
        ListOfCards l = new ListOfCards("My List","#555555",b,new ArrayList<>());

        when(boardRepo.findById(b.id)).thenReturn((Optional.of(b)));
        when(repo.findById(l.id)).thenReturn((Optional.of(l)));
        var actual = controller.removeListOfCardsById(b.id,l.id);

        assertEquals(OK, actual.getStatusCode());

    }



}