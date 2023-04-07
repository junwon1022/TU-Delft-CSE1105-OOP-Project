package client.scenes;

import com.google.inject.Inject;
import commons.CheckListItem;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RenameCheckListItemCtrl {

    @FXML
    private TextField text;

    @FXML
    private Button renameChecklist;

    @FXML
    private Button cancelChecklist;

    @FXML
    private Label nullText;

    public String storedText;

    public boolean success;

    private String MESSAGE = "Please enter a Description!";

    /**
     * Constructor with parameters for RenameCheckListItem Controller
     */
    @Inject
    public RenameCheckListItemCtrl() {
        success = false;
        nullText = new Label("");
    }

    /**
     * Initialize method for the controller
     *
     * @param checkListItem - the checklistItem for which we open the detailed view
     */
    public void initialize(CheckListItem checkListItem) {
        String oldText = checkListItem.text;
        text.setText(oldText);
    }

    /**
     * Method for the cancel button
     *
     * @param event - the cancel button being pressed
     */
    public void cancel(ActionEvent event) {
        success = false;
        closeWindow(event);
    }

    /**
     * Method that closes the current window
     *
     * @param event - button pressed
     */
    private void closeWindow(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    /**
     * Rename method for the checkListItem,
     * if the text for the text inputted is null,
     * it doesn't let the user proceed with the renaming
     *
     * @param event - the rename button being pressed
     */
    public void rename(ActionEvent event) {
        storedText = text.getText();
        if (storedText == null || storedText.length() == 0) {
            nullText.setText(MESSAGE);
        } else {
            success = true;
            closeWindow(event);
        }
    }

    /**
     * Handle the key pressed event.
     *
     * @param keyEvent the KeyEvent
     */
    public void handleKeyPressed(javafx.scene.input.KeyEvent keyEvent) {
        if (keyEvent.getCode().toString().equals("ENTER")) {
            renameKeyboard(keyEvent);
        } else if (keyEvent.getCode().toString().equals("ESCAPE")) {
            cancelKeyboard(keyEvent);
        }
    }

    /**
     * Renames the checklist
     * if the user didn't input anything
     * they can't proceed.
     *
     * @param event the KeyEvent
     */
    public void renameKeyboard(javafx.scene.input.KeyEvent event) {
        success = true;
        storedText = text.getText();
        if (storedText == null || storedText.length() == 0) {
            nullText.setText(MESSAGE);
        } else {
            success = true;
            closeWindowKeyboard(event);
        }
    }

    /**
     * Cancel the renaming of a checkList
     *
     * @param event the KeyEvent
     */
    private void cancelKeyboard(javafx.scene.input.KeyEvent event) {
        success = false;
        closeWindowKeyboard(event);
    }

    /**
     * Close the window
     *
     * @param event the KeyEvent
     */
    private static void closeWindowKeyboard(javafx.scene.input.KeyEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

}
