package commons;
import java.util.*;

public class Lists {
    private String name;
    private List<Card> cards;
    private String title;

    public Lists(String name, List<Card> cards, String title) {
        this.name = name;
        this.cards = cards;
        this.title = title;
    }

    //Getters
    public String getName() {
        return name;
    }

    public List<Card> getCards() {
        return cards;
    }

    public String getTitle() {
        return title;
    }

    //Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    //Methods
    public void addCard(Card card) {
        this.cards.add(card);
    }

    public void removeCard(Card card) {
        this.cards.remove(card);
    }

    public void moveCard(Card card, int index) {
        this.cards.remove(card);
        this.cards.add(index, card);
    }

    public void swapCard(Card card1, Card card2) {
        int index1 = this.cards.indexOf(card1);
        int index2 = this.cards.indexOf(card2);
        this.cards.set(index1, card2);
        this.cards.set(index2, card1);
    }

}
