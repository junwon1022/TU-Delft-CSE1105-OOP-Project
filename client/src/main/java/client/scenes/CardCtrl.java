package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Card;
import jakarta.ws.rs.WebApplicationException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.event.ActionEvent;
import java.io.IOException;

public class CardCtrl extends ListCell<Card> {
    private final ServerUtils server;
    private final BoardCtrl board;

    private Card data;

    @FXML
    private AnchorPane root;

    @FXML
    private Label title;

    @FXML
    private Label description;

    @FXML
    private Button delete;

    @FXML
    private Text text;


    /**
     * Create a new CardCtrl
     * @param server The server to use
     * @param board The board this card belongs to
     */
    @Inject
    public CardCtrl(ServerUtils server, BoardCtrl board) {
        this.server = server;
        this.board = board;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Card.fxml"));
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Is called whenever the parent CardList is changed. Sets the data in this controller.
     * @param item The new item for the cell.
     * @param empty whether this cell represents data from the list or not. If it
     *        is empty, then it does not represent any domain data, but is a cell
     *        being used to render an "empty" row.
     */
    @Override
    protected void updateItem(Card item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setText(null);
            setContentDisplay(ContentDisplay.TEXT_ONLY);
        } else {
            title.setText(item.title);
            data = item;

            if(data.description == null || data.description.equals("")) {
                setTextOpacity();
            }

            setGraphic(root);
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        }
    }

    /**
     * Method that removes the task from the list, visually
     * @param event - the remove button being clicked
     */
    public void remove(ActionEvent event){
        try {
            server.deleteCard(data);
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
     * Method that removes the task from the list, visually
     */
    private void setTextOpacity(){
        text.setOpacity(0.0);
    }


}