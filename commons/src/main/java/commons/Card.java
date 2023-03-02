package commons;
import java.util.*;

public class Card {

    private String title;
    private String description;

    private List<Tag> tags;

    public Card(String title, String description, List<Tag> tags) {
        this.title = title;
        this.description = description;
        this.tags = tags;
    }

    //Getters
    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public List<Tag> getTags() {
        return tags;
    }

    //Setters
    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }
}
