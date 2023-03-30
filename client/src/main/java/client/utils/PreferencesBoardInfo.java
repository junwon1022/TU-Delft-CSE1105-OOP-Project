package client.utils;

import java.io.Serializable;
import java.util.Objects;

public class PreferencesBoardInfo implements Serializable {
    private String title;
    private String key;
    private String font;
    private String backgroundColor;
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
    public PreferencesBoardInfo(String title, String key, String password,
                                String font, String backgroundColor) {
        this.title = title;
        this.key = key;
        this.password = password;
        this.font = font;
        this.backgroundColor = backgroundColor;
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

    /**
     * Method to get font
     * @return the font color
     */
    public String getFont() {
        return font;
    }

    /**
     * Method to get background color
     * @return the color
     */
    public String getBackgroundColor() {
        return backgroundColor;
    }

    /**
     * Method to get password
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Method to set password
     * @param password
     */
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
