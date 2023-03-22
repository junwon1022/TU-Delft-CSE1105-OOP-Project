package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Card;
import commons.ListOfCards;
import jakarta.ws.rs.WebApplicationException;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.event.ActionEvent;

import javax.swing.text.html.ImageView;
import java.io.IOException;
import java.util.List;

public class CardCtrl extends ListCell<Card> {
    private final ServerUtils server;
    private final BoardCtrl board;
    private final ListOfCardsCtrl parent;

    private Card data;

    @FXML
    private AnchorPane root;

    @FXML
    private Label title;

    @FXML
    private Button delete;
    @FXML
    private Text text;

    @FXML
    private Label description;


    /**
     * Create a new CardCtrl
     * @param server The server to use
     * @param board The board this card belongs to
     * @param parent The parent list of cards
     */
    @Inject
    public CardCtrl(ServerUtils server, BoardCtrl board, ListOfCardsCtrl parent) {
        this.server = server;
        this.board = board;
        this.parent = parent;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Card.fxml"));
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        root.setStyle("-fx-background-color: #00B4D8;" +
                " -fx-border-radius: 10;" +
                " -fx-background-radius: 10;");

        setOnDragDetected(this::handleDragDetected);

        setOnDragOver(this::handleDragOver);

        setOnDragEntered(this::handleDragEntered);

        setOnDragExited(this::handleDragExited);

        setOnDragDropped(this::handleDragDropped);

        setOnDragDone(Event::consume);
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
                description.setOpacity(0);
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
            server.removeCard(data);
        } catch (WebApplicationException e) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
        board.refresh();
    }

    /**
     * Method that removes the task from the list, visually
     */
    private void setTextOpacity(){
        text.setOpacity(0.0);
    }


    /**
     * Handles drag detected
     * @param event drag detected event
     */
    private void handleDragDetected(MouseEvent event) {
        if (getItem() == null) {
            return;
        }

        Dragboard dragboard = startDragAndDrop(TransferMode.MOVE);
        ClipboardContent content = new ClipboardContent();
        content.putString(getItem().id + "X" + getItem().list.id);
        dragboard.setContent(content);

        event.consume();
    }

    /**
     * Handles drag over object
     * @param event drag event
     */
    private void handleDragOver(DragEvent event) {
        if (event.getGestureSource() != this &&
                event.getDragboard().hasString()) {
            if (event.getGestureSource().getClass() == CardCtrl.class)
                event.acceptTransferModes(TransferMode.MOVE);
        }
        event.consume();
    }

    /**
     * Handles drag entering object
     * @param event drag event
     */
    private void handleDragEntered(DragEvent event) {
        if (event.getGestureSource() != this &&
                event.getDragboard().hasString()) {
            setOpacity(0.3);
        }
    }

    /**
     * Handles drag exiting object
     * @param event drag event
     */
    private void handleDragExited(DragEvent event) {
        if (event.getGestureSource() != this &&
                event.getDragboard().hasString()) {
            setOpacity(1);
        }
    }

    /**
     * Handles dropping drag
     * @param event drag event
     */
    private void handleDragDropped(DragEvent event) {
        if (getItem() != null) {
            dragDroppedOnCell(event);
        }
        else {
            dragDroppedOnEmptyList(event);
        }

    }

    /**
     * Handles dropping drag on a particular cell
     * @param event drag event
     */
    private void dragDroppedOnCell(DragEvent event) {
        Dragboard db = event.getDragboard();
        if (db.hasString()) {
            String[] strings = db.getString().split("X");
            long dbCardId = Long.decode(strings[0]);
            long dbListId = Long.decode(strings[1]);
            if (dbListId != this.parent.cardData.id) {
                Card draggedCard = moveCardToOtherList(dbCardId, dbListId);
                List<Card> items = this.parent.cardData.cards;

                int draggedIdx = (int) draggedCard.order;
                int thisIdx = items.indexOf(getItem());
                server.moveCard(this.parent.cardData, draggedIdx, thisIdx);
            }
            else {
                List<Card> items = this.parent.cardData.cards;
                int draggedIdx = 0;
                for (int i = 0; i < items.size(); i++)
                    if (items.get(i).id == dbCardId)
                        draggedIdx = i;
                int thisIdx = items.indexOf(getItem());
                server.moveCard(this.parent.cardData, draggedIdx, thisIdx);
            }
            board.refresh();
        }
        event.setDropCompleted(true);
        event.consume();
    }

    /**
     * Handles dropping drag on an empty list
     * @param event drag event
     */
    private void dragDroppedOnEmptyList(DragEvent event) {
        Dragboard db = event.getDragboard();
        if (db.hasString()) {
            String[] strings = db.getString().split("X");
            long dbCardId = Long.decode(strings[0]);
            long dbListId = Long.decode(strings[1]);

            moveCardToOtherList(dbCardId, dbListId);

            board.refresh();
        }
        event.setDropCompleted(true);
        event.consume();
    }

    /**
     * Moves a card from one list to a different one
     * @param dbCardId the id of the card from the database
     * @param dbListId the id of the card from the database
     * @return the moved card
     */
    private Card moveCardToOtherList(long dbCardId, long dbListId) {
        List<Card> draggedList = null;
        for (ListOfCards loc: this.board.data)
            if (loc.id == dbListId)
                draggedList = loc.cards;

        Card draggedCard = null;
        for (Card c: draggedList)
            if (c.id == dbCardId)
                draggedCard = c;

        server.removeCard(draggedCard);

        draggedCard.list = this.parent.cardData;
        draggedCard.order = this.parent.cardData.cards.size();
        server.addCard2(draggedCard);
        return draggedCard;
    }
}