package client.scenes;

import client.utils.ServerUtils;
import commons.Card;
import commons.ListOfCards;
import jakarta.ws.rs.WebApplicationException;
import javafx.animation.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;

public class ListOfCardsCtrl extends ListCell<ListOfCards> {
    private final ServerUtils server;
    private final BoardCtrl board;


    public ListOfCards cardData;

    public String storedText;

    @FXML
    private VBox root;

    @FXML
    private Label title;

    @FXML
    private Button addButton;

    @FXML
    public ListView<Card> list;

    @FXML
    private Button rename;

    @FXML
    private TextField name;

    @FXML
    private Button addCardButton;

    @FXML
    private Button hideCard;

    public ObservableList<Card> cards;

    int mainCount = 0;


    /**
     * Create a new ListOfCardsCtrl
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

        cards = FXCollections.observableArrayList();
        initialize();
    }

    public void initialize() {

        list.setItems(cards);
        list.setCellFactory(param -> new CardCtrl(server, board, this));
        list.getStylesheets().add("styles.css");

        setOnDragOver(this::handleDragOver);
        setOnDragDropped(this::handleDragDropped);
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
            for (ListOfCards loc : this.board.listOfCards)
                if (loc.id == dbListId)
                    draggedList = loc.cards;

            Card draggedCard = null;
            for (Card c: draggedList)
                if (c.id == dbCardId)
                    draggedCard = c;

            draggedCard.palette.cards.remove(this);
            draggedCard.palette = null;
            server.removeCard(draggedCard);

            draggedCard.list = this.cardData;
            draggedCard.order = this.cardData.cards.size();
            server.addCard2(draggedCard);

            board.refresh();
        }
        event.setDropCompleted(true);
        event.consume();
    }

    /**
     * Called whenever the parent ListView is changed. Sets the data in this controller.
     * @param item The new item for the cell.
     * @param empty whether this cell represents data from the list. If it
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

            changeColours();
            setGraphic(root);
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        }
    }

    /**
     * Method that changes the lists according to the customizations of the user
     */
    private void changeColours(){
        root.setStyle("-fx-background-color: " + cardData.board.listColour);
        title.setStyle("-fx-text-fill: " + cardData.board.listFont);
        list.setStyle("-fx-background-color: derive(" + cardData.board.listColour +", +60%)" +
                      ";-fx-control-inner-background:" + cardData.board.listColour);
    }

    /**
     * Adds a new card to the List of Cards.
     * Shows a text field that asks for the title.
     * If pressed enter, adds the card via the server and forces a board refresh.
     * @param event the KeyEvent
     */

    public void addCardEnter(KeyEvent event) {
        if(event.getCode().toString().equals("ENTER")
                && !name.getText().equals("") && name.getText() != null){
            String title = name.getText();
            Card card = getCard(title);
            server.addCard(card);

            Card addedCard = server.addCard2(card);
            card.id = addedCard.id;

            name.clear();
            name.setOpacity(0);
            addCardButton.setOpacity(0);

            board.refresh();
        }
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
                new KeyFrame(Duration.seconds(0.25), new KeyValue(list.prefHeightProperty(), 300))
        );
        Timeline timelineName = new Timeline(
                new KeyFrame(Duration.seconds(0.25), new KeyValue(name.visibleProperty(), true))
        );
        timelineList.play();
        timelineName.play();
        name.requestFocus();
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
                new KeyFrame(Duration.seconds(0.25), new KeyValue(list.prefHeightProperty(), 350))
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
                "#00B4D8",
                cardData,
                new ArrayList<>(),
                new HashSet<>(),
                null);
        card.order = cardData.cards.size();
        return card;
    }


    // From here are the keyboard cases


    /**
     * Handle the key pressed event.
     * @param keyEvent the KeyEvent
     */
    public void handleSwitchCardFront(javafx.scene.input.KeyEvent keyEvent,int count) {

        for(Card c : cardData.cards){
            count++;
            if(c.selected){
                server.selectCard(c,false);
                break;
            }
        }
        if(count >= cardData.cards.size()){
            while(count >= cardData.cards.size()) count -= cardData.cards.size();
        }
        System.out.println("Count is : " + count);
        if(count < cardData.cards.size()){
            Card thisCard = cardData.cards.get(count);
            for(ListOfCards l : board.listOfCards){
                for(Card card: l.cards){
                    if(card.selected) server.selectCard(card,false);
                }
            }
            System.out.println("This card is " + thisCard.title);
            server.selectCard(thisCard,true);
            initialize();
        }
        count = mainCount;
    }

    /**
     * Handle the key pressed event.
     * @param keyEvent the KeyEvent
     */
    public void handleSwitchCardBack(javafx.scene.input.KeyEvent keyEvent,int count) {

        for(Card c : cardData.cards){
            if(c.selected){
                server.selectCard(c,false);
                break;
            }
            count++;
        }
        if(count < 0){
             count += cardData.cards.size();
        }
        System.out.println("Count is : " + count);

        if(count < cardData.cards.size() && count > 0){
            Card thisCard = cardData.cards.get(count-1);
            System.out.println(thisCard.title);
            for(ListOfCards l : board.listOfCards){
                for(Card card: l.cards){
                    if(card.selected) server.selectCard(card,false);
                }
            }
            System.out.println("This card is " + thisCard.title);
            server.selectCard(thisCard,true);
            initialize();
        }
        count = mainCount;
    }



    /**
     * Handle the key pressed event.
     * @param keyEvent the KeyEvent
     */
    public void handleSwitchCardRight(javafx.scene.input.KeyEvent keyEvent,int count) {


    }



    /**
     * Handle the key pressed event.
     * @param keyEvent the KeyEvent
     */
    public void handleKeyPressed(javafx.scene.input.KeyEvent keyEvent) {
        if (keyEvent.getCode().toString().equals("ENTER")) {
            addCardKeyboard(keyEvent);
        }
        else if(keyEvent.getCode().toString().equals("ESCAPE")){
            cancelKeyboard(keyEvent);
        }
        else if(keyEvent.getCode().toString().equals("W")){
            handleSwitchCardBack(keyEvent,mainCount);
        }
        else if(keyEvent.getCode().toString().equals("S")){
            handleSwitchCardFront(keyEvent,mainCount);
        }
        else if(keyEvent.getCode().toString().equals("D")){

        }
        else if(keyEvent.getCode().toString().equals("A")){

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


}
