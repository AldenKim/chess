package service;

import dataaccess.*;
import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requests.CreateGameRequest;
import results.CreateGameResult;

import static org.junit.jupiter.api.Assertions.*;

public class CreateGameServiceTest {
    private GameDAO gameDAO;
    private AuthDAO authDAO;
    private CreateGameService createGameService;

    @BeforeEach
    public void setUp() {
        this.gameDAO = new MemoryGameDAO();
        this.authDAO = new MemoryAuthDAO();
        this.createGameService = new CreateGameService(gameDAO, authDAO);
    }

    @Test
    public void positiveCreateGameServiceTest() {
        AuthData authToken = new AuthData("testToken", "testUser");
        try {
            authDAO.createAuth(authToken);
        } catch (DataAccessException e) {
            fail("Exception thrown during setup: " + e.getMessage());
        }

        CreateGameRequest request = new CreateGameRequest("testGame");

        CreateGameResult result = null;
        try {
            result = createGameService.createGame(authToken.authToken(), request);
        } catch (DataAccessException e) {
            fail("Exception thrown during game creation: " + e.getMessage());
        }

        assertNotNull(result);
        assertNull(result.message());
        assertNotNull(result.gameID());
    }

    @Test
    public void negativeCreateGameServiceTest() {
        String authToken = "invalidToken";

        CreateGameRequest request = new CreateGameRequest("testGame");

        CreateGameResult result = null;
        try {
            result = createGameService.createGame(authToken, request);
        } catch (DataAccessException e) {
            fail("Exception thrown during game creation: " + e.getMessage());
        }

        assertNotNull(result);
        assertNull(result.gameID());
        assertNotNull(result.message());
        assertEquals("Error: Unauthorized", result.message());
    }
}
