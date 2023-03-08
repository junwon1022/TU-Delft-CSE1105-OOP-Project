package commons;
import javax.persistence.*;
import java.util.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

@Entity
@Table(name = "boards")
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    public long id;

    @Column(name = "board_title")
    public String title;

    @Column(name = "board_colour", columnDefinition = "varchar(7) default '#ffffff'")
    public String colour;

    @Column(name = "read_password")
    public String readpassword;

    @Column(name = "write_password")
    public String writepassword;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<ListOfCards> lists;

    /**
     * Default constructor
     */
    public Board() {
    }

    /**
     * Constructor with parameters
     * @param title
     * @param colour
     * @param readpassword
     * @param writepassword
     */
    public Board(String title, String colour,
                 String readpassword, String writepassword,List<ListOfCards> lists) {
        this.title = title;
        this.colour = colour;
        this.readpassword = readpassword;
        this.writepassword = writepassword;
        this.lists = lists;
    }

    /*
        BASIC FUNCTIONALITY
     */

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
