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
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class CheckListItemCtrl extends ListCell<CheckListItem> {

    private final ServerUtils server;

    private final CardDetailsCtrl cardDtlsCtrl;

    private final BoardCtrl board;

    private CheckListItem data;

    @FXML
    private HBox root;

    @FXML
    private Label checklistDescription;

    @FXML
    private CheckBox checkBox;

    @FXML
    private Button removeChecklist;


    @Inject
    CheckListItemCtrl(ServerUtils server, CardDetailsCtrl cardDtlsCtrl, BoardCtrl board) {
        this.server = server;
        this.cardDtlsCtrl = cardDtlsCtrl;
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
                int completed = cardDtlsCtrl.getCardCtrl().getCompleted();
                int total = cardDtlsCtrl.getCardCtrl().getTotal();
                if (checkListItem.completed) {
                    cardDtlsCtrl.getCardCtrl().setProgressText(completed+1,total);
                }
                else{
                    cardDtlsCtrl.getCardCtrl().setProgressText(completed-1,total);
                }
                board.refresh();
            });
            data = checkListItem;

            setGraphic(root);
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

        }
    }

    /**
     * Handles when a user drags a particular checklistitem in the
     * list view cell
     * @param event the starting drag event
     */
    private void handleDragDetected(MouseEvent event) {
        if (getItem() == null) {
            return;
        }

        Dragboard dragboard = startDragAndDrop(TransferMode.MOVE);
        ClipboardContent content = new ClipboardContent();
        content.putString(getItem().id + " ");
        dragboard.setContent(content);
        // set drag view to a snapshot of the checklist
        SnapshotParameters snapshotParams = new SnapshotParameters();
        snapshotParams.setFill(Color.rgb(0, 206, 209));
        dragboard.setDragView(root.snapshot(snapshotParams, null), event.getX(), event.getY());
        event.consume();
    }

    /**
     * Handles drag over object
     * @param event drag event
     */
    private void handleDragOver(DragEvent event) {
        if (event.getGestureSource() != this &&
                event.getDragboard().hasString()) {
            if (event.getGestureSource().getClass() == CheckListItemCtrl.class)
                event.acceptTransferModes(TransferMode.MOVE);
        }
        event.consume();
    }

    /**
     * Handles drag entering object
     * @param event drag event
     */
    private void handleDragEntered(DragEvent event) {
        if (event.getGestureSource() != this &&
                event.getDragboard().hasString()) {
            setOpacity(0.3);
        }
    }

    /**
     * Handles drag exiting object
     * @param event drag event
     */
    private void handleDragExited(DragEvent event) {
        if (event.getGestureSource() != this &&
                event.getDragboard().hasString()) {
            setOpacity(1);
        }
    }

    /**
     * Handles when the checklist is dropped
     * @param event drag event
     */
    private void handleDragDropped(DragEvent event) {
        if (getItem() != null) {
            dragDroppedOnCell(event);
        }
    }

    /**
     * Handles when the checklist is dropped in a cell
     * in the list view
     * @param event drag event
     */
    private void dragDroppedOnCell(DragEvent event) {
        Dragboard db = event.getDragboard();
        if (db.hasString()) {
            String[] strings = db.getString().split(" ");
            long dbCheckListId = Long.decode(strings[0]);
            List<CheckListItem> items = cardDtlsCtrl.getCheckListArray();
            int oldIndex = 0;
            for (int i = 0; i < items.size(); i++) {
                if (items.get(i).id == dbCheckListId)
                    oldIndex = i;
            }
            int newIndex = items.indexOf(getItem());
            cardDtlsCtrl.swapChecklists(oldIndex, newIndex);
        }
        event.setDropCompleted(true);
        event.consume();
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
                server.renameChecklist(data, newDescription);
                checklistDescription.setText(newDescription);
                data.text = newDescription;
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
        cardDtlsCtrl.removeChecklist(data);
        board.refresh();
    }
}
