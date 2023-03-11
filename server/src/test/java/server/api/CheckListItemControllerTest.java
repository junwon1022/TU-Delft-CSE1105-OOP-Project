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
import commons.Card;
import commons.CheckListItem;
import commons.ListOfCards;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import server.database.BoardRepository;
import server.database.CardRepository;
import server.database.CheckListItemRepository;
import server.database.ListOfCardsRepository;
import server.services.BoardService;
import server.services.CardService;
import server.services.CheckListItemService;
import server.services.ListOfCardsService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.OK;

public class CheckListItemControllerTest {

    private ListOfCardsRepository listRepo;

    private ListOfCardsService listService;

    private BoardRepository boardRepo;
    private BoardService boardService;

    private CardRepository cardRepo;

    private CardService cardService;

    private CheckListItemService service;

    private CheckListItemRepository repo;
    private CheckListItemController controller;



    @BeforeEach
    public void setup() {

        repo = Mockito.mock(CheckListItemRepository.class);
        boardRepo = Mockito.mock(BoardRepository.class);
        listRepo = Mockito.mock(ListOfCardsRepository.class);
        cardRepo = Mockito.mock(CardRepository.class);


        service = new CheckListItemService(repo);
        boardService = new BoardService(boardRepo);
        listService = new ListOfCardsService(listRepo);
        cardService = new CardService(cardRepo);

        controller = new CheckListItemController(cardService,listService,service,boardService);

    }


    @Test
    public void addCheckCorrect() {
        Board b = new Board("My Schedule", "#111111", "read", "write", new ArrayList<>());
        ListOfCards l = new ListOfCards("List 1","#555555",b,new ArrayList<>());
        Card c = new Card("Card 1","Finish CG Study","#555555",l,new ArrayList<>(),new HashSet<>());
        CheckListItem ch = new CheckListItem("Solve Phong Shading Questions",true,c);
        b.addList(l);
        l.addCard(c);
        c.addCheckListItem(ch);
        when(boardRepo.findById(1L)).thenReturn(Optional.of(b));
        when(listRepo.findById(2L)).thenReturn(Optional.of(l));
        when(cardRepo.findById(3L)).thenReturn(Optional.of(c));
        var actual = controller.createCheck(ch,1L,2L,3L);
        System.out.println(actual.getBody());
        assertEquals(HttpStatus.CREATED, actual.getStatusCode());
        assertEquals(ch, actual.getBody());
    }


    @Test
    public void addCheckWrong1() {
        Board b = new Board("My Schedule", "#111111", "read", "write", new ArrayList<>());
        ListOfCards l = new ListOfCards("List 1","#555555",b,new ArrayList<>());
        Card c = new Card("Card 1","Finish CG Study","#555555",l,new ArrayList<>(),new HashSet<>());
        CheckListItem ch = new CheckListItem(null,true,c);
        b.addList(l);
        l.addCard(c);
        c.addCheckListItem(ch);
        when(boardRepo.findById(1L)).thenReturn(Optional.of(b));
        when(listRepo.findById(2L)).thenReturn(Optional.of(l));
        when(cardRepo.findById(3L)).thenReturn(Optional.of(c));
        var actual = controller.createCheck(ch,1L,2L,3L);
        assertEquals(HttpStatus.BAD_REQUEST, actual.getStatusCode());

    }


    @Test
    public void addCheckWrong2() {
        Board b = new Board("My Schedule", "#111111", "read", "write", new ArrayList<>());
        ListOfCards l = new ListOfCards("List 1","#555555",b,new ArrayList<>());
        Card c = new Card("Card 1","Finish CG Study","#555555",l,new ArrayList<>(),new HashSet<>());
        CheckListItem ch = new CheckListItem("",true,c);
        b.addList(l);
        l.addCard(c);
        c.addCheckListItem(ch);
        when(boardRepo.findById(1L)).thenReturn(Optional.of(b));
        when(listRepo.findById(2L)).thenReturn(Optional.of(l));
        when(cardRepo.findById(3L)).thenReturn(Optional.of(c));
        var actual = controller.createCheck(ch,1L,2L,3L);
        assertEquals(HttpStatus.BAD_REQUEST, actual.getStatusCode());

    }


