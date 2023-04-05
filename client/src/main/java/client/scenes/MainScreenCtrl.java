package client.scenes;


import client.utils.PreferencesBoardInfo;
import client.utils.ServerUtils;
import client.utils.UserPreferences;
import com.google.inject.Inject;
import commons.Board;
import commons.Palette;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

public class MainScreenCtrl {

    @FXML
    private AnchorPane root;

    @FXML
    private Label title;

    @FXML
    private TextField joinField;

    @FXML
    private TextField addBoardField;

    @FXML
    private Label nullTitle;

    @FXML
    private Button addBoard;

    @FXML
    private Button joinBoard;

    private ServerUtils server;

    @FXML
    private ListView<PreferencesBoardInfo> list;

    ObservableList<PreferencesBoardInfo> data;

    private final MainCtrl mainCtrl;
    private final UserPreferences prefs;

    /**
     * Create a new CardListCtrl
     * @param server
     * @param mainCtrl
     * @param prefs
     */
    @Inject
    public MainScreenCtrl(ServerUtils server, MainCtrl mainCtrl, UserPreferences prefs) {

        this.server = server;
        this.mainCtrl = mainCtrl;
        this.prefs = prefs;

        data = FXCollections.observableArrayList();
        list = new ListView<>();


    }


    public void initialize() {
        var boardData = prefs.getBoards(server.getServerAddress());
        data.setAll(boardData);
        list.setItems(data);
        list.setCellFactory(param -> new BoardTitleCtrl(server,this, mainCtrl, prefs));
        list.setStyle("-fx-control-inner-background: " +  "#00B4D8" + ";");

        for (PreferencesBoardInfo b: boardData)
            server.registerForUpdates(b.getKey(), c -> {
                System.out.println("Received update for board " + b.getTitle() + " to title " + c.title);
                if (c.key.equals("REMOVED"))
                    prefs.leaveBoard(server.getServerAddress(), b);
                else
                    prefs.updateBoard(server.getServerAddress(), b, c);
                Platform.runLater(this::refresh);
            });
    }

    void addToData(PreferencesBoardInfo newPrefs) {
        data.add(newPrefs);
        server.registerForUpdates(newPrefs.getKey(), c -> {
            System.out.println("Received update for board " + newPrefs.getTitle() + " to title " + c.title);
            if (c.key.equals("REMOVED")) {
                server.unregisterForUpdates(newPrefs.getKey());
                prefs.leaveBoard(server.getServerAddress(), newPrefs);
            }
            else
                prefs.updateBoard(server.getServerAddress(), newPrefs, c);
            Platform.runLater(this::refresh);
        });
    }

    /**
     * Gets the board title from the database
     */
    public void refresh() {
        var boardData = prefs.getBoards(server.getServerAddress());
        data.setAll(boardData);
    }



    /**
     * Enters a specific board based on a key
     * Creates a new window (Board)
     * If successful, joins the board through the server
     *
     * @param event the ActionEvent
     * @return
     */
    public void connectToBoard(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Board.fxml"));
            Board board = server.getBoardByKey(joinField.getText());
            if(board != null) {
                var newPrefs = prefs.addBoard(server.getServerAddress(), board);
                newPrefs = prefs.updateBoardPassword(server.getServerAddress(), newPrefs, board.password);
                addToData(newPrefs);
                setPalette(board);
                mainCtrl.showBoard(joinField.getText(),0);
                joinField.clear();
                nullTitle.setText("");
            }
            else throw new Exception("Doesnt Exist");
        }
        catch(Exception e){
            nullTitle.setText("There is no board with such a key. " +
                    "The board might have been deleted " +
                    "or the key you enter is incorrect.");
        }
    }

    /**
     * Enters a specific board based on a key
     * Creates a new window (Board)
     * If successful, joins the board through the server
     *
     * @param event the KeyEvent
     * @return
     */
    public void connectToBoardKey(KeyEvent event) {
        if(event.getCode().toString().equals("ENTER")) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Board.fxml"));
                Board board = server.getBoardByKey(joinField.getText());
                if(board != null) {
                    var newPrefs = prefs.addBoard(server.getServerAddress(), board);
                    newPrefs = prefs.updateBoardPassword(server.getServerAddress(), newPrefs, board.password);
                    addToData(newPrefs);
                    setPalette(board);
                    mainCtrl.showBoard(joinField.getText(),0);
                    joinField.clear();
                    nullTitle.setText("");
                }
                else throw new Exception("Doesn't Exist");
            }
            catch(Exception e){
                nullTitle.setText("There is no board with such a key. " +
                        "The board might have been deleted " +
                        "or the key you enter is incorrect.");
            }
        }

    }

    /**
     * Adds a Board
     *
     * @param event the ActionEvent
     * @return
     */
    public void addBoard(ActionEvent event) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("AddBoard.fxml"));
        try {
            Parent root = fxmlLoader.load();
            AddBoardCtrl controller = fxmlLoader.getController();

            Stage stage = new Stage();
            stage.setTitle("Add new board");
            stage.setScene(new Scene(root, 520, 370));
            stage.showAndWait();

            if (controller.success) {
                String title = controller.storedText;
                String password = controller.password;
                String backgroundColor = controller.backgroundColor;
                String fontColor = controller.fontColor;
                System.out.println("The title is " + title);

                Board board = new Board(title,backgroundColor,fontColor,"#CAF0F8",
                        "#000000", password, new ArrayList<>(), new HashSet<>(), new HashSet<>());

                //Generates a random invite key (the preset password is "read")
                board.generateInviteKey();
                Board newBoard = server.addBoard(board);
                PreferencesBoardInfo prefBoard = prefs.addBoard
                        (server.getServerAddress(), newBoard);
                var newPrefs = prefs.updateBoardPassword(server.getServerAddress(), prefBoard, newBoard.password);
                addToData(newPrefs);
                setPalette(newBoard);
            }
        } catch (IOException e) {
            System.out.println(e.getStackTrace());
        }
    }


    /**
     * Method that sets a palette
     * @param board
     */
    void setPalette(Board board){
        Palette defaultPalette = new Palette("default", "#00B4D8", "#000000",
                true, board, new HashSet<>());
        defaultPalette.id = server.addPalette(board.id, defaultPalette).id;
    }

    /**
     * Redirects to connect to server screen
     * @param event - on click of button disconnect
     */
    public void disconnect(ActionEvent event) {
        mainCtrl.showConnect();
    }
}










