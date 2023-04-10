package client.scenes;

import client.utils.ServerUtils;
import client.utils.UserPreferences;
import commons.Card;
import commons.ListOfCards;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


import java.util.HashSet;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;

public class ServerUtilsTest {

    @Test
    void testChangeServerNullAddress() throws Exception {
        UserPreferences prefs = Mockito.mock(UserPreferences.class);
        ServerUtils utils = new ServerUtils(prefs);

        String serverAddress = null;
        assertThrows(Exception.class, () -> utils.changeServer(serverAddress));
    }

    @Test
    void testChangeServerValidAddress() throws Exception {
        UserPreferences prefs = Mockito.mock(UserPreferences.class);
        ServerUtils utils = new ServerUtils(prefs);

        String serverAddress = "http://localhost:8080/";
        utils.changeServer(serverAddress);

        assertEquals(serverAddress, utils.getServerAddress());
    }



    @Test
    void testAddCard() {
        UserPreferences prefs = Mockito.mock(UserPreferences.class);
        ServerUtils utils = new ServerUtils(prefs);

        Card card = new Card("title", "description", "colour", null,
                new LinkedList<>(), new HashSet<>(), null);
        ListOfCards list = new ListOfCards("test list", null, new LinkedList<>());
        list.addCard(card);
        utils.addList(list);

        assertTrue(utils.serverData.get(0).cards.contains(card));
    }

    @Test
    void testDeleteCard() {
        UserPreferences prefs = Mockito.mock(UserPreferences.class);
        ServerUtils utils = new ServerUtils(prefs);

        Card card = new Card("title", "description", "colour",
                null, new LinkedList<>(), new HashSet<>(), null);
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
    void testDeleteList2() {
        UserPreferences prefs = Mockito.mock(UserPreferences.class);
        ServerUtils utils = new ServerUtils(prefs);

        ListOfCards list = new ListOfCards("test list", null, new LinkedList<>());
        ListOfCards list2 = new ListOfCards("test list2", null, new LinkedList<>());
        utils.addList(list);
        utils.addList(list2);

        assertTrue(utils.serverData.contains(list));
        assertTrue(utils.serverData.contains(list2));

        utils.deleteList(list2);

        assertTrue(utils.serverData.contains(list));
        assertFalse(utils.serverData.contains(list2));
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

    @Test
    void testRenameList2() {
        UserPreferences prefs = Mockito.mock(UserPreferences.class);
        ServerUtils utils = new ServerUtils(prefs);

        ListOfCards list = new ListOfCards("test list", null, new LinkedList<>());
        utils.addList(list);

        assertTrue(utils.serverData.contains(list));

        utils.renameList1(list, null);

        assertEquals(null, list.title);
    }

    @Test
    void testRenameList3() {
        UserPreferences prefs = Mockito.mock(UserPreferences.class);
        ServerUtils utils = new ServerUtils(prefs);

        ListOfCards list = new ListOfCards("test list", null, new LinkedList<>());
        utils.addList(list);

        assertTrue(utils.serverData.contains(list));

        utils.renameList1(list, "");

        assertEquals("", list.title);
    }

}
