package dataaccess;

import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserDAODatabaseTests {
    private MySQLUserDAO userDAO;
    private MemoryUserDAO memoryUserDAO;
    @BeforeEach
    public void setUp() throws DataAccessException {
        userDAO = new MySQLUserDAO();
        memoryUserDAO = new MemoryUserDAO();
        userDAO.clear();
    }

    @AfterEach
    public void tearDown() {
        try {
            userDAO.clear();
            memoryUserDAO.clear();
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
        memoryUserDAO.createUser(user1);
        memoryUserDAO.createUser(user2);

        assertEquals(user1.username(), userDAO.getUser("testUser1").username());
        assertEquals(user2.username(), userDAO.getUser("testUser2").username());
        assertEquals(user1.username(), memoryUserDAO.getUser("testUser1").username());
        assertEquals(user2.username(), memoryUserDAO.getUser("testUser2").username());

        userDAO.clear();
        memoryUserDAO.clear();

        assertNull(userDAO.getUser("testUser1"));
        assertNull(userDAO.getUser("testUser2"));
        assertNull(memoryUserDAO.getUser("testUser1"));
        assertNull(memoryUserDAO.getUser("testUser2"));
    }

    @Test
    public void positiveCreateUserTest() throws DataAccessException {
        UserData user = new UserData("testUser1", "password1", "email@example.com");
        userDAO.createUser(user);
        memoryUserDAO.createUser(user);

        UserData retrievedUser = userDAO.getUser("testUser1");
        UserData retrievedUser2 = memoryUserDAO.getUser("testUser1");

        assertEquals(user.username(), retrievedUser.username());
        assertEquals(user.email(), retrievedUser.email());
        assertNotNull(retrievedUser.password());
        assertNotNull(retrievedUser);

        assertEquals(user.username(), retrievedUser2.username());
        assertEquals(user.email(), retrievedUser2.email());
        assertNotNull(retrievedUser2.password());
        assertNotNull(retrievedUser2);
    }

    @Test
    public void negativeCreateUserTest() throws DataAccessException {
        UserData user1 = new UserData("testUser1", "password1", "email@example.com");
        userDAO.createUser(user1);
        memoryUserDAO.createUser(user1);

        UserData user2 = new UserData("testUser1", "password2", "email2@example.com");
        assertThrows(DataAccessException.class, () -> userDAO.createUser(user2));
        assertThrows(DataAccessException.class, () -> memoryUserDAO.createUser(user2));
    }

    @Test
    public void positiveGetUserTest() throws DataAccessException {
        UserData user1 = new UserData("testUser1", "password1", "email@example.com");
        userDAO.createUser(user1);
        memoryUserDAO.createUser(user1);

        UserData retrievedUser = userDAO.getUser("testUser1");
        UserData retrievedUser2 = memoryUserDAO.getUser("testUser1");

        assertNotNull(retrievedUser.username());
        assertNotNull(retrievedUser.email());
        assertNotNull(retrievedUser.password());
        assertNotNull(retrievedUser);

        assertNotNull(retrievedUser2.username());
        assertNotNull(retrievedUser2.email());
        assertNotNull(retrievedUser2.password());
        assertNotNull(retrievedUser2);
    }

    @Test
    public void negativeGetUserTest() throws DataAccessException {
        String nonExistentUser = "nonExistent";
        assertNull(userDAO.getUser(nonExistentUser));
        assertNull(memoryUserDAO.getUser(nonExistentUser));
    }
}
