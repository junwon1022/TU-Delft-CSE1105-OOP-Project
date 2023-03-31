package client.scenes;

import com.google.inject.Inject;
import commons.Board;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AdminRenameCtrl {
    @FXML
    private TextField titleField;

    @FXML
    private Button renameTitle;

    @FXML
    private Label nullTitle;

    public String storedText;

    public boolean success;

    private String MESSAGE = "Please enter a title!";

    /**
     * Constructor for the Rename Card Controller
     */
    @Inject
    public AdminRenameCtrl(){
        success = false;
        nullTitle = new Label("");
    }

    /**
     * Initialize method for the controller
     *
     * @param board - the board for which we open the detailed view
     */
    public void initialize(Board board){
        String oldTitle = board.title;
        titleField.setText(oldTitle);
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
     * Rename method for the card,
     * if the textField for the title is null,
     * it doesn't let the user proceed with renaming
     * @param event - the rename button being pressed
     */
    public void rename(ActionEvent event){
        storedText = titleField.getText();
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
     * Renames the new card
     * if the user didn't input anything
     * they can't proceed.
     * @param event the KeyEvent
     */
    public void renameKeyboard(javafx.scene.input.KeyEvent event) {
        success = true;
        storedText = titleField.getText();
        if(storedText == null || storedText.length() == 0){
            nullTitle.setText(MESSAGE);
        }
        else {
            success = true;
            closeWindowKeyboard(event);
        }
    }

    /**
     * Cancel the renaming of a new card.
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