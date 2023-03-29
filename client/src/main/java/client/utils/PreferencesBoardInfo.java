package client.utils;

import java.io.Serializable;
import java.util.Objects;

public class PreferencesBoardInfo implements Serializable {
    private String title;
    private String key;
    private String password;

    /**
     * Default constructor
     */
    public PreferencesBoardInfo() {
    }


    /**
     * Constructor with parameters
     * @param title
     * @param key
     * @param password
     */
    public PreferencesBoardInfo(String title, String key, String password) {
        this.title = title;
        this.key = key;
        this.password = password;
    }

    /**
     * Method to get title
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Method to set title
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }


    /**
     * Method to get key
     * @return key
     */
    public String getKey() {
        return key;
    }


    /**
     * Method to set key
     * @param key
     */
    public void setKey(String key) {
        this.key = key;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Checks if two objects of type PreferenceBoardInfo are equal
     * @param o
     * @return true if they are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PreferencesBoardInfo that = (PreferencesBoardInfo) o;
        return Objects.equals(title, that.title) && Objects.equals(key, that.key);
    }

    /**
     * @return the hash of the object
     */
    @Override
    public int hashCode() {
        return Objects.hash(title, key);
    }
}
