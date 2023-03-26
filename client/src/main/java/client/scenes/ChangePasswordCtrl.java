package client.scenes;

import com.google.inject.Inject;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

public class ChangePasswordCtrl {

    public boolean success;

    public String storedText;

    @FXML
    private PasswordField password;
    @FXML
    private PasswordField confirmPassword;

    @FXML
    private Label nullTitle;

    private String NO_PASSWORD;
    private String NO_CONFIRMATION;
    private String NO_MATCH;


    /**
     * Create a new AddCardCtrl.
     *
     */
    @Inject
    public ChangePasswordCtrl() {
        success = false;
        nullTitle = new Label("Note that these changes will affect all users trying to edit your board.");
        NO_PASSWORD = "Please enter a password!";
        NO_CONFIRMATION = "Please enter the password in the second field again to confirm.";
        NO_MATCH = "Passwords should match!";
    }

    /**
     * Adds the new password to the board.
     * if the user didn't input anything
     * they can't proceed.
     * if the inputted passwords don't match
     * they can't proceed
     * @param event the ActionEvent
     */
    public void ok(ActionEvent event) {
        storedText = password.getText();
        if(storedText == null || storedText.length() == 0) {
            nullTitle.setStyle("-fx-text-fill: #8f2e30");
            nullTitle.setText(NO_PASSWORD);
        }
        else if(confirmPassword.getText() == null || confirmPassword.getText().length() == 0) {
            nullTitle.setStyle("-fx-text-fill: #8f2e30");
            nullTitle.setText(NO_CONFIRMATION);
        }
        else if(!confirmPassword.getText().equals(storedText)) {
            nullTitle.setStyle("-fx-text-fill: #8f2e30");
            nullTitle.setText(NO_MATCH);
        }
        else {
            success = true;
            clearFields();
            closeWindow(event);
        }
    }


    /**
     * Cancel the addition of a password
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
     * Removes the password of the board.
     * @param event the ActionEvent
     */
    public void removePassword(ActionEvent event) {
        success = true;
        storedText = null;
        clearFields();
        closeWindow(event);
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
     * Adds the new password to the board
     * if the user didn't input anything
     * they can't proceed.
     * if the passwords don't match
     * they can't proceed
     * @param event the KeyEvent
     */
    public void okKeyboard(javafx.scene.input.KeyEvent event) {
        storedText = password.getText();
        if(storedText == null || storedText.length() == 0) {
            nullTitle.setStyle("-fx-text-fill: #8f2e30");
            nullTitle.setText(NO_PASSWORD);
        }
        else if(confirmPassword.getText() == null || confirmPassword.getText().length() == 0) {
            nullTitle.setStyle("-fx-text-fill: #8f2e30");
            nullTitle.setText(NO_CONFIRMATION);
        }
        else if(!confirmPassword.getText().equals(storedText)) {
            nullTitle.setStyle("-fx-text-fill: #8f2e30");
            nullTitle.setText(NO_MATCH);
        }
        else {
            success = true;
            clearFields();
            closeWindowKeyboard(event);
        }
    }

    /**
     * Cancel the creation of a password.
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
        confirmPassword.clear();
    }
}
