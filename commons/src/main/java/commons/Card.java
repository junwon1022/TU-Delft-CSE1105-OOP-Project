package commons;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

@Entity
@Table(name = "cards")
@JsonIdentityInfo(
        scope = Card.class,
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "card_id")
    public long id;

    @Column(name = "card_order")
    public long order;

    @Column(name = "card_title")
    public String title;

    @Column(name = "description")
    public String description;


    @Column(name = "card_colour", columnDefinition = "varchar(7) default '#ffffff'")
    public String colour;

    @Column(name = "selected")
    public Boolean selected;

    @ManyToOne()
    @JoinColumn(name = "list_id")
    public ListOfCards list;
    @OneToMany(mappedBy = "card", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<CheckListItem> checklist = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "cards_tags",
            joinColumns = @JoinColumn(name = "card_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    public Set<Tag> tags = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "palette_id")
    public Palette palette;


    /**
     * Default constructor
     */
    public Card() {

    }

    /**
     * Constructor with parameters
     * @param title
     * @param description
     * @param colour
     * @param list
     * @param checklist
     * @param palette
     * @param tags
     */
    public Card(String title, String description,
                String colour, ListOfCards list,
                List<CheckListItem> checklist, Set<Tag> tags,
                Palette palette){
        this.title = title;
        this.description = description;
        this.colour = colour;
        this.list = list;
        this.checklist = checklist;
        this.tags = tags;
        this.palette = palette;
        this.selected = false;
    }

    /**
     * Constructor with parameters
     * @param title
     * @param description
     * @param colour
     * @param list
     * @param checklist
     * @param palette
     * @param tags
     * @param selected
     */
    public Card(String title, String description,
                String colour, ListOfCards list,
                List<CheckListItem> checklist, Set<Tag> tags,
                Palette palette,Boolean selected){
        this.title = title;
        this.description = description;
        this.colour = colour;
        this.list = list;
        this.checklist = checklist;
        this.tags = tags;
        this.palette = palette;
        this.selected = selected;
    }



    /*
        BASIC FUNCTIONALITY
     */

    /**
     * Method that adds a palette to a card
     * @param palette
     */
    public void addPalette(Palette palette){
        if (palette != null) {
            this.palette = palette;
            palette.cards.add(this);
        }
    }

    /**
     * Method that removes a palette from a card
     * @param palette
     */
    public void removePalette(Palette palette){
        if (palette != null) {
            palette.cards.remove(this);
            palette = palette.getDefault();
        }
    }
    /**
     * Add a tag to a card
     * @param tag
     */
    public void addTag(Tag tag) {
        if (tag != null && !tags.contains(tag)) {
            tags.add(tag);
            tag.cards.add(this);
        }
    }

    /**
     * Remove a tag from a card
     * @param tag
     */
    public void removeTag(Tag tag) {
        if (tag != null && tags.contains(tag)) {
            tags.remove(tag);
            tag.cards.remove(this);
        }
    }

    /**
     * Add a new checklist item to the checklist
     * @param item - the item to add
     */
    public void addCheckListItem(CheckListItem item) {
        if(item != null) {
            checklist.add(item);
            item.card = this;
        }
    }

    /**
     * Remove a given checklist item from the checklist
     * @param item - the item to remove
     */
    public void removeCheckListItem(CheckListItem item) {
        if(item != null) {
            checklist.remove(item);
            item.card = null;
        }
    }

    /**
     * @return the order of the card
     */
    public long getOrder() {
        return order;
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

