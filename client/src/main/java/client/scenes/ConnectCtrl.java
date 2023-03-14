package client.scenes;


import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Board;
import commons.Card;
import jakarta.ws.rs.client.ClientBuilder;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.glassfish.jersey.client.ClientConfig;

import java.io.IOException;
import java.util.ArrayList;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

public class ConnectCtrl {


    @FXML
    private AnchorPane root;

    @FXML
    private Label title;

    @FXML
    private TextField field;

    @FXML
    private Button connect;

    @FXML
    private Button connect_default;

    private ServerUtils server;

    private final MainCtrl mainCtrl;

    /**
     * Create a new CardListCtrl
     */
    @Inject
    public ConnectCtrl(ServerUtils server, MainCtrl mainCtrl) {

        this.server = server;
        this.mainCtrl = mainCtrl;
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
        ServerUtils s = new ServerUtils();
        System.out.println(field.getText());
                int a =  ClientBuilder.newClient(new ClientConfig())
                        .target("http://localhost:" + field.getText()).path("api/listen/connect")
                        .request(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .get(Integer.class);

                Board c = new Board("My Board","#111111", "read", "write", new ArrayList<>());


                Board b = ClientBuilder.newClient(new ClientConfig())
                        .target("http://localhost:" + field.getText()).path("api/listen/listen?id="+ a + "&board=" + c.id)
                        .request(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .get(Board.class);

                System.out.println(b);

                    mainCtrl.showBoard(b);

                return c;


        }
    }










