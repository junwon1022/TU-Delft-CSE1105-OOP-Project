package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TagTest {

    Board board;
    ListOfCards list;
    Card card1;
    Card card2;
    Card card3;

    Set<Tag> tags;
    Tag tag;
    Tag tag2;
    Tag tag3;

    Palette palette1;
    Palette palette2;

    Set<Palette> palettes;

    Set<Card> cards;

    /**
     * Set up all objects needed for testing
     */
    @BeforeEach
    public void setUp() {
        tags = new HashSet<>();

        palettes = new HashSet<>();
        palette1 = new Palette("Basic", "#ffffff",
                "#ffffff", true, board, new HashSet<Card>());
        palette2 = new Palette("Important", "#222222",
                "#222222", false, board, new HashSet<Card>());

        palettes.add(palette1);
        palettes.add(palette2);

        board = new Board("Algebra", "#ffffff", "#ffffff",
                "#ffffff", "#ffffff",
                "pass", new ArrayList<ListOfCards>(), tags, palettes);
        list = new ListOfCards("Grasple", board, new ArrayList<Card>());

        card1 = new Card("Homework", "Somewhat long description",
                "#ffffff", list, new ArrayList<CheckListItem>(), tags, palette1);
        card2 = new Card("Pre-lecture", "Somewhat long description",
                "#ffffff", list, new ArrayList<CheckListItem>(), tags, palette1);
        card3 = new Card("Project", "Somewhat long description",
                "#ffffff", list, new ArrayList<CheckListItem>(), tags, palette2);
        cards = new HashSet<>();
        cards.add(card1);
        cards.add(card2);

        tag = new Tag("urgent", "#ff00ff", board, cards);
        tag2 = new Tag("urgent", "#ff00ff",board, cards);
        tag3 = new Tag("oopp", "#00ff00",board, cards);
    }

    /**
     * Test the default constructor
     */
    @Test
    public void checkConstructor() {
        Tag defaultTag = new Tag();
        assertNotNull(defaultTag);
    }

    /**
     * Test the constructor with parameters
     */
    @Test
    public void checkParametrizedConstructor() {
        assertEquals("urgent", tag.name);
        assertEquals("#ff00ff", tag.colour);
        assertEquals(cards, tag.cards);
    }

    /**
     * Test equals and hashcode methods for two equal objects
     */
    @Test
    public void equalsHashCode() {
        assertEquals(tag, tag2);
    }

    /**
     * Test equals and hashcode methods for two different objects
     */
    @Test
    public void notEqualsHashCode() {
        assertNotEquals(tag, tag3);
    }

    /**
     * Test toString method
     */
    @Test
    public void hasToString() {
        var actual = tag.toString();
        assertTrue(actual.contains(Tag.class.getSimpleName()));
        assertTrue(actual.contains("\n"));
        assertTrue(actual.contains("name"));
        assertTrue(actual.contains("colour"));
        assertTrue(actual.contains("cards"));
    }

    /**
     * Test card addition
     */
    @Test
    public void testAddCard() {
        tag.addCard(card3);
        cards = Set.of(card1, card2, card3);
        assertEquals(cards, tag.cards);
    }

    /**
     * Test null addition
     */
    @Test
    public void testAddCardNull() {
        tag.addCard(null);
        assertEquals(cards, tag.cards);
    }

    /**
     * Test card removal
     */
    @Test
    public void testRemoveCard() {
        tag.removeCard(card2);
        cards = Set.of(card1);
        assertEquals(cards, tag.cards);
    }

    /**
     * Test null removal
     */
    @Test
    public void testRemoveCardNull() {
        tag.removeCard(null);
        assertEquals(cards, tag.cards);
    }
}
