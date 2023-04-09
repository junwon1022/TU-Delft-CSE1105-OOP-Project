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
import commons.CheckListItem;
import commons.ListOfCards;
import javafx.animation.*;
import commons.Tag;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.shape.Line;

import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
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

    /**
     * Gets the list view of list of cards in the board
     * @return the visual component list view
     */
    public ListView<ListOfCards> getList() {
        return list;
    }

    @FXML
    private ListView<ListOfCards> list;

    @FXML
    private Label key;

    @FXML
    private Accordion accordion;

    @FXML
    private Label title;

    @FXML
    private Button addTag;

    @FXML
    private AnchorPane addTagPane;

    @FXML
    private Button addList;

    @FXML
    private Label nullTitle;

    @FXML
    private TextField joinField;

    @FXML
    private ListView tagList;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private ListView<PreferencesBoardInfo> recentBoards;

    ObservableList<PreferencesBoardInfo> recentBoardsData;
    @FXML
    private Button lock;
    @FXML
    private Button unlock;

    @FXML
    private HBox readOnlyMessage;

    @FXML
    private Label addListDisabled;
    @FXML
    private Label addTagDisabled;

    @FXML
    private Label renameBoardDisabled;

    @FXML
    private Button customization;

    @FXML
    private Button rename;

    @FXML
    private AnchorPane boardAnchor;

    @FXML
    private TitledPane myBoards;

    ObservableList<ListOfCards> listOfCards;

    ObservableList<Tag> tags;

    private Board board;

    private boolean isUnlocked;

    private PreferencesBoardInfo prefBoard;

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
//        initialize();
    }

    /**
     * Initialize the scene.
     */
    public void initialize() {
        try {
            board = this.server.getBoardByKey(boardKey);
            if(board == null) System.out.println("BOARD IS NULL");
            listOfCards = FXCollections.observableArrayList();
            list.setFixedCellSize(0);
            list.setItems(listOfCards);
            list.setCellFactory(lv -> new ListOfCardsCtrl(server, this));
            list.setStyle("-fx-background-color: " + board.colour +
                    "; -fx-control-inner-background: " + board.listColour);
            key.setText(board.key);
            title.setText(board.title);
            title.setStyle("-fx-text-fill: " + board.font);
            if(adminFlag == 0)  {
                recentBoardsData = mainCtrl.mainScreenCtrl.data;
                styleMyBoards();
            } else {
                recentBoardsData = FXCollections.observableList
                    (server.getBoards().stream()
                        .map(x -> new PreferencesBoardInfo(x.title, x.key, x.password,
                                x.font, x.colour))
                        .collect(Collectors.toList()));
                styleMyBoardsAdmin();
            }
            prefBoard = recentBoardsData.stream().filter(x -> x.getKey().equals(boardKey))
                    .collect(Collectors.toList()).get(0);
            loadRecentBoards();
            loadTagList();
            refresh();
            handleSecurityLevel();
            // listen for card updates
            server.registerForMessages("/topic/" + board.id, Board.class, s -> {
                for (var list : s.lists) {
                    list.cards.sort(Comparator.comparingLong(Card::getOrder));
                    for(var card : list.cards) {
                        card.checklist.sort(Comparator.comparingLong(CheckListItem::getOrder));
                    }
                }
                Platform.runLater(() -> {
                    board = s;
                    this.title.setText(s.title);
                    changeColours();
                    handleSecurityLevel();});
                Platform.runLater(() -> listOfCards.setAll(s.lists));
                Platform.runLater(() -> tags.setAll(s.tags));
            });
        } catch(Exception e) {
            board = getNewBoard();
        }
    }

    /**
     * Style my boards for admin view
     */
    private void styleMyBoardsAdmin() {
        myBoards.setText("All Boards");
        myBoards.lookup(".title").setStyle("-fx-background-color: #1fa401;" +
                "-fx-border-color: #1fa401");
        myBoards.lookup(" > .content").setStyle("-fx-background-color: #daf6da;");
        accordion.lookup(".titled-pane").setStyle("-fx-text-fill: #e9f3e9");
        accordion.lookup(".arrow").setStyle("-fx-background-color: #e9f3e9;");
        accordion.setOnMouseEntered(event -> {
            accordion.lookup(" .titled-pane .title").setStyle(
                    "-fx-background-color: #E4F8FC;" +
                            "-fx-border-color: #1fa401;" +
                            "-fx-border-width: 1.5;" +
                            "-fx-background-radius: 10px;" +
                            "-fx-border-radius: 10px;"
            );
            accordion.lookup(".titled-pane").setStyle(
                    "-fx-text-fill: #1fa401"
            );
            accordion.lookup(".arrow").setStyle(
                    "-fx-background-color: #1fa401;"
            );
        });
        accordion.setOnMouseExited(event -> {
            accordion.lookup(" .titled-pane .title").setStyle(
                    "-fx-background-color: #1fa401;" +
                            "-fx-border-color: #1fa401;" +
                            "-fx-border-width: 1.5;" +
                            "-fx-background-radius: 10px;" +
                            "-fx-border-radius: 10px;"
            );
            accordion.lookup(".titled-pane").setStyle(
                    "-fx-text-fill: #E4F8FC"
            );
            accordion.lookup(".arrow").setStyle(
                    "-fx-background-color: #E4F8FC;"
            );
        });
    }

    /**
     * Style my boards for user view
     */
    private void styleMyBoards() {
        myBoards.setText("My Boards");
        myBoards.lookup(".title").setStyle("-fx-background-color: #00B4D8;" +
                "-fx-border-color: #00B4D8");
        myBoards.lookup(" > .content").setStyle("-fx-background-color: #daf1f6;");
        accordion.lookup(".titled-pane").setStyle("-fx-text-fill: #F8FDFE");
        accordion.lookup(".arrow").setStyle("-fx-background-color: #F8FDFE;");
        accordion.setOnMouseEntered(event -> {
            accordion.lookup(" .titled-pane .title").setStyle(
                    "-fx-background-color: #E4F8FC;" +
                            "-fx-border-color: #00B4D8;" +
                            "-fx-border-width: 1.5;" +
                            "-fx-background-radius: 10px;" +
                            "-fx-border-radius: 10px;"
            );
            accordion.lookup(".titled-pane").setStyle(
                    "-fx-text-fill: #00B4D8"
            );
            accordion.lookup(".arrow").setStyle(
                    "-fx-background-color: #00B4D8;"
            );
        });
        accordion.setOnMouseExited(event -> {
            accordion.lookup(" .titled-pane .title").setStyle(
                    "-fx-background-color: #00B4D8;" +
                            "-fx-border-color: #00B4D8;" +
                            "-fx-border-width: 1.5;" +
                            "-fx-background-radius: 10px;" +
                            "-fx-border-radius: 10px;"
            );
            accordion.lookup(".titled-pane").setStyle(
                    "-fx-text-fill: #E4F8FC"
            );
            accordion.lookup(".arrow").setStyle(
                    "-fx-background-color: #E4F8FC;"
            );
        });
    }


    /**
     * Method that changes the colours of the board, title and key
     */
    private void changeColours() {
        list.setStyle("-fx-background-color: " + board.colour +
            "; -fx-control-inner-background: " + board.colour);
        title.setStyle("-fx-text-fill: " + board.font);
        key.setStyle("-fx-text-fill: " + board.font);
    }

    /**
     * Adds a board (without its password)
     * only if it is not yet in user's list
     * @param boardList
     */
    private void addBoardToPrefs(List<PreferencesBoardInfo> boardList) {
        if (boardList
                .stream()
                .filter(x -> x.getKey().equals(boardKey))
                .collect(Collectors.toList())
                .size() == 0) {
            prefs.addBoard(server.getServerAddress(), board);
        }
    }

    /**
     * Handles whether read only view or write access should be given to user
     */
    private void handleSecurityLevel() {
        if (board.password == null || board.password.equals("") || adminFlag == 1) {
            this.writeAccess();
        } else {
            if(prefBoard.getPassword().equals(board.password)) {
                this.writeAccess();
            } else {
                this.readOnly();
            }
        }
    }

    /**
     * Makes the board read only
     */
    private void readOnly() {
        isUnlocked = false;
        lock.setVisible(true);
        unlock.setVisible(false);
        addTag.setDisable(true);
        addList.setDisable(true);
        rename.setDisable(true);
        readOnlyMessage.setManaged(true);
        readOnlyMessage.setVisible(true);
        readOnlyMessage.setOpacity(0.9);
        addListDisabled.setVisible(true);
        addTagDisabled.setVisible(true);
        renameBoardDisabled.setVisible(true);
    }

    /**
     * Gives write access
     */
    private void writeAccess() {
        isUnlocked = true;
        lock.setVisible(false);
        unlock.setVisible(true);
        addTag.setDisable(false);
        addList.setDisable(false);
        rename.setDisable(false);
        readOnlyMessage.setManaged(false);
        readOnlyMessage.setVisible(false);
        addListDisabled.setVisible(false);
        addTagDisabled.setVisible(false);
        renameBoardDisabled.setVisible(false);
    }

    /**
     * Closes the read only message
     * @param event
     */
    public void closeReadOnlyView(ActionEvent event) {
        FadeTransition fadeOutMessage = new FadeTransition(Duration.seconds(0.5), readOnlyMessage);
        fadeOutMessage.setFromValue(0.9);
        fadeOutMessage.setToValue(0.0);

        fadeOutMessage.setOnFinished(e -> {
            // Hide the message when the transition is finished
            readOnlyMessage.setManaged(false);
            readOnlyMessage.setVisible(false);
        });
        fadeOutMessage.play();
    }

    /**
     * Shows read-only message if button is disabled
     * @param event
     */
    public void showReadOnlyMessage(Event event) {
        readOnlyMessage.setManaged(true);
        readOnlyMessage.setVisible(true);
        FadeTransition fadeInTransition = new FadeTransition(
                Duration.seconds(0.3), readOnlyMessage);
        fadeInTransition.setFromValue(0.0);
        fadeInTransition.setToValue(0.9);
        fadeInTransition.play();
        Alert alert = new Alert(Alert.AlertType.WARNING,
                "Cannot edit in read-only mode. \n" +
                        "To gain write access, click on the lock icon and enter the password.",
                ButtonType.OK);// Load your custom icon image

        // Set the graphic of the alert dialog to custom image
        alert.setGraphic(new ImageView(new Image("warning-icon.png")));
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add("styles.css");
        dialogPane.getStyleClass().add("alert");
        dialogPane.lookupButton(ButtonType.OK).getStyleClass().add("normal-button");
        alert.showAndWait();
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
        board = server.getBoard(board.id);
        changeColours();
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
        tags = FXCollections.observableArrayList();
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

        AnchorPane.setBottomAnchor(addTagPane, 5.0);
        AnchorPane.setRightAnchor(addTagPane,
                (anchorPane.getWidth() - addTagPane.getWidth()) / 2);
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
                ListOfCards addedList = server.addListOfCards(list);

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
            prefBoard = prefs.updateBoardColors(server.getServerAddress(), prefBoard,
                    customizationCtrl.backgroundColor, customizationCtrl.fontColor);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        board = server.getBoard(board.id);
        changeColours();
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

            Board newBoard = server.getBoardByKey(joinField.getText());
            if (newBoard != null) {
                var newPrefs = prefs.addBoard(server.getServerAddress(), newBoard);
                newPrefs = prefs.updateBoardPassword(server.getServerAddress(),
                        newPrefs,
                        newBoard.password);
                mainCtrl.mainScreenCtrl.addToData(newPrefs);
                mainCtrl.mainScreenCtrl.setPalette(newBoard);
                mainCtrl.showBoard(joinField.getText(), adminFlag);
                joinField.clear();
                nullTitle.setText("");
            } else throw new Exception("Doesn't Exist");
        } catch (Exception e) {
            if (joinField.getText() == null || joinField.getText().length() == 0) {
                nullTitle.setText("Please enter a key!");
            } else {
                nullTitle.setText("There is no board with this key!");
            }
            joinField.clear();
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

                if (server.getBoardByKey(joinField.getText()) != null) {
                    mainCtrl.showBoard(joinField.getText(), adminFlag);
                    joinField.clear();
                    nullTitle.setText("");
                } else throw new Exception("Doesn't Exist");
            } catch(Exception e) {
                if (joinField.getText() == null || joinField.getText().length() == 0) {
                    nullTitle.setText("Please enter a key!");
                } else {
                    nullTitle.setText("There is no board with this key!");
                }
                joinField.clear();
            }
        }
    }


    /**
     * Method that lets you rename a board according to the title
     * @param event - the join button being clicked
     */
    public void renameBoard(ActionEvent event) {
        FXMLLoader fxmlLoader =
                new FXMLLoader(getClass().getResource("AdminRename.fxml"));
        try {
            Parent root = fxmlLoader.load();
            AdminRenameCtrl controller = fxmlLoader.getController();
            controller.initialize(board);

            Stage stage = new Stage();
            stage.setTitle("Add new board");
            stage.setScene(new Scene(root, 300, 200));
            stage.showAndWait();

            if (controller.success) {
                String newTitle = controller.storedText;
                title.setText(newTitle);
                server.renameBoard(board, newTitle);
                System.out.println("New title after calling the command: "+ board.title);
                refresh();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Redirects to connect to server screen
     * @param event - on click of button disconnect
     */
    public void disconnect(ActionEvent event) {
        mainCtrl.showConnect();
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

    /**
     * Handles whether a new password should be added or
     * it should open manage password pop-up
     * @param event
     */
    public void unlockButtonClicked(ActionEvent event) {
        if(board.password == null || board.password.length() == 0) {
            addPassword(event);
        } else {
            changePassword(event);
        }
    }


    /**
     * Opens a new window to add a new password.
     * @param event the ActionEvent
     */
    public void addPassword(ActionEvent event) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("AddPassword.fxml"));
        try {
            Parent root = fxmlLoader.load();
            AddPasswordCtrl controller = fxmlLoader.getController();

            Stage stage = new Stage();
            stage.setTitle("Protect your board");
            Scene addPasswordScene = new Scene(root);
            addPasswordScene.getStylesheets().add("styles.css");
            stage.setHeight(317);
            stage.setWidth(353);
            stage.setScene(addPasswordScene);
            stage.showAndWait();

            if (controller.success) {
                String password = controller.storedText;

                board = server.changeBoardPassword(board, password);
                System.out.println(board);

                prefBoard = prefs.updateBoardPassword(server.getServerAddress(),
                        prefBoard, password);
                this.refresh();
                writeAccess();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Opens a new window to add a new list of cards.
     * @param event the ActionEvent
     */
    public void enterPassword(ActionEvent event) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("EnterPassword.fxml"));
        try {
            Parent root = fxmlLoader.load();
            EnterPasswordCtrl controller = fxmlLoader.getController();
            controller.initialize(board);

            Stage stage = new Stage();
            stage.setTitle("Unlock board");
            Scene enterPasswordScene = new Scene(root);
            enterPasswordScene.getStylesheets().add("styles.css");
            stage.setHeight(307);
            stage.setWidth(353);
            stage.setScene(enterPasswordScene);
            stage.showAndWait();

            if (controller.success) {

                prefBoard = prefs.updateBoardPassword(server.getServerAddress(),
                        prefBoard, controller.enteredPassword);
                this.refresh();
                writeAccess();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Opens a new window to manage password.
     * @param event the ActionEvent
     */
    public void changePassword(ActionEvent event) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ChangePassword.fxml"));
        try {
            Parent root = fxmlLoader.load();
            ChangePasswordCtrl controller = fxmlLoader.getController();

            Stage stage = new Stage();
            stage.setTitle("Manage passwords of your board");
            Scene changePasswordScene = new Scene(root);
            changePasswordScene.getStylesheets().add("styles.css");
            stage.setHeight(363);
            stage.setWidth(527);
            stage.setScene(changePasswordScene);
            stage.showAndWait();

            if (controller.success) {
                String password = controller.storedText;

                if(password != null) {
                    board = server.changeBoardPassword(board, password);
                } else {
                    board = server.removePassword(board);
                }
                System.out.println(board);

                prefBoard = prefs.updateBoardPassword(server.getServerAddress(),
                        prefBoard, password);
                this.refresh();
                writeAccess();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Whether the board has been unlocked (or has no protection)
     * @return true iff unlocked
     */
    public boolean isUnlocked() {
        return isUnlocked;
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
}