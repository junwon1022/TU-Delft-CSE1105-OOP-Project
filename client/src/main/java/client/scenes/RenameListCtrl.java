package client.scenes;

import com.google.inject.Inject;
import commons.ListOfCards;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RenameListCtrl {

    @FXML
    private TextField textField;

    @FXML
    private Button rename;

    @FXML
    private Button cancel;

    @FXML
    private Label nullTitle;

    public String storedText;

    public boolean success;

    /**
     * Constructor for the Rename List Controller
     */
    @Inject
    public RenameListCtrl(){
        success = false;
        nullTitle = new Label("");
    }

    /**
     * Initialize method for the controller
     *
     * @param list - the list that needs renaming,
     *             because we need the old title of it
     */
    public void initialize(ListOfCards list){
        String oldTitle = list.title;
        textField.setText(oldTitle);
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
     * Rename method for the list, only does it locally,
     * if the textField for the title is null,
     * it doesn't let the user proceed with renaming
     * @param event - the rename button being pressed
     */
    public void rename(ActionEvent event){
        storedText = textField.getText();
        if(storedText == null || storedText.length() == 0){
            nullTitle.setText("Please enter a title");
        }
        else{
            success = true;
            closeWindow(event);
        }
    }


}
