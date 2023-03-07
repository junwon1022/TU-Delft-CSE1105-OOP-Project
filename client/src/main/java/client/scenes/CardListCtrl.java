package client.scenes;

import commons.Card;
import commons.CardList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class CardListCtrl extends ListCell<CardList> {
    CardList cardData;

    @FXML
    private AnchorPane root;

    @FXML
    private Label title;

    @FXML
    private Button addButton;

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
            cardData = item;
            setGraphic(root);
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        }
    }

    public void addCard(ActionEvent event) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("AddCard.fxml"));
        try {
            Parent root = fxmlLoader.load();
            AddCardCtrl controller = fxmlLoader.getController();
            Stage stage = new Stage();
            stage.setTitle("Add new card");
            stage.setScene(new Scene((Parent) root, 300, 200));
            stage.showAndWait();
            if (controller.success) {
                String title = controller.storedText;

                Card newCard = new Card(title);

                List<Card> cards = cardData.getCards();
                cards.add(newCard);
                cardData.setCards(cards);

                data.add(newCard);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
