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
import javafx.animation.PauseTransition;
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
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;

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

    ObservableList<ListOfCards> data;

    private Board board;

    /**
     * Create a new BoardCtrl.
     * @param server The server to use.
     */
    @Inject
    public BoardCtrl(ServerUtils server) {
        this.server = server;

        data = FXCollections.observableArrayList();

        // Placeholder for when Dave's branch is merged
        // so there are actually multiple boards
        try {
            board = this.server.getBoard(1);
        } catch (Exception e) {
            board = getBoard();
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
        data = FXCollections.observableArrayList();
        list.setFixedCellSize(0);
        list.setItems(data);
        list.setCellFactory(lv -> new ListOfCardsCtrl(server, this));
        list.setMaxHeight(600);
        list.getStylesheets().add("styles.css");
        key.setText(board.key);
        title.setText(board.title);
//        Tooltip tooltip = new Tooltip("Copy the invitamtion key.");
//        tooltip.setX(1000);
//        tooltip.setY(100);
//        Tooltip.install(copyButton, tooltip);
        refresh();
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
        tooltip.show(Window.getWindows().get(0));
        tooltip.setAnchorX(Window.getWindows().get(0).getWidth() * 0.97);
        tooltip.setAnchorY(Window.getWindows().get(0).getHeight() * 0.15);
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
     * Method that creates a new board
     * @return the new board
     */
    private Board getBoard(){
        return new Board("My Board", null, null, new ArrayList<>());
    }


    private ListOfCards getList(String title){
        return new ListOfCards(title, "A2E4F1", board, new ArrayList<>());
    }
}