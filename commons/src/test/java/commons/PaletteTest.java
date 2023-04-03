package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class PaletteTest {


    Board board;

    ListOfCards listOfCards;

    Card card1;
    Card card2;

    Set<Card> cards;

    Set<Tag> tags;

    Palette palette1;
    Palette palette2;
    Palette palette3;

    Set<Palette> palettes;

    /**
     * Initialize all the objects needed for testing
     */
    @BeforeEach
    void initialize(){

        tags = new HashSet<>();
        palettes = new HashSet<>();

        board = new Board("MyBoard", "#444444", "#444444",
                "#444444", "#444444",
                "password", new ArrayList<ListOfCards>(), tags, palettes);

        palette1 = new Palette("bla", "#444444", "#444444",
                true, board, new HashSet<>());

        palette2 = new Palette("bla", "#000000", "#000000",
                false, board, new HashSet<>());
        palette3 = new Palette("bla", "#444444", "#444444",
                true, board, new HashSet<>());

        palettes.add(palette1);
        palettes.add(palette2);

        listOfCards = new ListOfCards("Grasple",board, new ArrayList<Card>());

        card1 = new Card("first", "", "#444444", listOfCards, new ArrayList<CheckListItem>(),
                tags, palette1);
        card2 = new Card("second", "", "#444444", listOfCards, new ArrayList<CheckListItem>(),
                tags, palette2);

        cards = new HashSet<>();
        cards.add(card1);
        cards.add(card2);


    }

    /**
     * Test default constructor
     */
    @Test
    void constructorTest(){
        Palette palette = new Palette();
        assertNotNull(palette);
    }

    /**
     * Test parametrized constructor
     */
    @Test
    void parametrizedConstructorTest(){
        assertEquals("bla", palette1.title);
        assertEquals("#444444", palette1.background);
        assertEquals("#444444", palette1.font);
        assertEquals(true, palette1.isDefault);
    }

    /**
     * Test adding a card to a palette
     */
    @Test
    void addCardTest() {
        palette1.addCard(card1);
        assertEquals(card1.palette, palette1);
        assertEquals(Set.of(card1), palette1.cards);
    }

    /**
     * Test that adding a new null card doesn't make any changes
     */
    @Test
    void addNullCard(){
        Card card = null;
        palette1.addCard(card);
        assertEquals(0, palette1.cards.size());
    }


    /**
     * Test setting the background a specific colour
     */
    @Test
    void setBackgroundTest() {
        palette1.setBackground("#123456");
        assertEquals("#123456", palette1.background);
    }

    /**
     * Test setting the font colour
     */
    @Test
    void setFont() {
        palette2.setFont("#000000");
        assertEquals("#000000", palette2.font);
    }

    /**
     * Test checking if the palette is a default one
     */
    @Test
    void isDefault() {
        assertTrue(palette1.getIsDefault());
    }

    /**
     * Test setting the current palette as the default one
     */
    @Test
    void setDefault() {
        palette2.setIsDefault();
        assertTrue(palette2.isDefault);
    }

    /**
     * Test the equals method with 2 unequal palettes
     */
    @Test
    void testEqualsFalse() {
        assertFalse(palette1.equals(palette2));
    }

    /**
     * Test the equals method with 2 equal palettes
     */
    @Test
    void testEqualsTrue(){
        assertTrue(palette1.equals(palette3));
    }

    /**
     * Test the hashcode method with 2 equal palettes
     */
    @Test
    void testHashCodeTrue() {
        assertEquals(palette1.hashCode(), palette3.hashCode());
    }

    /**
     * Test the hashcode method with 2 unequal palettes
     */
    @Test
    void testHashCodeFalse() {
        assertNotEquals(palette1.hashCode(), palette2.hashCode());
    }

    /**
     * Test the toString method
     */
    @Test
    void testToString() {
        var actual = palette1.toString();
        assertTrue(actual.contains(Palette.class.getSimpleName()));
        assertTrue(actual.contains("\n"));
        assertTrue(actual.contains("title"));
        assertTrue(actual.contains("background"));
        assertTrue(actual.contains("font"));
        assertTrue(actual.contains("board"));
    }
}