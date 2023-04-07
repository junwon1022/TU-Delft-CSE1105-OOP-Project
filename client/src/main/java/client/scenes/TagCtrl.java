package client.scenes;

import client.utils.ServerUtils;
import commons.Tag;
import jakarta.ws.rs.WebApplicationException;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;


public class TagCtrl extends ListCell<Tag> {
    private ServerUtils server;
    private BoardCtrl board;

    @FXML
    private Button renameTag;

    @FXML
    private Button removeTag;

    @FXML
    private Label nameLabel;

    private Tag tag;

    @FXML
    private HBox root;

    @FXML
    private Label renameTagDisabled;

    @FXML
    private Label deleteTagDisabled;

    private TextField name;


    /**
     * Create a new TagCtrl
     * @param server The server to use
     * @param board The board this TagCtrl belongs to
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

        if(!board.isUnlocked()) { //TODO and user doesn't have it stored
            this.readOnly();
        } else {
            this.writeAccess();
        }
    }

    /**
     * Called whenever the parent ListView is changed. Sets the data in this controller.
     * @param item The new item for the cell.
     * @param empty whether this cell represents data from the list. If it
     *        is empty, then it does not represent any domain data, but is a cell
     *        being used to render an "empty" row.
     */
    @Override
    protected void updateItem(Tag item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setText(null);
            setContentDisplay(ContentDisplay.TEXT_ONLY);
        } else {
            nameLabel.setText(item.name);
            tag = item;
            root.setStyle("-fx-background-color: " + tag.colour + ";");
            nameLabel.setStyle("-fx-text-fill: " + tag.font + ";");
            setGraphic(root);
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        }
    }

    /**
     * Makes the tag readOnly
     */
    private void readOnly() {
        renameTag.setDisable(true);
        removeTag.setDisable(true);
        renameTagDisabled.setVisible(true);
        deleteTagDisabled.setVisible(true);
    }


    /**
     * Shows read-only message if button is disabled
     * @param event
     */
    public void showReadOnlyMessage(Event event) {
        board.showReadOnlyMessage(event);
    }


    /**
     * Gives write access
     */
    private void writeAccess() {
        renameTag.setDisable(false);
        removeTag.setDisable(false);
        renameTagDisabled.setVisible(false);
        deleteTagDisabled.setVisible(false);
    }

    /**
     * Method that removes the tag from the server
     * @param event - the 'x' button being clicked
     */
    public void remove(ActionEvent event){
        try {
            server.removeTag(tag);
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
            stage.setTitle("Edit Tag");
            stage.setScene(new Scene(root, 500, 270));
            stage.showAndWait();

            if (controller.success) {
                String newName = controller.storedText;
                String backgroundColor = controller.backgroundColor;
                String fontColor = controller.fontColor;
                Tag newTag = new Tag(newName, backgroundColor, fontColor, tag.board, tag.cards);
                // method that actually edits the tag in the database
                server.updateTag(tag, newTag);
                board.refresh();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
