/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package client.scenes;

import client.utils.PreferencesBoardInfo;
import client.utils.ServerUtils;
import client.utils.UserPreferences;
import com.google.inject.Inject;
import commons.Board;
import commons.Card;
import commons.ListOfCards;
import commons.Tag;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.shape.Line;

import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.stream.Collectors;

public class BoardCtrl {
    private final UserPreferences prefs;

    private final ServerUtils server;

    private final MainCtrl mainCtrl;

    int adminFlag;

    private String boardKey;

    @FXML
    private Line line;

    @FXML
    private Line line2;

    @FXML
    private Label copied;

    @FXML
    private ListView<ListOfCards> list;

    @FXML
    private Label key;
    @FXML
    private Label title;
    @FXML
    private Button copyButton;

    @FXML
    private Label nullTitle;

    @FXML
    private TextField joinField;

    @FXML
    private Button addTag;

    @FXML
    private ListView tagList;
    @FXML
    private AnchorPane anchorPane;

    @FXML
    private ListView<PreferencesBoardInfo> recentBoards;

    ObservableList<PreferencesBoardInfo> recentBoardsData;

    @FXML
    private Button customization;

    @FXML
    private AnchorPane boardAnchor;


    ObservableList<ListOfCards> listOfCards;
    ObservableList<Tag> tags;


    private Board board;


    /**
     * Create a new BoardCtrl.
     * @param prefs the preferences of the user
     * @param server    The server to use.
     * @param mainCtrl The main control
     * @param boardKey The key of a specific board
     */
    @Inject
    public BoardCtrl(UserPreferences prefs,
                     ServerUtils server,
                     MainCtrl mainCtrl,
                     String boardKey) {
        this.prefs = prefs;
        this.server = server;
        this.boardKey = boardKey;
        this.mainCtrl = mainCtrl;

        listOfCards = FXCollections.observableArrayList();
        tags = FXCollections.observableArrayList();
        initialize();
    }

    /**
     * Initialize the scene.
     */
    public void initialize() {
        boolean haveBoard = false;
        try {
            board = this.server.getBoardByKey(boardKey);
            if(board == null) System.out.println("BOARD IS NULL");
            haveBoard = true;

            listOfCards = FXCollections.observableArrayList();

            list.setFixedCellSize(0);
            list.setItems(listOfCards);
            list.setCellFactory(lv -> new ListOfCardsCtrl(server, this));
            list.setStyle("-fx-background-color: " + board.colour);
            key.setText(board.key);
            title.setText(board.title);
            title.setStyle("-fx-text-fill: " + board.font);

            tags = FXCollections.observableArrayList();
            loadTagList();

            if (haveBoard) {
                prefs.addBoard(server.getServerAddress(), board);
            }

            if(adminFlag == 0) recentBoardsData = FXCollections.observableList
                    (prefs.getBoards(server.getServerAddress()));
            else recentBoardsData = FXCollections.observableList
                (server.getBoards().stream()
                    .map(x -> new PreferencesBoardInfo
                    (x.title, x.key, x.password, x.font, x.colour))
                            .collect(Collectors.toList()));
            loadRecentBoards();

            AnchorPane.setBottomAnchor(addTag, 5.0);
            AnchorPane.setRightAnchor(addTag, (anchorPane.getWidth() - addTag.getWidth()) / 2);

            refresh();

            server.registerForMessages("/topic/" + board.id, Board.class, s -> {
                for (var list: s.lists)
                    list.cards.sort(Comparator.comparingLong(Card::getOrder));
                Platform.runLater(() -> listOfCards.setAll(s.lists));
                Platform.runLater(() -> tags.setAll(s.tags));
            });
        } catch(Exception e) {
            board = getBoard();
        }
    }


    /**
     * Method that changes the colours of the board, title and key
     */
    private void changeColours(){
        list.setStyle("-fx-background-color: " + board.colour);
        title.setStyle("-fx-text-fill: " + board.font);
        key.setStyle("-fx-text-fill: " + board.font);
    }

