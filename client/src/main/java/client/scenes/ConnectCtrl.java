package client.scenes;


import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class ConnectCtrl {


    @FXML
    private AnchorPane root;

    @FXML
    private Label title;

    @FXML
    private TextField field;


    @FXML
    private TextField adminField;


    @FXML
    private Button connect;

    @FXML
    private Button connect_default;

    private ServerUtils server;

    private final MainCtrl mainCtrl;

    /**
     * Create a new ConnectCtrl
     * @param server
     * @param mainCtrl
     */
    @Inject
    public ConnectCtrl(ServerUtils server, MainCtrl mainCtrl) {

        this.server = server;
        this.mainCtrl = mainCtrl;
    }


    /**
     * Enters a Server and shows the MainScreen
     * Creates a new window (MainScreen)
     * If successful, joins the screen through the server
     *
     * @param event the ActionEvent
     * @return
     */
    public void connectToMainScreen(ActionEvent event) throws Exception {
        try {
            server.changeServer(field.getText());
            mainCtrl.showMainScreen();
        }
        catch(Exception e){
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Exception.fxml"));
            try {
                Parent root = fxmlLoader.load();
                server.changeServer("http://localhost:8080");
                Stage stage = new Stage();
                stage.setTitle(e.getMessage());
                stage.setScene(new Scene(root, 300, 200));
                stage.showAndWait();

            } catch (IOException a) {
                throw new RuntimeException(a);
            }
        }
    }
    /**
     * Enters the standard server (8080) and shows the MainScreen
     * Creates a new window (MainScreen)
     * If successful, joins the screen through the server
     *
     * @param event the ActionEvent
     * @return
     */
    public void connectDefault(ActionEvent event) {mainCtrl.showMainScreen();}

    /**
     * Enters the standard server (8080) and shows the Admin Screen
     * Creates a new window (AdminScreen)
     * If successful, joins the screen through the server
     *
     * @param event the ActionEvent
     * @return
     */
    public void connectAdmin(ActionEvent event) throws Exception {
        if(adminField.getText().equals("admin")) {
            if(field.getText().equals("")) mainCtrl.showAdmin();

            else{
                try {
                    server.changeServer(field.getText());
                    mainCtrl.showAdmin();
                }
                catch(Exception e){
                    FXMLLoader fxmlLoader =
                            new FXMLLoader(getClass().getResource("Exception.fxml"));
                    try {
                        Parent root = fxmlLoader.load();
                        server.changeServer("http://localhost:8080");
                        Stage stage = new Stage();
                        stage.setTitle(e.getMessage());
                        stage.setScene(new Scene(root, 300, 200));
                        stage.showAndWait();

                    } catch (IOException a) {
                        throw new RuntimeException(a);
                    }
                }




            }

        }
        else throw new Exception("Admin password wrong");
    }
}










