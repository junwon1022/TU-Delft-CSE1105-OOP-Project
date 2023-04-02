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
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Pair;

public class MainCtrl {

    private Stage primaryStage;

    private Main main;
    private ServerUtils serverUtils;
//
//    private QuoteOverviewCtrl overviewCtrl;
//    private Scene overview;
//
//    private AddQuoteCtrl addCtrl;
//    private Scene add;
    private BoardCtrl boardCtrl;
    private Scene boardOverview;
    private ConnectCtrl connectCtrl;
    private Scene connect;
    private MainScreenCtrl mainScreenCtrl;
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
//            Pair<AddQuoteCtrl, Parent> add, Pair<QuoteOverviewCtrl, Parent> overview) {
        this.primaryStage = primaryStage;
        this.serverUtils = new ServerUtils(new UserPreferences());
//        this.overviewCtrl = overview.getKey();
//        this.overview = new Scene(overview.getValue());
//
//        this.addCtrl = add.getKey();
//        this.add = new Scene(add.getValue());

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
//        primaryStage.setHeight(690);
//        primaryStage.setWidth(1040);
        primaryStage.setMaximized(true);
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
        boardCtrl.setBoardKey(boardKey);
        boardCtrl.initialize();
    }

    /**
     * Shows the connect screen
     */

    public void showConnect() {
        primaryStage.setTitle("Connect");
        primaryStage.setScene(connect);
        primaryStage.setHeight(450);

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


        // Get the dimensions of the primary screen
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

        // Calculate the x and y coordinates to center the new scene on the primary stage
        double centerX = primaryStage.getX()
                + primaryStage.getWidth() / 2 - mainScreen.getWidth() / 2;
        double centerY = primaryStage.getY()
                + primaryStage.getHeight() / 2 - mainScreen.getHeight() / 2;

        // Set the new scene's position to the calculated coordinates
        mainScreen.getWindow().setX(centerX);
        mainScreen.getWindow().setY(centerY);
        centerStage();
        mainScreenCtrl.refresh();
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
        primaryStage.setHeight(600);
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

}