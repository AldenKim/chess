package service;

import dataaccess.*;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requests.JoinGameRequest;
import results.JoinGameResult;

import static org.junit.jupiter.api.Assertions.*;

public class JoinGameServiceTest {
    private GameDAO gameDAO;
    private AuthDAO authDAO;
    private JoinGameService joinGameService;

    @BeforeEach
    public void setUp() {
        this.gameDAO = new MemoryGameDAO();
        this.authDAO = new MemoryAuthDAO();
        this.joinGameService = new JoinGameService(gameDAO, authDAO);

        GameData testGame = new GameData(1, "testUser", null, "Test Game", null);
        try {
            gameDAO.createGame(testGame);
        } catch (DataAccessException e) {
            fail("Exception thrown during setup: " + e.getMessage());
        }
    }

    @Test
    public void positiveJoinGameServiceTest() {
        AuthData authToken = new AuthData("testToken", "testUser");
        try {
            authDAO.createAuth(authToken);
        } catch (DataAccessException e) {
            fail("Exception thrown during setup: " + e.getMessage());
        }

        JoinGameRequest request = new JoinGameRequest("BLACK",1);

        JoinGameResult result = null;
        try {
            result = joinGameService.joinGame(authToken.authToken(), request);
        } catch (DataAccessException e) {
            fail("Exception thrown during game joining: " + e.getMessage());
        }

        assertNotNull(result);
        assertNull(result.message());
    }

    @Test
    public void negativeJoinGameServiceTest() {
        AuthData authToken = new AuthData("testToken", "testUser");
        try {
            authDAO.createAuth(authToken);
        } catch (DataAccessException e) {
            fail("Exception thrown during setup: " + e.getMessage());
        }

        JoinGameRequest request = new JoinGameRequest("WHITE",1);

        JoinGameResult result = null;
        try {
            result = joinGameService.joinGame(authToken.authToken(), request);
        } catch (DataAccessException e) {
            fail("Exception thrown during game joining: " + e.getMessage());
        }

        assertNotNull(result);
        assertNotNull(result.message());
        assertEquals("Error: Game already taken", result.message());
    }

}
