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
package client.scenes;

import client.Main;
import client.utils.ServerUtils;
import client.utils.UserPreferences;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.shape.Line;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Pair;

public class MainCtrl {

    protected Stage primaryStage;

    private Main main;
    private ServerUtils serverUtils;
    protected BoardCtrl boardCtrl;
    private Scene boardOverview;
    private ConnectCtrl connectCtrl;
    private Scene connect;
    protected MainScreenCtrl mainScreenCtrl;
    private Scene mainScreen;
    private AdminScreenCtrl adminScreenCtrl;
    private Scene adminScreen;

    /**
     * Create a new MainCtrl.
     *
     * @param primaryStage The primary stage to use.
     * @param connect      The connect screen
     */
    public void initialize(Stage primaryStage,
                           Pair<ConnectCtrl, Parent> connect) {
        this.primaryStage = primaryStage;
        this.connectCtrl = connect.getKey();
        this.connectCtrl.initialize();
        this.connect = new Scene(connect.getValue());
        this.connect.getStylesheets().add("styles.css");
        this.main = new Main();

        showConnect();
        primaryStage.show();

    }

    /**
     * Create a new MainCtrl.
     *
     * @param primaryStage The primary stage to use.
     * @param board        The board screen to use.
     * @param connect      The connect screen
     * @param mainScreen   The main screen
     * @param adminScreen The admin screen
     */
    public void initialize(Stage primaryStage, Pair<BoardCtrl, Parent> board,
                           Pair<ConnectCtrl, Parent> connect,
                           Pair<MainScreenCtrl, Parent> mainScreen,
                           Pair<AdminScreenCtrl, Parent> adminScreen) {
        this.primaryStage = primaryStage;
        this.serverUtils = new ServerUtils(new UserPreferences());

        this.boardCtrl = board.getKey();
        this.boardOverview = new Scene(board.getValue());
        this.boardOverview.getStylesheets().add("styles.css");

        this.connectCtrl = connect.getKey();
        this.connectCtrl.initialize();
        this.connect = new Scene(connect.getValue());
        this.connect.getStylesheets().add("styles.css");

        this.mainScreenCtrl = mainScreen.getKey();
        this.mainScreen = new Scene(mainScreen.getValue());
        this.mainScreen.getStylesheets().add("styles.css");

        this.adminScreenCtrl = adminScreen.getKey();
        this.adminScreen = new Scene(adminScreen.getValue());
        this.adminScreen.getStylesheets().add("styles.css");
        this.main = new Main();

        showConnect();
        primaryStage.show();
    }

    /**
     * Show the board scene.
     * @param boardKey the key of a board
     * @param adminFlag can be either 1 (admin) or 0 (non admin)
     */
    public void showBoard(String boardKey, int adminFlag) {
        primaryStage.setTitle("My board");
        primaryStage.setScene(boardOverview);
        primaryStage.setMaximized(true);
        updateLineEnd(boardCtrl.getLine(), 520);
        updateLineEnd(boardCtrl.getLine2(), 150);

        // Add a listener to detect when the stage is no longer maximized and center it
        // Also sets the size of the resized stage
        primaryStage.maximizedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                primaryStage.setHeight(690);
                primaryStage.setWidth(1150);
                centerStage();
            }
        });

        boardCtrl.adminFlag = adminFlag;

        primaryStage.widthProperty().addListener((observable, oldWidth, newWidth) -> {
            updateLineEnd(boardCtrl.getLine(), 530);
            updateLineEnd(boardCtrl.getLine2(), 170);
        });

        boardCtrl.setBoardKey(boardKey);
        boardCtrl.initialize();
    }

    /**
     * As lines in java fx are not responsive since they are not resizable,
     * this is a custom resizing method
     * @param line - line to be resized
     * @param offset - width relative to the width of the container
     */
    private void updateLineEnd(Line line, int offset) {
        // update the line's end x coordinate to match the new width of the container
        double containerWidth = line.getParent().getLayoutBounds().getWidth();
        double newEndX = containerWidth - offset;
        line.setEndX(newEndX);
    }

    /**
     * Shows the connect screen
     */

    public void showConnect() {
        primaryStage.setMaximized(false);
        primaryStage.setHeight(434);
        primaryStage.setWidth(609);
        centerStage();
        primaryStage.setScene(connect);
        primaryStage.sizeToScene();
        primaryStage.maximizedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                primaryStage.sizeToScene();
                centerStage();
            }
        });
        primaryStage.setTitle("Connect");
    }



    /**
     * Show the board scene.
     * @param server the server the main screen connects to
     */
    public void showMainScreen(String server) throws Exception {

        main.startConnect(primaryStage);
        serverUtils.changeServer(server);
        primaryStage.setTitle("Main Screen");
        primaryStage.setScene(mainScreen);
        primaryStage.setMaximized(false);
        primaryStage.setHeight(600);
        primaryStage.setWidth(800);
        centerStage();
        mainScreenCtrl.initialize();
    }

    /**
     * Changes the server
     * @param server the server the admin connects to
     */
    public void changeServer(String server) throws Exception {
        serverUtils.changeServer(server);
    }

    /**
     * Show the admin scene.
     * @param server the server the admin connects to
     */
    public void showAdmin(String server) throws Exception {
        main.startConnect(primaryStage);
        serverUtils.changeServer(server);
        primaryStage.setTitle("Admin Screen");
        primaryStage.setScene(adminScreen);
        primaryStage.setHeight(550);
        primaryStage.setWidth(800);
        primaryStage.setMaximized(false);
        centerStage();
        adminScreenCtrl.refresh();
    }

    /**
     * Centers the stage in the computer window
     */
    private void centerStage() {
        // Get the screen size
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

        // Calculate the center position of the screen
        double centerX = screenBounds.getWidth() / 2;
        double centerY = screenBounds.getHeight() / 2;

        // Set the position of the stage to the center of the screen
        primaryStage.setX(centerX - primaryStage.getWidth() / 2);
        primaryStage.setY(centerY - primaryStage.getHeight() / 2);
    }

    /**
     * Method for stopping long polling
     */
    public void stop() {
        if (serverUtils != null)
            serverUtils.stop();
        Platform.exit();
        System.exit(0);
    }
}