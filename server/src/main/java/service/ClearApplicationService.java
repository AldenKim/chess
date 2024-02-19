package service;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import dataAccess.UserDAO;
import requests.ClearApplicationRequest;
import results.ClearApplicationResult;

public class ClearApplicationService {
    private final UserDAO userDAO;
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public ClearApplicationService(UserDAO userDAO, GameDAO gameDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public ClearApplicationResult clearApplication(ClearApplicationRequest request) {
        try {
            userDAO.clear();
            gameDAO.clear();
            authDAO.clear();

            return new ClearApplicationResult(null);
        } catch (DataAccessException e) {
            return new ClearApplicationResult("Error: " + e.getMessage());
        }
    }
}
