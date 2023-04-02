package client.scenes;

import com.google.inject.Inject;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddCheckListItemCtrl {

    public boolean success = false;

    public String storedText;

    @FXML
    private TextField checklistItemText;

    @FXML
    private Label nullText;

    @FXML
    private Button addChecklist;

    @FXML
    private Button cancelChecklist2;

    /**
     * Create a new AddChecklistItemCtrl.
     */
    @Inject
    public AddCheckListItemCtrl() {
        nullText = new Label("");
    }

    /**
     * cancel the checklist addition and exit window
     * @param event the ActionEvent
     */
    public void cancel(ActionEvent event) {
        success = false;
        clearFields();
        closeWindow(event);
    }

    /**
     * Close the window
     *
     * @param event the ActionEvent
     */

    private static void closeWindow(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    /**
     * Adds the new task to the list,
     * if the user didn't input anything
     * they can't proceed.
     *
     * @param event the ActionEvent
     */
    public void ok(ActionEvent event) {
        storedText = checklistItemText.getText();
        if (storedText == null || storedText.length() == 0) {
            nullText.setText("Please enter some Description!");
        } else {
            success = true;
            clearFields();
            closeWindow(event);
        }
    }

    /**
     * Clear the fields.
     */
    private void clearFields() {
        checklistItemText.clear();
    }

}
