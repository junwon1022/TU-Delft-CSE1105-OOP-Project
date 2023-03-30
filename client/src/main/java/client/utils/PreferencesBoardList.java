package client.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PreferencesBoardList implements Serializable {
    private List<PreferencesBoardInfo> info;

    /**
     * Default constructor
     */
    public PreferencesBoardList() {
        info = new ArrayList<>();
    }

    /**
     * Method for get info
     * @return info
     */
    public List<PreferencesBoardInfo> getInfo() {
        return info;
    }

    /**
     * Method for set info
     * @param info
     */
    public void setInfo(List<PreferencesBoardInfo> info) {
        this.info = info;
    }
}
