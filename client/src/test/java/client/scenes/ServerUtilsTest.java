package client.scenes;

import client.utils.ServerUtils;
import client.utils.UserPreferences;
import commons.Card;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ServerUtilsTest {
    private ServerUtils serverUtils;

    private UserPreferences prefs;
    @BeforeEach
    public void setup() {
        prefs = new UserPreferences();
        serverUtils = new ServerUtils(prefs);
    }


    /**
     * Test that a card can be added to the server
     */
    @Test
    public void addCardTest() {
        Card c = new Card("title", "description", "colour", null, null, null, null);
        serverUtils.addCard(c);

        assertTrue(serverUtils.getCards(0, 0).contains(c));
    }
}
