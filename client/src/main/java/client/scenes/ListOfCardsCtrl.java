package client.scenes;

import client.utils.ServerUtils;
import commons.Card;
import commons.ListOfCards;
import commons.Palette;
import jakarta.ws.rs.WebApplicationException;
import javafx.animation.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.skin.VirtualFlow;
import javafx.scene.input.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ListOfCardsCtrl extends ListCell<ListOfCards> {
    private final ServerUtils server;
    private final BoardCtrl board;

    public int selectedIndex;

    public ListOfCards cardData;

    public String storedText;

    @FXML
    private VBox root;

    @FXML
    private Label title;

    @FXML
    private Button addButton;

    @FXML
    private ListView<Card> list;

    @FXML
    private Button rename;

    @FXML
    private Button deleteList;

    @FXML
    private TextField name;

    @FXML
    private Button addCardButton;

    @FXML
    private Button hideCard;

    @FXML
    private Label addCardDisabled;
    @FXML
    private Label renameListDisabled;
    @FXML
    private Label deleteListDisabled;

    private ObservableList<Card> cards;


    /**
     * Create a new ListOfCardsCtrl
     * @param server The server to use
     * @param board The board this CardList belongs to
     */
    public ListOfCardsCtrl(ServerUtils server, BoardCtrl board) {
        this.server = server;
        this.board = board;

        selectedIndex = -1;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ListOfCards.fxml"));
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        cards = FXCollections.observableArrayList();

        list.setItems(cards);
        list.setCellFactory(param -> new CardCtrl(server, board, this));

        if (!board.isUnlocked()) {
            this.readOnly();
        } else {
            this.writeAccess();
        }
    }

    /**
     * Makes the list read-only
     */
    private void readOnly() {
        addButton.setDisable(true);
        rename.setDisable(true);
        deleteList.setDisable(true);
        addCardDisabled.setVisible(true);
        deleteListDisabled.setVisible(true);
        renameListDisabled.setVisible(true);
    }

    /**
     * Gives write access
     */
    private void writeAccess() {
        addButton.setDisable(false);
        rename.setDisable(false);
        deleteList.setDisable(false);
        addCardDisabled.setVisible(false);
        deleteListDisabled.setVisible(false);
        renameListDisabled.setVisible(false);

        setOnDragOver(this::handleDragOver);

        setOnDragDropped(this::handleDragDropped);

        list.addEventFilter(KeyEvent.KEY_PRESSED, this::handleKeyPressed);
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
     * Handles dropping drag
     * @param event drag event
     */
    private void handleDragDropped(DragEvent event) {
        Dragboard db = event.getDragboard();
        if (db.hasString() && this.cardData != null) {
            String[] strings = db.getString().split("X");
            long dbCardId = Long.decode(strings[0]);
            long dbListId = Long.decode(strings[1]);

            List<Card> draggedList = null;
            for (ListOfCards loc : this.board.listOfCards) {
                if (loc.id == dbListId) {
                    draggedList = loc.cards;
                    break;
                }
            }

            Card draggedCard = null;
            for (Card c: draggedList) {
                if (c.id == dbCardId) {
                    draggedCard = c;
                    break;
                }
            }

            Palette p = draggedCard.palette;

            server.removeCard(draggedCard);

            p = server.getPalette(p.board.id, p.id);
            draggedCard.palette = null;
            draggedCard.list = this.cardData;
            draggedCard.order = this.cardData.cards.size();
            draggedCard = server.addCard2(draggedCard);
            draggedCard = server.addPaletteToCard(draggedCard, p);
            board.refresh();
        }
        event.setDropCompleted(true);
        event.consume();
    }

    /**
     * Called whenever the parent ListView is changed. Sets the data in this controller.
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
            cards.setAll(item.cards);
            cardData = item;
            if (!board.isUnlocked()) {
                this.readOnly();
            } else {
                this.writeAccess();
            }

            changeColours();
            setGraphic(root);
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        }
    }

    /**
     * Method that changes the lists according to the customizations of the user
     */
    private void changeColours() {
        root.setStyle("-fx-background-color: " + cardData.board.listColour);
        title.setStyle("-fx-text-fill: " + cardData.board.listFont);
        list.setStyle("-fx-background-color: derive(" + cardData.board.listColour +", +60%)" +
                      ";-fx-control-inner-background:" + cardData.board.listColour);
    }

    /**
     * Adds a new card to the List of cards.
     * Shows a text field that asks for the title.
     * If pressed OK button, adds the card via the server and forces a board refresh.
     * @param event the Action event
     */
    public void addCard(ActionEvent event) {
        if(!name.getText().equals("") && name.getText() != null){
            regularStyle();
            String title = name.getText();
            Card card = getCard(title);

            Card addedCard = server.addCard2(card);
            card.id = addedCard.id;
            Palette palette = cardData.board.getDefaultPalette();
            card = server.addPaletteToCard(card, palette);
            hideButton(event);
            board.refresh();
        } else {
            errorStyle();
        }
    }

    /**
     * Shows the text field and the button to add a card
     * @param event
     */
    public void showButton(ActionEvent event) {
        addCardButton.setVisible(true);
        hideCard.setVisible(true);
        regularStyle();
        addButton.setVisible(false);
        // Shorten the list
        Timeline timelineList = new Timeline(
                new KeyFrame(Duration.seconds(0.25), new KeyValue(list.prefHeightProperty(), 288))
        );
        Timeline timelineName = new Timeline(
                new KeyFrame(Duration.seconds(0.25), new KeyValue(name.visibleProperty(), true))
        );
        timelineList.play();
        timelineName.play();
        // Requests focus for the text field
        name.visibleProperty().addListener((observable, oldValue, isVisible) -> {

            if (isVisible) {

                name.requestFocus();

            }

        });
    }

    /**
     * Cancel the renaming of a new card.
     * @param event the KeyEvent
     */
    public void hideButton(ActionEvent event) {
        name.clear();
        name.setVisible(false);
        addCardButton.setVisible(false);
        hideCard.setVisible(false);
        addButton.setVisible(true);
        // Elongate the list
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0.25), new KeyValue(list.prefHeightProperty(), 338))
        );
        timeline.play();
    }

    /**
     * Hide add card buttons with keyboard
     * @param keyEvent
     */
    public void hideButtonKeyboard(javafx.scene.input.KeyEvent keyEvent) {
        name.clear();
        name.setVisible(false);
        addCardButton.setVisible(false);
        hideCard.setVisible(false);
        addButton.setVisible(true);
        // Elongate the list
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0.2), new KeyValue(list.prefHeightProperty(), 350))
        );
        timeline.play();
    }

    private void regularStyle() {
        name.setPromptText("Enter a title . . .");
        name.setStyle("-fx-prompt-text-fill: #665A5D;");
        addCardButton.setStyle("-fx-border-color: #4FCAE2; -fx-background-color: #4FCAE2;");
        addCardButton.setOnMouseEntered(e -> addCardButton.setStyle
                ("-fx-background-color: #CAF0F8; -fx-text-fill: #00B4D8;"));
        addCardButton.setOnMouseExited(e -> addCardButton.setStyle
                ("-fx-background-color: #4FCAE2; -fx-text-fill: #E4F8FC;"));

        hideCard.setStyle("-fx-border-color: #4FCAE2; -fx-background-color: #4FCAE2;");
        hideCard.setOnMouseEntered(e -> hideCard.setStyle
                ("-fx-background-color: #CAF0F8; -fx-text-fill: #00B4D8;"));
        hideCard.setOnMouseExited(e -> hideCard.setStyle
                ("-fx-background-color: #4FCAE2; -fx-text-fill: #E4F8FC;"));
    }

    private void errorStyle() {
        name.setPromptText("You need a title.");
        name.setStyle("-fx-prompt-text-fill: #C34042;");
        addCardButton.setStyle("-fx-border-color: #C34042; -fx-background-color: #C34042;");
        addCardButton.setOnMouseEntered(e -> addCardButton.setStyle
                ("-fx-border-color: #C34042; -fx-background-color: #CAF0F8;" +
                        " -fx-text-fill: #C34042;"));
        addCardButton.setOnMouseExited(e -> addCardButton.setStyle
                ("-fx-border-color: #C34042; -fx-background-color: #C34042;" +
                        " -fx-text-fill: #E4F8FC;"));

        hideCard.setStyle("-fx-border-color: #C34042; -fx-background-color: #C34042;");
        hideCard.setOnMouseEntered(e -> hideCard.setStyle
                ("-fx-border-color: #C34042; -fx-background-color: #CAF0F8;" +
                        " -fx-text-fill: #C34042;"));
        hideCard.setOnMouseExited(e -> hideCard.setStyle
                ("-fx-border-color: #C34042; -fx-background-color: #C34042;" +
                        " -fx-text-fill: #E4F8FC;"));
    }

    /**
     * Method that removes the list from the server
     * @param event - the 'x' button being clicked
     */
    public void remove(ActionEvent event){
        try {
            server.removeList(cardData);
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
            stage.setScene(new Scene(root, 300, 200));
            stage.showAndWait();

            if (controller.success) {
                String newTitle = controller.storedText;

                //method that actually renames the list in the database
                server.renameList(cardData, newTitle);
                board.refresh();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Method that creates a new card with given title
     * @param title - title of new card
     * @return - the new card
     */
    public Card getCard(String title){
        Card card =  new Card(title,
                "",
                "",
                cardData,
                new ArrayList<>(),
                new HashSet<>(),
                null);
        card.order = cardData.cards.size();
        return card;
    }

    /**
     * Getter for the list of cards displayed
     * @return the list entity
     */
    public ListOfCards getListOfCards() {
        return cardData;
    }

    /**
     * Shows read-only message if button is disabled
     * @param event
     */
    public void showReadOnlyMessage(Event event) {
        board.showReadOnlyMessage(event);
    }

    // From here are the keyboard cases

    /**
     * Handle the key pressed event.
     * @param keyEvent the KeyEvent
     */
    public void handleKeyPressed(javafx.scene.input.KeyEvent keyEvent) {
        if (keyEvent.getCode().toString().equals("ENTER")) {
            if (!addButton.isVisible())
                addCardKeyboard(keyEvent);
            else
                openCardDetails(keyEvent);
        }
        else if(keyEvent.getCode().toString().equals("ESCAPE")){
            if (!addButton.isVisible())
                cancelKeyboard(keyEvent);
        }
        else if (keyEvent.getCode() == KeyCode.E ||
                keyEvent.getCode() == KeyCode.DELETE ||
                keyEvent.getCode() == KeyCode.BACK_SPACE ||
                (keyEvent.getCode() == KeyCode.UP && keyEvent.isShiftDown()) ||
                (keyEvent.getCode() == KeyCode.DOWN && keyEvent.isShiftDown()) ||
                keyEvent.getCode() == KeyCode.RIGHT ||
                keyEvent.getCode() == KeyCode.LEFT)  {
            handleKeyPressedHelper(keyEvent);
        }
    }

    /**
     * Helper to handle the key pressed event.
     * @param keyEvent the KeyEvent
     */
    public void handleKeyPressedHelper(javafx.scene.input.KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.E) {
            editCardKeyboard(keyEvent);
        }
        else if (keyEvent.getCode() == KeyCode.DELETE ||
                keyEvent.getCode() == KeyCode.BACK_SPACE)  {
            deleteCardKeyboard(keyEvent);
        }
        else if (keyEvent.getCode() == KeyCode.UP && keyEvent.isShiftDown()) {
            moveCardUp(keyEvent);
        }
        else if (keyEvent.getCode() == KeyCode.DOWN && keyEvent.isShiftDown()) {
            moveCardDown(keyEvent);
        }
        else if (keyEvent.getCode() == KeyCode.RIGHT || keyEvent.getCode() == KeyCode.LEFT) {
            changeHighlight(keyEvent);
        }
    }

    private void changeHighlight(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.RIGHT) {
            moveCardRight(keyEvent);
        }
        else if (keyEvent.getCode() == KeyCode.LEFT) {
            moveCardLeft(keyEvent);
        }
    }

    private void moveCardUp(KeyEvent keyEvent) {
        if (keyEvent.isConsumed())
            return;
        keyEvent.consume();
        ObservableList<Card> selectedItems = list.getSelectionModel().getSelectedItems();
        if (selectedItems.size() == 1) {
            int selectedIndex = -1;
            Card item = null;
            for (int i = 0; i < list.getItems().size(); i++) {
                item = list.getItems().get(i);
                if (item.id == selectedItems.get(0).id) {
                    selectedIndex = i;
                    break;
                }
            }

            if (selectedIndex > 0) {
                this.selectedIndex = selectedIndex - 1;
                server.moveCard(getListOfCards(), selectedIndex, selectedIndex - 1);
                list.refresh();
            }
        }
    }

    private void moveCardDown(KeyEvent keyEvent) {
        if (keyEvent.isConsumed())
            return;
        keyEvent.consume();
        ObservableList<Card> selectedItems = list.getSelectionModel().getSelectedItems();
        if (selectedItems.size() == 1) {
            int selectedIndex = -1;
            for (int i = 0; i < list.getItems().size(); i++)
                if (list.getItems().get(i).id == selectedItems.get(0).id)
                    selectedIndex = i;

            if (selectedIndex < list.getItems().size() - 1) {
                this.selectedIndex = selectedIndex + 1;
                server.moveCard(getListOfCards(), selectedIndex, selectedIndex + 1);
                list.refresh();
            }
        }
    }

    private void moveCardRight(KeyEvent keyEvent) {
        if (keyEvent.isConsumed())
            return;
        keyEvent.consume();
        ObservableList<Card> selectedItems = list.getSelectionModel().getSelectedItems();
        int selectedIndex = -1;
        for (int i = 0; i < list.getItems().size(); i++) {
            if (list.getItems().get(i).id == selectedItems.get(0).id) {
                selectedIndex = i;
            }
        }

        List<ListOfCardsCtrl> listViews = getAllListOfCardsCtrls(board);
        for (int i = 0; i < listViews.size() - 1; i++) {
            ListView l = listViews.get(i).getList();
            if (l.equals(list)) {
                l.getSelectionModel().clearSelection();
                l = listViews.get(i + 1).getList();
                l.requestFocus();
                selectAdjacent(l, selectedIndex);
            }
        }
    }

    private void moveCardLeft(KeyEvent keyEvent) {
        if (keyEvent.isConsumed())
            return;
        keyEvent.consume();
        ObservableList<Card> selectedItems = list.getSelectionModel().getSelectedItems();
        int selectedIndex = -1;
        for (int i = 0; i < list.getItems().size(); i++) {
            if (list.getItems().get(i).id == selectedItems.get(0).id) {
                selectedIndex = i;
            }
        }

        List<ListOfCardsCtrl> listViews = getAllListOfCardsCtrls(board);
        for (int i = 1; i < listViews.size(); i++) {
            ListView l = listViews.get(i).getList();
            if (l.equals(list)) {
                l.getSelectionModel().clearSelection();
                l = listViews.get(i - 1).getList();
                l.requestFocus();
                selectAdjacent(l, selectedIndex);
            }
        }
    }

    private void selectAdjacent(ListView l, int selectedIndex) {
        if(l.getItems().size() <= selectedIndex) {
            l.getSelectionModel().selectLast();
        } else {
            l.getSelectionModel().select(selectedIndex);
        }
    }

    /**
     * Gets all ListOfCardsCtrls within a board
     * @param board the current board displayed
     * @return all ListOfCardsCtrls within a board
     */
    private List<ListOfCardsCtrl> getAllListOfCardsCtrls(BoardCtrl board) {
        ObservableList<ListOfCards> listsOfCards = board.getList().getItems();
        List<ListOfCardsCtrl> listViews = new ArrayList<>();
        for (int i = 0; i < listsOfCards.size(); i++) {
            Optional<VirtualFlow> virtualFlowOptional = board.getList()
                    .getChildrenUnmodifiable()
                    .stream()
                    .filter(node -> node instanceof VirtualFlow)
                    .map(n -> (VirtualFlow) n)
                    .findFirst();
            VirtualFlow<ListCell<?>> virtualFlow = virtualFlowOptional.get();
            listViews.add((ListOfCardsCtrl) virtualFlow.getCell(i));
        }
        return listViews;
    }

    private void openCardDetails(KeyEvent event) {
        ObservableList<Card> cards = list.getSelectionModel().getSelectedItems();
        if (cards.size() == 1) {
            Card card = list.getItems().filtered(c -> c.id == cards.get(0).id).get(0);
            if (card.isOpen == 0) {
                card.isOpen = 1;
                list.refresh();
            }
        }
        event.consume();
    }

    /**
     * Method for delete Card with shortcuts "Delete" and "Backspace"
     * @param event the KeyEvent
     */
    private void deleteCardKeyboard(javafx.scene.input.KeyEvent event) {
        ObservableList<Card> cards = list.getSelectionModel().getSelectedItems();
        if (cards.size() == 1) {
            Card removedCard = cards.get(0);
            removeCard(removedCard);
        }
        event.consume();
    }

    /**
     * Method for edit Card with shortcut "E"
     * @param event the KeyEvent
     */
    private void editCardKeyboard(javafx.scene.input.KeyEvent event) {
        if(event.isConsumed())
            return;
        event.consume();
        ObservableList<Card> cards = list.getSelectionModel().getSelectedItems();
        if (cards.size() == 1) {
            Card editedCard = cards.get(0);
            renameCard(editedCard);
        }
    }

    /**
     * Renames the new card
     * if the user didn't input anything
     * they can't proceed.
     * @param event the KeyEvent
     */
    public void addCardKeyboard(javafx.scene.input.KeyEvent event) {
        if(!name.getText().equals("") && name.getText() != null){
            regularStyle();
            String title = name.getText();
            Card card = getCard(title);

            Card addedCard = server.addCard2(card);
            card.id = addedCard.id;
            Palette palette = cardData.board.getDefaultPalette();
            card = server.addPaletteToCard(card, palette);

            hideButtonKeyboard(event);

            board.refresh();
        } else {
            errorStyle();
        }
    }

    /**
     * Cancel the renaming of a new card.
     * @param event the KeyEvent
     */
    private void cancelKeyboard(javafx.scene.input.KeyEvent event) {
        hideButtonKeyboard(event);
    }

    /**
     * Method that renames the card
     * @param card the card
     */
    public void renameCard(Card card){
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
     * Method that removes the card
     * @param card the card
     */
    public void removeCard(Card card) {
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

    /** Gets the list view
     *
     * @return the list view
     */
    public ListView<Card> getList() {
        return list;
    }
}
