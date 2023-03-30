package client.scenes;

import client.utils.PreferencesBoardInfo;
import client.utils.ServerUtils;
import client.utils.UserPreferences;
import com.google.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Set;

public class BoardTitleCtrl extends ListCell<PreferencesBoardInfo> {
    private final ServerUtils server;
    private final MainScreenCtrl mainScreenCtrl;

    private final MainCtrl mainCtrl;
    private final UserPreferences prefs;
    private PreferencesBoardInfo data;

    @FXML
    private VBox root;

    @FXML
    private Label title;

    @FXML
    private Label key;
    @FXML
    private Label description;

    @FXML
    private Button delete;
    @FXML
    private Button copyButton;

    /**
     * Create a new Board title control
     *
     * @param server         The server to use
     * @param mainScreenCtrl The mainscreen the title is part of
     * @param mainCtrl
     * @param prefs
     */
    @Inject
    public BoardTitleCtrl(ServerUtils server,
                          MainScreenCtrl mainScreenCtrl,
                          MainCtrl mainCtrl,
                          UserPreferences prefs) {
        this.server = server;
        this.mainScreenCtrl = mainScreenCtrl;
        this.mainCtrl = mainCtrl;
        this.prefs = prefs;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("BoardTitle.fxml"));
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

        root.setOnMouseEntered(this::showButtons);

        root.setOnMouseExited(this::hideButtons);
    }

    /**
     * Is called whenever the parent Board is changed. Sets the data in this controller.
     * @param item The new item for the cell.
     * @param empty whether this cell represents data from the list or not. If it
     *        is empty, then it does not represent any domain data, but is a cell
     *        being used to render an "empty" row.
     */
    @Override
    protected void updateItem(PreferencesBoardInfo item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setText(null);
            setContentDisplay(ContentDisplay.TEXT_ONLY);
        } else {
            title.setText(item.getTitle());
            key.setText(item.getKey());
            data = item;
            root.setStyle("-fx-background-color: " + data.colour);
            title.setStyle("-fx-text-fill: " + data.font);

            setGraphic(root);
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        }
    }

    /**
     * Method that removes the board title from the main screen, visually
     * @param event - the remove button being clicked
     */

    public void remove(ActionEvent event){
        try {
            prefs.leaveBoard(server.getServerAddress(), data);
        } catch (WebApplicationException e) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
        mainScreenCtrl.refresh();
    }

    /**
     * Method that lets you join a board according to the title
     * @param event - the join button being clicked
     */
    public void join(ActionEvent event){
        System.out.println("The key is " + data.getKey());
        mainCtrl.showBoard(data.getKey());
    }


    /**
     * Method that lets you rename a board according to the title
     * @param event - the join button being clicked
     */
    public void rename(ActionEvent event) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("RenameBoard.fxml"));
        try {
            Parent root = fxmlLoader.load();
            RenameBoardCtrl controller = fxmlLoader.getController();
            controller.initialize(data);

            Stage stage = new Stage();
            stage.setTitle("Add new board");
            stage.setScene(new Scene(root, 300, 200));
            stage.showAndWait();

            if (controller.success) {
                String newTitle = controller.storedText;

                //method that actually renames the list in the database
                data = server.renameBoard(data, newTitle);
                System.out.println("New title after calling the command: "+ data.getTitle());
                mainScreenCtrl.refresh();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


        /**
         * Copies the key to the clipboard and shows a notification to the user
         * @param event
         */
    public void copyKeyToClipboard(ActionEvent event) {
        copyToClipboard(data.getKey());
        Tooltip tooltip = new Tooltip("Key copied to clipboard!");

        PauseTransition delay = new PauseTransition(Duration.seconds(4));
        delay.setOnFinished(e -> tooltip.hide());
        tooltip.show(copyButton, copyButton.getLayoutX(), copyButton.getLayoutY());
        delay.play();
    }

    /**
     * Copies a given key to the clipboard.
     * @param key
     */
    private void copyToClipboard(String key) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(key);
        clipboard.setContent(content);
    }

    /**
     * Shows the join and delete button of a board
     * @param event
     */
    public void showButtons(MouseEvent event) {
        Set<Node> nodes = root.lookupAll(".board-title-button");
        for (Node node : nodes) {
            node.setVisible(true);
        }
    }

    /**
     * Hides the join and delete buttons of a board
     * @param event
     */
    public void hideButtons(MouseEvent event) {
        Set<Node> nodes = root.lookupAll(".board-title-button");
        for (Node node : nodes) {
            node.setVisible(false);
        }
    }
}
