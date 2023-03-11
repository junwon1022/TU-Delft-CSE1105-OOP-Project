package server.services;

import commons.Card;
import commons.CheckListItem;
import org.springframework.stereotype.Service;
import server.database.CheckListItemRepository;

import java.util.List;

@Service
public class CheckListItemService {

    private CheckListItemRepository checklistItemRepository;

    /**
     * Constructor with parameters
     * @param checkListItemRepository
     */
    public CheckListItemService(CheckListItemRepository checkListItemRepository) {
        this.checklistItemRepository = checklistItemRepository;
    }

    /**
     * Check if an item exists in a card
     * @param card
     * @param checkListItem
     * @return true if a checklist is in a card
     */
    public boolean checkInCard(CheckListItem checkListItem,Card card) {

        return card.checklist.contains(checkListItem);
    }

    /**
     * Get all checkLists within a given card
     * @param card
     * @return the checklist item
     */
    public List<CheckListItem> getChecklists(Card card) {
        return card.checklist;
    }

    /**
     * Retrieve a check given its id
     * @param id
     * @return a check
     */
    public CheckListItem getCheckById(Long id) throws Exception {
        return checklistItemRepository.findById(id)
                .orElseThrow(() -> new Exception("Check not found with id " + id));
    }

    /**
     * Create a new checklist item
     * @param check
     * @param card
     * @return checkListItemRepository
     */
    public CheckListItem createChecklistItem(CheckListItem check, Card card) throws Exception {
        if(check.text == null || check.text.isEmpty()) {
            throw new Exception("a checklist item cannot be created without text.");
        }
        card.checklist.add(check);
        check.card = card;
        return checklistItemRepository.save(check);
    }

    /**
     * Delete a check item given its id
     * @param id
     */
    public void deleteChecklistItemById(Long id) {

        checklistItemRepository.deleteById(id);
    }

    /**
     * Change completion of a Checklist Item
     * @param check
     */

    public void editCompletion(CheckListItem check) {
        check.changeCompletion();
        checklistItemRepository.save(check);
    }

    /**
     * Edit the text of a check and store the edited check in the database
     * @param id
     * @param newText
     * @return the edited card
     */
    public CheckListItem editCheckText(Long id, String newText) throws Exception {
        if(newText == null || newText.isEmpty()) {
            throw new Exception("Text should not be null or empty.");
        }
        CheckListItem check = checklistItemRepository.getById(id);
        check.text = newText;
        return checklistItemRepository.save(check);
    }






}
