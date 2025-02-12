/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package client;

import static com.google.inject.Guice.createInjector;

import java.io.IOException;
import java.net.URISyntaxException;

import client.scenes.*;
import com.google.inject.Injector;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    private static final Injector INJECTOR = createInjector(new MyModule());
    private static final MyFXML FXML = new MyFXML(INJECTOR);

    /**
     * The main method for the application.
     * @param args the command line arguments.
     * @throws IOException If an I/O error occurs.
     */
    public static void main(String[] args) throws URISyntaxException, IOException {
        launch();
    }

    /**
     * Start the application.
     *
     * @param primaryStage the primary stage for this application, onto which
     * the application scene can be set.
     * Applications may create other stages, if needed, but they will not be
     * primary stages.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    public void start(Stage primaryStage) throws IOException {

        var connect = FXML.load(ConnectCtrl.class,
                "client", "scenes", "ConnectToServer.fxml");

        var mainCtrl = INJECTOR.getInstance(MainCtrl.class);

        primaryStage.setOnCloseRequest(e -> mainCtrl.stop());

        mainCtrl.initialize(primaryStage,connect);
    }


    /**
     * Start the application.
     *
     * @param primaryStage the primary stage for this application, onto which
     * the application scene can be set.
     * Applications may create other stages, if needed, but they will not be
     * primary stages.
     * @throws IOException If an I/O error occurs.
     */

    public void startConnect(Stage primaryStage) throws IOException {

        var connect = FXML.load(ConnectCtrl.class,
                "client", "scenes", "ConnectToServer.fxml");
        var mainScreen = FXML.load(MainScreenCtrl.class,
                "client", "scenes", "MainScreen.fxml");
        var board = FXML.load(BoardCtrl.class,
                "client", "scenes", "Board.fxml");
        var adminScreen = FXML.load(AdminScreenCtrl.class,
                "client", "scenes", "AdminScreen.fxml");

        var mainCtrl = INJECTOR.getInstance(MainCtrl.class);

        primaryStage.setOnCloseRequest(e -> mainCtrl.stop());

        mainCtrl.initialize(primaryStage, board, connect, mainScreen, adminScreen);
    }
}