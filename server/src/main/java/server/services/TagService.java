package server.services;

import commons.Board;
import commons.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import server.database.TagRepository;

import java.util.Set;

@Service
public class TagService {

    private TagRepository tagRepository;

    /**
     * Constructor with parameters
     * @param tagRepository
     */
    @Autowired
    public TagService(@Qualifier("tag") TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    /**
     * Check if a tag is in the given board
     * @param tag
     * @param board
     * @return true if the tag is in the board, false otherwise
     */
    public boolean tagInBoard(Tag tag, Board board) {
        return tag.board == board;
    }


    /**
     * Get all tags of a given Board
     * @param board
     * @return the set of tags
     */
    public Set<Tag> getTags(Board board) {
        return board.tags;
    }

    /**
     * Get all tags of a given Card
     *
     * @param card
     * @return the set of tags
     */
    public Set<Tag> getTags(commons.Card card) {
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
     * @param board
     * @return the new tag
     */
    public Tag createTag(Tag tag, Board board) throws Exception {
        if(tag.name == null || tag.name.isEmpty()) {
            throw new Exception("Tag cannot be created without a name.");
        }
        tag.board = board;
        board.tags.add(tag);
        return tagRepository.save(tag);
    }


    /**
     * Delete a tag given its id
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
        Tag tag = getTagById(id);
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
        Tag tag = getTagById(id);
        tag.colour = newColour;
        return tagRepository.save(tag);
    }

    /**
     * Save a tag in the database
     *
     * @param tag
     */
    public void saveTag(Tag tag) {
        tagRepository.save(tag);
    }
}
