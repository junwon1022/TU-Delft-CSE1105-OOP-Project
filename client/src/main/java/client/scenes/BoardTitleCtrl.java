package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.BoardTitle;
import commons.Card;
import jakarta.ws.rs.WebApplicationException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;

import java.io.IOException;

public class BoardTitleCtrl extends ListCell<BoardTitle> {
    private final ServerUtils server;
    private final MainScreenCtrl mainScreenCtrl;

    private BoardTitle data;

    @FXML
    private AnchorPane root;

    @FXML
    private Label title;

    @FXML
    private Label description;

    @FXML
    private Button delete;

    /**
     * Create a new Board title control
     *
     * @param server         The server to use
     * @param mainScreenCtrl The mainscreen the title is part of
     */
    @Inject
    public BoardTitleCtrl(ServerUtils server, MainScreenCtrl mainScreenCtrl) {
        this.server = server;
        this.mainScreenCtrl = mainScreenCtrl;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("BoardTitle.fxml"));
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException e) {
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
    protected void updateItem(BoardTitle item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setText(null);
            setContentDisplay(ContentDisplay.TEXT_ONLY);
        } else {
            title.setText(item.text);
            data = item;

            setGraphic(root);
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        }
    }



}