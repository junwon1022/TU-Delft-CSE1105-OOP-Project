package client.scenes;

import client.utils.ServerUtils;
import commons.*;

import com.google.inject.Inject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.IOException;

public class TagCtrl {
    private final ServerUtils server;
    private final BoardCtrl board;

    @FXML
    private ListView<Tag> tags;

    @FXML
    private TextField name;

    ObservableList<Tag> data;



    @Inject
    public TagCtrl(ServerUtils server, BoardCtrl board) {
        this.server = server;
        this.board = board;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Tag.fxml"));
        fxmlLoader.setController(this);

        try{
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        data = FXCollections.observableArrayList();

        tags.setItems(data);

        board.refresh();
    }


    public void addTag(ActionEvent event) {
        String name = this.name.getText();
        if (name != null && !name.isEmpty()) {
            //Hardcoded line
            Board b = board.getBoard();
            Tag tag = new Tag(name, "red", b, null);
            server.addTag(tag);
            board.refresh();
        }
    }
}
