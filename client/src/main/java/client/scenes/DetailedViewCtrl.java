package client.scenes;

import com.google.inject.Inject;
import commons.Card;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class DetailedViewCtrl {
    @FXML
    private TextField titleField;

    @FXML
    private Button renameTitle;



    @FXML
    private Label nullTitle;

    public String storedText;

    public boolean success;

    /**
     * Constructor for the Detailed View Controller
     */
    @Inject
    public DetailedViewCtrl(){
        success = false;
        nullTitle = new Label("");
    }

    /**
     * Initialize method for the controller
     *
     * @param card - the card for which we open the detailed view
     */
    public void initialize(Card card){
        String oldTitle = card.title;
        titleField.setText(oldTitle);
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
        storedText = titleField.getText();
        if(storedText == null || storedText.length() == 0){
            nullTitle.setText("Please enter a title");
        }
        else{
            success = true;
            closeWindow(event);
        }
    }
}
