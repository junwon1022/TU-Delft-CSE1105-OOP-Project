package client.scenes;

import com.google.inject.Inject;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddListOfCardsCtrl {

    public boolean success = false;

    public String storedText;

    @FXML
    private TextField listTitle;

    @FXML
    private Label nullTitle;


    /**
     * Create a new AddCardCtrl.
     *
     */
    @Inject
    public AddListOfCardsCtrl() {
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
        success = true;
        storedText = listTitle.getText();
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
        listTitle.clear();
    }
}
