package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Board;
import jakarta.ws.rs.WebApplicationException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminTitleCtrl extends ListCell<Board> {
    private final ServerUtils server;
    private final AdminScreenCtrl adminScreenCtrl;

    private final MainCtrl mainCtrl;
    private Board data;

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
     * @param adminScreenCtrl The mainscreen the title is part of
     * @param mainCtrl
     */
    @Inject
    public AdminTitleCtrl(ServerUtils server, AdminScreenCtrl adminScreenCtrl, MainCtrl mainCtrl) {
        this.server = server;
        this.adminScreenCtrl = adminScreenCtrl;
        this.mainCtrl = mainCtrl;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("AdminBoardTitle.fxml"));
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
    protected void updateItem(Board item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setText(null);
            setContentDisplay(ContentDisplay.TEXT_ONLY);
        } else {
            title.setText(item.title);
            key.setText("Key: " + item.key);
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
            server.removeBoard(data);
            Thread.sleep(100);
        } catch (WebApplicationException e) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        adminScreenCtrl.refresh();
    }

    /**
     * Method that lets you join a board according to the title
     * @param event - the join button being clicked
     */
    public void join(ActionEvent event){
        System.out.println("The key is " + data.key);
        mainCtrl.showBoard(data.key);
    }


    /**
     * Method that lets you rename a board according to the title
     * @param event - the join button being clicked
     */
    public void rename(ActionEvent event){
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
                data = server.renameServerBoard(data, newTitle);
                System.out.println("New title after calling the command: "+ data.title);
                adminScreenCtrl.refresh();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}