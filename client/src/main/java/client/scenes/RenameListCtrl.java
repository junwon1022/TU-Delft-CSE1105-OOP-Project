package client.scenes;

import com.google.inject.Inject;
import commons.ListOfCards;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RenameListCtrl {

    @FXML
    private TextField textField;

    @FXML
    private Button rename;

    @FXML
    private Button cancel;

    @FXML
    private Label nullTitle;

    public String storedText;

    public boolean success;

    private String MESSAGE = "Please enter a title!";

    /**
     * Constructor for the Rename List Controller
     */
    @Inject
    public RenameListCtrl(){
        success = false;
        nullTitle = new Label("");
    }

    /**
     * Initialize method for the controller
     *
     * @param list - the list that needs renaming,
     *             because we need the old title of it
     */
    public void initialize(ListOfCards list){
        String oldTitle = list.title;
        textField.setText(oldTitle);
    }

    /**
     * Method for the cancel button
     * @param event - the cancel button being pressed
     */
    public void cancel(ActionEvent event){
        success = false;
        closeWindow(event);
    }

    /**
     * Method that closes the current window
     * @param event - button pressed
     */
    private void closeWindow(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    /**
     * Rename method for the list, only does it locally,
     * if the textField for the title is null,
     * it doesn't let the user proceed with renaming
     * @param event - the rename button being pressed
     */
    public void rename(ActionEvent event){
        storedText = textField.getText();
        if(storedText == null || storedText.length() == 0){
            nullTitle.setText(MESSAGE);
        }
        else{
            success = true;
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
            renameKeyboard(keyEvent);
        }
        else if(keyEvent.getCode().toString().equals("ESCAPE")){
            cancelKeyboard(keyEvent);
        }
    }

    /**
     * Renames the new list
     * if the user didn't input anything
     * they can't proceed.
     * @param event the KeyEvent
     */
    public void renameKeyboard(javafx.scene.input.KeyEvent event) {
        storedText = textField.getText();
        if(storedText == null || storedText.length() == 0){
            nullTitle.setText(MESSAGE);
        }
        else {
            success = true;
            closeWindowKeyboard(event);
        }
    }

    /**
     * Cancel the renaming of a new list.
     * @param event the KeyEvent
     */
    private void cancelKeyboard(javafx.scene.input.KeyEvent event) {
        success = false;
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
}
