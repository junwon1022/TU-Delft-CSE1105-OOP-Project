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
import commons.Card;
import commons.ListOfCards;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;

public class BoardCtrl {

    private final ServerUtils server;

    private final MainCtrl mainCtrl;

    public String boardKey;

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
    private AnchorPane anchorPane;

    @FXML
    private VBox vBox;
    @FXML
    private AnchorPane anchorPane2;

    @FXML
    private VBox vBox2;

    int adminFlag;
    ObservableList<ListOfCards> data;

    private Board board;

    /**
     * Create a new BoardCtrl.
     *
     * @param server    The server to use.
     * @param mainCtrl The main control
     * @param boardKey The key of a specific board
     */
    @Inject
    public BoardCtrl(ServerUtils server, MainCtrl mainCtrl, String boardKey) {
        this.server = server;
        this.boardKey = boardKey;
        this.mainCtrl = mainCtrl;
        data = FXCollections.observableArrayList();
        initialize();
    }


    /**
     * Initialize the scene.
     */
    public void initialize() {

        try {
            if (board != null && !boardKey.isEmpty()) {
                board = this.server.getBoardByKey(boardKey);
                System.out.println("This Board is " + board.toString());
            }
            if (board == null) throw new NullPointerException();


            data = FXCollections.observableArrayList();
            list.setFixedCellSize(0);
            list.setItems(data);
            list.setCellFactory(lv -> new ListOfCardsCtrl(server, this));
            list.setMaxHeight(600);
            list.getStylesheets().add("styles.css");
            key.setText(board.key);
            title.setText(board.title);

            AnchorPane.setBottomAnchor(addTag, 5.0);
            loadVBox();
            loadVBox2();
            refresh();

            server.registerForMessages("/topic/" + board.id, Board.class, s -> {
                for (var list : s.lists)
                    list.cards.sort(Comparator.comparingLong(Card::getOrder));
                Platform.runLater(() -> data.setAll(s.lists));
            });
        }
        catch(Exception e){
            board = getBoard();
        }

    }

    /**
     * Uses the server util class to fetch board data from the server.
     */
    public void refresh() {
        //the method call of getServerData will be with the board parameter
        var serverData = server.getServerData(board.id);
        data.setAll(serverData);
    }

    /**
     * Loads the vbox to auto-fit its parent
     */
    public void loadVBox() {
        // set the VBox to always grow to fill the AnchorPane
        vBox.setPrefHeight(Region.USE_COMPUTED_SIZE);
        vBox.setPrefWidth(Region.USE_COMPUTED_SIZE);
        vBox.setMaxHeight(Double.MAX_VALUE);
        vBox.setMaxWidth(Double.MAX_VALUE);

        // set the constraints for the VBox to fill the AnchorPane
        AnchorPane.setTopAnchor(vBox, 0.0);
        AnchorPane.setBottomAnchor(vBox, 35.0);
        AnchorPane.setLeftAnchor(vBox, 0.0);
        AnchorPane.setRightAnchor(vBox, 0.0);
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
            stage.setHeight(240);
            stage.setWidth(320);
            stage.setScene(addTagScene);
            stage.showAndWait();

            if (controller.success) {
                String name = controller.storedText;

                //TODO add getTag in Server Utils
                //Tag tag = getTag(name);
                //TODO add addTag in Server Utils
                //Tag addedTag = server.addTag(tag);
                //System.out.println(addedTag);

                //change the id of the board locally
                //tag.id = addedTag.id;

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
//        tooltip.setAnchorX(Window.getWindows().get(0).getWidth() * 0.97);
//        tooltip.setAnchorY(Window.getWindows().get(0).getHeight() * 0.15);
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
    public void goToOverview(ActionEvent event) {

        if(adminFlag == 0) mainCtrl.showMainScreen();
        else mainCtrl.showAdmin();
    }

    /**
     * Method that creates a new board
     * @return the new board
     */
    private Board getBoard(){


        return new Board("My Board", null, null,
                null, null, null, new ArrayList<>(), new HashSet<>());
    }


    private ListOfCards getList(String title){
        return new ListOfCards(title, board, new ArrayList<>());
    }
}