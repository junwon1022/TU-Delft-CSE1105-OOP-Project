package client.scenes;

import client.utils.PreferencesBoardInfo;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class RecentBoardsCtrl extends ListCell<PreferencesBoardInfo> {
    private BoardCtrl boardCtrl;
    private MainCtrl mainCtrl;

    private String boardTitle;
    private String boardKey;

    @FXML
    private HBox root;

    @FXML
    private Button sw;

    @FXML
    private Label title;

    /**
     * Constructor for RecentBoardsCtrl
     * @param boardCtrl
     * @param mainCtrl
     */
    public RecentBoardsCtrl(BoardCtrl boardCtrl, MainCtrl mainCtrl) {
        this.boardCtrl = boardCtrl;
        this.mainCtrl = mainCtrl;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("RecentBoards.fxml"));
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Called whenever the parent ListView is changed. Sets the data in this controller.
     * @param item The new item for the cell.
     * @param empty whether or not this cell represents data from the list. If it
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
            boardTitle = item.getTitle();
            boardKey = item.getKey();
            title.setText(item.getTitle());
            root.getStylesheets().add("styles.css");
            title.setStyle("-fx-text-fill: " + item.getFont());
            root.setStyle("-fx-background-color: " + item.getBackgroundColor());

            setGraphic(root);
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        }
    }

    /**
     * Method for switch between boards
     */
    public void switchBoard() {
        mainCtrl.showBoard(this.boardKey,this.boardCtrl.adminFlag);
    }
}
