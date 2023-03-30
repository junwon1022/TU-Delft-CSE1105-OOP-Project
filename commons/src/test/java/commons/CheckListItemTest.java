package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CheckListItemTest {

    Board board;
    ListOfCards list;
    Card card;
    CheckListItem checkListItem;
    CheckListItem checkListItem2;
    CheckListItem checkListItem3;

    /**
     * Set up all objects needed for testing
     */
    @BeforeEach
    public void setUp() {
        board = new Board("Algebra", "#ffffff", "#ffffff",
                "#ffff22", "#ffff22",
                "pass", new ArrayList<ListOfCards>(),
                new HashSet<Tag>(), new HashSet<Palette>());
        list = new ListOfCards("Grasple", board, new ArrayList<Card>());

        card = new Card("Homework", "Somewhat long description",
                "#ffffff", list, new ArrayList<CheckListItem>(),
                new HashSet<Tag>(), null);

        checkListItem = new CheckListItem("Do X", false, card);
        checkListItem2 = new CheckListItem("Do X", false, card);
        checkListItem3 = new CheckListItem("Do Z", true, card);
    }

    /**
     * Test the default constructor
     */
    @Test
    public void checkConstructor() {
        CheckListItem defaultItem = new CheckListItem();
        assertNotNull(defaultItem);
    }

    /**
     * Test the constructor with parameters
     */
    @Test
    public void checkParametrizedConstructor() {
        assertEquals("Do X", checkListItem.text);
        assertEquals(false, checkListItem.completed);
        assertEquals(card, checkListItem.card);
    }

    /**
     * Test equals and hashcode methods for two equal objects
     */
    @Test
    public void equalsHashCode() {
        assertEquals(checkListItem, checkListItem2);
        assertEquals(checkListItem.hashCode(), checkListItem2.hashCode());
    }

    /**
     * Test equals and hashcode methods for two different objects
     */
    @Test
    public void notEqualsHashCode() {
        assertNotEquals(checkListItem, checkListItem3);
        assertNotEquals(checkListItem.hashCode(), checkListItem3.hashCode());
    }

    /**
     * Test toString method
     */
    @Test
    public void hasToString() {
        var actual = checkListItem.toString();
        assertTrue(actual.contains(CheckListItem.class.getSimpleName()));
        assertTrue(actual.contains("\n"));
        assertTrue(actual.contains("text"));
        assertTrue(actual.contains("completed"));
        assertTrue(actual.contains("card"));
    }

    /**
     * Test checking a checklist item
     */
    @Test
    public void testChangeCompletionTrue() {
        checkListItem.changeCompletion();
        assertTrue(checkListItem.completed);
    }

    /**
     * Test unchecking a checklist item
     */
    @Test
    public void testChangeCompletionFalse() {
        checkListItem3.changeCompletion();
        assertFalse(checkListItem.completed);
    }
}
