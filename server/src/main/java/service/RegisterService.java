package service;

import dataAccess.DataAccessException;
import dataAccess.UserDAO;
import model.AuthData;
import model.UserData;

public class RegisterService {

    private UserDAO userDAO;

    public RegisterService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public AuthData register(UserData user) throws DataAccessException {
        return new AuthData("Placeholder", user.getUsername());
    }
}
