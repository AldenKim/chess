package dataaccess;

import chess.ChessGame;
import model.GameData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameDAODatabaseTests {
    private GameDAO gameDAO;
    private GameDAO memGameDAO;

    @BeforeEach
    public void setUp() throws DataAccessException {
        gameDAO = new MySQLGameDAO();
        memGameDAO = new MemoryGameDAO();
        gameDAO.clear();
    }

    @AfterEach
    public void tearDown() throws DataAccessException {
        try {
            gameDAO.clear();
            memGameDAO.clear();
        } catch (DataAccessException e) {
            fail("Exception thrown during clearance: " + e.getMessage());
        }
    }

    @Test
    public void positiveClearTest() throws DataAccessException {
        GameData game1 = new GameData(1, "P1", "P2", "G1", new ChessGame());
        GameData game2 = new GameData(2, "P3", "P4", "G2", new ChessGame());

        gameDAO.createGame(game1);
        gameDAO.createGame(game2);
        memGameDAO.createGame(game1);
        memGameDAO.createGame(game2);

        assertEquals(2, gameDAO.listGames().length);
        assertEquals(2, memGameDAO.listGames().length);

        gameDAO.clear();
        memGameDAO.clear();

        assertEquals(0,gameDAO.listGames().length);
        assertEquals(0,memGameDAO.listGames().length);
    }

    @Test
    public void positiveCreateGameTest() throws DataAccessException {
        GameData gameData = new GameData(1, "Player1", "Player2", "Game 1", new ChessGame());

        gameDAO.createGame(gameData);
        memGameDAO.createGame(gameData);

        GameData retrievedGameData = gameDAO.getGame(1);
        GameData retrievedGameData2 = memGameDAO.getGame(1);

        assertEquals(gameData.gameID(), retrievedGameData.gameID());
        assertEquals(gameData.whiteUsername(), retrievedGameData.whiteUsername());
        assertEquals(gameData.blackUsername(), retrievedGameData.blackUsername());
        assertEquals(gameData.gameName(), retrievedGameData.gameName());

        assertEquals(gameData.gameID(), retrievedGameData2.gameID());
        assertEquals(gameData.whiteUsername(), retrievedGameData2.whiteUsername());
        assertEquals(gameData.blackUsername(), retrievedGameData2.blackUsername());
        assertEquals(gameData.gameName(), retrievedGameData2.gameName());
    }

    @Test
    public void negativeCreateGameTest() throws DataAccessException {
        GameData gameData1 = new GameData(1, "Player1", "Player2", "Game 1", new ChessGame());

        gameDAO.createGame(gameData1);
        memGameDAO.createGame(gameData1);

        GameData gameData2 = new GameData(1, "Player3", "Player4", "Game 2", new ChessGame());
        assertThrows(DataAccessException.class, () -> gameDAO.createGame(gameData2));
        assertThrows(DataAccessException.class, () -> memGameDAO.createGame(gameData2));
    }

    @Test
    public void positiveGetGameTest() throws DataAccessException {
        ChessGame testGame = new ChessGame();
        GameData testData = new GameData(1, "white", "black", "Test Game", testGame);
        gameDAO.createGame(testData);
        memGameDAO.createGame(testData);

        GameData retrievedData = gameDAO.getGame(1);
        GameData retrievedData2 = memGameDAO.getGame(1);

        assertNotNull(retrievedData);
        assertEquals(1, retrievedData.gameID());
        assertEquals("white", retrievedData.whiteUsername());
        assertEquals("black", retrievedData.blackUsername());
        assertEquals("Test Game", retrievedData.gameName());
        assertNotNull(retrievedData.game());

        assertNotNull(retrievedData2);
        assertEquals(1, retrievedData2.gameID());
        assertEquals("white", retrievedData2.whiteUsername());
        assertEquals("black", retrievedData2.blackUsername());
        assertEquals("Test Game", retrievedData2.gameName());
        assertNotNull(retrievedData2.game());
    }

    @Test
    public void negativeGetGameTest() throws DataAccessException {
        int nonExistentGame = -1;
        assertNull(gameDAO.getGame(nonExistentGame));
        assertNull(memGameDAO.getGame(nonExistentGame));
    }

    @Test
    public void positiveListGamesTest() throws DataAccessException {
        ChessGame testGame1 = new ChessGame();
        GameData testData1 = new GameData(1, "white1", "black1", "Test Game 1", testGame1);
        gameDAO.createGame(testData1);
        memGameDAO.createGame(testData1);

        ChessGame testGame2 = new ChessGame();
        GameData testData2 = new GameData(2, "white2", "black2", "Test Game 2", testGame2);
        gameDAO.createGame(testData2);
        memGameDAO.createGame(testData2);

        GameData[] games = gameDAO.listGames();
        GameData[] games2 = memGameDAO.listGames();

        assertNotNull(games);
        assertEquals(2, games.length);
        assertEquals(games[0].gameID(),testData1.gameID());
        assertEquals(games[1].gameID(), testData2.gameID());

        assertNotNull(games2);
        assertEquals(2, games2.length);
        assertEquals(games2[0].gameID(),testData1.gameID());
        assertEquals(games2[1].gameID(), testData2.gameID());
    }

    @Test
    public void negativeListGamesTest() throws DataAccessException {
        gameDAO.clear();
        memGameDAO.clear();

        GameData[] games = gameDAO.listGames();
        GameData[] games2 = gameDAO.listGames();

        assertNotNull(games);
        assertEquals(0, games.length);

        assertNotNull(games2);
        assertEquals(0, games2.length);
    }

    @Test
    public void positiveUpdateGameTest() throws DataAccessException {
        ChessGame testGame = new ChessGame();
        GameData testData = new GameData(1, "white1", "black1", "Test Game", testGame);
        gameDAO.createGame(testData);
        memGameDAO.createGame(testData);

        GameData newData = new GameData(1, "white1", "black1", "New", testGame);
        gameDAO.updateGame(1, newData);
        memGameDAO.updateGame(1, newData);

        GameData updatedData = gameDAO.getGame(1);
        GameData updatedData2 = memGameDAO.getGame(1);

        assertNotNull(updatedData);
        assertEquals("New", updatedData.gameName());

        assertNotNull(updatedData2);
        assertEquals("New", updatedData2.gameName());
    }

    @Test
    public void negativeUpdateGameTest() throws DataAccessException {
        ChessGame testGame = new ChessGame();
        GameData testData = new GameData(999, "white1", "black1", "Test Game", testGame);

        gameDAO.createGame(testData);
        assertNotNull(gameDAO.getGame(999));

        memGameDAO.createGame(testData);
        assertNotNull(memGameDAO.getGame(999));
    }
}
