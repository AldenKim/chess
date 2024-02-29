package databaseTests;

import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import dataAccess.MySQLGameDAO;
import model.GameData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

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
    public void testCreateGamePositive() throws DataAccessException {

    }
}
