package commons;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

@Entity
@Table(name = "checklist_item")
@JsonIdentityInfo(
        scope = CheckListItem.class,
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class CheckListItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    public long id;

    @Column(name = "check_order")
    public long order;

    @Column(name = "item_text")
    public String text;

    @Column(name = "completed")
    public boolean completed;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "card_id")
    public Card card;

    /**
     * Default constructor
     */
    public CheckListItem() {

    }

    /**
     * Constructor with parameters
     * @param text
     * @param completed
     * @param card
     */
    public CheckListItem(String text, boolean completed, Card card) {
        this.text = text;
        this.completed = completed;
        this.card = card;
    }

    /*
        BASIC FUNCTIONALITY
     */

    /**
     * Change the completion of a checklist item.
     */
    public void changeCompletion() {
        if(completed) {
            completed = false;
        } else {
            completed = true;
        }
    }

    /**
     * @return the order of the card
     */
    public long getOrder() {
        return order;
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
