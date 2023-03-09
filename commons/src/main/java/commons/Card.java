package commons;

public class Card {
    private String title;

    /**
     * Constructor for CardList.
     * @param title
     */
    public Card(String title) {
        this.title = title;
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
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }
}
