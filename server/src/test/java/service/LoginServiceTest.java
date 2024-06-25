package service;

import dataAccess.*;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requests.LoginRequest;
import results.LoginResult;

import static org.junit.jupiter.api.Assertions.*;

public class LoginServiceTest {
    private UserDAO userDAO;
    private AuthDAO authDAO;
    private LoginService loginService;

    @BeforeEach
    public void setUp() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        loginService = new LoginService(userDAO, authDAO);

        UserData user = new UserData("userTest", "password", "test@example.com");
        try {
            userDAO.createUser(user);
        } catch (DataAccessException e) {
            fail("Exception thrown during setup: " + e.getMessage());
        }
    }

    @Test
    public void positiveLoginServiceTest ()  {
        LoginRequest request = new LoginRequest("userTest", "password");

        LoginResult result = null;
        try {
            result = loginService.login(request);
        } catch (DataAccessException e) {
            fail("Exception thrown during successful login: " + e.getMessage());
        }

        assertNotNull(result);
        assertNull(result.message());
        assertNotNull(result.authToken());
        assertEquals("userTest", result.username());
    }

    @Test
    public void negativeLoginServiceTest() {
        LoginRequest request = new LoginRequest("nonexistent", "password");

        LoginResult result = null;

        try {
            result = loginService.login(request);
        } catch (DataAccessException e) {
            fail("Exception thrown during login: " + e.getMessage());
        }

        assertNotNull(result);
        assertNull(result.authToken());
        assertNull(result.username());
        assertEquals("Error: Invalid username or password", result.message());
    }
}
