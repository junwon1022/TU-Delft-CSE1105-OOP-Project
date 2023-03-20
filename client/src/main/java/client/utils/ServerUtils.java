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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import commons.Board;
import commons.Card;
import commons.ListOfCards;
import org.glassfish.jersey.client.ClientConfig;

import commons.Quote;
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

    private static final String SERVER = "http://localhost:8080/";

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
    List<ListOfCards> serverData = null;

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
        return serverData;
    }

    /**
     * Get boards from server
     *
     * @param boardId
     * @return boards
     */
    public Board getBoard(long boardId){
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/boards/" + boardId)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
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
     * @param boardId
     * @return - the lists from the server
     */
    public ListOfCards getList(long boardId){
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("/api/boards/" + boardId +"/lists")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<ListOfCards>(){});
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

    private StompSession session = connect("ws://localhost:8080/websocket");

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
}