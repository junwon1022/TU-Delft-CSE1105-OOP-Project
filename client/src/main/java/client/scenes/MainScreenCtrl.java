package client.scenes;


import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Board;
import commons.BoardTitle;
import commons.Card;
import commons.ListOfCards;
import jakarta.ws.rs.client.ClientBuilder;
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
import org.glassfish.jersey.client.ClientConfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

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
     */
    @Inject
    public MainScreenCtrl(ServerUtils server, MainCtrl mainCtrl) {

        this.server = server;
        this.mainCtrl = mainCtrl;

  /*      FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MainScreen.fxml"));
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/

        data = FXCollections.observableArrayList();
        list = new ListView<>();

        System.out.println(data.toString());

        list.setItems(data);
        list.setCellFactory(param -> new BoardTitleCtrl(server,this));


        list.setStyle("-fx-control-inner-background: " +  "#00B4D8" + ";");
    }

    /**
     * Gets the board title from the database
     */
    public void refresh() {
        var boardData = server.getMyBoards();
        data.setAll(boardData);
    }



    /**
     * Enters a Server (Temporarily, it will enter the board object)
     * Creates a new window (Board)
     * If successful, joins the board through the server
     *
     * @param event the ActionEvent
     * @return
     */
    public Board connectToBoard(ActionEvent event) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Board.fxml"));

        return server.getBoardById(Long.valueOf(joinField.getText()));
    }


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
                BoardTitle boardTitle = new BoardTitle(title,"red",new Board(title, "red","read","write", new ArrayList<>()));
                boardTitle.board.boardTitle = boardTitle;
                server.addBoardTitle(boardTitle);

                refresh();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }



}










