package client.scenes;

import com.google.inject.Inject;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class AddCardCtrl {


    public boolean success = false;

    public String storedText;

    @FXML
    private TextField cardTitle;

    @FXML
    private Label nullTitle;

    /**
     * Create a new AddCardCtrl.
     *
     */
    @Inject
    public AddCardCtrl() {
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
     * Adds the new task to the list,
     * if the user didn't input anything
     * they can't proceed.
     * @param event the ActionEvent
     */
    public void ok(ActionEvent event) {
        success = true;
        storedText = cardTitle.getText();
        if(storedText == null || storedText.length() == 0){
            nullTitle.setText("Please enter a title");
        }
        else{
            clearFields();
            closeWindow(event);
        }
    }

    /**
     * Clear the fields.
     *
     */
    private void clearFields() {
        cardTitle.clear();
    }

}