package commons;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

@Entity
@Table(name = "palettes")
@JsonIdentityInfo(
        scope = Palette.class,
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class Palette {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public long id;

    @Column(name = "title")
    public String title;

    @Column(name = "background_colour", columnDefinition = "varchar(7) default '#ffffff'")
    public String background;

    @Column(name = "font_colour", columnDefinition = "varchar(7) default '#ffffff'")
    public String font;

    @Column(name = "isDefault")
    public boolean isDefault;

    @ManyToOne
    @JoinColumn(name = "board_id")
    public Board board;

    @OneToMany(mappedBy = "palette" , cascade = CascadeType.ALL, orphanRemoval = false)
    public Set<Card> cards = new HashSet<>();

    /**
     * Default constructor
     */
    public Palette() {
    }


    /**
     * Constructor for palette
     * @param title
     * @param background
     * @param font
     * @param isDefault
     * @param board
     * @param cards
     */
    public Palette(String title, String background, String font,
                   boolean isDefault, Board board, Set<Card> cards) {
        this.title = title;
        this.background = background;
        this.font = font;
        this.isDefault = isDefault;
        this.board = board;
        this.cards = cards;
    }

    /**
     * Method that adds a card to the palettes
     * @param card
     */
    public void addCard(Card card){
        if (card != null && !cards.contains(card)) {
            cards.add(card);
            card.palette = this;
        }
    }

    /**
     * Method that returns the default palette in a board
     * @return the default palette of a board
     */
    public Palette getDefault(){
        Board board = this.board;

        for(Palette p: board.palettes){
            if(p.isDefault)
                return p;
        }

        return null;
    }
    /**
     * Remove this Card from a given palette
     * @param card
     */
    public void removeCard(Card card) {
        if (card != null && cards.contains(card)) {
            cards.remove(card);
            card.palette = getDefault();
        }
    }

    /**
     * Method that sets the background of a palette
     * @param background
     */
    public void setBackground(String background) {
        this.background = background;
    }

    /**
     * Method that sets the font colour of a palette
     * @param font the font colour
     */
    public void setFont(String font) {
        this.font = font;
    }

    /**
     * Method that checks if the palette is default
     * @return a boolean
     */
    public boolean isDefault(){
        return this.isDefault;
    }

    /**
     * Method that sets the palette as default
     */
    public void setDefault(){
        this.isDefault = true;
    }

    /**
     *
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    /**
     *
     * @return
     */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, MULTI_LINE_STYLE);
    }
}