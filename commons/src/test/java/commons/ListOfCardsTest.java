package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ListOfCardsTest {
    Board board;
    List<ListOfCards> listOfCards;
    ListOfCards list;
    ListOfCards list2;
    ListOfCards list3;
    List<Card> cards;
    Card card1;
    Card card2;
    Card card3;

    List<CheckListItem> checklist;
    CheckListItem checkListItem1;
    CheckListItem checkListItem2;

    Set<Tag> tags;
    Tag tag1;
    Tag tag2;

    Palette palette1;
    Palette palette2;

    Set<Palette> palettes;

    /**
     * Set up all objects needed for testing
     */
    @BeforeEach
    public void setUp() {
        checkListItem1 = new CheckListItem("Do X", false, card1);
        checkListItem2 = new CheckListItem("Do Y", false, card1);
        checklist = List.of(checkListItem1, checkListItem2);

        tag1 = new Tag("urgent", "#ff00ff",  "#ffff00", board, new HashSet<Card>());
        tag2 = new Tag("math", "#ffff00",  "#ffff00", board, new HashSet<Card>());
        tags = Set.of(tag1, tag2);

        palettes = new HashSet<>();
        palette1 = new Palette("Basic", "#ffffff",
                "#ffffff", true, board, new HashSet<Card>());
        palette2 = new Palette("Important", "#222222",
                "#222222", false, board, new HashSet<Card>());

        palettes.add(palette1);
        palettes.add(palette2);

        card1 = new Card("Homework", "Somewhat long description",
                "#ffffff", list, checklist, tags, palette1);
        card2 = new Card("Pre-lecture", "Somewhat long description",
                "#ffffff", list, checklist, tags, palette1);
        card3 = new Card("Project", "Somewhat long description",
                "#ffffff", list, checklist, tags, palette2);
        cards = new ArrayList<>();
        cards.add(card1);
        cards.add(card2);

        board = new Board("Algebra", "#ffffff", "#ff00ff",
                "#ffffff", "#ff00ff",
                "pass", listOfCards, tags, palettes);
        listOfCards = new ArrayList<>();
        list = new ListOfCards("Grasple",board, cards);
        list2 = new ListOfCards("Grasple", board, cards);
        list3 = new ListOfCards("Grasple3", board, cards);
        listOfCards.add(list);
        listOfCards.add(list2);
    }

    /**
     * Test the default constructor
     */
    @Test
    public void checkConstructor() {
        ListOfCards defaultList = new ListOfCards();
        assertNotNull(defaultList);
    }

    /**
     * Test the constructor with parameters
     */
    @Test
    public void checkParametrizedConstructor() {
        assertEquals("Grasple", list.title);
        assertEquals(board, list.board);
        assertEquals(cards, list.cards);
    }

    /**
     * Test equals and hashcode methods for two equal objects
     */
    @Test
    public void equalsHashCode() {
        assertEquals(list, list2);
        assertEquals(list.hashCode(), list2.hashCode());
    }

    /**
     * Test equals and hashcode methods for two different objects
     */
    @Test
    public void notEqualsHashCode() {
        assertNotEquals(list, list3);
        assertNotEquals(list.hashCode(), list3.hashCode());
    }

    /**
     * Test toString method
     */
    @Test
    public void hasToString() {
        var actual = list.toString();
        assertTrue(actual.contains(ListOfCards.class.getSimpleName()));
        assertTrue(actual.contains("\n"));
        assertTrue(actual.contains("title"));
        assertTrue(actual.contains("board"));
        assertTrue(actual.contains("cards"));
    }

    /**
     * Test card addition
     */
    @Test
    public void testAddCard() {
        list.addCard(card3);
        cards = List.of(card1, card2, card3);
        assertEquals(cards, list.cards);
    }

    /**
     * Test null addition
     */
    @Test
    public void testAddCardNull() {
        list.addCard(null);
        assertEquals(cards, list.cards);
    }

    /**
     * Test list removal
     */
    @Test
    public void testRemoveCard() {
        list.removeCard(card2);
        cards = List.of(card1);
        assertEquals(cards, list.cards);
    }

    /**
     * Test null removal
     */
    @Test
    public void testRemoveCardNull() {
        list.removeCard(null);
        assertEquals(cards, list.cards);
    }

    /**
     * Test card swapping
     */
    @Test
    public void testSwapCards() {
        list.swapCard(card1, card2);
        List<Card> cards2 = List.of(card2, card1);
        assertEquals(cards2, list.cards);
    }

    /**
     * Test moving cards
     */
    @Test
    public void testMoveCards() {
        list.moveCard(card1, 1);
        List<Card> cards2 = new ArrayList<>();
        cards2.add(card2);
        cards2.add(card1);
        assertEquals(cards2, list.cards);
    }
}
