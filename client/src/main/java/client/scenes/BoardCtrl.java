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
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class BoardCtrl {

    private final ServerUtils server;

    @FXML
    private ListView<ListOfCards> list;

    ObservableList<ListOfCards> data;

    /**
     * Create a new BoardCtrl.
     * @param server The server to use.
     */
    @Inject
    public BoardCtrl(ServerUtils server) {
        this.server = server;
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
        list.setStyle("-fx-control-inner-background: " +  "#03045E" + ";");
        refresh();
    }

    /**
     * Uses the server util class to fetch board data from the server.
     */
    public void refresh() {
        var serverData = server.getServerData();
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

                //Hard coded lines, should be changed to use the server data
                Board board = new Board("title", "#ffffff", "pass", new ArrayList<>());
                ListOfCards list = new ListOfCards(title, "00B4D8", board, new ArrayList<>());
                board.addList(list);
                //End of hard coded lines

                server.addList(list);
                this.refresh();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}