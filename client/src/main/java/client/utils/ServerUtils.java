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
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import commons.Board;
import commons.Card;
import commons.ListOfCards;
import org.glassfish.jersey.client.ClientConfig;

import commons.Quote;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;

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
    ArrayList<ListOfCards> serverData = null;

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
     * Placeholder method to get data from server
     * @return a list of cardlists.
     */
    public List<ListOfCards> getServerData() {
        if (serverData == null) {
            Board board = new Board();

            ListOfCards list1 = new ListOfCards("List 1", "red", board, new ArrayList<>());
            list1.cards.add(new Card("Card 1", "desc", "red", list1, null, null));
            list1.cards.add(new Card("Card 2", "desc", "red", list1, null, null));

            ListOfCards list2 = new ListOfCards("List 2", "red", board, new ArrayList<>());
            list2.cards.add(new Card("Card 3", "desc", "red", list2, null, null));
            list2.cards.add(new Card("Card 4", "desc", "red", list2, null, null));

            serverData = new ArrayList<>(List.of(list1, list2));
        }

        return serverData;
    }
}