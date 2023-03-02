package commons;
import java.util.*;

public class Board {
    private int key;
    private String name;
    private String background;
    private String readpassword;
    private String writepassword;

    private List<Lists> lists;

    public Board(int key, String name, String background, String readpassword, String writepassword, List<Lists> lists) {
        this.key = key;
        this.name = name;
        this.background = background;
        this.readpassword = readpassword;
        this.writepassword = writepassword;
        this.lists = lists;
    }

    //Getters
    public int getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public String getBackground() {
        return background;
    }

    public String getReadpassword() {
        return readpassword;
    }

    public String getWritepassword() {
        return writepassword;
    }

    public List<Lists> getLists() {
        return lists;
    }

    //Setters
    public void setKey(int key) {
        this.key = key;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public void setReadpassword(String readpassword) {
        this.readpassword = readpassword;
    }

    public void setWritepassword(String writepassword) {
        this.writepassword = writepassword;
    }

    public void setLists(List<Lists> lists) {
        this.lists = lists;
    }

    //List modification
    public void addList(Lists list) {
        lists.add(list);
    }

    public void removeList(Lists list) {
        lists.remove(list);
    }

    public void moveList(int index, int newIndex) {
        Lists list = lists.get(index);
        lists.remove(index);
        lists.add(newIndex, list);
    }

    public void swapList(int index1, int index2) {
        Lists list1 = lists.get(index1);
        Lists list2 = lists.get(index2);
        lists.set(index1, list2);
        lists.set(index2, list1);
    }
}
