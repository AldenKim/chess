package databaseTests;

import dataAccess.DataAccessException;
import dataAccess.MySQLUserDAO;
import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserDAODatabaseTests {
    private MySQLUserDAO userDAO;

    @BeforeEach
    public void setUp() throws DataAccessException {
        userDAO = new MySQLUserDAO();
    }

    @AfterEach
    public void tearDown() {
        try {
            userDAO.clear();
        } catch (DataAccessException e) {
            fail("Exception thrown during clearance: " + e.getMessage());
        }
    }

    @Test
    public void positiveClearTest() throws DataAccessException {
        UserData user1 = new UserData("testUser1", "password1", "user1@example.com");
        UserData user2 = new UserData("testUser2", "password2", "user2@example.com");

        userDAO.createUser(user1);
        userDAO.createUser(user2);

        assertEquals(user1.username(), userDAO.getUser("testUser1").username());
        assertEquals(user2.username(), userDAO.getUser("testUser2").username());

        userDAO.clear();

        assertNull(userDAO.getUser("testUser1"));
        assertNull(userDAO.getUser("testUser2"));
    }
}
