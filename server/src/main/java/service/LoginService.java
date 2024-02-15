package service;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.UserDAO;
import model.AuthData;
import requests.LoginRequest;
import results.LoginResult;

import java.util.UUID;

public class LoginService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public LoginService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public LoginResult login(LoginRequest request) throws DataAccessException {
        try {
            var user = userDAO.getUser(request.username());

            if(user == null || !user.password().equals(request.password())) {
                return new LoginResult(null, null, "Invalid username or password");
            }

            String authToken = UUID.randomUUID().toString();
            AuthData newAuth = new AuthData(authToken, user.username());

            authDAO.createAuth(newAuth);

            return new LoginResult(user.username(), authToken, null);
        } catch (DataAccessException e) {
            throw e;
        }
    }
}
