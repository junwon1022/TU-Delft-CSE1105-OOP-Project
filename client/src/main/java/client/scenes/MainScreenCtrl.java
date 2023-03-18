package client.scenes;


import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Board;
import commons.BoardTitle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class MainScreenCtrl {


    @FXML
    private AnchorPane root;

    @FXML
    private Label title;

    @FXML
    private TextField joinField;

    @FXML
    private TextField addBoardField;


    @FXML
    private Button addBoard;

    @FXML
    private Button joinBoard;


    private ServerUtils server;

    @FXML
    private ListView<BoardTitle> list;

    ObservableList<BoardTitle> data;

    private final MainCtrl mainCtrl;

    /**
     * Create a new CardListCtrl
     * @param server
     * @param mainCtrl
     */
    @Inject
    public MainScreenCtrl(ServerUtils server, MainCtrl mainCtrl) {

        this.server = server;
        this.mainCtrl = mainCtrl;

        data = FXCollections.observableArrayList();
        list = new ListView<>();


    }

    /**
     * Gets the board title from the database
     */
    public void refresh() {
        var boardData = server.getMyBoardTitles();
        data.setAll(boardData);
        list.setItems(data);
        list.setCellFactory(param -> new BoardTitleCtrl(server,this, mainCtrl));
        list.setStyle("-fx-control-inner-background: " +  "#00B4D8" + ";");
    }



    /**
     * Enters a specific board based on a key
     * Creates a new window (Board)
     * If successful, joins the board through the server
     *
     * @param event the ActionEvent
     * @return
     */
    public void connectToBoard(ActionEvent event) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Board.fxml"));

        Board b = server.getBoardByKey(joinField.getText());

        mainCtrl.showBoard(b);

    }

    /**
     * Adds a Board
     *
     * @param event the ActionEvent
     * @return
     */
    public void addBoard(ActionEvent event) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("AddBoard.fxml"));
        try {
            Parent root = fxmlLoader.load();
            AddBoardCtrl controller = fxmlLoader.getController();

            Stage stage = new Stage();
            stage.setTitle("Add new board");
            stage.setScene(new Scene(root, 300, 200));
            stage.showAndWait();

            if (controller.success) {
                String title = controller.storedText;
                Board board = new Board(title,"red","read",new ArrayList<>());
                BoardTitle boardTitle = new BoardTitle(title,"red",board);

                //Generates a random invite key (the preset password is "read")
                boardTitle.board.generateInviteKey();
                server.addBoardTitle(boardTitle);

                refresh();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }



}










