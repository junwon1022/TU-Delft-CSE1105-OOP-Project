package client.scenes;

import javafx.scene.control.Label;
import javafx.scene.control.ListCell;

public class ServerCellsCtrl extends ListCell<String> {

    private ConnectCtrl connectCtrl;
    private Label label;

    /**
     * Constructor for the cells of the server list
     * @param connectCtrl
     */
    public ServerCellsCtrl(ConnectCtrl connectCtrl) {
        this.label = new Label();
        this.connectCtrl = connectCtrl;
        setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                handleDoubleClick();
            }
        });
    }

    /**
     * Updates the info in the server list
     * @param item The new item for the cell.
     * @param empty whether or not this cell represents data from the list. If it
     *        is empty, then it does not represent any domain data, but is a cell
     *        being used to render an "empty" row.
     */
    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if (!empty && item != null) {
            label.setText(item);
            setGraphic(label);
        } else {
            setGraphic(null);
        }
    }

    /**
     * Copies the text of the label to the text field
     */
    private void handleDoubleClick() {
        connectCtrl.getField().setText(label.getText());
    }
}