    /**
     * Uses the server util class to fetch board data from the server.
     */
    public void refresh() {

        //the method call of getListsInBoard will be with the board parameter
        var serverData = server.getLists(board.id);
        listOfCards.setAll(serverData);
        //the method call of getTagsInBoard will be with the board parameter
        var serverDataTags = server.getTagsInBoard(board.id);
        tags.setAll(serverDataTags);
    }

    /**
     * Loads the listview to auto-fit its parent
     */
    public void loadRecentBoards() {
        recentBoardsData = FXCollections.observableList(prefs.getBoards(server.getServerAddress()));
        recentBoards.setItems(recentBoardsData);
        recentBoards.setCellFactory(lv -> new RecentBoardsCtrl(this, mainCtrl));
        recentBoards.setFixedCellSize(0);
        recentBoards.setMaxHeight(600);

        // set the VBox to always grow to fill the AnchorPane
        recentBoards.setPrefHeight(Region.USE_COMPUTED_SIZE);
        recentBoards.setPrefWidth(Region.USE_COMPUTED_SIZE);
        recentBoards.setMaxHeight(Double.MAX_VALUE);
        recentBoards.setMaxWidth(Double.MAX_VALUE);

        // set the constraints for the VBox to fill the AnchorPane
        AnchorPane.setTopAnchor(recentBoards, 0.0);
        AnchorPane.setBottomAnchor(recentBoards, 0.0);
        AnchorPane.setLeftAnchor(recentBoards, 0.0);
        AnchorPane.setRightAnchor(recentBoards, 0.0);
    }


    /**
     * Loads the list to auto-fit its parent
     */
    public void loadTagList() {
        tagList.setItems(tags);
        tagList.setCellFactory(lv -> new TagCtrl(server, this));
        tagList.getStylesheets().add("styles.css");
        tagList.setFixedCellSize(0);
        tagList.setMaxHeight(400);

        // set the tagList to always grow to fill the AnchorPane
        tagList.setPrefHeight(Region.USE_COMPUTED_SIZE);
        tagList.setPrefWidth(Region.USE_COMPUTED_SIZE);
        tagList.setMaxHeight(Double.MAX_VALUE);
        tagList.setMaxWidth(Double.MAX_VALUE);

        // set the constraints for the tagList to fill the AnchorPane
        AnchorPane.setTopAnchor(tagList, 0.0);
        AnchorPane.setBottomAnchor(tagList, 35.0);
        AnchorPane.setLeftAnchor(tagList, 0.0);
        AnchorPane.setRightAnchor(tagList, 0.0);
    }

