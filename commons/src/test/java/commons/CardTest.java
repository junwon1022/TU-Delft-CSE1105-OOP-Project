package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CardTest {

    Board board;
    List<ListOfCards> listOfCards;
    ListOfCards list;
    ListOfCards list2;
    ListOfCards list3;
    List<Card> cards;
    Card card;
    Card card2;
    Card card3;

    List<CheckListItem> checklist;
    CheckListItem checkListItem1;
    CheckListItem checkListItem2;
    CheckListItem checkListItem3;

    Set<Tag> tags;
    Tag tag1;
    Tag tag2;
    Tag tag3;

    Palette palette1;
    Palette palette2;

    Set<Palette> palettes;

    /**
     * Set up all objects needed for testing
     */
    @BeforeEach
    public void setUp() {
        checkListItem1 = new CheckListItem("Do X", false, card);
        checkListItem2 = new CheckListItem("Do Y", false, card);
        checkListItem3 = new CheckListItem("Do Z", false, card);
        checklist = new ArrayList<>();
        checklist.add(checkListItem1);
        checklist.add(checkListItem2);

        tag1 = new Tag("urgent", "#ff00ff", board, new HashSet<Card>());
        tag2 = new Tag("math", "#ffff00", board, new HashSet<Card>());
        tag3 = new Tag("oopp", "#00ff00",board, new HashSet<Card>());
        tags = new HashSet<>();
        tags.add(tag1);
        tags.add(tag2);

        palettes = new HashSet<>();
        palette1 = new Palette("Basic", "#ffffff",
                "#ffffff", true, board, new HashSet<Card>());
        palette2 = new Palette("Important", "#222222",
                "#222222", false, board, new HashSet<Card>());

        palettes.add(palette1);
        palettes.add(palette2);

        board = new Board("Algebra", "#ffffff", "#ffffff",
                "#ffff22", "#ffff22",
                "pass", listOfCards, tags, palettes);
        listOfCards = new ArrayList<>();
        list = new ListOfCards("Grasple",board, cards);
        list2 = new ListOfCards("Grasple", board, cards);
        list3 = new ListOfCards("Grasple3",  board, cards);
        listOfCards.add(list);
        listOfCards.add(list2);

        card = new Card("Homework", "Somewhat long description",
                "#ffffff", list, checklist, tags, palette1);
        card2 = new Card("Homework", "Somewhat long description",
                "#ffffff", list, checklist, tags, palette1);
        card3 = new Card("Project", "Somewhat long description",
                "#ffffff", list, checklist, tags, palette2);
        cards = new ArrayList<>();
        cards.add(card);
        cards.add(card2);
    }

    /**
     * Test the default constructor
     */
    @Test
    public void checkConstructor() {
        Card defaultCard = new Card();
        assertNotNull(defaultCard);
    }

    /**
     * Test the constructor with parameters
     */
    @Test
    public void checkParametrizedConstructor() {
        assertEquals("Homework", card.title);
        assertEquals("#ffffff", card.colour);
        assertEquals("Somewhat long description", card.description);
        assertEquals(list, card.list);
        assertEquals(checklist, card.checklist);
        assertEquals(tags, card.tags);
    }

    /**
     * Test equals and hashcode methods for two equal objects
     */
    @Test
    public void equalsHashCode() {
        assertEquals(card, card2);
    }

    /**
     * Test equals and hashcode methods for two different objects
     */
    @Test
    public void notEqualsHashCode() {
        assertNotEquals(card, card3);
    }

    /**
     * Test toString method
     */
    @Test
    public void hasToString() {
        var actual = card.toString();
        assertTrue(actual.contains(Card.class.getSimpleName()));
        assertTrue(actual.contains("\n"));
        assertTrue(actual.contains("title"));
        assertTrue(actual.contains("colour"));
        assertTrue(actual.contains("description"));
        assertTrue(actual.contains("list"));
        assertTrue(actual.contains("checklist"));
        assertTrue(actual.contains("tags"));
    }

    /**
     * Test checklist item addition
     */
    @Test
    public void testAddCheckListItem() {
        card.addCheckListItem(checkListItem3);
        checklist = List.of(checkListItem1, checkListItem2, checkListItem3);
        assertEquals(checklist, card.checklist);
    }

    /**
     * Test null addition
     */
    @Test
    public void testAddCheckListItemNull() {
        card.addCheckListItem(null);
        assertEquals(checklist, card.checklist);
    }

    /**
     * Test checklist item removal
     */
    @Test
    public void testRemoveCheckListItem() {
        card.removeCheckListItem(checkListItem2);
        checklist = List.of(checkListItem1);
        assertEquals(checklist, card.checklist);
    }

    /**
     * Test null removal
     */
    @Test
    public void testRemoveCheckListItemNull() {
        card.removeCheckListItem(null);
        assertEquals(checklist, card.checklist);
    }

    /**
     * Test tag addition
     */
    @Test
    public void testAddTag() {
        card.addTag(tag3);
        tags = Set.of(tag1, tag2, tag3);
        assertEquals(tags, card.tags);
    }

    /**
     * Test null addition
     */
    @Test
    public void testAddTagNull() {
        card.addTag(null);
        assertEquals(tags, card.tags);
    }

    /**
     * Test tag removal
     */
    @Test
    public void testRemoveTagItem() {
        card.removeTag(tag2);
        tags = Set.of(tag1);
        assertEquals(tags, card.tags);
    }

    /**
     * Test null removal
     */
    @Test
    public void testRemoveTagNull() {
        card.removeTag(null);
        assertEquals(tags, card.tags);
    }

    /**
     * Test addPalette method
     */
    @Test
    public void testAddPalette() {
        card.addPalette(palette1);
        assertTrue(card.palette == palette1);
    }

    /**
     * Test removePalette method
     */
    @Test
    public void testRemovePalette() {
        Palette p = new Palette("Name", "#ffffff",
                "#ffffff", true, board, new HashSet<Card>());
        card.addPalette(p);
        assertTrue(card.palette.background.equals("#ffffff"));
        card.removePalette(p);
        assertTrue(card.palette.background.equals("#ffffff"));
    }

    /**
     * Test getOrder method
     */
    @Test
    public void testGetOrder() {
        assertEquals(0, card.getOrder());
    }
}
