package client.scenes;

import com.google.inject.Inject;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddListOfCardsCtrl {

    public boolean success;

    public String storedText;

    @FXML
    private TextField listTitle;

    @FXML
    private Label nullTitle;

    private String MESSAGE = "Please enter a title!";


    /**
     * Create a new AddCardCtrl.
     *
     */
    @Inject
    public AddListOfCardsCtrl() {
        success = false;
        nullTitle = new Label("");
    }

    /**
     * Initialize the controller.
     * @param event the ActionEvent
     */
    public void cancelB(ActionEvent event) {
        success = false;
        clearFields();
        closeWindowB(event);
    }

    /**
     * Close the window
     * @param event the ActionEvent
     */

    private static void closeWindowB(ActionEvent event) {
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
    public void okB(ActionEvent event) {
        success = true;
        storedText = listTitle.getText();
        if(storedText == null || storedText.length() == 0){
            nullTitle.setText(MESSAGE);
        }
        else{
            clearFields();
            closeWindowB(event);
        }
    }

    // From here are the keyboard cases

    /**
     * Handle the key pressed event.
     * @param keyEvent the KeyEvent
     */
    public void handleKeyPressed(javafx.scene.input.KeyEvent keyEvent) {
        if (keyEvent.getCode().toString().equals("ENTER")) {
            ok(keyEvent);
        }
        else if(keyEvent.getCode().toString().equals("ESCAPE")){
            cancel(keyEvent);
        }
    }

    /**
     * Adds thew new list to the board
     * if the user didn't input anything
     * they can't proceed.
     * @param event the KeyEvent
     */
    public void ok(javafx.scene.input.KeyEvent event) {
        success = true;
        storedText = listTitle.getText();
        if(storedText == null || storedText.length() == 0){
            nullTitle.setText(MESSAGE);
        }
        else{
            clearFields();
            closeWindow(event);
        }
    }

    /**
     * Cancel the creation of a new list.
     * @param event the KeyEvent
     */
    private void cancel(javafx.scene.input.KeyEvent event) {
        success = false;
        clearFields();
        closeWindow(event);
    }


    /**
     * Close the window
     * @param event the KeyEvent
     */
    private static void closeWindow(javafx.scene.input.KeyEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    /**
     * Clear the fields.
     *
     */
    private void clearFields() {
        listTitle.clear();
    }
}
