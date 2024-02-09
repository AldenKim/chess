package service;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import org.eclipse.jetty.util.log.Log;

public class LogoutService {

    private AuthDAO authDAO;

    public LogoutService(AuthDAO authDAO) {
        this.authDAO = authDAO;
    }

    public void logout(String authToken) throws DataAccessException {

    }
}
