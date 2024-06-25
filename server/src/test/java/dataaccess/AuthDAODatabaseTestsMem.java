package dataaccess;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.MemoryAuthDAO;
import model.AuthData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AuthDAODatabaseTestsMem {
    private AuthDAO authDAO;

    @BeforeEach
    public void setUp() throws DataAccessException {
        authDAO = new MemoryAuthDAO();
        authDAO.clear();
    }

    @AfterEach
    public void tearDown() {
        try {
            authDAO.clear();
        } catch (DataAccessException e) {
            fail("Exception thrown during clearance: " + e.getMessage());
        }
    }

    @Test
    public void positiveClearTest() throws DataAccessException {
        AuthData authData1 = new AuthData("token1", "user1");
        AuthData authData2 = new AuthData("token2", "user2");

        authDAO.createAuth(authData1);
        authDAO.createAuth(authData2);

        assertNotNull(authDAO.getAuth("token1"));
        assertNotNull(authDAO.getAuth("token2"));

        authDAO.clear();

        assertNull(authDAO.getAuth("token1"));
        assertNull(authDAO.getAuth("token2"));
    }

    @Test
    public void positiveCreateAuthTest() throws DataAccessException {
        AuthData authData = new AuthData("test_token", "test_user");

        authDAO.createAuth(authData);

        AuthData retrievedAuth = authDAO.getAuth("test_token");

        assertNotNull(retrievedAuth);
        assertEquals(authData.authToken(), retrievedAuth.authToken());
        assertEquals(authData.username(), retrievedAuth.username());
    }

    @Test
    public void negativeCreateAuthTest() throws DataAccessException{
        AuthData authData = new AuthData("test_token", "test_user");

        authDAO.createAuth(authData);

        assertThrows(DataAccessException.class, () -> {
            authDAO.createAuth(authData);
        });
    }

    @Test
    public void positiveGetAuthTest() throws DataAccessException {
        AuthData authData = new AuthData("testToken", "testUser");
        authDAO.createAuth(authData);

        AuthData retrievedAuthData = authDAO.getAuth("testToken");

        assertEquals(authData.authToken(), retrievedAuthData.authToken());
        assertEquals(authData.username(), retrievedAuthData.username());
    }

    @Test
    public void negativeGetAuthTest() throws DataAccessException {
        String nonExistingToken = "nonExistent";

        assertNull(authDAO.getAuth(nonExistingToken));
    }

    @Test
    public void positiveDeleteAuthTest() throws DataAccessException {
        AuthData authData = new AuthData("test_token", "test_user");
        authDAO.createAuth(authData);

        authDAO.deleteAuth("test_token");

        assertNull(authDAO.getAuth("test_token"));
    }

    @Test
    public void negativeDeleteAuthTest() {
        String nonExistingToken = "non_existing_token";

        assertDoesNotThrow(() -> authDAO.deleteAuth(nonExistingToken));
    }
}
