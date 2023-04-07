package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
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

    Palette palette;

    /**
     * Set up all objects needed for testing
     */
    @BeforeEach
    public void setUp() {
        listsOfCards = new ArrayList<>();
        list1 = new ListOfCards("Grasple",  board, new ArrayList<>() );
        list2 = new ListOfCards("Grasple2", board, new ArrayList<>());
        list3 = new ListOfCards("Grasple3", board, new ArrayList<>());
        listsOfCards.add(list1);
        listsOfCards.add(list2);
        board = new Board("Algebra", "#ffffff", "#000000",
                "#ffffff", "#000000",
                "pass", listsOfCards, new HashSet<>(), new HashSet<>());
        board2 = new Board("Algebra", "#ffffff", "#000000",
                "#ffffff", "#000000",
                "pass", listsOfCards, new HashSet<>(), new HashSet<>());
        board3 = new Board("Algebra", "#ffffff", "#000000",
                "#ffffff", "#000000",
                "pass2", listsOfCards, new HashSet<>(), new HashSet<>());
        palette = new Palette("Palette", "#ffffff", "#000000", true, board, new HashSet<>());

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
        assertEquals("#000000", board.font);
        assertEquals("#ffffff", board.listColour);
        assertEquals("#000000", board.listFont);
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
        assertTrue(actual.contains("font"));
        assertTrue(actual.contains("listColour"));
        assertTrue(actual.contains("listFont"));
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
     * Tests whether an 'invite' key has been generated
     */
    @Test
    public void testGenerateInviteKey() {
        board.generateInviteKey();
        assertNotNull(board.key);
    }

    /**
     * Tests the getRandomString method
     */
    @Test
    public void testGetRandomString() {
        String key = board.getRandomString(3);
        assertEquals(3, key.length());
        assertNotNull(key);
    }

    /**
     * Tests the addPalette method
     */
    @Test
    public void testAddPalette() {
        Palette p = new Palette("Palette", "#ffffff", "#000000", true, board, new HashSet<>());
        assertFalse(board.palettes.contains(p));
        board.addPalette(p);
        assertEquals(p.title, "Palette");
    }

    /**
     * Tests the removePalette method
     */
    @Test
    public void testRemovePalette() {
        board.addPalette(palette);
        board.removePalette(palette);
        assertFalse(board.palettes.contains(palette));
    }


    /**
     * Tests the removeTag method
     */
    @Test
    public void testRemoveTag() {
        Tag tag = new Tag("Tag", "color","font", board, new HashSet<>());
        board.addTag(tag);
        board.removeTag(tag);
        assertFalse(board.tags.contains(tag));
    }

    /**
     * Tests the addTag method
     */
    @Test
    public void testAddTag() {
        Tag tag = new Tag("Tag", "color", "font",  board, new HashSet<>());
        board.addTag(tag);
        assertTrue(board.tags.contains(tag));
    }

    /**
     * Tests the getTagByName method
     */
    @Test
    public void testGetTagByName() {
        Tag tag = new Tag("Tag", "color", "font", board, new HashSet<>());
        board.addTag(tag);
        assertEquals(tag, board.getTagByName("Tag"));
    }

    /**
     * Test getPalettes method
     */
    @Test
    public void testGetPalette() {
        board.addPalette(palette);
        assertEquals(palette.title, board.getPalettes().iterator().next().title);
    }

    /**
     * Test setPalettes method
     */
    @Test
    public void testSetPalette() {
        board.addPalette(palette);
        board.setPalettes(new HashSet<>());
        assertEquals(new HashSet<>(), board.getPalettes());
    }

    /**
     * Test getDefaultPalette method
     */
    @Test
    public void testGetDefaultPalette() {
        board.addPalette(palette);
        assertEquals(palette, board.getDefaultPalette());
    }


}
