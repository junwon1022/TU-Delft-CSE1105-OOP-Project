package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Card;
import commons.CheckListItem;
import commons.ListOfCards;
import commons.Palette;
import commons.Tag;
import jakarta.ws.rs.WebApplicationException;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.List;

public class CardCtrl extends ListCell<Card> {
    private final ServerUtils server;
    private final BoardCtrl board;
    private final ListOfCardsCtrl list;

    private boolean isOpen = false;

    private Stage storeDetailsStage;

    private Card card;

    private int completedTasks;

    private int totalTasks;

    @FXML
    private AnchorPane root;

    @FXML
    private Label title;

    @FXML
    private Label progressText;

    @FXML
    private Button delete;
    @FXML
    private Text text;

    @FXML
    private ImageView description;

    @FXML
    private Button renameButton;
    @FXML
    private GridPane tagGrid;
    private List<Tag> tags;

    @FXML
    private Button addDescription;

    /**
     * Create a new CardCtrl
     * @param server The server to use
     * @param board The board this card belongs to
     * @param list The parent list of cards
     */
    @Inject
    public CardCtrl(ServerUtils server, BoardCtrl board, ListOfCardsCtrl list) {
        this.server = server;
        this.board = board;
        this.list = list;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Card.fxml"));
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        board.refresh();

        setOnDragDetected(this::handleDragDetected);

        setOnDragOver(this::handleDragOver);

        setOnDragEntered(this::handleDragEntered);

        setOnDragExited(this::handleDragExited);

        setOnDragDropped(this::handleDragDropped);

        setOnDragDone(Event::consume);
    }

