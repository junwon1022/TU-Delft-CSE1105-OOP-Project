package server.services;

import commons.Board;
import commons.ListOfCards;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import server.database.ListOfCardsRepository;

import java.util.List;

@Service
public class ListOfCardsService {

    private ListOfCardsRepository listOfCardsRepository;

    /**
     * Constructor with parameters
     * @param listOfCardsRepository
     */
    @Autowired
    public ListOfCardsService(@Qualifier("list") ListOfCardsRepository listOfCardsRepository) {
        this.listOfCardsRepository = listOfCardsRepository;
    }

    /**
     * Check if a list of cards is in the given board
     * @param list
     * @param board
     * @return true if list in board
     */
    public boolean listInBoard(ListOfCards list, Board board) {
        return list.board == board;
    }

    /**
     * Get all lists within a given board
     * @param board
     * @return the list of lists
     */
    public List<ListOfCards> getListsOfCards(Board board) {
        return board.lists;
    }

    /**
     * Retrieve a list given its id
     * @param id
     * @return a list of cards
     */
    public ListOfCards getListById(Long id) throws Exception {
        return listOfCardsRepository.findById(id)
                .orElseThrow(() -> new Exception("List not found with id " + id));
    }

    /**
     * Create a new list of cards
     * @param list
     * @param board
     * @return listOfCardsRepository
     */
    public ListOfCards createListOfCards(ListOfCards list, Board board) throws Exception {
        if(list.title == null || list.title.isEmpty()) {
            throw new Exception("List cannot be created without a title.");
        }
        list.board = board;
        return listOfCardsRepository.save(list);
    }

    /**
     * Delete a list given its id
     * @param id
     */
    public void deleteListOfCardsById(Long id) {
        listOfCardsRepository.deleteById(id);
    }

    /**
     * Edit the title of a list and store the edited list in the database
     * @param id
     * @param newTitle
     * @return the edited list
     */
    public ListOfCards editListOfCardsTitle(Long id, String newTitle) throws Exception {
        if(newTitle == null || newTitle.isEmpty()) {
            throw new Exception("Title should not be null or empty.");
        }
        ListOfCards list = getListById(id);
        list.title = newTitle;
        return listOfCardsRepository.save(list);
    }

    /**
     * Move two cards' positions given the list they belong to and their ids
     * @param listId the id of the list
     * @param fromIdx the index of the first card
     * @param toIdx the index of the second card
     * @return the updated list
     */
    public ListOfCards moveCardsInListOfCards(long listId, int fromIdx, int toIdx) {
        ListOfCards list = listOfCardsRepository.getById(listId);
        if (fromIdx < toIdx) {
            list.cards.get(fromIdx).order = toIdx;
            for (int i = fromIdx + 1; i <= toIdx; i++)
                list.cards.get(i).order = i - 1;
        }
        else {
            list.cards.get(fromIdx).order = toIdx;
            for (int i = toIdx; i < fromIdx; i++)
                list.cards.get(i).order = i + 1;
        }
        return listOfCardsRepository.save(list);
    }
}
