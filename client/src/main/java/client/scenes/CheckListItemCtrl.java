package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.CheckListItem;
import jakarta.ws.rs.WebApplicationException;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class CheckListItemCtrl extends ListCell<CheckListItem> {

    private final ServerUtils server;

    private final CardDetailsCtrl parent;

    private final BoardCtrl board;

    private CheckListItem data;

    @FXML
    private AnchorPane root;

    @FXML
    private Label checklistDescription;

    @FXML
    private CheckBox checkBox;

    @FXML
    private Button removeChecklist;


    @Inject
    CheckListItemCtrl(ServerUtils server, CardDetailsCtrl parent, BoardCtrl board) {
        this.server = server;
        this.parent = parent;
        this.board = board;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("CheckListItem.fxml"));
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        
        setOnDragDetected(this::handleDragDetected);

        setOnDragOver(this::handleDragOver);

        setOnDragEntered(this::handleDragEntered);

        setOnDragExited(this::handleDragExited);

        setOnDragDropped(this::handleDragDropped);

        setOnDragDone(Event::consume);
    }


    private void handleDragDropped(DragEvent dragEvent) {
        Dragboard db = dragEvent.getDragboard();
        boolean success = false;
        if (db.hasString()) {
            String draggedText = db.getString();
            // Do something with the dragged text
            success = true;
        }
        dragEvent.setDropCompleted(success);
        dragEvent.consume();
    }

    private void handleDragExited(DragEvent dragEvent) {
        // Set the background color back to its original color
        root.setStyle("-fx-background-color: #f8f8f8;");
    }

    private void handleDragEntered(DragEvent dragEvent) {
        if (dragEvent.getGestureSource() != root && dragEvent.getDragboard().hasString()) {
            // Set the background color to indicate a valid drop target
            root.setStyle("-fx-background-color: #e0f4fd;");
        }
        dragEvent.consume();
    }

    private void handleDragOver(DragEvent dragEvent) {
        if (dragEvent.getGestureSource() != root && dragEvent.getDragboard().hasString()) {
            // Accept the drag and drop transfer mode
            dragEvent.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        }
        dragEvent.consume();
    }

    private void handleDragDetected(MouseEvent mouseEvent) {
        // Start a drag-and-drop gesture
        Dragboard db = root.startDragAndDrop(TransferMode.ANY);
        // Put the text to be dragged on the dragboard
        ClipboardContent content = new ClipboardContent();
        content.putString("Some text to be dragged");
        db.setContent(content);
        mouseEvent.consume();
    }

    @Override
    protected void updateItem(CheckListItem checkListItem, boolean empty) {
        super.updateItem(checkListItem, empty);
        if (empty || checkListItem == null) {
            setText(null);
            setGraphic(null);
        } else{
            checklistDescription.setText(checkListItem.text);
            checkBox.setSelected(checkListItem.completed);
            checkBox.setOnAction(event -> {
                checkListItem.changeCompletion();
                server.updateChecklistCheck(checkListItem, checkListItem.completed);
                // do something else with the checked state
                board.refresh();
            });
            data = checkListItem;

            setGraphic(root);
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

        }
    }

    /**
     * renames the checklist that is selected.
     * @param event
     * also updates the view of cardDetails
     */
    public void renameChecklist(MouseEvent event) {
        if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("RenameCheckListItem.fxml"));
            Stage renameStage = new Stage();

            Parent root;
            try {
                root = loader.load();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            RenameCheckListItemCtrl renameCheckListItemCtrl = loader.getController();

            renameStage.setTitle("Change the sub-task description");
            Scene scene = new Scene(root);
            renameStage.setScene(scene);
            renameStage.showAndWait();

            if (renameCheckListItemCtrl.success) {
                String newDescription = renameCheckListItemCtrl.storedText;
                checklistDescription.setText(newDescription);

                CheckListItem addedChecklist= server.renameChecklist(data, newDescription);
                parent.changeChecklistDescription(addedChecklist);
                board.refresh();
            }
        }
    }

    /**
     * removes the selected checklist
     * also updates the view of cardDetails
     * @param event
     */
    public void removeChecklist(ActionEvent event) {
        try {
            server.removeChecklist(data);
        } catch (WebApplicationException e) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
        parent.removeChecklist(data);
        board.refresh();
    }
}
