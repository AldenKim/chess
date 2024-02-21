package service;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import results.LogoutResult;

public class LogoutService {
    private final AuthDAO authDAO;

    public LogoutService(AuthDAO authDAO) {
        this.authDAO = authDAO;
    }

    public LogoutResult logout(String authToken) throws DataAccessException {
        try {
            if (!isValidAuthToken(authToken)) {
                return new LogoutResult("Error: Unauthorized");
            }

            authDAO.deleteAuth(authToken);

            return new LogoutResult(null);
        } catch (DataAccessException e) {
            return new LogoutResult("Error: " + e.getMessage());
        }
    }

    private boolean isValidAuthToken(String authToken) throws DataAccessException {
        return authDAO.getAuth(authToken) != null;
    }
}
