package client.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import commons.Board;

import java.util.List;
import java.util.prefs.Preferences;

public class UserPreferences {
    private Preferences prefs;
    private ObjectMapper objectMapper;

    /**
     * UserPreferences constructor
     */
    public UserPreferences() {
        objectMapper = new ObjectMapper();
        prefs = Preferences.userRoot().node(this.getClass().getName());
    }

    /**
     * Adds a board
     * @param serverAddress the address of the server
     * @param board the board
     */
    public void addBoard(String serverAddress, Board board) {
        String boardListJson = prefs.get(serverAddress, "{\"info\": []}");

        try {
            PreferencesBoardList boardList = objectMapper.readValue(boardListJson,
                    PreferencesBoardList.class);

            List<PreferencesBoardInfo> boards = boardList.getInfo();

            PreferencesBoardInfo currentBoard = new PreferencesBoardInfo(board.title, board.key);

            boards.remove(currentBoard);

            boards.add(0, currentBoard);

            PreferencesBoardList newBoardList = new PreferencesBoardList();
            newBoardList.setInfo(boards);

            String newBoardListJson = objectMapper.writeValueAsString(newBoardList);
            prefs.put(serverAddress, newBoardListJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Method for get boards
     * @param serverAddress
     * @return the boards
     */
    public List<PreferencesBoardInfo> getBoards(String serverAddress) {
        String boardListJson = prefs.get(serverAddress, "{\"info\": []}");

        try {
            return objectMapper.readValue(boardListJson, PreferencesBoardList.class).getInfo();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Method for leave board
     * @param serverAddress
     * @param board
     */
    public void leaveBoard(String serverAddress, Board board) {
        String boardListJson = prefs.get(serverAddress, "{\"info\": []}");

        try {
            PreferencesBoardList boardList = objectMapper.readValue(boardListJson,
                    PreferencesBoardList.class);

            List<PreferencesBoardInfo> boards = boardList.getInfo();

            PreferencesBoardInfo currentBoard = new PreferencesBoardInfo(board.title, board.key);

            boards.remove(currentBoard);

            PreferencesBoardList newBoardList = new PreferencesBoardList();
            newBoardList.setInfo(boards);

            String newBoardListJson = objectMapper.writeValueAsString(newBoardList);
            prefs.put(serverAddress, newBoardListJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Method for leave board
     * @param serverAddress
     * @param board
     */
    public void leaveBoard(String serverAddress, PreferencesBoardInfo board) {
        String boardListJson = prefs.get(serverAddress, "{\"info\": []}");

        try {
            PreferencesBoardList boardList = objectMapper.readValue(boardListJson,
                    PreferencesBoardList.class);

            List<PreferencesBoardInfo> boards = boardList.getInfo();

            PreferencesBoardInfo currentBoard = new PreferencesBoardInfo(board.getTitle(), board.getKey());

            boards.remove(currentBoard);

            PreferencesBoardList newBoardList = new PreferencesBoardList();
            newBoardList.setInfo(boards);

            String newBoardListJson = objectMapper.writeValueAsString(newBoardList);
            prefs.put(serverAddress, newBoardListJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
