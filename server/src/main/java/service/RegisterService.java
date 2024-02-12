package service;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.UserDAO;
import model.AuthData;
import model.UserData;
import requests.RegisterRequest;
import results.RegisterResult;
import java.util.UUID;

public class RegisterService {
    private UserDAO userDAO;
    private AuthDAO authDAO;

    public RegisterService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public RegisterResult register(RegisterRequest request) throws DataAccessException {
        try {
            if(userDAO.getUser(request.username()) != null) {
                throw new DataAccessException("Error 403: Already Taken");
            }

            UserData newUser = new UserData(request.username(), request.password(), request.email());

            String authToken = UUID.randomUUID().toString();
            AuthData newAuth = new AuthData(authToken, newUser.username());

            userDAO.createUser(newUser);
            authDAO.createAuth(newAuth);

            return new RegisterResult(newUser.username(), authToken);
        }
        catch (DataAccessException e) {
            throw e;
        }
    }
}
