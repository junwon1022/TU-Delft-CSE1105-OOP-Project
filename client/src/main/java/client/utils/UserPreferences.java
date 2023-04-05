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
     * Adds a board without its password
     * @param serverAddress the address of the server
     * @param board the board
     * @return the board
     */
    public PreferencesBoardInfo addBoard(String serverAddress, Board board) {
        String boardListJson = prefs.get(serverAddress, "{\"info\": []}");

        try {
            PreferencesBoardList boardList = objectMapper.readValue(boardListJson,
                    PreferencesBoardList.class);

            List<PreferencesBoardInfo> boards = boardList.getInfo();

            PreferencesBoardInfo currentBoard = new PreferencesBoardInfo(board.title,
                    board.key,
                    "",
                    board.font,
                    board.colour);

            boards.remove(currentBoard);

            boards.add(boards.size(), currentBoard);

            PreferencesBoardList newBoardList = new PreferencesBoardList();
            newBoardList.setInfo(boards);

            String newBoardListJson = objectMapper.writeValueAsString(newBoardList);
            prefs.put(serverAddress, newBoardListJson);
            return currentBoard;
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

            PreferencesBoardInfo currentBoard = new PreferencesBoardInfo(board.title,
                    board.key,
                    board.password,
                    board.font,
                    board.colour);

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

            PreferencesBoardInfo currentBoard = new PreferencesBoardInfo(board.getTitle(),
                    board.getKey(),
                    board.getPassword(),
                    board.getFont(),
                    board.getBackgroundColor());

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
     * Method for updating the title of a board
     * @param serverAddress
     * @param board
     * @param newTitle
     * @return the updated PreferencesBoardInfo
     */
    public PreferencesBoardInfo updateBoardTitle(String serverAddress,
                                                 PreferencesBoardInfo board,
                                                 String newTitle) {
        String boardListJson = prefs.get(serverAddress, "{\"info\": []}");

        try {
            PreferencesBoardList boardList = objectMapper.readValue(boardListJson,
                    PreferencesBoardList.class);

            List<PreferencesBoardInfo> boards = boardList.getInfo();

            PreferencesBoardInfo newBoard = new PreferencesBoardInfo(newTitle,
                    board.getKey(),
                    board.getPassword(),
                    board.getFont(),
                    board.getBackgroundColor());

            for(int i=0; i<boards.size(); i++) {
                PreferencesBoardInfo b = boards.get(i);
                if(board.equals(b)) {
                    boards.set(i, newBoard);
                }
            }

            PreferencesBoardList newBoardList = new PreferencesBoardList();
            newBoardList.setInfo(boards);

            String newBoardListJson = objectMapper.writeValueAsString(newBoardList);
            prefs.put(serverAddress, newBoardListJson);

            return newBoard;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Method for updating the colors of a board
     * @param serverAddress
     * @param board
     * @param backgroundColor
     * @param fontColor
     * @return the updated PreferencesBoardInfo
     */
    public PreferencesBoardInfo updateBoardColors(String serverAddress,
                                                 PreferencesBoardInfo board,
                                                 String backgroundColor,
                                                  String fontColor) {
        String boardListJson = prefs.get(serverAddress, "{\"info\": []}");

        try {
            PreferencesBoardList boardList = objectMapper.readValue(boardListJson,
                    PreferencesBoardList.class);

            List<PreferencesBoardInfo> boards = boardList.getInfo();

            PreferencesBoardInfo newBoard = new PreferencesBoardInfo(board.getTitle(),
                    board.getKey(),
                    board.getPassword(),
                    fontColor,
                    backgroundColor);

            for(int i=0; i<boards.size(); i++) {
                PreferencesBoardInfo b = boards.get(i);
                if(board.equals(b)) {
                    boards.set(i, newBoard);
                }
            }

            PreferencesBoardList newBoardList = new PreferencesBoardList();
            newBoardList.setInfo(boards);

            String newBoardListJson = objectMapper.writeValueAsString(newBoardList);
            prefs.put(serverAddress, newBoardListJson);

            return newBoard;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Method for updating the password of a board
     * @param serverAddress
     * @param board
     * @param newPass
     * @return the updated PreferencesBoardInfo
     */
    public PreferencesBoardInfo updateBoardPassword(String serverAddress,
                                                 PreferencesBoardInfo board,
                                                 String newPass) {
        String boardListJson = prefs.get(serverAddress, "{\"info\": []}");

        try {
            PreferencesBoardList boardList = objectMapper.readValue(boardListJson,
                    PreferencesBoardList.class);

            List<PreferencesBoardInfo> boards = boardList.getInfo();

            PreferencesBoardInfo newBoard = new PreferencesBoardInfo(board.getTitle(),
                    board.getKey(),
                    newPass,
                    board.getFont(),
                    board.getBackgroundColor());

            for(int i = 0; i < boards.size(); i++) {
                PreferencesBoardInfo b = boards.get(i);
                if(board.equals(b)) {
                    boards.set(i, newBoard);
                }
            }

            PreferencesBoardList newBoardList = new PreferencesBoardList();
            newBoardList.setInfo(boards);

            String newBoardListJson = objectMapper.writeValueAsString(newBoardList);
            prefs.put(serverAddress, newBoardListJson);

            return newBoard;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Method for updating board
     * @param serverAddress the address of the server
     * @param oldBoard the old board
     * @param updatedBoard the new board
     */
    public void updateBoard(String serverAddress,
                            PreferencesBoardInfo oldBoard,
                            Board updatedBoard) {
        String boardListJson = prefs.get(serverAddress, "{\"info\": []}");

        try {
            PreferencesBoardList boardList = objectMapper.readValue(boardListJson,
                    PreferencesBoardList.class);

            List<PreferencesBoardInfo> boards = boardList.getInfo();

            PreferencesBoardInfo newBoard = new PreferencesBoardInfo(updatedBoard.title,
                    updatedBoard.key,
                    updatedBoard.password,
                    updatedBoard.font,
                    updatedBoard.colour);

            for (int i = 0; i < boards.size(); i++) {
                PreferencesBoardInfo b = boards.get(i);
                if (newBoard.getKey().equals(updatedBoard.key)) {
                    boards.set(i, newBoard);
                }
            }

            PreferencesBoardList newBoardList = new PreferencesBoardList();
            newBoardList.setInfo(boards);

            String newBoardListJson = objectMapper.writeValueAsString(newBoardList);
            prefs.put(serverAddress, newBoardListJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }
}
