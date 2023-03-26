package client.scenes;

import com.google.inject.Inject;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddTagCtrl {

    public boolean success;

    public String storedText;

    @FXML
    private TextField tagName;

    @FXML
    private Label nullTitle;

    private String MESSAGE = "Please enter a name for the tag!";


    /**
     * Create a new AddTagCtrl.
     *
     */
    @Inject
    public AddTagCtrl() {
        success = false;
        nullTitle = new Label("");
    }

    /**
     * Initialize the controller.
     * @param event the ActionEvent
     */
    public void cancel(ActionEvent event) {
        success = false;
        clearFields();
        closeWindow(event);
    }

    /**
     * Close the window
     * @param event the ActionEvent
     */

    private static void closeWindow(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    /**
     * Adds the new list to the board.
     * if the user didn't input anything
     * they can't proceed.
     * @param event the ActionEvent
     */
    public void ok(ActionEvent event) {
        storedText = tagName.getText();
        if(storedText == null || storedText.length() == 0){
            nullTitle.setText(MESSAGE);
        }
        else {
            success = true;
            clearFields();
            closeWindow(event);
        }
    }

    // From here are the keyboard cases

    /**
     * Handle the key pressed event.
     * @param keyEvent the KeyEvent
     */
    public void handleKeyPressed(javafx.scene.input.KeyEvent keyEvent) {
        if (keyEvent.getCode().toString().equals("ENTER")) {
            okKeyboard(keyEvent);
        }
        else if(keyEvent.getCode().toString().equals("ESCAPE")){
            cancelKeyboard(keyEvent);
        }
    }

    /**
     * Adds the new tag to the board
     * if the user didn't input anything
     * they can't proceed.
     * @param event the KeyEvent
     */
    public void okKeyboard(javafx.scene.input.KeyEvent event) {
        storedText = tagName.getText();
        if(storedText == null || storedText.length() == 0){
            nullTitle.setText(MESSAGE);
        }
        else {
            success = true;
            clearFields();
            closeWindowKeyboard(event);
        }
    }

    /**
     * Cancel the creation of a new tag.
     * @param event the KeyEvent
     */
    private void cancelKeyboard(javafx.scene.input.KeyEvent event) {
        success = false;
        clearFields();
        closeWindowKeyboard(event);
    }


    /**
     * Close the window
     * @param event the KeyEvent
     */
    private static void closeWindowKeyboard(javafx.scene.input.KeyEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    /**
     * Clear the fields.
     *
     */
    private void clearFields() {
        tagName.clear();
    }
}
