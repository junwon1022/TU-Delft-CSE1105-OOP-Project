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

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;

public class MainCtrl {

    private Stage primaryStage;
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
//        this.overviewCtrl = overview.getKey();
//        this.overview = new Scene(overview.getValue());
//
//        this.addCtrl = add.getKey();
//        this.add = new Scene(add.getValue());

        this.boardCtrl = board.getKey();
        this.boardOverview = new Scene(board.getValue());
        this.boardOverview.getStylesheets().add("styles.css");

        this.connectCtrl = connect.getKey();
        this.connect = new Scene(connect.getValue());

        this.mainScreenCtrl = mainScreen.getKey();
        this.mainScreen = new Scene(mainScreen.getValue());

        this.adminScreenCtrl = adminScreen.getKey();
        this.adminScreen = new Scene(adminScreen.getValue());

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
        primaryStage.setHeight(690);
        primaryStage.setWidth(1040);
        primaryStage.setResizable(false);
        boardCtrl.boardKey = boardKey;
        boardCtrl.adminFlag = adminFlag;
        boardCtrl.initialize();
    }

    /**
     * Shows the connect screen
     */

    public void showConnect() {
        primaryStage.setTitle("Connect");
        primaryStage.setScene(connect);
        primaryStage.setHeight(400);

    }



    /**
     * Show the board scene.
     */
    public void showMainScreen() {
        primaryStage.setTitle("Main Screen");
        primaryStage.setScene(mainScreen);
        primaryStage.setHeight(600);
        mainScreenCtrl.refresh();
    }


    /**
     * Show the admin scene.
     */
    public void showAdmin() {
        primaryStage.setTitle("Admin Screen");
        primaryStage.setScene(adminScreen);
        primaryStage.setHeight(600);
        adminScreenCtrl.refresh();
    }



    /**
     * Show the overview scene.
     */
//    public void showOverview() {
//        primaryStage.setTitle("Quotes: Overview");
//        primaryStage.setScene(overview);
//        overviewCtrl.refresh();
//    }

    /**
     * Show the add scene.
     */
//    public void showAdd() {
//        primaryStage.setTitle("Quotes: Adding Quote");
//        primaryStage.setScene(add);
//        add.setOnKeyPressed(e -> addCtrl.keyPressed(e));
//    }
}