package client.scenes;

import client.utils.ServerUtils;
import commons.Tag;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.shape.Circle;

import java.io.IOException;


public class TagCircleCtrl extends ListCell<Tag> {
    private ServerUtils server;
    private BoardCtrl board;
    private ListOfCardsCtrl list;
    private CardCtrl card;
    @FXML
    private Circle circle;

    private Tag tag;



    /**
     * Create a new TagCtrl
     * @param server The server to use
     * @param board The board this Tag belongs to
     * @param list The list this Tag belongs to
     * @param card The board this Tag belongs to
     */
    public TagCircleCtrl(ServerUtils server, BoardCtrl board, ListOfCardsCtrl list, CardCtrl card) {
        this.server = server;
        this.board = board;
        this.list = list;
        this.card = card;
        circle.setStyle("-fx-background-color: " + tag.colour + ";");

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("TagCircle.fxml"));
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Called whenever the parent ListView is changed. Sets the data in this controller.
     * @param item The new item for the cell.
     * @param empty whether this cell represents data from the list. If it
     *        is empty, then it does not represent any domain data, but is a cell
     *        being used to render an "empty" row.
     */
    @Override
    protected void updateItem(Tag item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setText(null);
            setContentDisplay(ContentDisplay.TEXT_ONLY);
        } else {
            circle.setStyle("-fx-background-color: " + tag.colour + ";");
            tag = item;

            setGraphic(circle);
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        }
    }
}
