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
        Tag tag = tagRepository.getById(id);
        if(tag != null) {
            tag.removeTagsFromCards();
            tagRepository.deleteById(id);
        }
    }

    /**
     * Edit the name of a tag and store the edited tag in the database
     * @param id
     * @param newTag
     * @return the edited tag
     */
    public Tag editTag(Long id, Tag newTag) throws Exception {
        Tag tag = getTagById(id);
        tag.name = newTag.name;
        tag.colour = newTag.colour;
        tag.font = newTag.font;
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

