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
import commons.ListOfCards;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

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
}