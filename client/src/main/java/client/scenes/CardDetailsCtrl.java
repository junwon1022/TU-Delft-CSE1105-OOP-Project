package client.scenes;

import client.utils.ServerUtils;
import commons.Card;
import commons.CheckListItem;
import commons.Palette;
import commons.Tag;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.inject.Inject;
import java.util.List;
import java.util.Set;


public class CardDetailsCtrl{

    private final ServerUtils server;
    private final CardCtrl cardCtrl;

    private final BoardCtrl board;

    @FXML
    private Label titleLabel;

    @FXML
    private TextArea descriptionText;

    @FXML
    private Label copied;

    @FXML
    private GridPane scenePane;

    @FXML
    private Button saveDescription;

    @FXML
    private Button addChecklist;

    @FXML
    private ListView<CheckListItem> checklistView;

    @FXML
    private Label paletteTitle;

    @FXML
    private Rectangle background;

    @FXML
    private Rectangle font;

    @FXML
    private ListView<Palette> palettes;

    @FXML
    private ListView<Tag> chooseTag;

    @FXML
    private ListView<Tag> tagsView;

    @FXML
    private Button addSubtask;

    @FXML
    private AnchorPane disabled;
    @FXML
    private HBox readOnlyMessage;

    @FXML
    private HBox subtaskAddition;

    @FXML
    private TextField subtaskTitle;

    @FXML
    private Label nullTitle;

    @FXML
    private Button hide;


    ObservableList<Palette> paletteData;

    ObservableList<Tag> tagsData;

    ObservableList<Tag> showableTags;

    private Card card;

    ObservableList<CheckListItem> data;

    /**
     * Creates a new CardDetailsCtrl
     * @param server server utils connection
     * @param board board that owns the card
     * @param cardCtrl the card controller of this card
     */
    @Inject
    public CardDetailsCtrl(ServerUtils server, BoardCtrl board, CardCtrl cardCtrl) {
        this.server = server;
        this.board = board;
        this.cardCtrl = cardCtrl;
    }

    /**
     * Initializes the CardDetailsCtrl for the checklists
     */
    public void initialize() {
        data = FXCollections.observableArrayList();
        chooseTag.setStyle("-fx-background-color: #A2E4F1; " +
                "-fx-background-radius: 10");
        checklistView.setItems(data);
        checklistView.setCellFactory(clv -> new CheckListItemCtrl(server, this, board));
        tagsData = FXCollections.observableArrayList();
        tagsView.setItems(tagsData);
        tagsView.setCellFactory(tlv -> new TagCtrl(server, board, this, true));
        showableTags = FXCollections.observableArrayList();
        chooseTag.setStyle("-fx-background-color: #A2E4F1; " +
                "-fx-background-radius: 10");
        chooseTag.setItems(showableTags);
        chooseTag.setCellFactory(ctv -> new TagCtrl(server, board, this, false));

        subtaskAddition.setVisible(false);

        nullTitle.setVisible(false);
        hide.setVisible(false);
        scenePane.setOnKeyPressed(this::escapeFromWindow);
        palettes.setOnKeyPressed(this::escapeFromWindow);
        chooseTag.setOnKeyPressed(this::escapeFromWindow);
        checklistView.setOnKeyPressed(this::escapeFromWindow);
        tagsView.setOnKeyPressed(this::escapeFromWindow);

        loadPalettes();
        loadTags();
        if(!board.isUnlocked()) {
            disabled.setVisible(true);
        } else {
            disabled.setVisible(false);
        }
    }

