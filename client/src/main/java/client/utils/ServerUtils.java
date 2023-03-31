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
package client.utils;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import com.google.inject.Inject;
import commons.*;
import org.glassfish.jersey.client.ClientConfig;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

public class ServerUtils {

    private final UserPreferences prefs;

    private static String SERVER = "http://localhost:8080/";

    private static String SERVER_ADDRESS = "localhost:8080";

    /**
     * ServerUtils constructor
     * @param prefs
     */
    @Inject
    public ServerUtils(UserPreferences prefs) {
        this.prefs = prefs;
    }

    /**
     * @return the address of the server
     */
    public String getServerAddress() {
        return SERVER;
    }

    /**
     * Changes the preset server adress from 8080 to the textbox input
     * @param server
     */
    public void changeServer(String server) throws Exception {

        if(server == null || server.equals("")) {
            throw new Exception("Please enter a valid server to connect" +
                    " or select one from the list with double click!");
        }
        if(server.charAt(server.length() - 1) != '/')  {
            server = server + "/";
        }

        this.SERVER = server;
        this.SERVER_ADDRESS = server;

        //removes the http so that websockets can be accessed
        if(server.contains("http")) SERVER_ADDRESS = server.substring(7);

        System.out.println(SERVER);
        try {
            //check that the server is connectable to web sockets
            connect("ws://" + SERVER_ADDRESS + "websocket");
        }
        catch(Exception e) {
            throw new Exception("This is not a valid server! Please try again!");
        }
        try {
            //checks if the server is valid,
            // that is if it is able to make a dummy request to the api
            String check = checkServer(SERVER);
            if (!check.contains("TimeWise Server"))
                throw new Exception("Not a TimeWise Server");
        }
        catch(Exception e){
            throw new Exception("This is not a TimeWise server! Please try again!");
        }

    }

    /**
     * Get all quotes from the server.
     */
    public void getQuotesTheHardWay() throws IOException {
        var url = new URL("http://localhost:8080/api/quotes");
        var is = url.openConnection().getInputStream();
        var br = new BufferedReader(new InputStreamReader(is));
        String line;
        while ((line = br.readLine()) != null) {
            System.out.println(line);
        }
    }

