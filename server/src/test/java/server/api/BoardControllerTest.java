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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import server.database.BoardRepository;
import server.services.BoardService;

import java.util.ArrayList;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;


@SuppressWarnings({"MissingJavadocMethod","JavadocMethod"})
@SpringBootTest
public class BoardControllerTest {

    private BoardRepository repo;

    private BoardController controller;

    private BoardService service;

    private SimpMessagingTemplate simpMessagingTemplate;

    //Begin testing


    @BeforeEach
    public void setup() {

        repo = Mockito.mock(BoardRepository.class);
        service = new BoardService(repo);
        simpMessagingTemplate = Mockito.mock(SimpMessagingTemplate.class);
        controller = new BoardController(service, simpMessagingTemplate);

        Board b = new Board(null, "#111111", "#111111","#111111","#111111", "pass", new ArrayList<>());
        when(repo.getById(0L)).thenReturn(b);

    }

    @Test
    public void addBoardCorrect() {
        Board b = new Board("My Schedule", "#111111","#111111","#111111","#111111", "pass", new ArrayList<>());
        var actual = controller.createBoard(b);
        assertEquals(HttpStatus.CREATED, actual.getStatusCode());
        assertEquals(b, actual.getBody());
    }

    //Check a null board
    @Test
    public void addBoardInCorrectNull() {
        Board b = new Board(null, "#111111","#111111","#111111","#111111", "pass", new ArrayList<>());
        var actual = controller.createBoard(b);
        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }
    //Check an empty board
    @Test
    public void addBoardInCorrectEmpty() {
        Board b = new Board("", "#111111", "#111111","#111111","#111111","pass", new ArrayList<>());
        var actual = controller.createBoard(b);
        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }
    //EditByTitle
    @Test
    public void editBoardTitleByIdCorrect() {
        Board b = new Board("My Board", "#111111","#111111","#111111","#111111", "pass", new ArrayList<>());
        when(repo.findById(b.id)).thenReturn(Optional.of(b));
        var actual = controller.editBoardTitleById("My New Board", b.id);

        assertEquals(OK, actual.getStatusCode());

    }

    //EditByTitle-Wrong
    @Test
    public void editBoardTitleByIdWrong() {
        Board b = new Board("My Board", "#111111", "#111111","#111111","#111111","pass", new ArrayList<>());
        var actual = controller.editBoardTitleById("", b.id);
        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }
    //EditByTitle-Null
    @Test
    public void editBoardTitleByIdWrongNull() {
        Board b = new Board("My Board", "#111111","#111111","#111111","#111111", "pass", new ArrayList<>());
        var actual = controller.editBoardTitleById(null, b.id);
        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }
    //DoesntExist
    @Test
    public void deleteBoardTitleDoesntExist() {
        Board b = new Board("My Board", "#111111", "#111111","#111111","#111111","pass", new ArrayList<>());
        when(repo.findById(b.id)).thenReturn(Optional.of(b));
        var actual = controller.removeBoardById(0);
        assertEquals(OK, actual.getStatusCode());
    }

}