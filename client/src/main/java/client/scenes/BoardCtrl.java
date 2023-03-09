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
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;


public class BoardCtrl {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    private ListView<ListOfCards> list;

    ObservableList<ListOfCards> data;

    /**
     * Create a new BoardCtrl.
     * @param server The server to use.
     * @param mainCtrl The main controller to use.
     */
    @Inject
    public BoardCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    /**
     * Initialize the scene.
     */
    public void initialize() {
        //Wrong constructors for ListOfCards
//        list.setFixedCellSize(0);
//        ListOfCards list1 = new ListOfCards("List 1", new ArrayList<>());
//        list1.cards.add(new Card("Card 1"));
//        list1.cards.add(new Card("Card 2"));
//
//        ListOfCards list2 = new ListOfCards("List 2", new ArrayList<>());
//        list2.cards.add(new Card("Card 3"));
//        list2.cards.add(new Card("Card 4"));
//
//        // Add the card lists to the data list
//        data = FXCollections.observableList(List.of(list1, list2));
//        list.setItems(data);
//        list.setCellFactory(lv -> new CardListCtrl());
    }
}