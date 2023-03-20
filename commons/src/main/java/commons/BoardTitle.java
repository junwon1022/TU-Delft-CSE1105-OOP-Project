package commons;

import com.fasterxml.jackson.annotation.JsonBackReference;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

@Entity
@Table(name = "boardTitle")
public class BoardTitle{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "boardtitle_id")
    public long id;

    @Column(name = "boardtitle_title")
    public String text;


    @Column(name = "boardtitle_colour", columnDefinition = "varchar(7) default '#ffffff'")
    public String colour;

    @OneToOne(cascade = CascadeType.PERSIST)
    public Board board;

    /**
     * Default constructor
     */
    public BoardTitle() {

    }

    /**
     * @param text
     * @param colour
     * @param board - a board which the element connects to
     */
    public BoardTitle(String text, String colour, Board board) {
        this.text = text;
        this.colour = colour;
        this.board = board;
    }

    /**
     * Check if two cards are equal
     * @param obj
     * @return true if equal
     */
    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }



    /**
     * Return a String representation of a card
     * @return a string
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, MULTI_LINE_STYLE);
    }
}

