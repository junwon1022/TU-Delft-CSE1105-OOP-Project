package client.scenes;

import client.utils.ServerUtils;
import commons.*;

import com.google.inject.Inject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class TagCtrl {
    private final ServerUtils server;
    private final BoardCtrl board;

    @FXML
    private AnchorPane root;

    @FXML
    private ListView<Tag> tags;

    @FXML
    private Button addTag;

    @FXML
    private TextField name;

    ObservableList<Tag> data;
    private boolean success;


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

    public Tag getTag(String name) {
        Tag tag = new Tag();
        tag.name = name;
        return tag;
    }

}
