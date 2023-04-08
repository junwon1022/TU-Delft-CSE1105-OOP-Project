package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Board;
import commons.Palette;
import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.HashSet;

public class CustomizationCtrl {

    public boolean success;

    public String backgroundColor;
    public String fontColor;

    private final ServerUtils server;

    private final BoardCtrl boardCtrl;
    @FXML
    private AnchorPane addition;

    @FXML
    private Button addPalette;

    @FXML
    private TextField addTitle;

    @FXML
    private ColorPicker addBackground;

    @FXML
    private ColorPicker addFont;

    @FXML
    private CheckBox makeDefault;

    @FXML
    private Button add;

    @FXML
    private ColorPicker boardBackground;

    @FXML
    private ColorPicker boardFont;

    @FXML
    private ColorPicker listBackground;

    @FXML
    private ColorPicker listFont;

    @FXML
    private Label boardName;

    @FXML
    private Label nullTitle;

    @FXML
    private Button resetBoard;

    @FXML
    private Button resetLists;

    @FXML
    private ListView<Palette> list;

    @FXML
    private AnchorPane disabled;
    @FXML
    private HBox readOnlyMessage;

    private ObservableList<Palette> data;

    private Board board;

    /**
     * Constructor for the customization controller
     * @param server
     * @param boardCtrl
     * @param board
     */
    @Inject
    public CustomizationCtrl(ServerUtils server,
                             BoardCtrl boardCtrl, Board board) {
        this.server = server;
        this.boardCtrl = boardCtrl;
        this.board = board;
        backgroundColor = board.colour;
        fontColor = board.font;
    }

    /**
     * Method that initializes the controller
     */
    public void initialize() {
        data = FXCollections.observableArrayList();

        list.setItems(data);
        list.setStyle("-fx-background-color: #A2E4F1; " +
                "-fx-background-radius: 10");
        list.setCellFactory(param -> new PaletteCtrl(server, this));
        addition.setVisible(false);
        nullTitle.setVisible(false);
        boardName.setText(board.title);
        boardBackground.setValue(Color.web(board.colour));
        boardFont.setValue(Color.web(board.font));
        listBackground.setValue(Color.web(board.listColour));
        listFont.setValue(Color.web(board.listFont));
        clearFields();
        var palettes = server.getAllPalettes(board.id);
        data.setAll(palettes);
        if(!boardCtrl.isUnlocked()) {
            disabled.setVisible(true);
        } else {
            disabled.setVisible(false);
        }
    }

    /**
     * Method that refreshes the customization scene and the board
     */
    public void refresh(){
        var palettes = server.getAllPalettes(board.id);
        data.setAll(palettes);
        list.refresh();
        boardCtrl.refresh();
    }

    /**
     * Method that changes a Color object to a hex code
     * @param color
     * @return
     */
    private String hexCode(Color color){
        String hex = color.toString();
        hex = hex.substring(2, hex.length()-2);
        hex = "#" + hex;
        return  hex;
    }

    /**
     * Method that changes the board's background in the database
     */
    private void changeBoardBackground(){
        Color color = boardBackground.getValue();
        backgroundColor = hexCode(color);
        server.changeBoardBackground(board, backgroundColor);
    }

    /**
     * Method that changes the board's font in the database
     */
    private void changeBoardFont(){
        Color color = boardFont.getValue();
        fontColor = hexCode(color);
        server.changeBoardFont(board, fontColor);
    }

    /**
     * Method that changes the list's font in the database
     */
    private void changeListsFont(){
        Color color = listFont.getValue();
        server.changeListsFont(board,hexCode(color));
    }

    /**
     * Method that changes the list's background in the database
     */
    private void changeListsBackground(){
        Color color = listBackground.getValue();
        server.changeListsBackground(board,hexCode(color));
    }

