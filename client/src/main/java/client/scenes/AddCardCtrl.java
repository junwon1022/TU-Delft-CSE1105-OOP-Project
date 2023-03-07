package client.scenes;

import com.google.inject.Inject;

import client.utils.ServerUtils;
import commons.Person;
import commons.Quote;
import jakarta.ws.rs.WebApplicationException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
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
     *
     */
    public void cancel(ActionEvent event) {
        success = false;
        clearFields();
        closeWindow(event);
    }

    private static void closeWindow(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    /**
     * Clear the fields.
     *
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