    /**
     * Opens a new window to add a new list of cards.
     * @param event the ActionEvent
     */
    public void addListOfCards(ActionEvent event) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("AddListOfCards.fxml"));
        try {
            Parent root = fxmlLoader.load();
            AddListOfCardsCtrl controller = fxmlLoader.getController();

            Stage stage = new Stage();
            stage.setTitle("Add new list");
            Scene addListScene = new Scene(root);
            addListScene.getStylesheets().add("styles.css");
            stage.setHeight(240);
            stage.setWidth(320);
            stage.setScene(addListScene);
            stage.showAndWait();

            if (controller.success) {
                String title = controller.storedText;

                ListOfCards list = getList(title);
                //server.addList(list);
                ListOfCards addedList = server.addListOfCards(list);
                System.out.println(addedList);

                //change the id of the board locally
                list.id = addedList.id;

                this.refresh();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Opens a new window to add a new list of cards.
     * @param event the ActionEvent
     */
    public void addTag(ActionEvent event) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("AddTag.fxml"));
        try {
            Parent root = fxmlLoader.load();
            AddTagCtrl controller = fxmlLoader.getController();

            Stage stage = new Stage();
            stage.setTitle("Add a new tag");
            Scene addTagScene = new Scene(root);
            addTagScene.getStylesheets().add("styles.css");
            stage.setHeight(320);
            stage.setWidth(510);
            stage.setScene(addTagScene);
            stage.showAndWait();

            if (controller.success) {
                String name = controller.storedText;
                String backgroundColor = controller.backgroundColor;
                String fontColor = controller.fontColor;

                Tag tag = getTag(name, backgroundColor, fontColor);
                Tag addedTag = server.addTag(tag);
                System.out.println(addedTag);

                //change the id of the tag locally
                tag.id = addedTag.id;

                this.refresh();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Opens the help screen
     * @param event - Key event when the user presses shift + /
     */
    public void openHelpScreen(KeyEvent event) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("HelpScreen.fxml"));

        if(event.getCode().toString().equals("SLASH") && event.isShiftDown()) {
            try {
                Parent root = fxmlLoader.load();

                Stage stage = new Stage();
                stage.setTitle("Help");
                stage.setScene(new Scene(root, 600, 400));
                stage.showAndWait();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Copies the key to the clipboard and shows a notification to the user
     * @param event
     */
    public void copyKeyToClipboard(ActionEvent event) {
        copyToClipboard(board.key);
        copied.setVisible(true);
        PauseTransition delay = new PauseTransition(Duration.seconds(4));
        delay.setOnFinished(e -> copied.setVisible(false));
        delay.play();
    }

    /**
     * Copies a given key to the clipboard.
     * @param key
     */
    private void copyToClipboard(String key) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(key);
        clipboard.setContent(content);
    }

    /**
     * Goes back to overview
     * @param event - Key event when the user clicks the mouse + /
     */
    public void goToOverview(ActionEvent event) throws Exception {
        if(adminFlag == 0) mainCtrl.showMainScreen(server.getServerAddress());
        else mainCtrl.showAdmin(server.getServerAddress());
    }

    /**
     * Method that creates a new board
     * @return the new board
     */
    private Board getNewBoard(){
        return new Board("My Board", null, null,
                null, null, null, new ArrayList<>(), new HashSet<>(), new HashSet<>());
    }

    /**
     * Method that returns this board
     * @return the board
     */
    public Board getBoard(){
        return board;
    }


    /**
     * Method that returns a list
     * @param title
     * @return a new list
     */
    private ListOfCards getList(String title) {
        return new ListOfCards(title, board, new ArrayList<>());
    }

    /**
<<<<<<< HEAD
     * Method that opens the customization window
     * @param event
     */
    public void openCustomization(ActionEvent event){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Customization.fxml"));
        try {

            CustomizationCtrl customizationCtrl = new CustomizationCtrl(server,this, board);
            fxmlLoader.setController(customizationCtrl);
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle("Customization of board");
            stage.setScene(new Scene(root, 686, 527));
            stage.showAndWait();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        board = server.getBoard(board.id);
        changeColours();
    }


     /**
     * Creates a new tag
     * @param name
     * @param backgroundColor
     * @param fontColor
     * @return
     */
    private Tag getTag(String name, String backgroundColor, String fontColor) {
        return new Tag(name, backgroundColor, fontColor, board, new HashSet<>());
    }


    /**
     * Setter for the board key of the board displayed
     * @param boardKey
     * @return
     */
    public void setBoardKey(String boardKey) {
        this.boardKey = boardKey;
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

            if (server.getBoardByKey(joinField.getText()) != null) {
                mainCtrl.showBoard(joinField.getText(),adminFlag);
                joinField.clear();
                nullTitle.setText("");
            } else throw new Exception("Doesnt Exist");
        } catch (Exception e) {
            joinField.clear();
            nullTitle.setText("There is no board with this key!");
        }
    }

    /**
     * Enters a specific board based on a key
     * Creates a new window (Board)
     * If successful, joins the board through the server
     *
     * @param event the ActionEvent
     * @return
     */
    public void connectToBoardKey(KeyEvent event) {
        if(event.getCode().toString().equals("ENTER")) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Board.fxml"));

                if(server.getBoardByKey(joinField.getText()) != null) {
                    mainCtrl.showBoard(joinField.getText(),adminFlag);
                    joinField.clear();
                    nullTitle.setText("");
                }
                else throw new Exception("Doesn't Exist");
            }
            catch(Exception e) {
                joinField.clear();
                nullTitle.setText("There is no board with this key!");
            }
        }
    }

    /**
     * Getter for a line in the board design
     * @return a ref to the line
     */
    public Line getLine() {
        return line;
    }

    /**
     * Getter for a line in the board design
     * @return a ref to the line
     */
    public Line getLine2() {
        return line2;
    }
}