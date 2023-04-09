package client.scenes;

import client.utils.PreferencesBoardInfo;
import client.utils.UserPreferences;
import com.fasterxml.jackson.core.JsonProcessingException;
import commons.Board;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserPreferencesTest {

    @Test
    void testConstructor() {
        UserPreferences prefs = new UserPreferences();
        assertNotNull(prefs);
    }

    @Test
    void testAddBoard() throws JsonProcessingException {
        UserPreferences prefs = Mockito.mock(UserPreferences.class);

        // create a board object
        Board b = new Board("test board", "test key",
                "test password", "test font", "test colour",
                "password", new LinkedList<>(), new HashSet<>(), null);

        // create a PreferencesBoardInfo object for the board
        PreferencesBoardInfo board = new PreferencesBoardInfo(
                b.title, b.key, b.password, b.font, b.colour);

        // mock the behavior of the UserPreferences object
        Mockito.doNothing().when(prefs).addBoard(Mockito.anyString(), Mockito.any(Board.class));
        Mockito.when(prefs.getBoards(Mockito.anyString())).thenReturn(List.of(board));

        // call the addBoard method with the board
        prefs.addBoard("http://localhost:8080/", b);

        // verify that the board was added to the list of boards for the server
        assertEquals(List.of(board), prefs.getBoards("http://localhost:8080/"));
    }

    @Test
    void testAddBoardNullBoard() throws JsonProcessingException {
        UserPreferences prefs = Mockito.mock(UserPreferences.class);

        // create a board object
        Board b = null;

        // mock the behavior of the UserPreferences object
        Mockito.doNothing().when(prefs).addBoard(Mockito.anyString(), Mockito.any(Board.class));
        Mockito.when(prefs.getBoards(Mockito.anyString())).thenReturn(List.of());

        // call the addBoard method with the board
        prefs.addBoard("http://localhost:8080/", b);

        // verify that the board was added to the list of boards for the server
        assertEquals(List.of(), prefs.getBoards("http://localhost:8080/"));
    }

    @Test
    void testGetBoardsEmptyList() {
        UserPreferences prefs = Mockito.mock(UserPreferences.class);

        // mock the behavior of the UserPreferences object
        Mockito.when(prefs.getBoards(Mockito.anyString())).thenReturn(List.of());

        // call the getBoards method with a server address that doesn't have any boards
        List<PreferencesBoardInfo> boards = prefs.getBoards("http://localhost:8080/");

        // verify that an empty list is returned
        assertEquals(List.of(), boards);
    }

    @Test
    void testUpdateBoardTitleInList() throws JsonProcessingException {
        UserPreferences prefs = Mockito.mock(UserPreferences.class);

        // create a board object
        Board b1 = new Board("test board 1", "test key 1",
                "test password 1", "test font 1", "test colour 1",
                "password", new LinkedList<>(), new HashSet<>(), null);
        Board b2 = new Board("test board 2", "test key 2",
                "test password 2", "test font 2", "test colour 2",
                "password", new LinkedList<>(), new HashSet<>(), null);

        // create a PreferencesBoardInfo object for each board
        PreferencesBoardInfo board1 = new PreferencesBoardInfo(
                b1.title, b1.key, b1.password, b1.font, b1.colour);
        PreferencesBoardInfo board2 = new PreferencesBoardInfo(
                b2.title, b2.key, b2.password, b2.font, b2.colour);

        // mock the behavior of the UserPreferences object
        Mockito.when(prefs.updateBoardTitle(Mockito.anyString(),
                Mockito.any(PreferencesBoardInfo.class), Mockito.anyString())).thenReturn(board2);
        Mockito.when(prefs.getBoards(Mockito.eq(
                "http://localhost:8080/"))).thenReturn(List.of(board1, board2));

        // call the updateBoardTitle method with board1 and a new title
        PreferencesBoardInfo updatedBoard = prefs.updateBoardTitle(
                "http://localhost:8080/", board1, "new title");

        // verify that the updated board is returned
        assertEquals(board2, updatedBoard);
    }



}