    /**
     * Checks if the key is ESCAPE, then close the details window
     * @param keyEvent event
     */
    protected void escapeFromWindow(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ESCAPE) {
            exitDetails(keyEvent);
            keyEvent.consume();
        }
    }


    private void selectPalettes(){
        paletteData = FXCollections.observableArrayList();
        Set<Palette> allPalettes = card.list.board.palettes;
        for(Palette p: allPalettes){
            if(!p.equals(card.palette))
                paletteData.add(p);
        }
        palettes.setStyle("-fx-background-color: #A2E4F1; " +
                "-fx-background-radius: 10");
        palettes.setItems(paletteData);
        palettes.setCellFactory(pal-> new PresetCtrl(server, this, board, card));
    }


    /**
     * Sets the title of the descriptionTitle label to the title of the card
     * and changes the font and style.
     * @param title the title of the card
     */
    public void setTitle(String title){
        titleLabel.setText(title);
    }

    /**
     * Sets the text of the descriptionText label to the description of the card
     * @param description the description of the card
     */
    public void setDescriptionText(String description) {
        descriptionText.setText(description);
    }

    /**
     * Shortcut to save the description of the card
     * @param event the keys that are pressed
     */

    public void handleSaveShortcut(KeyEvent event) {
        if (event.isControlDown() && event.getCode().toString().equals("S") ||
               event.isMetaDown() && event.getCode().toString().equals("S")) {
            event.consume();
            String text = descriptionText.getText();
            if (text.isEmpty()) {
                text = " ";
                descriptionText.setText(text);
                text = descriptionText.getText();
            }
            server.updateDescription(card, text);
            board.refresh();
        }
    }

    /**
     * Exits the detailed view of the card
     * @param event Click of the exit label
     */
    public void exitDetails(Event event){
        Stage stage = (Stage) scenePane.getScene().getWindow();
        cardCtrl.setOpen(0);
        stage.close();
    }

    /**
     * Sets the card for the controller
     * @param card the card that the detailed view is opened for
     */
    public void setCard(Card card) {
        this.card = card;
        selectPalettes();
    }

    /**
     * updates the description of the card in the database
     * if the description is empty it makes it a space which is then reconverted
     * also updates the visibility of the description icon
     * @param event the click of the "Save Description" button
     */
    public void updateDescription(MouseEvent event) {
        String text = descriptionText.getText();
        if (text.isEmpty()) {
            text = " ";
            descriptionText.setText(text);
            text = descriptionText.getText();
        }
        copied.setVisible(true);
        PauseTransition delay = new PauseTransition(Duration.seconds(4));
        delay.setOnFinished(e -> copied.setVisible(false));
        delay.play();
        server.updateDescription(card, text);
        board.refresh();
    }

    /**
     * Adds a checklist "to be implemented"
     * @param event adding the checklist event
     */
    public void showAddChecklist(ActionEvent event) {
        subtaskAddition.setVisible(true);
        addChecklist.setVisible(false);
        hide.setVisible(true);
    }

    /**
     * Method that adds the checklist
     * @param event
     */
    public void addChecklist(ActionEvent event){
        String title = subtaskTitle.getText();
        if(title.length() == 0){
            nullTitle.setVisible(true);
        }
        else{
            nullTitle.setVisible(false);
            CheckListItem checkListItem = createCheckList(title);
            CheckListItem addedCheckListItem = server.addChecklist(checkListItem);
            checkListItem.id = addedCheckListItem.id;
            data.add(addedCheckListItem);
            int completed = cardCtrl.getCompleted();
            int total = cardCtrl.getTotal();
            subtaskAddition.setVisible(false);
            addChecklist.setVisible(true);
            subtaskTitle.setText("");
            cardCtrl.setProgressText(completed,total+1);
            board.refresh();
        }
    }

    /**
     * removes the checklistItem from data
     * @param checkListItem checklist to be removed
     */
    public void removeChecklist(CheckListItem checkListItem) {
        data.remove(checkListItem);
        int completed = cardCtrl.getCompleted();
        int total = cardCtrl.getTotal();
        if(checkListItem.completed){
            cardCtrl.setProgressText(completed-1,total-1);
        }
        else{
            cardCtrl.setProgressText(completed,total-1);
        }
    }

    /**
     * Creates a checklist with the specific description
     * @param description the title of the new checklist
     * @return the new checklist
     */
    private CheckListItem createCheckList(String description) {
        CheckListItem checkListItem = new CheckListItem(
                description,
                false,
                card);
        checkListItem.order = data.size();
        return checkListItem;
    }

    /**
     * adds a tag to the detailed view of the card if it isnt added yet
     * @param tag the tag to add
     */
    public void addTag(Tag tag) {
        if(!tagsData.contains(tag)){
            tagsData.add(tag);
            card.addTag(tag);
            server.addTagToCard(tag, card);
            showableTags.remove(tag);
        }
    }

    /**
     * when the carddetails are opened, sets the data arraylist
     * to contain the checklists of the card.
     * @param checklist
     */
    public void setChecklists(List<CheckListItem> checklist) {
        int total = 0;
        int completed = 0;
        for(int i = 0; i<checklist.size(); i++){
            data.add(checklist.get(i));
            if(checklist.get(i).completed){
                completed++;
            }
            total++;
        }
        cardCtrl.setProgressText(completed,total);
    }

    /**
     * Gets the card controller for this card details
     * @return the card controller
     */
    public CardCtrl getCardCtrl() {
        return this.cardCtrl;
    }

    /**
     * Changes the renamed description in the observable array
     * so that it updates at the moment
     * @param addedChecklist the checklist with the new name
     */
    public void changeChecklistDescription(CheckListItem addedChecklist) {
        for(int i = 0; i<data.size(); i++){
            if(data.get(i).id == (addedChecklist.id)){
                data.get(i).text = addedChecklist.text;
            }
        }
    }

    /**
     * returns the reference to the list of checklists
     * of this card
     * @return the reference of the list
     */
    public ObservableList<CheckListItem> getCheckListArray() {
        return this.data;
    }

    /**
     * swaps the checklists on the two provided indexes
     * @param oldIdx index of the dragged checklist
     * @param newIdx index of the dragged onto checklist
     */
    public void swapChecklists(int oldIdx, int newIdx){
        server.updateChecklists(card, oldIdx, newIdx);
        CheckListItem temp = data.get(oldIdx);
        data.set(oldIdx, data.get(newIdx));
        data.set(newIdx, temp);

        board.refresh();
    }

    /**
     * Method that sets the Preset for a card
     */
    public void setPreset(){
        background.setFill(Color.web(card.palette.background));
        font.setFill(Color.web(card.palette.font));
        paletteTitle.setText(card.palette.title);
    }


    /**
     * Method that refreshes the card details overview
     */
    public void refresh(){
        selectPalettes();
        setPreset();
    }

    /**
     * Sets the CardTags ListView when the details are opened
     */
    public void setCardTags(){
        showableTags.addAll(board.tags);
        for (Tag tag : card.tags) {
            tagsData.add(tag);
            showableTags.remove(tag);
        }
    }

    /**
     * removes the tag from the card
     * @param tag the tag to remove
     */
    public void removeTagFromCard(Tag tag) {
        tagsData.remove(tag);
        showableTags.add(tag);
        card.removeTag(tag);
        server.removeTagFromCard(tag, card);
        card = server.getCard(card);
    }

    /**
     * Loads the list to auto-fit its parent
     */
    private void loadPalettes() {
        // set the palettes to always grow to fill the AnchorPane
        palettes.setPrefHeight(Region.USE_COMPUTED_SIZE);
        palettes.setPrefWidth(Region.USE_COMPUTED_SIZE);
        palettes.setMaxHeight(Double.MAX_VALUE);
        palettes.setMaxWidth(Double.MAX_VALUE);

        // set the constraints for the palettes to fill the AnchorPane
        AnchorPane.setTopAnchor(palettes, 0.0);
        AnchorPane.setBottomAnchor(palettes, 0.0);
        AnchorPane.setLeftAnchor(palettes, 0.0);
        AnchorPane.setRightAnchor(palettes, 0.0);
    }

    /**
     * Loads the list to auto-fit its parent
     */
    private void loadTags() {
        // set the tagList to always grow to fill the AnchorPane
        chooseTag.setPrefHeight(Region.USE_COMPUTED_SIZE);
        chooseTag.setPrefWidth(Region.USE_COMPUTED_SIZE);
        chooseTag.setMaxHeight(Double.MAX_VALUE);
        chooseTag.setMaxWidth(Double.MAX_VALUE);

        // set the constraints for the tagList to fill the AnchorPane
        AnchorPane.setTopAnchor(chooseTag, 0.0);
        AnchorPane.setBottomAnchor(chooseTag, 0.0);
        AnchorPane.setLeftAnchor(chooseTag, 0.0);
        AnchorPane.setRightAnchor(chooseTag, 0.0);
    }

    /**
     * Cancel the renaming of a new card.
     * @param event the KeyEvent
     */
    public void hideButton(ActionEvent event) {
        subtaskAddition.setVisible(false);
        addChecklist.setVisible(true);
        hide.setVisible(false);
        nullTitle.setVisible(false);
    }

    /**
     * Closes the read only message
     * @param event
     */
    public void closeReadOnlyView(ActionEvent event) {
        FadeTransition fadeOutMessage = new FadeTransition(Duration.seconds(0.5), readOnlyMessage);
        fadeOutMessage.setFromValue(0.9);
        fadeOutMessage.setToValue(0.0);

        fadeOutMessage.setOnFinished(e -> {
            // Hide the message when the transition is finished
            readOnlyMessage.setVisible(false);
        });
        fadeOutMessage.play();
    }

    /**
     * Shows read-only message if button is disabled
     * @param event
     */
    public void showReadOnlyMessage(Event event) {
        readOnlyMessage.setVisible(true);
        FadeTransition fadeInTransition = new FadeTransition(
                Duration.seconds(0.3), readOnlyMessage);
        fadeInTransition.setFromValue(0.0);
        fadeInTransition.setToValue(0.9);
        fadeInTransition.play();
        Alert alert = new Alert(Alert.AlertType.WARNING,
                "Cannot edit in read-only mode. \n" +
                        "To gain write access, click on the lock icon and enter the password.",
                ButtonType.OK);// Load your custom icon image

        // Set the graphic of the alert dialog to custom image
        alert.setGraphic(new ImageView(new Image("warning-icon.png")));
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add("styles.css");
        dialogPane.getStyleClass().add("alert");
        dialogPane.lookupButton(ButtonType.OK).getStyleClass().add("normal-button");
        alert.showAndWait();
    }
}
