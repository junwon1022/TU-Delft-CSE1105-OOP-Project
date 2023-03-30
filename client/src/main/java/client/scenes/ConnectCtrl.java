package client.scenes;


import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

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
    private Button connectDefault;

    @FXML
    private ListView serverList;
    @FXML
    private Label nullTitle;

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
     * Initialize method for the controller
     *
     */
    public void initialize() {
        ObservableList<String> items = FXCollections.observableArrayList();
        items.add("http://localhost:8080");
        items.add("http://145.94.196.182:8080");
        serverList.setCellFactory(param -> new ServerCellsCtrl(this));
        serverList.setItems(items);
    }

    /**
     * Getter for the textField
     * @return reference to the textField
     */
    public TextField getField() {
        return field;
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
        catch(Exception e) {
            try {
                server.changeServer("http://localhost:8080");
                nullTitle.setText(e.getMessage());
            }
            catch (IOException a) {
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
    public void connectDefault(ActionEvent event) { mainCtrl.showMainScreen(); }

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