    /**
     * Is called whenever the parent CardList is changed. Sets the data in this controller.
     *
     * @param item  The new item for the cell.
     * @param empty whether this cell represents data from the list or not. If it
     *              is empty, then it does not represent any domain data, but is a cell
     *              being used to render an "empty" row.
     */
    @Override
    protected void updateItem(Card item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setText(null);
            setContentDisplay(ContentDisplay.TEXT_ONLY);
            if(isOpen){
                storeDetailsStage.close();
            }
        } else {
            title.setText(item.title);
            description.setVisible(updateDescriptionIcon(item.description));
            updateProgressText(item.checklist);
            card = item;
            if(card.palette != null)
                setColors(root, title);
            this.loadTags();
            setGraphic(root);
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        }
    }

    private void setColors(AnchorPane root, Label title){
        root.setStyle("-fx-background-color: " + card.palette.background +
                "; -fx-background-radius: 10");
        title.setStyle("-fx-text-fill: " + card.palette.font);
    }




    private boolean updateDescriptionIcon(String description) {
        if(!description.trim().equals("")) {
            return true;
        }
        return false;
    }


    /**
     * loads the tags colors on the card
     */
    public void loadTags() {
        tagGrid.getChildren().clear();
        int numRows = 0;
        int numCols = 5;
        int i = 0;
        for (Tag tag : card.tags) {
            Circle circle = new Circle(6, Color.web(tag.colour));
            circle.setSmooth(true);
            circle.setStroke(Color.web("#186B90"));
            circle.setStrokeWidth(0.5);
            tagGrid.add(circle, i % numCols, numRows);

            if (i % numCols == numCols - 1) {
                numRows++;
                double height = root.getPrefHeight() + 0.3;
                root.setPrefHeight(height);
            }
            i++;
        }
    }

    /**
     * Method that removes the task from the list, visually
     * @param event - the remove button being clicked
     */
    public void remove(ActionEvent event) {
        try {
            server.removeCard(card);
        } catch (WebApplicationException e) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
        board.refresh();
    }



    /**
     * Retrieves the title of the card
     * @return the title of the card
     */
    public String getTitle() {
        return title.getText();
    }

    /**
     * gets the description image reference of this card
     * @return the reference
     */
    public ImageView getDescription(){
        return description;
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
        // set drag view to a snapshot of the card
        SnapshotParameters snapshotParams = new SnapshotParameters();
        snapshotParams.setFill(Color.TRANSPARENT);
        dragboard.setDragView(root.snapshot(snapshotParams, null), event.getX(), event.getY());
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
        } else {
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
            if (dbListId != this.list.cardData.id) {
                Card draggedCard = moveCardToOtherList(dbCardId, dbListId);
                List<Card> items = this.list.cardData.cards;

                int draggedIdx = (int) draggedCard.order;
                int thisIdx = items.indexOf(getItem());
                server.moveCard(this.list.cardData, draggedIdx, thisIdx);
            } else {
                List<Card> items = this.list.cardData.cards;
                int draggedIdx = 0;
                for (int i = 0; i < items.size(); i++)
                    if (items.get(i).id == dbCardId)
                        draggedIdx = i;
                int thisIdx = items.indexOf(getItem());
                server.moveCard(this.list.cardData, draggedIdx, thisIdx);
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
        for (ListOfCards loc : this.board.listOfCards)
            if (loc.id == dbListId)
                draggedList = loc.cards;

        Card draggedCard = null;
        for (Card c : draggedList)
            if (c.id == dbCardId)
                draggedCard = c;

        Palette p = draggedCard.palette;
        server.removeCard(draggedCard);
        p = server.getPalette(p.board.id, p.id);
        draggedCard.palette = null;
        draggedCard.list = this.list.cardData;
        draggedCard.order = this.list.cardData.cards.size();
        draggedCard = server.addCard2(draggedCard);
        server.addPaletteToCard(draggedCard, p);
        return draggedCard;
    }

    /**
     * Method that opens the detailed view
     * --right now only does the renaming of a card functionality--
     * @param event - the rename button being clicked
     */
    public void renameCard(ActionEvent event) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("RenameCard.fxml"));
        try {
            Parent root = fxmlLoader.load();
            RenameCardCtrl controller = fxmlLoader.getController();
            controller.initialize(card);

            Stage stage = new Stage();
            stage.setTitle("Rename the card: " + card.title);
            stage.setScene(new Scene(root, 300, 200));
            stage.showAndWait();

            if (controller.success) {
                String newTitle = controller.storedText;

                //method that actually renames the list in the database
                server.renameCard(card, newTitle);
                board.refresh();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Opens the detailed view of a card when the description button is double clicked
     * @param event - the icon-button being clicked
     */
    public void openDetails(MouseEvent event) {
        if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
            if(isOpen){
                return;
            }
            setOpen(true);
            Stage detailsStage = new Stage();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("CardDetails.fxml"));
            loader.setControllerFactory(c -> new CardDetailsCtrl(server, board, this));

            Parent root;
            try {
                root = loader.load();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            CardDetailsCtrl cardDetailsCtrl = loader.getController();
            cardDetailsCtrl.setCard(card);
            cardDetailsCtrl.setTitle(getTitle());
            cardDetailsCtrl.setDescriptionText(card.description.equals(" ")
                    ? "" : card.description);
            if(!card.checklist.isEmpty()){
                cardDetailsCtrl.setChecklists(card.checklist);
            }
            cardDetailsCtrl.setPreset();
            storeDetailsStage = detailsStage;

            Scene scene = new Scene(root);
            detailsStage.setScene(scene);
            //make it so that details can only be closed if exitDetails is called.
            detailsStage.setOnCloseRequest(Event -> {
                Event.consume();
            });
            detailsStage.show();
        }
    }

    /**
     * Sets the progress text to the checked checklist and total
     * checklists. Also checks if total is 0 then it
     * makes the progress text disappear
     * @param completed the number of checked checklists
     * @param total total number of checklists
     */
    public void setProgressText(int completed, int total) {
        completedTasks = completed;
        totalTasks = total;
        if(total > 0){
            progressText.setText(completed + "/" + total);
        }
        else{
            progressText.setText("");
        }
    }

    /**
     * gets the number of checked checklists
     * @return the number of checked checklits
     */
    public int getCompleted(){
        return this.completedTasks;
    }

    /**
     * gets the total number of checklists
     * @return the number of cehcked checklists
     */
    public int getTotal() {
        return this.totalTasks;
    }

    /**
     * Updates the progress text when the checklists are changed
     * and broadcasted to the rest of the clients
     * @param checklist the list of checklists
     */
    public void updateProgressText(List<CheckListItem>  checklist){
        int total = 0;
        int completed = 0;
        for(int i = 0; i<checklist.size(); i++){
            if(checklist.get(i).completed){
                completed++;
            }
            total++;
        }
        setProgressText(completed,total);
    }

    /**
     * Sets the boolean variable isOpen in card to the inputted
     * boolean so that it can be checked if the cardDetails window
     * is opened or not
     * @param b the value to set isOpen to
     */
    public void setOpen(boolean b) {
        this.isOpen = b;
    }
}
