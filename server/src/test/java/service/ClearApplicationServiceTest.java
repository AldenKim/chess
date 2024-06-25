package service;

import dataAccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ClearApplicationServiceTest {
    private UserDAO userDAO;
    private GameDAO gameDAO;
    private AuthDAO authDAO;
    private ClearApplicationService clearApplicationService;

    @BeforeEach
    public void setUp() {
        this.userDAO = new MemoryUserDAO();
        this.gameDAO = new MemoryGameDAO();
        this.authDAO = new MemoryAuthDAO();
        this.clearApplicationService = new ClearApplicationService(userDAO, gameDAO, authDAO);
    }

    @Test
    public void positiveClearApplicationServiceTest() {
        try {
            userDAO.createUser(new UserData("user1", "password1", "user1@example.com"));
            userDAO.createUser(new UserData("user2", "password2", "user2@example.com"));
            gameDAO.createGame(new GameData(1, "user1", null, "Game 1", null));
            authDAO.createAuth(new AuthData("authToken", "user1"));
        } catch (DataAccessException e) {
            fail("Exception thrown during setup: " + e.getMessage());
        }

        try {
            clearApplicationService.clearApplication();
        } catch (DataAccessException e) {
            fail("Exception thrown during clearing the application: " + e.getMessage());
        }

        try {
            assertNull(userDAO.getUser("user1"));
            assertNull(userDAO.getUser("user2"));
            assertEquals(0, gameDAO.listGames().length);
            assertNull(authDAO.getAuth("authToken"));
        } catch (DataAccessException e) {
            fail("Exception thrown during assertion: " + e.getMessage());
        }
    }
}