    @Test
    public void addCheckWrongBoardinList() {
        Board b = new Board("My Schedule", "#111111", "read", "write", new ArrayList<>());
        Board b2 = new Board("My Schedule", "#111111", "read", "write", new ArrayList<>());
        ListOfCards l = new ListOfCards("List 1","#555555",b2,new ArrayList<>());
        Card c = new Card("Card 1","Finish CG Study","#555555",l,new ArrayList<>(),new HashSet<>());
        CheckListItem ch = new CheckListItem("Solve Phong Shading Questions",true,c);

        l.addCard(c);
        c.addCheckListItem(ch);
        when(boardRepo.findById(1L)).thenReturn(Optional.of(b));
        when(listRepo.findById(2L)).thenReturn(Optional.of(l));
        when(cardRepo.findById(3L)).thenReturn(Optional.of(c));
        var actual = controller.createCheck(ch,1L,2L,3L);
        assertEquals(HttpStatus.BAD_REQUEST, actual.getStatusCode());

    }




    @Test
    public void addCheckWrongListInCard() {
        Board b = new Board("My Schedule", "#111111", "read", "write", new ArrayList<>());
        ListOfCards l = new ListOfCards("List 1","#555555",b,new ArrayList<>());
        ListOfCards l2 = new ListOfCards("List 1","#555555",b,new ArrayList<>());
        Card c = new Card("Card 1","Finish CG Study","#555555",l2,new ArrayList<>(),new HashSet<>());
        CheckListItem ch = new CheckListItem("Solve Phong Shading Questions",true,c);
        b.addList(l);
        c.addCheckListItem(ch);

        when(boardRepo.findById(1L)).thenReturn(Optional.of(b));
        when(listRepo.findById(2L)).thenReturn(Optional.of(l));
        when(cardRepo.findById(3L)).thenReturn(Optional.of(c));
        var actual = controller.createCheck(ch,1L,2L,3L);
        assertEquals(HttpStatus.BAD_REQUEST, actual.getStatusCode());

    }




    @Test
    public void editCheckCorrect() {
        Board b = new Board("My Schedule", "#111111", "read", "write", new ArrayList<>());
        ListOfCards l = new ListOfCards("List 1","#555555",b,new ArrayList<>());
        Card c = new Card("CG","Finish CG Study","#555555",l,new ArrayList<>(),new HashSet<>());
        CheckListItem ch = new CheckListItem("Solve Phong Shading Questions",true,c);
        b.addList(l);
        l.addCard(c);
        c.addCheckListItem(ch);
        when(boardRepo.findById(1L)).thenReturn(Optional.of(b));
        when(listRepo.findById(2L)).thenReturn(Optional.of(l));
        when(cardRepo.findById(3L)).thenReturn(Optional.of(c));
        when(repo.findById(ch.id)).thenReturn(Optional.of(ch));
        var actual = controller.editCheckTextById("Finish ADS Study",1L,2L,3L,ch.id);

        assertEquals(OK, actual.getStatusCode());
    }

