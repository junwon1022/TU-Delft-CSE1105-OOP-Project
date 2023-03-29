package client.scenes;

import client.utils.PreferencesBoardInfo;
import client.utils.ServerUtils;
import client.utils.UserPreferences;
import com.google.inject.Inject;
import commons.Board;
import jakarta.ws.rs.WebApplicationException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;

import java.io.IOException;

public class BoardTitleCtrl extends ListCell<PreferencesBoardInfo> {
    private final ServerUtils server;
    private final MainScreenCtrl mainScreenCtrl;

    private final MainCtrl mainCtrl;
    private final UserPreferences prefs;
    private PreferencesBoardInfo data;

    @FXML
    private AnchorPane root;

    @FXML
    private Label title;

    @FXML
    private Label key;
    @FXML
    private Label description;

    @FXML
    private Button delete;

    /**
     * Create a new Board title control
     *
     * @param server         The server to use
     * @param mainScreenCtrl The mainscreen the title is part of
     * @param mainCtrl
     */
    @Inject
    public BoardTitleCtrl(ServerUtils server, MainScreenCtrl mainScreenCtrl, MainCtrl mainCtrl, UserPreferences prefs) {
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
            key.setText("Key: " + item.getKey());
            data = item;

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
    public void rename(ActionEvent event){
        System.out.println("The key is " + data.getKey());
        mainCtrl.showBoard(data.getKey());
    }


}