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


    @BeforeEach
    public void setUp() {
        board = new Board("Algebra", "#ffffff",
                "read", "write", new ArrayList<ListOfCards>());
        list = new ListOfCards("Grasple", "#000000",
                                    board, new ArrayList<Card>());

        card = new Card("Homework", "Somewhat long description",
                "#ffffff", list, new ArrayList<CheckListItem>(), new HashSet<Tag>());

        checkListItem = new CheckListItem("Do X", false, card);
        checkListItem2 = new CheckListItem("Do X", false, card);
        checkListItem3 = new CheckListItem("Do Z", true, card);
    }

    @Test
    public void checkConstructor() {
        CheckListItem defaultItem = new CheckListItem();
        assertNotNull(defaultItem);
    }

    @Test
    public void checkParametrizedConstructor() {
        assertEquals("Do X", checkListItem.text);
        assertEquals(false, checkListItem.completed);
        assertEquals(card, checkListItem.card);
    }

    @Test
    public void equalsHashCode() {
        assertEquals(checkListItem, checkListItem2);
        assertEquals(checkListItem.hashCode(), checkListItem2.hashCode());
    }

    @Test
    public void notEqualsHashCode() {
        assertNotEquals(checkListItem, checkListItem3);
        assertNotEquals(checkListItem.hashCode(), checkListItem3.hashCode());
    }

    @Test
    public void hasToString() {
        var actual = checkListItem.toString();
        assertTrue(actual.contains(CheckListItem.class.getSimpleName()));
        assertTrue(actual.contains("\n"));
        assertTrue(actual.contains("text"));
        assertTrue(actual.contains("completed"));
        assertTrue(actual.contains("card"));
    }

    @Test
    public void testChangeCompletionTrue() {
        checkListItem.changeCompletion();
        assertTrue(checkListItem.completed);
    }

    @Test
    public void testChangeCompletionFalse() {
        checkListItem3.changeCompletion();
        assertFalse(checkListItem.completed);
    }
}
