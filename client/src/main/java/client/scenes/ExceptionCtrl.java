package client.scenes;

import com.google.inject.Inject;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class ExceptionCtrl {


    public boolean success = false;

    public String storedText;

    @FXML
    private Label title;

    /**
     * Create a new AddCardCtrl.
     *
     */
    @Inject
    public ExceptionCtrl() {
        title = new Label("");
    }


    /**
     * Close the window
     * @param event the ActionEvent
     */

    private static void closeWindow(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();    }


    /**
     * Adds the new task to the list,
     * if the user didn't input anything
     * they can't proceed.
     * @param event the ActionEvent
     */
    public void ok(ActionEvent event) {
        closeWindow(event);
    }


}