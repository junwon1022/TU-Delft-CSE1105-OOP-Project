package commons;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;
import java.util.*;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

@Entity
@Table(name = "lists")
@JsonIdentityInfo(
        scope = ListOfCards.class,
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class ListOfCards {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "list_id")
    public long id;

    @Column(name = "list_title")
    public String title;

    @Column(name = "list_colour", columnDefinition = "varchar(7) default '#ffffff'")
    public String colour;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "board_id")
    public Board board;

    @OneToMany(mappedBy = "list", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<Card> cards = new ArrayList<>();

    /**
     * Default constructor
     */
    public ListOfCards() {

    }

    /**
     * Constructor with parameters
     * @param title
     * @param colour
     * @param board
     * @param cards
     */
    public ListOfCards(String title, String colour,
                       Board board, List<Card> cards) {
        this.title = title;
        this.colour = colour;
        this.board = board;
        this.cards = cards;
    }

    /*
        BASIC FUNCTIONALITY
     */

    /**
     * Add a card to a list
     * @param card
     */
    public void addCard(Card card) {
        if(card != null) {
            this.cards.add(card);
            card.list = this;
        }
    }

    /**
     * Remove a card from a list
     * @param card
     */
    public void removeCard(Card card) {
        if(card != null) {
            this.cards.remove(card);
            card.list = null;
        }
    }

    /**
     * Move a given card to a new index
     * @param card
     * @param index
     */
    public void moveCard(Card card, int index) {
        this.cards.remove(card);
        this.cards.add(index, card);
    }

    /**
     * Swap two cards' places in a list of cards
     * @param card1
     * @param card2
     */
    public void swapCard(Card card1, Card card2) {
        int index1 = this.cards.indexOf(card1);
        int index2 = this.cards.indexOf(card2);
        this.cards.set(index1, card2);
        this.cards.set(index2, card1);
    }

    /**
     * Check if two lists of cards are equal
     * @param obj
     * @return true if equal
     */
    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }


    /**
     * Generate a hash code for a list of cards
     * @return an integer
     */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    /**
     * Return a String representation of a list of cards
     * @return a string
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, MULTI_LINE_STYLE);
    }
}
