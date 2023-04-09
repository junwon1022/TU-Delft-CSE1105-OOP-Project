package client.scenes;

import client.utils.ServerUtils;
import commons.Card;
import commons.CheckListItem;
import commons.Palette;
import commons.Tag;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import javax.inject.Inject;
import java.io.IOException;
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
    private AnchorPane scenePane;

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


        scenePane.setOnKeyPressed(this::exitDetails);

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
     * Shortcut to save the dscription of the card
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
        server.updateDescription(card, text);
        board.refresh();
    }

    /**
     * Adds a checklist "to be implemented"
     * @param event adding the checklist event
     */
    public void addChecklist(ActionEvent event) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("AddCheckListItem.fxml"));
        Stage addCheckListStage = new Stage();
        Parent root;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        AddCheckListItemCtrl controller = fxmlLoader.getController();

        addCheckListStage.setTitle("Add a new sub-task");
        Scene addListScene = new Scene(root);
        addCheckListStage.setScene(addListScene);
        addCheckListStage.showAndWait();

        if (controller.success) {
            String title = controller.storedText;

            CheckListItem checkListItem = createCheckList(title);
            CheckListItem addedCheckListItem = server.addChecklist(checkListItem);
            checkListItem.id = addedCheckListItem.id;
            data.add(addedCheckListItem);
            int completed = cardCtrl.getCompleted();
            int total = cardCtrl.getTotal();
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
    }
}
