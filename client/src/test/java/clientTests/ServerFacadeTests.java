package clientTests;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.*;
import server.Server;
import ui.ServerFacade;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;
    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(8080);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    public void clearDatabase() {
        try {
            URL url = new URL("http://localhost:8080" + "/db");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE");
            conn.connect();

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                System.out.println("Database cleared successfully!");
            } else {
                InputStreamReader inputStreamReader = new InputStreamReader(conn.getErrorStream());
                JsonObject errorResponse = JsonParser.parseReader(inputStreamReader).getAsJsonObject();
                String errorMessage = errorResponse.get("message").getAsString();
                System.out.println("Failed to clear database: " + errorMessage);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void positiveLoginTest() {
        String authToken = facade.register("validUsername", "validPassword", "validEmail");
        Assertions.assertNotNull(authToken);

        boolean logoutSuccess = facade.logout(authToken);
        Assertions.assertTrue(logoutSuccess);

        authToken = facade.login("validUsername", "validPassword");
        Assertions.assertNotNull(authToken);
    }

    @Test
    public void negativeLoginTest() {
        String authToken = facade.login("invalid", "invalidPassword");
        Assertions.assertNull(authToken);
    }

    @Test
    public void positiveRegisterTest() {
        String authToken = facade.register("validUsername", "validPassword", "validEmail");
        Assertions.assertNotNull(authToken);
    }

    @Test
    public void negativeRegisterTest() {
        String authToken = facade.register("existing", "existing", "validEmail");
        Assertions.assertNotNull(authToken);

        authToken = facade.register("existing", "existing", "validEmail");
        Assertions.assertNull(authToken);
    }

    @Test
    public void positiveLogoutTest() {
        String authToken = facade.register("validUsername", "validPassword", "validEmail");
        Assertions.assertNotNull(authToken);

        boolean logoutSuccess = facade.logout(authToken);
        Assertions.assertTrue(logoutSuccess);
    }

    @Test
    public void negativeLogoutTest() {
        boolean logoutSuccess = facade.logout("invalidAuthToken");
        Assertions.assertFalse(logoutSuccess);
    }

    @Test
    public void positiveCreateGameTest() {
        String authToken = facade.register("validUsername", "validPassword", "validEmail");
        Assertions.assertNotNull(authToken);

        boolean gameCreationSuccess = facade.createGame(authToken, "TestGame");
        Assertions.assertTrue(gameCreationSuccess);
    }

    @Test
    public void negativeCreateGameTest() {
        boolean gameCreationSuccess = facade.createGame("invalidAuthToken", "TestGame");
        Assertions.assertFalse(gameCreationSuccess, "Game creation should fail without a valid authentication token.");
    }

    @Test
    public void positiveListGamesTest() {
        String authToken = facade.register("validUsername", "validPassword", "validEmail");
        Assertions.assertNotNull(authToken);

        facade.createGame(authToken, "TestGame"); // Creating a game for testing purposes
        facade.listGames(authToken);
        Assertions.assertDoesNotThrow(() -> facade.listGames(authToken));
    }

    @Test
    public void negativeListGamesTest() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        facade.listGames("invalidAuthToken");

        System.setOut(System.out);

        String printedMessage = outputStream.toString();

        Assertions.assertTrue(printedMessage.contains("Failed"));
    }

    @Test
    public void positiveJoinGameTest() {
        String authToken = facade.register("validUsername", "validPassword", "validEmail");
        Assertions.assertNotNull(authToken);

        boolean gameCreationSuccess = facade.createGame(authToken, "Test");
        Assertions.assertTrue(gameCreationSuccess);

        facade.listGames(authToken);

        boolean joinGameSuccess = facade.joinGame(1, "WHITE", authToken);
        Assertions.assertTrue(joinGameSuccess);
    }

    @Test
    public void negativeJoinGameTest() {
        String authToken = facade.register("validUsername", "validPassword", "validEmail");
        Assertions.assertNotNull(authToken);

        boolean joinGameSuccess = facade.joinGame(1000, "white", "invalidAuthToken");
        Assertions.assertFalse(joinGameSuccess);
    }

    @Test
    public void positiveJoinObserverTest() {
        String authToken = facade.register("validUsername", "validPassword", "validEmail");
        Assertions.assertNotNull(authToken);

        boolean gameCreationSuccess = facade.createGame(authToken, "Test1");
        Assertions.assertTrue(gameCreationSuccess);

        facade.listGames(authToken);

        boolean joinGameSuccess = facade.joinObserver(1, authToken);
        Assertions.assertTrue(joinGameSuccess);
    }

    @Test
    public void negativeJoinObserverTest() {
        String authToken = facade.register("validUsername", "validPassword", "validEmail");
        Assertions.assertNotNull(authToken);

        boolean joinGameSuccess = facade.joinObserver(1000, "invalidAuthToken");
        Assertions.assertFalse(joinGameSuccess);
    }
}
