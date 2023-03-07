package client.scenes;

import commons.Card;
import commons.CardList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class CardListCtrl extends ListCell<CardList> {

    @FXML
    private AnchorPane root;

    @FXML
    private Label title;

    @FXML
    private ListView<Card> list;

    private ObservableList<Card> data;

    public CardListCtrl() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("CardList.fxml"));
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        setMinHeight(300);
        data = FXCollections.observableArrayList();
        list.setItems(data);
        list.setCellFactory(param -> new CardCtrl());
        list.setFixedCellSize(150);
    }

    @Override
    protected void updateItem(CardList item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setText(null);
            setContentDisplay(ContentDisplay.TEXT_ONLY);
        } else {
            title.setText(item.getTitle());
            data.setAll(item.getCards());
            setGraphic(root);
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        }
    }
}
