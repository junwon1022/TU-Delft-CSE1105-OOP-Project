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

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Board;
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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

public class BoardCtrl {

    private final ServerUtils server;

    @FXML
    private ListView<ListOfCards> list;

    @FXML
    private Label key;
    @FXML
    private Label title;
    @FXML
    private Button copyButton;

    @FXML
    private Button addTag;
    @FXML
    private Button addList;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private ListView tagList;
    @FXML
    private AnchorPane anchorPane2;

    @FXML
    private VBox vBox2;
    @FXML
    private Button lock;
    @FXML
    private Button unlock;

    @FXML
    private HBox readOnlyMessage;

    @FXML
    private Button closeReadOnly;

    @FXML
    private Label addListDisabled;

    ObservableList<ListOfCards> listOfCards;
    ObservableList<Tag> tags;

    private Board board;

    private boolean isUnlocked;

    /**
     * Create a new BoardCtrl.
     * @param server The server to use.
     */
    @Inject
    public BoardCtrl(ServerUtils server) {
        this.server = server;

        listOfCards = FXCollections.observableArrayList();
        tags = FXCollections.observableArrayList();

        // Placeholder for when Dave's branch is merged
        // so there are actually multiple boards
        try {
            board = this.server.getBoard(1);
        } catch (Exception e) {
            board = new Board("My Board", "#CAF0F8",
                    "#000000", "#A2E4F1", "#000000",
                    null, new ArrayList<ListOfCards>(), new HashSet<>());
            Board addedBoard = server.addBoard(board);
            board = server.getBoard(addedBoard.id);
        }
        refresh();
        /*
        board = getBoard();

        //add the board to the database
        Board addedBoard =  server.addBoard(board);

        //change the id of the board locally
        board.id = server.getBoard(addedBoard.id).id;
        System.out.println(board.id);
        */
    }

    /**
     * Initialize the scene.
     */
    public void initialize() {
        listOfCards = FXCollections.observableArrayList();
        list.setFixedCellSize(0);
        list.setItems(listOfCards);
        list.setCellFactory(lv -> new ListOfCardsCtrl(server, this));
        list.setMaxHeight(600);
        list.getStylesheets().add("styles.css");

        tags = FXCollections.observableArrayList();
        tagList.setFixedCellSize(0);
        tagList.setItems(tags);
        tagList.setCellFactory(lv -> new TagCtrl(server, this));
        tagList.setMaxHeight(400);
        tagList.getStylesheets().add("styles.css");

        key.setText(board.key);
        title.setText(board.title);
        AnchorPane.setBottomAnchor(addTag, 5.0);
        loadTagList();
        loadVBox2();
        refresh();
        if (board.password == null) {
            isUnlocked = true;
            lock.setVisible(false);
            unlock.setVisible(true);
        } else {
            //TODO check whether user has saved the password
            isUnlocked = false;
            lock.setVisible(true);
            unlock.setVisible(false);
            this.readOnly();
        }

        server.registerForMessages("/topic/" + board.id, Board.class, s -> {
            Platform.runLater(() -> listOfCards.setAll(s.lists));
            Platform.runLater(() -> tags.setAll(s.tags));
        });
    }

    /**
     * Makes the board read only
     */
    private void readOnly() {
        addTag.setDisable(true);
        addList.setDisable(true);
        closeReadOnly.setVisible(true);
        readOnlyMessage.setVisible(true);
        addListDisabled.setVisible(true);
    }

    /**
     * Gives write access
     */
    private void writeAccess() {
        addTag.setDisable(false);
        addList.setDisable(false);
        closeReadOnly.setVisible(false);
        readOnlyMessage.setVisible(false);
        addListDisabled.setVisible(false);
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
            readOnlyMessage.setVisible(false);
        });
        fadeOutMessage.play();
    }

    /**
     * Shows read-only message if button is disabled
     * @param event
     */
    public void showReadOnlyMessage(Event event) {
        readOnlyMessage.setVisible(true);
        FadeTransition fadeOutMessage = new FadeTransition(Duration.seconds(0.3), readOnlyMessage);
        fadeOutMessage.setFromValue(0.0);
        fadeOutMessage.setToValue(0.9);
        fadeOutMessage.play();
        Alert alert = new Alert(Alert.AlertType.WARNING,
                "Cannot edit in read-only mode. " +
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
        var serverData = server.getListsInBoard(board.id);
        listOfCards.setAll(serverData);
        //the method call of getTagsInBoard will be with the board parameter
        var serverDataTags = server.getTagsInBoard(board.id);
        tags.setAll(serverDataTags);
    }

    /**
     * Loads the list to auto-fit its parent
     */
    public void loadTagList() {
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
     * Loads the second vbox to auto-fit its parent
     */
    public void loadVBox2() {
        // set the VBox to always grow to fill the AnchorPane
        vBox2.setPrefHeight(Region.USE_COMPUTED_SIZE);
        vBox2.setPrefWidth(Region.USE_COMPUTED_SIZE);
        vBox2.setMaxHeight(Double.MAX_VALUE);
        vBox2.setMaxWidth(Double.MAX_VALUE);

        // set the constraints for the VBox to fill the AnchorPane
        AnchorPane.setTopAnchor(vBox2, 0.0);
        AnchorPane.setBottomAnchor(vBox2, 0.0);
        AnchorPane.setLeftAnchor(vBox2, 0.0);
        AnchorPane.setRightAnchor(vBox2, 0.0);
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
            stage.setHeight(240);
            stage.setWidth(320);
            stage.setScene(addTagScene);
            stage.showAndWait();

            if (controller.success) {
                String name = controller.storedText;

                Tag tag = getTag(name);
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
        Tooltip tooltip = new Tooltip("Key copied to clipboard!");
        PauseTransition delay = new PauseTransition(Duration.seconds(4));
        delay.setOnFinished(e -> tooltip.hide());
        tooltip.show(copyButton, copyButton.getLayoutX() + 45, copyButton.getLayoutY() + 68);
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
     * Method that creates a new board that is shown in case of an exception
     * @return the new board
     */
//    private Board getBoard(){
//        return new Board("My Board", null, null,
//                null, null, null, new ArrayList<>(), new HashSet<>());
//    }

    /**
     * Method that creates a new list of cards in the board
     * @param title
     * @return the new list
     */
    private ListOfCards getList(String title){
        return new ListOfCards(title, board, new ArrayList<>());
    }

    /**
     * Handles whether a new password should be added or
     * it should open manage password pop-up
     * @param event
     */
    public void unlockButtonClicked(ActionEvent event) {
        if(board.password != null) {
            changePassword(event);
        } else {
            addPassword(event);
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

                lock.setVisible(true);
                unlock.setVisible(false);
                //TODO decide whether addition of password
                // should lock the board for the user who added it
                isUnlocked = false;
                readOnly();
                //TODO add password in user file

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

                //TODO store password locally in user data, so that it can be remembered
                lock.setVisible(false);
                unlock.setVisible(true);
                isUnlocked = true;
                this.writeAccess();

                this.refresh();
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

                //TODO change password in user file

                this.refresh();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Getter for the board displayed
     * @return the board entity
     */
    public Board getBoard() {
        return this.board;
    }


    /**
     * Whether the board has been unlocked (or has no protection)
     * @return true iff unlocked
     */
    public boolean isUnlocked() {
        return isUnlocked;
    }

    /**
     * Makes a new tag
     * @param name
     * @return
     */
    private Tag getTag(String name) {
        return new Tag(name, "#00B4D8", board, new HashSet<>());
    }
}