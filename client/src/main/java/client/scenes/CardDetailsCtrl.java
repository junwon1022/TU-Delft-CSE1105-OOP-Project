package client.scenes;

import client.utils.ServerUtils;
import commons.Card;
import commons.CheckListItem;
import javafx.event.ActionEvent;
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
import javafx.scene.text.Font;
import javafx.stage.Stage;

import javax.inject.Inject;
import java.io.IOException;


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

    private Card card;

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
     * Initializes the CardDetailsCtrl for the checklists "Currently not implemented"
     */
    public void initialize() {

    }

    /**
     * Sets the title of the descriptionTitle label to the title of the card
     * and changes the font and style.
     * @param title the title of the card
     */
    public void setTitle(String title){
        titleLabel.setText(title);
        titleLabel.setFont(Font.font("System",17));
        titleLabel.setStyle("-fx-font-weight: bold;");
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
            }
            server.updateDescription(card, text);
            board.refresh();
        }
    }

    /**
     * Exits the detailed view of the card
     * @param event Click of the exit label
     */
    public void exitDetails(ActionEvent event){
        Stage stage = (Stage) scenePane.getScene().getWindow();
        stage.close();
    }

    /**
     * Sets the card for the controller
     * @param card the card that the detailed view is opened for
     */
    public void setCard(Card card) {
        this.card = card;
    }

    /**
     * updates the description of the card in the database
     * if the description is empty it makes it a space which is then reconverted
     * @param event the click of the "Save Description" button
     */
    public void updateDescription(MouseEvent event) {
        String text = descriptionText.getText();
        if (text.isEmpty()) {
            text = " ";
            descriptionText.setText(text);
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

        addCheckListStage.setTitle("Add a new CheckList");
        Scene addListScene = new Scene(root);
        addCheckListStage.setScene(addListScene);
        addCheckListStage.showAndWait();

        if (controller.success) {
            String title = controller.storedText;

            CheckListItem checkListItem = getCheckList(title);
             //   CheckListItem addedCheckListItem = server.addCheckListItem(list);

             //   this.refresh();
        }

    }
    /**
     * Creates a checklist with the specific title
     * @param title the title of the new checklist
     * @return the new checklist
     */
    private CheckListItem getCheckList(String title) {
        CheckListItem checkListItem = new CheckListItem();
        return  checkListItem;
    }
}
