package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import results.LogoutResult;

import static org.junit.jupiter.api.Assertions.*;

public class LogoutServiceTest {
    private AuthDAO authDAO;
    private LogoutService logoutService;

    @BeforeEach
    public void setUp() {
        this.authDAO = new MemoryAuthDAO();
        this.logoutService = new LogoutService(authDAO);

        AuthData authToken = new AuthData("testToken", "userTest");
        try {
            authDAO.createAuth(authToken);
        } catch (DataAccessException e) {
            fail("Exception thrown during setup: " + e.getMessage());
        }
    }

    @Test
    public void positiveLogoutServiceTest() {
        String authToken = "testToken";

        LogoutResult result = null;

        try {
            result = logoutService.logout(authToken);
        } catch (DataAccessException e) {
            fail("Exception thrown during successful logout: " + e.getMessage());
        }

        assertNotNull(result);
        assertNull(result.message());
        try {
            assertNull((authDAO).getAuth("testToken"));
        } catch (DataAccessException e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }

    @Test
    public void negativeLogoutServiceTest() {
        String authToken = "invalidToken";

        LogoutResult result = null;

        try {
            result = logoutService.logout(authToken);
        } catch (DataAccessException e) {
            fail("Exception thrown during logout: " + e.getMessage());
        }

        assertNotNull(result);
        assertNotNull(result.message());
        assertEquals("Error: Unauthorized", result.message());
    }
}
