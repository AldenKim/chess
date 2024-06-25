package service;

import dataaccess.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requests.RegisterRequest;
import results.RegisterResult;

import static org.junit.jupiter.api.Assertions.*;

public class RegisterServiceTest {
    private UserDAO userDAO;
    private AuthDAO authDAO;
    private RegisterService registerService;


    @BeforeEach
    public void setUp() {
        this.userDAO = new MemoryUserDAO();
        this.authDAO = new MemoryAuthDAO();
        this.registerService = new RegisterService(userDAO, authDAO);

        RegisterRequest request = new RegisterRequest("existingUser", "passwordExists", "exists@example.com");

        try {
            registerService.register(request);
        } catch (DataAccessException e) {
            fail("Exception thrown during registration: " + e.getMessage());
        }
    }

    @Test
    public void positiveRegisterServiceTest() {
        RegisterRequest request = new RegisterRequest("randomUser", "passwordRandom", "test@example.com");

        RegisterResult result = null;

        try {
            result = registerService.register(request);
        } catch (DataAccessException e) {
            fail("Exception thrown during successful registration: " + e.getMessage());
        }

        assertNotNull(result);
        assertNull(result.message());
        assertNotNull(result.authToken());
        assertEquals("randomUser", result.username());
    }

    @Test
    public void negativeRegisterServiceTest() {
        RegisterRequest request = new RegisterRequest("existingUser", "passwordRandom", "existing@example.com");

        RegisterResult result = null;
        try {
            result = registerService.register(request);
        } catch (DataAccessException e) {
            fail("Exception thrown during registration: " + e.getMessage());
        }

        assertNotNull(result);
        assertNull(result.authToken());
        assertNull(result.username());
        assertNotNull(result.message());
        assertEquals("Error: User Already Taken", result.message());
    }
}
