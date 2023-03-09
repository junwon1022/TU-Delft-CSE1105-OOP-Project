package commons;

import java.util.List;

public class CardList {
    private String title;

    private List<Card> cards;

    /**
     * Constructor for CardList.
     *
     * @param title
     * @param cards
     */
    public CardList(String title, List<Card> cards) {
        this.title = title;
        this.cards = cards;
    }

    /**
     * getTitle method for CardList.
     * @return title the title of the list
     */
    public String getTitle() {
        return title;
    }


    /**
     * setTitle method for CardList.
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * getCards method for CardList.
     * @return cards the list of cards
     */
    public List<Card> getCards() {
        return cards;
    }

    /**
     * setCards method for CardList.
     * @param cards
     */
    public void setCards(List<Card> cards) {
        this.cards = cards;
    }
}
