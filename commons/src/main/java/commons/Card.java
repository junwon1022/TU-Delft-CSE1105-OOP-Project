package commons;

public class Card {
    private String title;

    private CardList parent;

    /**
     * Constructor for Card.
     * @param title Title of task
     * @param parent List it is part of
     */
    public Card(String title, CardList parent) {
        this.title = title;
        this.parent = parent;
    }

    /**
     * getTitle method for Card.
     * @return title the title of the card
     */
    public String getTitle() {
        return title;
    }

    /**
     * setTitle method for Card.
     * @param title Title of card
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * getParent method for Card.
     * @return the parent list of the card
     */
    public CardList getParent() {
        return parent;
    }

    /**
     * setParent method for Card.
     * @param parent the parent list of the card
     */
    public void setParent(CardList parent) {
        this.parent = parent;
    }
}
