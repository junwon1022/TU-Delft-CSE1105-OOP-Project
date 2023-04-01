package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Board;
import commons.Palette;
import javafx.event.ActionEvent;
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

    private Board board;

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
            board = item.board;
            background.setValue(Color.web(data.background));
            font.setValue(Color.web(data.font));
            if(data.isDefault)
                setDefault.setVisible(false);
            setGraphic(root);
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        }
    }


    public void removePalette(ActionEvent event){
        if(board.palettes.size() > 1){
            server.deletePalette(board.id, data);
            parent.refresh();
        }
    }

    private String hexCode(Color color){
        String hex = color.toString();
        hex = hex.substring(2, hex.length()-2);
        hex = "#" + hex;
        return  hex;
    }
    public void setBackground(ActionEvent event){
        String colour = hexCode(background.getValue());
        server.setPaletteBackground(board.id, data.id, colour);
    }

    public void setFont(ActionEvent event){
        String colour = hexCode(font.getValue());
        server.setPaletteFont(board.id, data.id, colour);
    }
}
