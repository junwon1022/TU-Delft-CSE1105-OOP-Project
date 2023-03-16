package client.scenes;

import client.utils.ServerUtils;
import commons.Card;
import commons.ListOfCards;
import jakarta.ws.rs.WebApplicationException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class ListOfCardsCtrl extends ListCell<ListOfCards> {
    private final ServerUtils server;
    private final BoardCtrl board;

    private ListOfCards cardData;

    public String storedText;

    @FXML
    private AnchorPane root;

    @FXML
    private Label title;

    @FXML
    private Button addButton;

    @FXML
    private ListView<Card> list;

    @FXML
    private Button rename;

    @FXML
    private TextField name;

    @FXML
    private Button addCardButton;

    private ObservableList<Card> data;


    /**
     * Create a new CardListCtrl
     * @param server The server to use
     * @param board The board this CardList belongs to
     */
    public ListOfCardsCtrl(ServerUtils server, BoardCtrl board) {
        this.server = server;
        this.board = board;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ListOfCards.fxml"));
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        data = FXCollections.observableArrayList();

        list.setItems(data);
        list.setCellFactory(param -> new CardCtrl(server, board));


        list.setStyle("-fx-control-inner-background: " +  "#00B4D8" + ";");
    }

    /**
     * Called whenever the parent ListView is changed. Sets the data in thsi controller.
     * @param item The new item for the cell.
     * @param empty whether or not this cell represents data from the list. If it
     *        is empty, then it does not represent any domain data, but is a cell
     *        being used to render an "empty" row.
     */
    @Override
    protected void updateItem(ListOfCards item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setText(null);
            setContentDisplay(ContentDisplay.TEXT_ONLY);
        } else {
            title.setText(item.title);
            data.setAll(item.cards);
            cardData = item;

            setGraphic(root);
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        }
    }

    /**
     * Adds a new card to the CardList.
     * Shows a text field that asks for the title.
     * If pressed enter, adds the card via the server and forces a board refresh.
     * @param event the KeyEvent
     */
    public void addCard(KeyEvent event) {
        if(event.getCode().toString().equals("ENTER")
                && !name.getText().equals("") && name.getText() != null){
            storedText = name.getText();
            server.addCard(new Card(storedText, "description", "red", cardData, null, null));
            name.clear();
            board.refresh();
        }
    }

    /**
     * Adds a new card to the CardList.
     * Shows a text field that asks for the title.
     * If pressed OK button, adds the card via the server and forces a board refresh.
     * @param event the Action event
     */
    public void addCardB(ActionEvent event) {
        if(!name.getText().equals("") && name.getText() != null) {
            storedText = name.getText();
            server.addCard(new Card(storedText, "description", "red", cardData, null, null));
            name.clear();
            board.refresh();
        }
    }

    /**
     * Shows the text field and the button to add a card
     * @param event
     */
    public void showButton(ActionEvent event) {
        name.setOpacity(1);
        addCardButton.setOpacity(1);
    }



    /**
     * Method that removes the list from the server
     * @param event - the 'x' button being clicked
     */
    public void remove(ActionEvent event){
        try {
            server.deleteList(cardData);
            Thread.sleep(100);
        } catch (WebApplicationException e) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        board.refresh();
    }

    /**
     * Method that renames the list;
     *
     * It shows a pop-up prompting the user to input
     * a new title for the said list;
     *
     * @param event - the rename button being pressed
     */
    public void renameList(ActionEvent event){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("RenameList.fxml"));
        try {
            Parent root = fxmlLoader.load();
            RenameListCtrl controller = fxmlLoader.getController();
            controller.initialize(cardData);

            Stage stage = new Stage();
            stage.setTitle("Add new card");
            stage.setScene(new Scene(root, 320, 200));
            stage.showAndWait();

            if (controller.success) {
                String newTitle = controller.storedText;
                server.renameList1(cardData, newTitle);
                board.refresh();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
