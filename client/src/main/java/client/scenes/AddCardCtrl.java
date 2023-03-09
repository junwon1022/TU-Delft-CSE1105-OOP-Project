package client.scenes;

import com.google.inject.Inject;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddCardCtrl {


    public boolean success = false;

    public String storedText;

    @FXML
    private TextField cardTitle;


    /**
     * Create a new AddCardCtrl.
     *
     */
    @Inject
    public AddCardCtrl() {

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
     * Clear the fields.
     * @param event the ActionEvent
     */
    public void ok(ActionEvent event) {
        success = true;
        storedText = cardTitle.getText();
        clearFields();
        closeWindow(event);
    }

    /**
     * Clear the fields.
     *
     */
    private void clearFields() {
        cardTitle.clear();
    }

}