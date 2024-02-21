package serviceTests;

import dataAccess.*;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import results.ListGamesResult;
import service.ListGamesService;

import static org.junit.jupiter.api.Assertions.*;

public class ListGamesServiceTest {
    private GameDAO gameDAO;
    private AuthDAO authDAO;
    private ListGamesService listGamesService;

    @BeforeEach
    public void setUp() {
        this.gameDAO = new MemoryGameDAO();
        this.authDAO = new MemoryAuthDAO();
        this.listGamesService = new ListGamesService(gameDAO, authDAO);
    }

    @Test
    public void positiveListGamesServiceTest() {
        AuthData authToken = new AuthData("testToken", "testUser");
        try{
            authDAO.createAuth(authToken);
        } catch (DataAccessException e) {
            fail("Exception thrown during setup: " + e.getMessage());
        }

        GameData game1 = new GameData(1, "user1", null, "Game 1", null);
        GameData game2 = new GameData(2, "user2", null, "Game 2", null);
        try {
            gameDAO.createGame(game1);
            gameDAO.createGame(game2);
        } catch (DataAccessException e) {
            fail("Exception thrown during setup: " + e.getMessage());
        }

        ListGamesResult result = null;
        try {
            result = listGamesService.listGames(authToken.authToken());
        } catch (DataAccessException e) {
            fail("Exception thrown during successful game listing: " + e.getMessage());
        }

        assertNotNull(result);
        assertNull(result.message());
        assertNotNull(result.games());
        assertEquals(2, result.games().length);
    }

    @Test
    public void negativeListGamesServiceTest() {
        String authToken = "invalidAuthToken";

        ListGamesResult result = null;
        try {
            result = listGamesService.listGames(authToken);
        } catch (DataAccessException e) {
            fail("Exception thrown during game listing: " + e.getMessage());
        }

        assertNotNull(result);
        assertNotNull(result.message());
        assertEquals("Error: Unauthorized", result.message());
        assertNull(result.games());
    }
}
