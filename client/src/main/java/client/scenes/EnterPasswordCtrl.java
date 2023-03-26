package client.scenes;

import com.google.inject.Inject;
import commons.Board;
import commons.Card;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class EnterPasswordCtrl {

    public boolean success;

    public String enteredPassword;

    @FXML
    private TextField password;

    @FXML
    private Label nullTitle;

    private String NO_PASSWORD;
    private String INCORRECT_PASSWORD;

    private Board board;


    /**
     * Create a new AddCardCtrl.
     *
     */
    @Inject
    public EnterPasswordCtrl() {
        success = false;
        nullTitle = new Label("");
        NO_PASSWORD = "You need to enter a password to proceed!";
        INCORRECT_PASSWORD = "Incorrect password. Please try again!";
    }

    /**
     * Initialize method for the controller
     *
     * @param board - the board for which enter a password
     */
    public void initialize(Board board){
        this.board = board;
    }

    /**
     * Checks whether the entered password is correct.
     * if the user didn't input anything
     * or the password is incorrect
     * they can't proceed.
     * @param event the ActionEvent
     */
    public void ok(ActionEvent event) {
        enteredPassword = password.getText();
        if(enteredPassword == null || enteredPassword.length() == 0){
            nullTitle.setText(NO_PASSWORD);
        }
        else if(!board.password.equals(enteredPassword)) {
            nullTitle.setText(INCORRECT_PASSWORD);
        }
        else {
            success = true;
            clearFields();
            closeWindow(event);
        }
    }

    /**
     * Cancel trying to enter a password
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
     * Adds thew new password to the board
     * if the user didn't input anything
     * they can't proceed.
     * @param event the KeyEvent
     */
    public void okKeyboard(javafx.scene.input.KeyEvent event) {
        enteredPassword = password.getText();
        if(enteredPassword == null || enteredPassword.length() == 0){
            nullTitle.setText(NO_PASSWORD);
        }
        else if(!board.password.equals(enteredPassword)) {
            nullTitle.setText(INCORRECT_PASSWORD);
        }
        else {
            success = true;
            clearFields();
            closeWindowKeyboard(event);
        }
    }

    /**
     * Cancel the creation of a new list.
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
        password.clear();
    }
}