    /**
     * Get all quotes from the server.
     *
     * @return The quotes.
     */
    public List<Quote> getQuotes() {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER).path("api/quotes") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .get(new GenericType<List<Quote>>() {});
    }

    /**
     * Add a quote to the server.
     *
     * @param quote The quote to add.
     *
     * @return The quote that was added.
     */
    public Quote addQuote(Quote quote) {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER).path("api/quotes") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .post(Entity.entity(quote, APPLICATION_JSON), Quote.class);
    }

    /**
     * Placeholder serverData until connection is made.
     */
    private List<ListOfCards> serverData = null;

    //Data related to board titles (How the boards are displayed on the main screen)
    private List<Board> boardData = null;



    /**
     * Placeholder add card function.
     * @param card the card to add
     */
    public void addCard(Card card) {
        for (ListOfCards list: serverData)
            if (card.list.equals(list))
                list.cards.add(card);
    }


    /**
     * Placeholder add card function.
     * @param boardTitle the board title to add
     */
    public void addBoardTitle(Board boardTitle) {
        if(boardData == null) {
            boardData = new ArrayList<>();
        }
        Board b = boardTitle;
        addBoard(b);
        //Gets the id from the last board added
        boardTitle = getBoards().get(getBoards().size()-1);
        boardData.add(boardTitle);

    }

    /**
     * Placeholder/ method for the delete card function,
     * the actual method should follow the logic behind this
     * @param card - card to be deleted
     */
    public void deleteCard(Card card){
        int listNo = 0;
        int cardNo = 0;

        boolean found = false;
        if(serverData != null){
            for(ListOfCards list: serverData){
                cardNo = 0;
                for(Card c: list.cards){
                    if(c.equals(card)) {
                        found = true;
                        break;
                    }
                    cardNo++;
                }
                if(found)
                    break;
                listNo++;
            }
            serverData.get(listNo).cards.remove(cardNo);
        }

    }

    /**
     * Placeholder/ method for the delete board function,
     * the actual method should follow the logic behind this
     * @param board - board to be deleted
     */
    public void deleteBoard(Board board){
        int number = 0;
        boolean found = false;
        if(boardData != null){
            for(Board b: boardData){
                if(b.equals(board)) {
                    found = true;
                    break;
                }
                number++;
            }
        }
        if(found == true)
            boardData.remove(number);

    }







    /**
     * Placeholder method to get data from server
     * @param l - the list which needs a new name
     */
    public void addList(ListOfCards l){
        if(serverData == null)
            serverData = new ArrayList<>();
        serverData.add(l);
    }

    /**
     * Placeholder/ method for the delete card function,
     * the actual method should follow the logic behind this
     * @param l - card to be deleted
     */
    public void deleteList(ListOfCards l){
        int listNo = 0;
        if(serverData != null){
            for(ListOfCards list: serverData){
                if(list.equals(l))
                    break;
                listNo++;
            }
            serverData.remove(listNo);
        }
    }

    /**
     * Method that renames the list locally,
     * still needs to be implemented with database connectivity.
     * The method shows the logic that should be implemented.
     * @param l - the list which needs a new name
     * @param newTitle - the new title of said list
     */
    public void renameList1(ListOfCards l, String newTitle){
        int listNo = 0;
        if(serverData != null){
            for(ListOfCards list: serverData){
                if(list.equals(l))
                    break;
                listNo++;
            }
            serverData.get(listNo).title = newTitle;
        }

    }

    /**
     * Moves a card in a list
     * from index fromIdx to index toIdx
     * @param list list of cards
     * @param fromIdx index to move from
     * @param toIdx index to move to
     */
    public void moveCard(ListOfCards list, int fromIdx, int toIdx) {
        ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/boards/" + list.board.id
                        + "/lists/" + list.id
                        + "/from/" + fromIdx
                        + "/to/" + toIdx)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .put(Entity.json(""));
    }



    /**
     * Method that returns the lists from the database
     * @param boardId - the id of the board of the lists
     * @return a list containing all lists of cards
     */
    public List<ListOfCards> getLists(long boardId){
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("/api/boards/" + boardId +"/lists")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<List<ListOfCards>>(){});
    }


    /**
     * Method that returns the lists from the database
     * @param serverUrl - the url of th server
     * @return a list containing all lists of cards
     */
    public String checkServer(String serverUrl){
        System.out.println("The Url is " + URLEncoder.encode(SERVER,StandardCharsets.UTF_8));


        try {
            return ClientBuilder.newClient(new ClientConfig())
                    .target(SERVER).path("/api/checkServer/")
                    .request(APPLICATION_JSON)
                    .accept(APPLICATION_JSON)
                    .get(new GenericType<String>() {
                    });
        }
        catch(Exception e){
            System.out.println("The exception is " + e);
            throw new RuntimeException();
        }
    }

    /**
     * Placeholder method to get data from server
     * @param boardId the id of the board
     * @return a list of cardlists.
     */
    public List<ListOfCards> getServerData(long boardId) {

        // once the database references are solved
        // -- as when the get method retrieves the list of all lists
        //the reference to the board is null --
        //this part can be uncommented
        // -- the method signature will get as a parameter
        //Board board --
        //
        serverData = getLists(boardId);
        for (var list: serverData)
            list.cards.sort(Comparator.comparingLong(Card::getOrder));
        return serverData;
    }

    /**
     * Placeholder method to get data from server
     * @return a list of board title objects.
     */

    public List<Board> getMyBoardTitles(){

        if(boardData == null) {
            boardData = new ArrayList<>();
        }
        return boardData;
    }


    /**
     * Placeholder method to get data from server
     * @return a list of board title objects.
     */

    public List<Board> getMyServerBoardTitles(){

        if(boardData == null) {
            boardData = new ArrayList<>();
        }
        boardData = getBoards();
        return boardData;
    }
    /**
     * Get boards from server
     *
     * @param boardId
     * @return boards
     */
    public Board getBoard(long boardId) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/boards/" + boardId)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<Board>() {});
    }

    /**
     * Get boards from server based on key
     * @param key
     * @return boards
     */
    public Board getBoardByKey(String key){

        return ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER).path("api/boards/getByKey/" + key) //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .get(new GenericType<Board>() {});
    }


    /**
     * Add a new board to the server
     * @param board - board to be added to server
     * @return - the board that was added
     */
    public Board addBoard(Board board){
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/boards")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(board, APPLICATION_JSON), Board.class);
    }

    /**
     * Get all lists from the server
     *
     * @return - the lists from the server
     * @param boardId
     */
    public ListOfCards getList(long boardId){
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("/api/boards/" + boardId +"/lists")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<ListOfCards>(){});
    }

    /**
     * Get all lists from the server
     * @return - the lists from the server
     */
    public List<Board> getBoards(){
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("/api/boards")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<List<Board>>(){});
    }


    /**
     * Add a new list to the server
     * @param list - the list that needs to be added to the server
     * @return the added list to the server
     */
    public ListOfCards addListOfCards(ListOfCards list){
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("/api/boards/" + list.board.id + "/lists")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(list, APPLICATION_JSON), ListOfCards.class);
    }

    /**
     * Method that gets the cards from the server from a certain list
     * @return a list of the cards
     */
    public List<Card> getCards(){
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER).path("/api/boards/{board_id}/lists/{list_id}/cards") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .get(new GenericType<List<Card>>() {});
    }

    /**
     * Add a new card to the server
     * @param card - the card that needs to be added to the server
     * @return - the added card
     */
    public Card addCard2(Card card){
        long boardId = card.list.board.id;
        long listId = card.list.id;
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("/api/boards/" + boardId +"/lists/"+ listId + "/cards")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(card, APPLICATION_JSON), Card.class);
    }

    /**
     * Removes a card from the server
     * @param card the card that needs to be removed
     * @return - the removed card
     */
    public Card removeCard(Card card){
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("/api/boards/{board_id}/lists/{list_id}/cards/{card_id}")
                .resolveTemplate("board_id", card.list.board.id)
                .resolveTemplate("list_id", card.list.id)
                .resolveTemplate("card_id", card.id)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .delete(Card.class);
    }


    /**
     * Removal of List from server
     *
     * @param list
     * @return - return the removed List
     */
    public ListOfCards removeList(ListOfCards list){
        long boardId = list.board.id;
        long listId = list.id;
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/boards/" + boardId + "/lists/" + listId)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .delete(ListOfCards.class);
    }

    /**
     * gets the description of a card from the database
     * @param card the card to get the description from
     * @return the description
     */
    public String getDescription(Card card) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("/api/boards/{board_id}" +
                      "/lists/{list_id}/cards/{card_id}/description")
                .resolveTemplate("board_id", card.list.board.id)
                .resolveTemplate("list_id", card.list.id)
                .resolveTemplate("card_id", card.id)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<String>() {});
    }

    /**
     * Get a card from the server
     * @return the card we need
     */
    public Card getCard(){
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER).path("/api/boards/{board_id}/lists/{list_id}/cards/{card_id}") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .get(new GenericType<Card>() {});
    }

    /**
     * Renaming a card with a new title
     * @param card - the card that needs renaming
     * @param title - the new title of the card
     * @return the Card that was renamed
     */
    public Card renameCard(Card card, String title){

        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("/api/boards/{board_id}/lists/{list_id}/cards/{card_id}")
                .resolveTemplate("board_id", card.list.board.id)
                .resolveTemplate("list_id", card.list.id)
                .resolveTemplate("card_id", card.id)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .put(Entity.entity(title, APPLICATION_JSON), Card.class);
    }

    /**
     * Rename a list with a new title
     * @param list - the list that needs to be renamed
     * @param title - the title of the new list
     * @return the list with the new name
     */
    public ListOfCards renameList(ListOfCards list, String title){
        long boardId = list.board.id;
        long listId = list.id;
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/boards/" + boardId + "/lists/" + listId)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .put(Entity.entity(title, APPLICATION_JSON), ListOfCards.class);
    }

    /**
     * Sends the request to update the description to the specific url,
     * @param card the card that the description is to be updated
     * @param description the new description
     * @return the updated card
     */
    public Card updateDescription(Card card, String description){
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("/api/boards/{board_id}/lists/{list_id}/cards" +
                        "/{card_id}/description")
                .resolveTemplate("board_id", card.list.board.id)
                .resolveTemplate("list_id", card.list.id)
                .resolveTemplate("card_id", card.id)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .put(Entity.entity(description, APPLICATION_JSON), Card.class);
    }

    private StompSession session = connect("ws://" +  SERVER_ADDRESS + "/websocket");



    /**
     * Creates a StompSession to connect the client to the server from the specified url
     * @param url The url the session will connect to
     * @return the created StompSession
     * @throws RuntimeException if there is an error when the connection to the url is being made
     * @throws IllegalStateException if there is an unexpected error
     */
    private StompSession connect(String url) {
        var client = new StandardWebSocketClient();
        var stomp = new WebSocketStompClient(client);
        stomp.setMessageConverter(new MappingJackson2MessageConverter());
        try {
            return stomp.connect(url, new StompSessionHandlerAdapter() {}).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        throw new IllegalStateException();
    }

    /**
     * Every time a new message is received, it is deserialized into the generic type and registers
     * a consumer to receive messages
     * @param dest destination of the subscription
     * @param type type of the message payload
     * @param consumer consumer that is informed every time there is a new message
     * @param <T> Generic type
     * @throws ResponseStatusException If any of the parameters are null
     */
    public <T> void registerForMessages(String dest, Class<T> type, Consumer<T> consumer) {
        try {
            session.subscribe(dest, new StompFrameHandler() {

                @Override
                public Type getPayloadType(StompHeaders headers) {
                    return type;
                }

                @SuppressWarnings("unchecked")
                @Override
                public void handleFrame(StompHeaders headers, Object payload) {
                    consumer.accept((T) payload);
                }
            });
        } catch (IllegalArgumentException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Arguments");
        }
    }

    /**
     * Sends an object to the specified destination if there is a connection made
     * @param dest destination where the object is going to be sent
     * @param o object to be sent
     * @throws IllegalArgumentException if the destination is null
     */
    public void send(String dest, Object o) {
        if (dest == null) {
            throw new IllegalArgumentException("Destination cannot be null");
        }
        session.send(dest, o);
    }
    /**
     *
     * @param board
     * @param newTitle
     * @return The renamed board
     */
    public Board renameBoard(Board board, String newTitle) {
        long boardId = board.id;
        //Puts the board into the databse
        Board b = ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/boards/" + boardId)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .put(Entity.entity(newTitle, APPLICATION_JSON), Board.class);
        boardData.remove(board);
        boardData.add(b);

        return b;
    }

    /**
     * Method to rename a board
     * @param board
     * @param newTitle
     * @return The renamed board
     */
    public PreferencesBoardInfo renameBoard(PreferencesBoardInfo board, String newTitle) {
        // Get board associated with this PreferencesBoardInfo
        Board actualBoard = getBoardByKey(board.getKey());

        //Puts the board with the new title into the database
        long boardId = actualBoard.id;
        Board b = ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/boards/" + boardId)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .put(Entity.entity(newTitle, APPLICATION_JSON), Board.class);

        // Update the board title in the preferences
        return prefs.updateBoardTitle(getServerAddress(), board, newTitle);
    }



    /**
     *
     * @param board
     * @param newTitle
     * @return The renamed board
     */
    public Board renameServerBoard(Board board, String newTitle) {
        long boardId = board.id;
        //Puts the board into the databse
        Board b = ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/boards/" + boardId)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .put(Entity.entity(newTitle, APPLICATION_JSON), Board.class);


        return b;
    }


    /**
     * Removal of Board from server
     *
     * @param board
     * @return - return the removed List
     */
    public Board removeBoard(Board board){
        long boardId = board.id;
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/boards/" + boardId)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .delete(Board.class);
    }


}
