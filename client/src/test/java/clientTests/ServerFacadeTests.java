package clientTests;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dataAccess.AuthDAO;
import org.junit.jupiter.api.*;
import server.Server;
import ui.ServerFacade;

import java.io.IOException;
import java.io.InputStreamReader;
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
}
