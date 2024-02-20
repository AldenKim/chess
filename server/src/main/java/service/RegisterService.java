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
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public RegisterService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public RegisterResult register(RegisterRequest request) throws DataAccessException {
        try {
            UserData existingUser = userDAO.getUser(request.username());
            if (existingUser != null) {
                RegisterResult result = new RegisterResult(null, null, "Error: User Already Taken");
                return result;
            }

            UserData newUser = new UserData(request.username(), request.password(), request.email());

            String authToken = UUID.randomUUID().toString();
            AuthData newAuth = new AuthData(authToken, newUser.username());

            userDAO.createUser(newUser);
            authDAO.createAuth(newAuth);

            return new RegisterResult(newUser.username(), authToken, null);
        }
        catch (DataAccessException e) {
            throw e;
        }
    }
}
