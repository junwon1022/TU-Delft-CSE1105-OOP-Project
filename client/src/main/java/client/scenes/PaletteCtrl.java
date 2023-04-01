package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Palette;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

import java.io.IOException;

public class PaletteCtrl extends ListCell<Palette> {

    private Palette data;
    private final ServerUtils server;
    private final CustomizationCtrl parent;

    @FXML
    private Label title;

    @FXML
    private ColorPicker background;

    @FXML
    private ColorPicker font;

    @FXML
    private Button setDefault;

    @FXML
    private Button remove;

    @FXML
    private HBox root;

    @FXML
    private Button addPalette;

    @Inject
    public PaletteCtrl(ServerUtils server, CustomizationCtrl parent){
        this.server = server;
        this.parent = parent;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Palette.fxml"));
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateItem(Palette item, boolean empty){
        super.updateItem(item, empty);

        if (empty || item == null) {
            setText(null);
            setContentDisplay(ContentDisplay.TEXT_ONLY);
        } else {
            title.setText(item.title);
            data = item;
            background.setValue(Color.web(data.background));
            font.setValue(Color.web(data.font));
            setGraphic(root);
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        }
    }




}
