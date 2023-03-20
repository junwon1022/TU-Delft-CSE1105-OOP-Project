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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class BoardCtrl {

    private final ServerUtils server;

    @FXML
    private ListView<ListOfCards> list;

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
        list.setFixedCellSize(0);
        list.setItems(data);
        list.setCellFactory(lv -> new ListOfCardsCtrl(server, this));
        list.setMaxHeight(600);
        list.setStyle("-fx-control-inner-background: " +  "#03045E" + ";");
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
            stage.setScene(new Scene(root, 300, 200));
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
     * Method that creates a new board
     * @return the new board
     */
    private Board getBoard(){
        return new Board("hardcoded board", null, null, new ArrayList<>());
    }


    private ListOfCards getList(String title){
        return new ListOfCards(title, "00B4D8", board, new ArrayList<>());
    }
}