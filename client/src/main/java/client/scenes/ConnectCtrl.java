package client.scenes;

import client.Main;
import com.google.inject.Inject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class ConnectCtrl {

    private Main main;

    @FXML
    private AnchorPane root;

    @FXML
    private Label title;

    @FXML
    private TextField field;

    @FXML
    private PasswordField adminField;

    @FXML
    private Button connect;

    @FXML
    private Button connectDefault;

    @FXML
    private ListView serverList;
    @FXML
    private Label nullTitle;

    private final MainCtrl mainCtrl;

    /**
     * Create a new ConnectCtrl
     * @param mainCtrl
     */
    @Inject
    public ConnectCtrl( MainCtrl mainCtrl) {
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
            mainCtrl.showMainScreen(field.getText());
            field.setText("");
        }
        catch(Exception e) {
            mainCtrl.changeServer("http://localhost:8080");
            nullTitle.setText(e.getMessage());
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
    public void connectDefault(ActionEvent event) throws Exception {
        mainCtrl.showMainScreen("http://localhost:8080");
    }

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
            if(field.getText().equals(""))
                nullTitle.setText("Please enter a server.");
            else {
                try {
                    mainCtrl.showAdmin(field.getText());
                }
                catch(Exception e) {
                    nullTitle.setText(e.getMessage());
                }
            }
        }
        else nullTitle.setText("You have entered incorrect password. Please try again!");
    }

    /**
     * Opens the help screen
     * @param event - Key event when the user presses shift + /
     */
    public void openHelpScreen(KeyEvent event) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("HelpScreen.fxml"));

        if(event.getCode().toString().equals("SLASH") && event.isShiftDown()) {
            try {
                Parent root = fxmlLoader.load();

                Stage stage = new Stage();
                stage.setTitle("Help");
                stage.setScene(new Scene(root, 600, 400));
                stage.showAndWait();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}










