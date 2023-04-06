package client.scenes;

import client.utils.ServerUtils;
import client.utils.UserPreferences;
import commons.Card;
import commons.Board;
import commons.ListOfCards;
import commons.Tag;
import commons.Palette;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ServerUtilsTest {

    @Test
    void testChangeServer_validAddress() throws Exception {
        UserPreferences prefs = Mockito.mock(UserPreferences.class);
        ServerUtils utils = new ServerUtils(prefs);

        String serverAddress = "http://localhost:8080/";
        utils.changeServer(serverAddress);

        assertEquals(serverAddress, utils.getServerAddress());
    }

    @Test
    void testChangeServer_invalidAddress() throws Exception {
        UserPreferences prefs = Mockito.mock(UserPreferences.class);
        ServerUtils utils = new ServerUtils(prefs);

        String serverAddress = "http://invalidaddress:8080/";
        assertThrows(Exception.class, () -> utils.changeServer(serverAddress));
    }

    @Test
    void testAddCard() {
        UserPreferences prefs = Mockito.mock(UserPreferences.class);
        ServerUtils utils = new ServerUtils(prefs);

        Card card = new Card("title", "description", "colour", null, new LinkedList<>(), new HashSet<>(), null);
        ListOfCards list = new ListOfCards("test list", null, new LinkedList<>());
        list.addCard(card);
        utils.addList(list);

        assertTrue(utils.serverData.get(0).cards.contains(card));
    }

    @Test
    void testDeleteCard() {
        UserPreferences prefs = Mockito.mock(UserPreferences.class);
        ServerUtils utils = new ServerUtils(prefs);

        Card card = new Card("title", "description", "colour", null, new LinkedList<>(), new HashSet<>(), null);
        ListOfCards list = new ListOfCards("test list", null, new LinkedList<>());
        list.addCard(card);
        utils.addList(list);

        assertTrue(utils.serverData.get(0).cards.contains(card));

        utils.deleteCard(card);

        assertFalse(utils.serverData.get(0).cards.contains(card));
    }

    @Test
    void testAddList() {
        UserPreferences prefs = Mockito.mock(UserPreferences.class);
        ServerUtils utils = new ServerUtils(prefs);

        ListOfCards list = new ListOfCards("test list", null, new LinkedList<>());
        utils.addList(list);

        assertTrue(utils.serverData.contains(list));
    }

    @Test
    void testDeleteList() {
        UserPreferences prefs = Mockito.mock(UserPreferences.class);
        ServerUtils utils = new ServerUtils(prefs);

        ListOfCards list = new ListOfCards("test list", null, new LinkedList<>());
        utils.addList(list);

        assertTrue(utils.serverData.contains(list));

        utils.deleteList(list);

        assertFalse(utils.serverData.contains(list));
    }

    @Test
    void testRenameList() {
        UserPreferences prefs = Mockito.mock(UserPreferences.class);
        ServerUtils utils = new ServerUtils(prefs);

        ListOfCards list = new ListOfCards("test list", null, new LinkedList<>());
        utils.addList(list);

        assertTrue(utils.serverData.contains(list));

        utils.renameList1(list, "new name");

        assertEquals("new name", list.title);
    }

//    @Test
//    void testMoveCard() {
//        UserPreferences prefs = Mockito.mock(UserPreferences.class);
//        ServerUtils utils = new ServerUtils(prefs);
//
//        Card card = new Card("title", "description", "colour", null, new LinkedList<>(), new HashSet<>(), null);
//        ListOfCards list = new ListOfCards("test list", null, new LinkedList<>());
//        Card card2 = new Card("title2", "description2", "colour2", null, new LinkedList<>(), new HashSet<>(), null);
//        list.addCard(card);
//        list.addCard(card2);
//        utils.addList(list);
//
//        utils.moveCard(list, 0, 1);
//
//        assertEquals(card2, list.cards.get(0));
//        assertEquals(card, list.cards.get(1));
//    }

}
