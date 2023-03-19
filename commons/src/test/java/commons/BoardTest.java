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
    List<ListOfCards> listsOfCards;
    ListOfCards list1;
    ListOfCards list2;
    ListOfCards list3;

    /**
     * Set up all objects needed for testing
     */
    @BeforeEach
    public void setUp() {
        listsOfCards = new ArrayList<>();
        list1 = new ListOfCards("Grasple", "#000000", board, new ArrayList<>());
        list2 = new ListOfCards("Grasple2", "#000000", board, new ArrayList<>());
        list3 = new ListOfCards("Grasple3", "#000000", board, new ArrayList<>());
        listsOfCards.add(list1);
        listsOfCards.add(list2);
        board = new Board("Algebra", "#ffffff",
                "pass", listsOfCards);
        board2 = new Board("Algebra", "#ffffff",
                "pass", listsOfCards);
        board3 = new Board("Algebra", "#ffffff",
                "pass2", listsOfCards);

    }

    /**
     * Test the default constructor
     */
    @Test
    public void checkConstructor() {
        Board defaultBoard = new Board();
        assertNotNull(defaultBoard);
    }

    /**
     * Test the constructor with parameters
     */
    @Test
    public void checkParametrizedConstructor() {
        assertEquals("Algebra", board.title);
        assertEquals("#ffffff", board.colour);
        assertEquals("pass", board.password);
        assertEquals(listsOfCards, board.lists);
    }

    /**
     * Test equals and hashcode methods for two equal objects
     */
    @Test
    public void equalsHashCode() {
        assertEquals(board, board2);
        assertEquals(board.hashCode(), board2.hashCode());
    }

    /**
     * Test equals and hashcode methods for two different objects
     */
    @Test
    public void notEqualsHashCode() {
        assertNotEquals(board, board3);
        assertNotEquals(board.hashCode(), board3.hashCode());
    }

    /**
     * Test toString method
     */
    @Test
    public void hasToString() {
        var actual = board.toString();
        assertTrue(actual.contains(Board.class.getSimpleName()));
        assertTrue(actual.contains("\n"));
        assertTrue(actual.contains("title"));
        assertTrue(actual.contains("colour"));
        assertTrue(actual.contains("password"));
        assertTrue(actual.contains("lists"));
    }

    /**
     * Test list addition
     */
    @Test
    public void testAddList() {
        board.addList(list3);
        listsOfCards.add(list3);
        assertEquals(listsOfCards, board.lists);
    }

    /**
     * Test null addition
     */
    @Test
    public void testAddListNull() {
        board.addList(null);
        assertEquals(listsOfCards, board.lists);
    }

    /**
     * Test list removal
     */
    @Test
    public void testRemoveList() {
        board.removeList(list2);
        listsOfCards.remove(list2);
        assertEquals(listsOfCards, board.lists);
    }

    /**
     * Test null removal
     */
    @Test
    public void testRemoveListNull() {
        board.removeList(null);
        assertEquals(listsOfCards, board.lists);
    }

    /**
     * Test list swapping
     */
    @Test
    public void testSwapLists() {
        board.swapList(list1, list2);
        List<ListOfCards> listOfCards2 = new ArrayList<>();
        listOfCards2.add(list2);
        listOfCards2.add(list1);
        assertEquals(listOfCards2, board.lists);
    }

    /**
     * Test moving lists
     */
    @Test
    public void testMoveLists() {
        board.moveList(list1, 1);
        List<ListOfCards> listOfCards2 = new ArrayList<>();
        listOfCards2.add(list2);
        listOfCards2.add(list1);
        assertEquals(listOfCards2, board.lists);
    }

    /**
     * Tests whether an invite key has been generated
     */
    @Test
    public void testGenerateInviteKey() {
        board.generateInviteKey();
        assertNotNull(board.key);
    }
}