    /**
     * Method that closes the customization screen
     * @param event
     */
    private static void closeWindow(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    /**
     * Method that shows the add a palette functionality
     */
    public void setAddPalette(){
        addPalette.setVisible(false);
        addition.setVisible(true);
    }

    /**
     * Method that adds a palette on given user input
     */
    public void addPalette(){
        if(addTitle.getText() == null || addTitle.getText().length() == 0)
            nullTitle.setVisible(true);
        else{
            nullTitle.setVisible(false);
            String background = hexCode(addBackground.getValue());
            String font = hexCode(addFont.getValue());

            Palette p = new Palette(addTitle.getText(), background, font,
                    false,board, new HashSet<>());
            Palette addedP = server.addPalette(board.id, p);
            p.id = addedP.id;
            if(makeDefault.isSelected()){
                server.setDefault(board.id, p.id);
            }
            addPalette.setVisible(true);
            addition.setVisible(false);
            clearFields();
            refresh();
        }
    }

    /**
     * Method that clears the fields for adding a palette
     */
    private void clearFields(){
        addTitle.clear();
        addBackground.setValue(Color.WHITE);
        addFont.setValue(Color.BLACK);
        makeDefault.setSelected(false);
    }

    /**
     * Method that resets the board colors
     * @param event
     */
    public void resetBoardColors(ActionEvent event){
        backgroundColor = "#A2E4F1";
        fontColor = "#000000";
        server.changeBoardBackground(board,  "#A2E4F1");
        server.changeBoardFont(board, "#000000");

        boardBackground.setValue(Color.web("#A2E4F1"));
        boardFont.setValue(Color.web("#000000"));
        boardCtrl.refresh();
    }

    /**
     * Method that resets the list colors
     * @param event
     */
    public void resetListColors(ActionEvent event){
        server.changeListsBackground(board,  "#CAF0F8");
        server.changeListsFont(board, "#000000");

        listBackground.setValue(Color.web("#CAF0F8"));
        listFont.setValue(Color.web("#000000"));

        boardCtrl.refresh();
    }

    /**
     * Method that changes the board background when the color is changes
     * @param event
     */
    public void changeBoardBOnAction(ActionEvent event){
        changeBoardBackground();
        boardCtrl.refresh();
    }

    /**
     * Method that changes the board font when the color is changes
     * @param event
     */
    public void changeBoardFOnAction(ActionEvent event){
        changeBoardFont();
        boardCtrl.refresh();
    }

    /**
     * Method that changes the lists' background when the color is changes
     * @param event
     */
    public void changeListBOnAction(ActionEvent event){
        changeListsBackground();
        boardCtrl.refresh();
    }

    /**
     * Method that changes the lists' font when the color is changes
     * @param event
     */
    public void changeListFOnAction(ActionEvent event){
        changeListsFont();
        boardCtrl.refresh();
    }

    /**
     * Closes the read only message
     * @param event
     */
    public void closeReadOnlyView(ActionEvent event) {
        FadeTransition fadeOutMessage = new FadeTransition(Duration.seconds(0.5), readOnlyMessage);
        fadeOutMessage.setFromValue(0.9);
        fadeOutMessage.setToValue(0.0);

        fadeOutMessage.setOnFinished(e -> {
            // Hide the message when the transition is finished
            readOnlyMessage.setVisible(false);
        });
        fadeOutMessage.play();
    }

    /**
     * Shows read-only message if button is disabled
     * @param event
     */
    public void showReadOnlyMessage(Event event) {
        readOnlyMessage.setVisible(true);
        FadeTransition fadeInTransition = new FadeTransition(
                Duration.seconds(0.3), readOnlyMessage);
        fadeInTransition.setFromValue(0.0);
        fadeInTransition.setToValue(0.9);
        fadeInTransition.play();
        Alert alert = new Alert(Alert.AlertType.WARNING,
                "Cannot edit in read-only mode. \n" +
                        "To gain write access, click on the lock icon and enter the password.",
                ButtonType.OK);// Load your custom icon image

        // Set the graphic of the alert dialog to custom image
        alert.setGraphic(new ImageView(new Image("warning-icon.png")));
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add("styles.css");
        dialogPane.getStyleClass().add("alert");
        dialogPane.lookupButton(ButtonType.OK).getStyleClass().add("normal-button");
        alert.showAndWait();
    }
}
