package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Card;
import commons.Palette;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import java.io.IOException;

public class PresetCtrl extends ListCell<Palette> {

    private final ServerUtils server;
    private final CardDetailsCtrl parent;

    private Palette palette;

    @FXML
    private Label title;

    @FXML
    private Rectangle background;

    @FXML
    private Rectangle font;

    @FXML
    private HBox root;

    private Card card;

    private final BoardCtrl boardCtrl;

    /**
     * Constructor for preset
     * @param server
     * @param parent
     * @param boardCtrl
     * @param card
     */
    @Inject
    public PresetCtrl(ServerUtils server, CardDetailsCtrl parent, BoardCtrl boardCtrl, Card card){
        this.server = server;
        this.parent = parent;
        this.boardCtrl = boardCtrl;
        this.card = card;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Preset.fxml"));
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Update method for the preset
     *
     * @param item The new item for the cell.
     * @param empty whether this cell represents data from the list. If it
     *        is empty, then it does not represent any domain data, but is a cell
     *        being used to render an "empty" row.
     */
    @Override
    public void updateItem(Palette item, boolean empty){
        super.updateItem(item, empty);

        if (empty || item == null) {
            setText(null);
            setContentDisplay(ContentDisplay.TEXT_ONLY);
        } else {
            title.setText(item.title);
            palette = item;
            background.setFill(Color.web(item.background));
            font.setFill(Color.web(item.font));
            setGraphic(root);
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        }
    }

    /**
     * Method that lets the user choose another preset for the task
     * @param event
     */
    @FXML
    public void choosePreset(MouseEvent event){
        if(event.getClickCount() == 2){
            card.palette = palette;
            server.addPaletteToCard(card, palette);
            palette = server.getPalette(palette.board.id, palette.id);
            boardCtrl.refresh();
            parent.refresh();
        }
    }

}
