package server.services;

import commons.*;
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
     * Make a card selected/unselected
     * @param id
     * @param b
     * @return a card
     */
    public Card editSelected(Long id, boolean b) throws Exception {
        Card card = cardRepository.getById(id);
        if(b == true) card.selected = true;
        else card.selected = false;
        return cardRepository.save(card);
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
        Card card = cardRepository.getById(id);
        if(card != null) {
            cardRepository.getById(id).removeCardsFromTags();
            if(card.palette != null)
                removePaletteFromCard(card, card.palette);
            cardRepository.deleteById(id);
        }
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
     * Updates the new description in the database
     * @param id id of the card to update the description
     * @param newDescription the new description
     * @return the updated card
     * @throws Exception the card with the required id is not found
     */
    public Card editCardDescription(Long id, String newDescription) throws Exception {
        Card card = getCardById(id);
        card.description = newDescription;
        return cardRepository.save(card);
    }
    /**
     * Gets the description of the card with the specified id
     * @param id id of the card
     * @return the description of the card
     * @throws Exception if a card with the specified id is not found
     */
    public String getCardDescription(Long id) throws Exception {
        Card card = getCardById(id);
        return card.description;
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

    /**
     * Method that adds a palette to the given card
     * @param card
     * @param palette
     */
    public void addPaletteToCard(Card card, Palette palette) throws Exception{
        card.palette = palette;
        cardRepository.save(card);
    }

    /**
     * Method that removes the palette from the card
     * @param card
     * @param palette
     */
    public void removePaletteFromCard(Card card, Palette palette){
        palette.cards.remove(card);
        card.palette = null;
        cardRepository.save(card);
    }

    /**
     * Changes the order of the checklists in the database
     * with respect to the oldIdx and newIdx that represent
     * the checklist that was dragged and the checklist
     * that was dragged onto respectively.
     * @param cardId id of the card
     * @param oldIdx old index
     * @param newIdx new index
     * @return the new card
     * @throws Exception if getCardById returns null
     */
    public Card editCardChecklists(long cardId, int oldIdx, int newIdx) throws Exception {
        Card card = getCardById(cardId);
        if (oldIdx < newIdx) {
            card.checklist.get(oldIdx).order = newIdx;
            for (int i = oldIdx + 1; i <= newIdx; i++)
                card.checklist.get(i).order = i - 1;
        }
        else {
            card.checklist.get(oldIdx).order = newIdx;
            for (int i = newIdx; i < oldIdx; i++)
                card.checklist.get(i).order = i + 1;
        }
        return cardRepository.save(card);
    }
}
