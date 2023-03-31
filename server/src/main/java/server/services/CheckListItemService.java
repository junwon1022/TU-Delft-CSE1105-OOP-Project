package server.services;

import commons.Card;
import commons.CheckListItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import server.database.CheckListItemRepository;

import java.util.List;

@Service
public class CheckListItemService {

    private CheckListItemRepository checklistItemRepository;

    /**
     * Constructor with parameters
     * @param checklistItemRepository
     */
    @Autowired
    public CheckListItemService(@Qualifier("check")
                                CheckListItemRepository checklistItemRepository) {
        this.checklistItemRepository = checklistItemRepository;
    }

    /**
     * Check if an item exists in a card
     * @param card
     * @param checkListItem
     * @return true if a checklist item is in a card
     */
    public boolean checkInCard(CheckListItem checkListItem,Card card) {

        return card.checklist.contains(checkListItem);
    }

    /**
     * Get the checklist of a given card
     * @param card
     * @return the checklist
     */
    public List<CheckListItem> getChecklist(Card card) {
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
    public CheckListItem createCheckListItem(CheckListItem check, Card card) throws Exception {
        check.card = card;
        card.checklist.add(check);
        return checklistItemRepository.save(check);
    }

    /**
     * Delete a check item given its id
     * @param id
     */
    public void deleteCheckListItemById(Long id) throws Exception {
        CheckListItem checkListItem = getCheckById(id);
        checkListItem.card.checklist.remove(checkListItem);
        checklistItemRepository.deleteById(id);
    }

    /**
     * Change the completion of a CheckListItem
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
     * @return the edited check
     */
    public CheckListItem editCheckText(Long id, String newText) throws Exception {
        CheckListItem check = getCheckById(id);
        check.text = newText;
        return checklistItemRepository.save(check);
    }
}
