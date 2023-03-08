package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BoardTest {

    Board board;
    Board board2;
    Board board3;
    List<ListOfCards> listOfCards;
    ListOfCards list1;
    ListOfCards list2;
    ListOfCards list3;

    @BeforeEach
    public void setUp() {
        listOfCards = new ArrayList<>();
        list1 = new ListOfCards("Grasple", "#000000", board, new ArrayList<>());
        list2 = new ListOfCards("Grasple2", "#000000", board, new ArrayList<>());
        list3 = new ListOfCards("Grasple3", "#000000", board, new ArrayList<>());
        listOfCards.add(list1);
        listOfCards.add(list2);
        board = new Board("Algebra", "#ffffff",
                "read", "write", listOfCards);
        board2 = new Board("Algebra", "#ffffff",
                "read", "write", listOfCards);
        board3 = new Board("Algebra", "#ffffff",
                "read", "write2", listOfCards);

    }

    @Test
    public void checkConstructor() {
        Board defaultBoard = new Board();
        assertNotNull(defaultBoard);
    }

    @Test
    public void checkParametrizedConstructor() {
        assertEquals("Algebra", board.title);
        assertEquals("#ffffff", board.colour);
        assertEquals("read", board.readpassword);
        assertEquals("write", board.writepassword);
        assertEquals(listOfCards, board.lists);
    }

    @Test
    public void equalsHashCode() {
        assertEquals(board, board2);
        assertEquals(board.hashCode(), board2.hashCode());
    }

    @Test
    public void notEqualsHashCode() {
        assertNotEquals(board, board3);
        assertNotEquals(board.hashCode(), board3.hashCode());
    }

    @Test
    public void hasToString() {
        var actual = board.toString();
        assertTrue(actual.contains(Board.class.getSimpleName()));
        assertTrue(actual.contains("\n"));
        assertTrue(actual.contains("title"));
        assertTrue(actual.contains("colour"));
        assertTrue(actual.contains("readpassword"));
        assertTrue(actual.contains("writepassword"));
        assertTrue(actual.contains("lists"));
    }

    @Test
    public void testAddList() {
        board.addList(list3);
        listOfCards.add(list3);
        assertEquals(listOfCards, board.lists);
    }

    @Test
    public void testAddListNull() {
        board.addList(null);
        assertEquals(listOfCards, board.lists);
    }

    @Test
    public void testRemoveList() {
        board.removeList(list2);
        listOfCards.remove(list2);
        assertEquals(listOfCards, board.lists);
    }

    @Test
    public void testRemoveListNull() {
        board.removeList(null);
        assertEquals(listOfCards, board.lists);
    }

    @Test
    public void testSwapLists() {
        board.swapList(list1, list2);
        List<ListOfCards> listOfCards2 = new ArrayList<>();
        listOfCards2.add(list2);
        listOfCards2.add(list1);
        assertEquals(listOfCards2, board.lists);
    }

    @Test
    public void testMoveLists() {
        board.moveList(list1, 1);
        List<ListOfCards> listOfCards2 = new ArrayList<>();
        listOfCards2.add(list2);
        listOfCards2.add(list1);
        assertEquals(listOfCards2, board.lists);
    }
}
