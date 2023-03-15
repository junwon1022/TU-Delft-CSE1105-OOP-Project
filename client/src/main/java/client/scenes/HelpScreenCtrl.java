package client.scenes;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class HelpScreenCtrl {

    /**
     * Create a new HelpScreenCtrl.
     */
    public HelpScreenCtrl() {
    }

    /**
     * Initialize the controller.
     */
    public void initialize() {
    }

    /**
     * Close the window.
     * @param event the ActionEvent
     */
    public void closeHelpScreen(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }


    public void close(KeyEvent event) {
        if(event.getCode().toString().equals("ESCAPE")) {
            Node source = (Node) event.getSource();
            Stage stage = (Stage) source.getScene().getWindow();
            stage.close();
        }
    }
}
