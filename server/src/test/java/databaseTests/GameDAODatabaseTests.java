package databaseTests;

import chess.ChessGame;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import dataAccess.MySQLGameDAO;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameDAODatabaseTests {
    private GameDAO gameDAO;

    @BeforeEach
    public void setUp() throws DataAccessException {
        gameDAO = new MySQLGameDAO();
        gameDAO.clear();
    }

    @AfterEach
    public void tearDown() throws DataAccessException {
        try {
            gameDAO.clear();
        } catch (DataAccessException e) {
            fail("Exception thrown during clearance: " + e.getMessage());
        }
    }

    @Test
    public void positiveClearTest() throws DataAccessException {
        GameData game1 = new GameData(1, "P1", "P2", "G1", null);
        GameData game2 = new GameData(2, "P3", "P4", "G2", null);

        gameDAO.createGame(game1);
        gameDAO.createGame(game2);

        assertEquals(2, gameDAO.listGames().length);

        gameDAO.clear();

        assertEquals(0,gameDAO.listGames().length);
    }

    @Test
    public void positiveCreateGameTest() throws DataAccessException {
        GameData gameData = new GameData(1, "Player1", "Player2", "Game 1", null);

        gameDAO.createGame(gameData);

        GameData retrievedGameData = gameDAO.getGame(1);

        assertEquals(gameData.gameID(), retrievedGameData.gameID());
        assertEquals(gameData.whiteUsername(), retrievedGameData.whiteUsername());
        assertEquals(gameData.blackUsername(), retrievedGameData.blackUsername());
        assertEquals(gameData.gameName(), retrievedGameData.gameName());
    }

    @Test
    public void negativeCreateGameTest() throws DataAccessException {
        GameData gameData1 = new GameData(1, "Player1", "Player2", "Game 1", null);

        gameDAO.createGame(gameData1);

        GameData gameData2 = new GameData(1, "Player3", "Player4", "Game 2", null);
        assertThrows(DataAccessException.class, () -> gameDAO.createGame(gameData2));
    }

    @Test
    public void positiveGetGameTest() throws DataAccessException {
        ChessGame testGame = new ChessGame();
        GameData testData = new GameData(1, "white", "black", "Test Game", testGame);
        gameDAO.createGame(testData);

        GameData retrievedData = gameDAO.getGame(1);

        assertNotNull(retrievedData);
        assertEquals(1, retrievedData.gameID());
        assertEquals("white", retrievedData.whiteUsername());
        assertEquals("black", retrievedData.blackUsername());
        assertEquals("Test Game", retrievedData.gameName());
        assertNotNull(retrievedData.game());
    }
}
