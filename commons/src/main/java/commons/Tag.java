package commons;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

@Entity
@Table(name = "tags")
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    public long id;

    @Column(name = "tag_name")
    public String name;

    @Column(name = "tag_colour", columnDefinition = "varchar(7) default '#ffffff'")
    public String colour;

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
     * @param cards
     */
    public Tag(String name, String colour, Set<Card> cards) {
        this.name = name;
        this.colour = colour;
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
     * Check if two tags are equal
     * @param obj
     * @return true if equal
     */
    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }


    /**
     * Generate a hash code for a tag
     * @return an integer
     */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
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
