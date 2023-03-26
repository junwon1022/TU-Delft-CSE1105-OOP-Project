package commons;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

@Entity
@Table(name = "tags")
@JsonIdentityInfo(
        scope = Tag.class,
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    public long id;

    @Column(name = "tag_name")
    public String name;

    @Column(name = "tag_colour", columnDefinition = "varchar(7) default '#ffffff'")
    public String colour;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "board_id")
    public Board board;

    @ManyToMany(mappedBy = "tags")
    public Set<Card> cards = new HashSet<>();

    /**
     * Default constructor
     */
    public Tag() {

    }

    /**
     * Constructor with parameters
     * @param name
     * @param colour
     * @param board
     * @param cards
     */
    public Tag(String name, String colour, Board board, Set<Card> cards) {
        this.name = name;
        this.colour = colour;
        this.board = board;
        this.cards = cards;
    }

    /*
        BASIC FUNCTIONALITY
     */

    /**
     * Apply this tag to a given card
     * @param card
     */
    public void addCard(Card card) {
        if (card != null && !cards.contains(card)) {
            cards.add(card);
            card.tags.add(this);
        }
    }

    /**
     * Remove this tag from a given card
     * @param card
     */
    public void removeCard(Card card) {
        if (card != null && cards.contains(card)) {
            cards.remove(card);
            card.tags.remove(this);
        }
    }

    /**
     * Set the color of the tag
     * @param colour
     */
    public void setColour(String colour) {
        this.colour = colour;
    }

    /**
     * Get the color of the tag
     * @return color
     */
    public String getColour() {
        return this.colour;
    }

    /**
     * Check if two tags are equal
     * @param obj
     * @return true if equal
     */
    @Override
    public boolean equals(Object obj) {
        //return EqualsBuilder.reflectionEquals(this, obj);

        if(obj instanceof Tag) {
            Tag t = (Tag) obj;
            return t.id == this.id && t.colour.equals(this.colour) && t.name.equals(this.name);

        }
        return false;
    }

    /**
     * Return a String representation of a tag
     * @return a string
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, MULTI_LINE_STYLE);
    }
}
