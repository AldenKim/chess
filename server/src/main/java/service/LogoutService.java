package service;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import requests.LogoutRequest;
import results.LogoutResult;

public class LogoutService {
    private final AuthDAO authDAO;

    public LogoutService(AuthDAO authDAO) {
        this.authDAO = authDAO;
    }

    public LogoutResult logout(LogoutRequest request) throws DataAccessException {
        try {

            if (!isValidAuthToken(request.authToken())) {
                return new LogoutResult("Error: Unauthorized");
            }

            authDAO.deleteAuth(request.authToken());

            return new LogoutResult(null);
        } catch (DataAccessException e) {
            return new LogoutResult("Error: " + e.getMessage());
        }
    }

    private boolean isValidAuthToken(String authToken) throws DataAccessException {
        return authDAO.getAuth(authToken) != null;
    }
}