    @Test
    public void editCheckWrong() {
        Board b = new Board("My Schedule", "#111111", "read", "write", new ArrayList<>());
        ListOfCards l = new ListOfCards("List 1","#555555",b,new ArrayList<>());
        Card c = new Card("CG","Finish CG Study","#555555",l,new ArrayList<>(),new HashSet<>());
        CheckListItem ch = new CheckListItem("Solve Phong Shading Questions",true,c);
        b.addList(l);
        l.addCard(c);
        c.addCheckListItem(ch);
        when(boardRepo.findById(1L)).thenReturn(Optional.of(b));
        when(listRepo.findById(2L)).thenReturn(Optional.of(l));
        when(cardRepo.findById(3L)).thenReturn(Optional.of(c));
        when(repo.findById(ch.id)).thenReturn(Optional.of(ch));
        var actual = controller.editCheckTextById("" ,1L,2L,3L,ch.id);
        System.out.println(actual.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, actual.getStatusCode());
    }
    @Test
    public void editCheckWrongNull() {
        Board b = new Board("My Schedule", "#111111", "read", "write", new ArrayList<>());
        ListOfCards l = new ListOfCards("List 1","#555555",b,new ArrayList<>());
        Card c = new Card("CG","Finish CG Study","#555555",l,new ArrayList<>(),new HashSet<>());
        CheckListItem ch = new CheckListItem("Solve Phong Shading Questions",true,c);
        b.addList(l);
        l.addCard(c);
        c.addCheckListItem(ch);
        when(boardRepo.findById(1L)).thenReturn(Optional.of(b));
        when(listRepo.findById(2L)).thenReturn(Optional.of(l));
        when(cardRepo.findById(3L)).thenReturn(Optional.of(c));
        when(repo.findById(ch.id)).thenReturn(Optional.of(ch));
        var actual = controller.editCheckTextById(null,1L,2L,3L,ch.id);
        assertEquals(HttpStatus.BAD_REQUEST, actual.getStatusCode());
    }
    @Test
    public void editCheckWrongBoardInList() {
        Board b = new Board("My Schedule", "#111111", "read", "write", new ArrayList<>());
        Board b2 = new Board("My Schedule", "#111111", "read", "write", new ArrayList<>());
        ListOfCards l = new ListOfCards("List 1","#555555",b2,new ArrayList<>());
        Card c = new Card("Card 1","Finish CG Study","#555555",l,new ArrayList<>(),new HashSet<>());
        CheckListItem ch = new CheckListItem("Solve Phong Shading Questions",true,c);

        l.addCard(c);
        c.addCheckListItem(ch);
        when(boardRepo.findById(1L)).thenReturn(Optional.of(b));
        when(listRepo.findById(2L)).thenReturn(Optional.of(l));
        when(cardRepo.findById(3L)).thenReturn(Optional.of(c));
        when(repo.findById(ch.id)).thenReturn(Optional.of(ch));
        var actual = controller.editCheckTextById("Finish ADS Study",1L,2L,3L,ch.id);
        assertEquals(HttpStatus.BAD_REQUEST, actual.getStatusCode());
    }
    @Test
    public void editCheckWrongListInCard() {
        Board b = new Board("My Schedule", "#111111", "read", "write", new ArrayList<>());
        ListOfCards l = new ListOfCards("List 1","#555555",b,new ArrayList<>());
        ListOfCards l2 = new ListOfCards("List 1","#555555",b,new ArrayList<>());
        Card c = new Card("Card 1","Finish CG Study","#555555",l2,new ArrayList<>(),new HashSet<>());
        CheckListItem ch = new CheckListItem("Solve Phong Shading Questions",true,c);
        b.addList(l);
        c.addCheckListItem(ch);

        when(boardRepo.findById(1L)).thenReturn(Optional.of(b));
        when(listRepo.findById(2L)).thenReturn(Optional.of(l));
        when(cardRepo.findById(3L)).thenReturn(Optional.of(c));
        when(repo.findById(ch.id)).thenReturn(Optional.of(ch));
        var actual = controller.editCheckTextById("Finish ADS Study",1L,2L,3L,ch.id);
        assertEquals(HttpStatus.BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void deleteCheckByIdCorrect() {
        Board b = new Board("My Board", "#111111", "read", "write", new ArrayList<>());
        ListOfCards l = new ListOfCards("My List","#555555",b,new ArrayList<>());
        Card c = new Card("CG","Finish CG Study","#555555",l,new ArrayList<>(),new HashSet<>());
        CheckListItem ch = new CheckListItem("Solve Phong Shading Questions",true,c);
        b.addList(l);
        l.addCard(c);
        c.addCheckListItem(ch);

        when(boardRepo.findById(1L)).thenReturn((Optional.of(b)));
        when(listRepo.findById(2L)).thenReturn((Optional.of(l)));
        when(cardRepo.findById(3L)).thenReturn((Optional.of(c)));
        when(repo.findById(ch.id)).thenReturn(Optional.of(ch));
        var actual = controller.removeCheckById(1L,2L,3L,ch.id);

        assertEquals(OK, actual.getStatusCode());

    }



}