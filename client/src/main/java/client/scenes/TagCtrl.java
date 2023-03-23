package client.scenes;

import client.utils.ServerUtils;
import commons.Tag;
import jakarta.ws.rs.WebApplicationException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;


public class TagCtrl {
    private ServerUtils server;
    private BoardCtrl board;

    @FXML
    private Button renameTag;

    @FXML
    private Button removeTag;

    @FXML
    private TextField textField;

    @FXML
    private Label nameLabel;

    private Tag tag;
//
//    @FXML
//    private VBox root;

    @FXML
    private Button rename;

    @FXML
    private TextField name;


    /**
     * Create a new CardListCtrl
     * @param server The server to use
     * @param board The board this CardList belongs to
     */
    public TagCtrl(ServerUtils server, BoardCtrl board) {
        this.server = server;
        this.board = board;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Tag.fxml"));
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Method that removes the tag from the server
     * @param event - the 'x' button being clicked
     */
    public void remove(ActionEvent event){
        try {
            //TODO in Server Utils
            //server.removeTag(tag);
            Thread.sleep(100);
        } catch (WebApplicationException e) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        board.refresh();
    }

    /**
     * Method that renames the tag;
     *
     * It shows a pop-up prompting the user to input
     * a new title for the said tag;
     *
     * @param event - the rename button being pressed
     */
    public void renameTag(ActionEvent event){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("RenameTag.fxml"));
        try {
            Parent root = fxmlLoader.load();
            RenameTagCtrl controller = fxmlLoader.getController();
            controller.initialize(tag);

            Stage stage = new Stage();
            stage.setTitle("Rename a tag");
            stage.setScene(new Scene(root, 300, 200));
            stage.showAndWait();

            if (controller.success) {
                String newName = controller.storedText;

                //method that actually renames the list in the database
                //TODO add renameTag in ServerUtils
                //server.renameTag(tag, newName);
                board.refresh();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
