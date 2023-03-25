package commons;
import javax.persistence.*;
import java.security.SecureRandom;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

@Entity
@Table(name = "boards")
@JsonIdentityInfo(
        scope = Board.class,
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    public long id;

    @Column(name = "board_title")
    public String title;

    @Column(name = "board_colour", columnDefinition = "varchar(7) default '#ffffff'")
    public String colour;

    @Column(name = "b_font_colour", columnDefinition = "varchar(7) default '#ffffff'")
    public String font;

    @Column(name = "list_colour", columnDefinition = "varchar(7) default '#ffffff'")
    public String listColour;

    @Column(name = "list_font_colour", columnDefinition = "varchar(7) default '#ffffff'")
    public String listFont;
    @Column(name = "password")
    public String password;

    @Column(name = "invite_key", unique = true)
    public String key;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<ListOfCards> lists;

    @OneToMany(mappedBy = "board" , cascade = CascadeType.ALL, orphanRemoval = true)
    public Set<Palette> palettes;


    /**
     * Default constructor
     */
    public Board() {
    }

    /**
     * Constructor with parameters
     * @param title
     * @param boardColour
     * @param fontColour
     * @param listColour
     * @param listFont
     * @param password
     * @param lists
     * @param palettes
     */
    public Board(String title, String boardColour, String fontColour,
                 String listColour, String listFont,
                 String password, List<ListOfCards> lists, Set<Palette> palettes) {
        this.title = title;
        this.colour = boardColour;
        this.font = fontColour;

        this.listColour = listColour;
        this.listFont = listFont;
        this.password = password;
        this.lists = lists;
        this.palettes = palettes;
    }

    /*
        BASIC FUNCTIONALITY
     */

    /**
     * Generates an invite key of a board
     */
    @PrePersist
    public void generateInviteKey() {
        SecureRandom random = new SecureRandom();

        // Define a list of common words to use in the invite key
        List<String> words = Arrays.asList(
                "apple", "banana", "cherry", "dog", "elephant",
                "flower", "grape", "house", "igloo", "jacket",
                "kite", "lemon", "monkey", "necklace", "orange",
                "pizza", "queen", "rainbow", "sun", "tree",
                "umbrella", "violin", "water", "xylophone", "yellow",
                "zebra", "airplane", "book", "car", "dragon",
                "eagle", "fire", "guitar", "honey", "island",
                "jungle", "key", "lion", "moon", "ninja",
                "ocean", "parrot", "quest", "rain", "star",
                "tiger", "unicorn", "volcano", "wolf", "x-ray",
                "yacht", "zombie"
        );

        // Select three random words from the words list
        List<String> selectedWords = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            int index = random.nextInt(words.size());
            selectedWords.add(words.get(index));
        }

        // Generate a random 6-letter string
        String randomChars = getRandomString(6);

        // Combine the selected words and random number into an invite key
        key = String.join("-", selectedWords) + "-" + randomChars;
    }

    /**
     * Generates a random string of some length
     * @param length
     * @return the generated string
     */
    private String getRandomString(int length) {
        String CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(CHARS.length());
            sb.append(CHARS.charAt(index));
        }
        return sb.toString();
    }

    /**
     * Add a list to a board
     * @param list
     */
    public void addList(ListOfCards list) {
        if(list != null) {
            lists.add(list);
            list.board = this;
        }
    }

    /**
     * Remove an existing list from a board
     * @param list
     */
    public void removeList(ListOfCards list) {
        if(list != null) {
            lists.remove(list);
            list.board = null;
        }
    }

    /**
     * Move a list from one index to another
     * @param list - the list to move
     * @param newIndex - the new index it should be moved to
     */
    public void moveList(ListOfCards list, int newIndex) {
        lists.remove(list);
        lists.add(newIndex, list);
    }

    /**
     * Swap two given lists' places within a board
     * @param list1
     * @param list2
     */
    public void swapList(ListOfCards list1, ListOfCards list2) {
        int index1 = this.lists.indexOf(list1);
        int index2 = this.lists.indexOf(list2);
        this.lists.set(index1, list2);
        this.lists.set(index2, list1);
    }

    /**
     * Add a palette to a board
     * @param palette
     */
    public void addPalette(Palette palette){
        if(palette != null)
            palettes.add(palette);
    }

    /**
     * Remove a palette from a board
     * @param palette
     */
    public void removePalette(Palette palette){
        if(palette != null)
            palettes.remove(palette);
    }

    /**
     * Check if two boards are equal
     * @param obj
     * @return true if equal
     */
    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }


    /**
     * Generate a hash code for a board
     * @return an integer
     */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    /**
     * Return a String representation of a board
     * @return a string
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, MULTI_LINE_STYLE);
    }
}
