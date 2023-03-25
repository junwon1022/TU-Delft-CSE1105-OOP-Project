package server.services;

import commons.Board;
import commons.ListOfCards;
import commons.Card;
import commons.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import server.database.CardRepository;

import java.util.List;
import java.util.Set;

@Service
public class CardService {

    private CardRepository cardRepository;

    /**
     * Constructor with parameters
     * @param cardRepository
     */
    @Autowired
    public CardService(@Qualifier("card") CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    /**
     * Check if a card is in the given list
     * @param card
     * @param list
     * @return true if card in list
     */
    public boolean cardInList(Card card, ListOfCards list) {
        return card.list == list;
    }

    /**
     * Get all cards within a given list
     * @param list
     * @return the list of cards
     */
    public List<Card> getCards(ListOfCards list) {
        return list.cards;
    }

    /**
     * Retrieve a card given its id
     * @param id
     * @return a card
     */
    public Card getCardById(Long id) throws Exception {
        return cardRepository.findById(id)
                .orElseThrow(() -> new Exception("Card not found with id " + id));
    }

    /**
     * Create a new card
     * @param card
     * @param list
     * @param board
     * @return cardRepository
     */
    public Card createCard(Card card, ListOfCards list, Board board) throws Exception {
        if(card.title == null || card.title.isEmpty()) {
            throw new Exception("Card cannot be created without a title.");
        }
        if(list.board != board) {
            throw new Exception("Invalid path.");
        }
        card.list = list;
        return cardRepository.save(card);
    }

    /**
     * Delete a card given its id
     * @param id
     */
    public void deleteCardById(Long id) {
        cardRepository.deleteById(id);
    }

    /**
     * Edit the title of a card and store the edited card in the database
     * @param id
     * @param newTitle
     * @return the edited card
     */
    public Card editCardTitle(Long id, String newTitle) throws Exception {
        if(newTitle == null || newTitle.isEmpty()) {
            throw new Exception("Title should not be null or empty.");
        }
        Card card = getCardById(id);
        card.title = newTitle;
        return cardRepository.save(card);
    }

    /**
     * Get all the tags of a given card
     * @param card
     * @return the set of tags
     */
    public Set<Tag> getTags(Card card) {
        return card.tags;
    }

    /**
     * Add a tag to a card
     * @param card
     * @param tag
     */
    public void addTagToCard(Card card, Tag tag) {
        card.tags.add(tag);
        cardRepository.save(card);
    }

    /**
     * Remove a tag from a card
     * @param card
     * @param tag
     */
    public void removeTagFromCard(Card card, Tag tag) {
        card.tags.remove(tag);
        cardRepository.save(card);
    }
}
