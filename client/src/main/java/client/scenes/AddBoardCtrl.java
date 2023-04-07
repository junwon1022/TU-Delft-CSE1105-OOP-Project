package client.scenes;

import com.google.inject.Inject;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class AddBoardCtrl {


    public boolean success = false;

    public String storedText;

    public String password;
    public String backgroundColor;
    public String fontColor;

    @FXML
    private TextField boardTitle;

    @FXML
    private Label nullTitle;

    @FXML
    private ColorPicker backgroundColorPicker;

    @FXML
    private ColorPicker fontColorPicker;

    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPassword;

    /**
     * Create a new AddCardCtrl.
     *
     */
    @Inject
    public AddBoardCtrl() {
        nullTitle = new Label("");
    }

    /**
     * Cancel the creation of a board.
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
     * Adds the new task to the list,
     * if the user didn't input anything
     * they can't proceed.
     * @param event the ActionEvent
     */
    public void ok(ActionEvent event) {
        storedText = boardTitle.getText();
        String confirmPass = confirmPassword.getText();
        password = passwordField.getText();
        backgroundColor = String.format("#%02x%02x%02x",
                (int)(backgroundColorPicker.getValue().getRed() * 255),
                (int)(backgroundColorPicker.getValue().getGreen() * 255),
                (int)(backgroundColorPicker.getValue().getBlue() * 255));
        fontColor = String.format("#%02x%02x%02x",
                (int)(fontColorPicker.getValue().getRed() * 255),
                (int)(fontColorPicker.getValue().getGreen() * 255),
                (int)(fontColorPicker.getValue().getBlue() * 255));
        if (storedText == null || storedText.length() == 0) {
            nullTitle.setText("Please enter a title!");
        }
        else if (password.length() > 0 && (confirmPass == null || confirmPass.length() == 0)) {
            nullTitle.setText("Please enter your password again to confirm!");
        }
        else if (!confirmPass.equals(password)) {
            nullTitle.setText("Passwords should match!");
        }
        else {
            success = true;
            clearFields();
            closeWindow(event);
        }
    }

    /**
     * Clear the fields.
     *
     */
    private void clearFields() {
        boardTitle.clear();
        passwordField.clear();
        confirmPassword.clear();
        backgroundColorPicker.setValue(Color.valueOf("#caf0f8"));
        fontColorPicker.setValue(Color.BLACK);
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
     * Adds thew new list to the board
     * if the user didn't input anything
     * they can't proceed.
     * @param event the KeyEvent
     */
    public void okKeyboard(javafx.scene.input.KeyEvent event) {
        storedText = boardTitle.getText();
        String confirmPass = confirmPassword.getText();
        password = passwordField.getText();
        backgroundColor = String.format("#%02x%02x%02x",
                (int)(backgroundColorPicker.getValue().getRed() * 255),
                (int)(backgroundColorPicker.getValue().getGreen() * 255),
                (int)(backgroundColorPicker.getValue().getBlue() * 255));
        fontColor = String.format("#%02x%02x%02x",
                (int)(fontColorPicker.getValue().getRed() * 255),
                (int)(fontColorPicker.getValue().getGreen() * 255),
                (int)(fontColorPicker.getValue().getBlue() * 255));
        password = passwordField.getText();
        if (storedText == null || storedText.length() == 0) {
            nullTitle.setText("Please enter a title!");
        }
        else if (password.length() > 0 && (confirmPass == null || confirmPass.length() == 0)) {
            nullTitle.setText("Please enter your password again to confirm!");
        }
        else if (!confirmPass.equals(password)) {
            nullTitle.setText("Passwords should match!");
        }
        else {
            success = true;
            clearFields();
            closeWindowKeyboard(event);
        }
    }


    /**
     * Close the window
     * @param event the ActionEvent
     */

    private static void closeWindowKeyboard(javafx.scene.input.KeyEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }


    /**
     * Cancel the creation of a board.
     * @param event the ActionEvent
     */
    public void cancelKeyboard(javafx.scene.input.KeyEvent event) {
        success = false;
        clearFields();
        closeWindowKeyboard(event);
    }
}