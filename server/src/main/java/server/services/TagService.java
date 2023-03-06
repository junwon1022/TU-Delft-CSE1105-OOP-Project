package server.services;

import commons.Board;
import commons.Card;
import commons.ListOfCards;
import commons.Tag;
import org.springframework.stereotype.Service;
import server.database.ListOfCardsRepository;
import server.database.TagRepository;

import java.util.List;
import java.util.Set;

@Service
public class TagService {

    private TagRepository tagRepository;

    /**
     * Constructor with parameters
     * @param tagRepository
     */
    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    /**
     * Check if a card contains a tag
     * @param tag
     * @param card
     * @return true if list in board
     */
    public boolean tagInCard(Tag tag,Card card) {
        return card.tags.contains(tag);
    }

    /**
     * Get all tags within a given card
     * @param card
     * @return the list of tags
     */
    public Set<Tag> getTags(Card card) {
        return card.tags;
    }

    /**
     * Retrieve a tag given its id
     * @param id
     * @return a tag
     */
    public Tag getTagById(Long id) throws Exception {
        return tagRepository.findById(id)
                .orElseThrow(() -> new Exception("Tag not found with id " + id));
    }

    /**
     * Create a new tag
     * @param tag
     * @param card
     * @return tagRepository
     */
    public Tag createTag(Tag tag,Card card) throws Exception {
        if(tag.name == null || tag.name.isEmpty()) {
            throw new Exception("Tag cannot be created without a name.");
        }
        tag.cards.add(card);
        card.tags.add(tag);
        return tagRepository.save(tag);
    }

    /**
     * Delete a list given its id
     * @param id
     */
    public void deleteTagById(Long id) {
        tagRepository.deleteById(id);
    }

    /**
     * Edit the name of a tag and store the edited tag in the database
     * @param id
     * @param newName
     * @return the edited tag
     */
    public Tag editTagName(Long id, String newName) throws Exception {
        if(newName == null || newName.isEmpty()) {
            throw new Exception("Name should not be null or empty.");
        }
        Tag tag = tagRepository.getById(id);
        tag.name = newName;
        return tagRepository.save(tag);
    }

    /**
     * Edit the colour of a tag and store the edited tag in the database
     * @param id
     * @param newColour
     * @return the edited tag
     */
    public Tag editTagColour(Long id, String newColour) throws Exception {
        if(newColour == null || newColour.isEmpty()) {
            throw new Exception("Colour should not be null or empty.");
        }
        Tag tag = tagRepository.getById(id);
        tag.colour = newColour;
        return tagRepository.save(tag);
    }
}
