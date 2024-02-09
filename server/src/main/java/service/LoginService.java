package service;

import dataAccess.DataAccessException;
import dataAccess.UserDAO;
import model.AuthData;
import model.UserData;

public class LoginService {

    private UserDAO userDAO;

    public LoginService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public AuthData login(UserData user) throws DataAccessException {
        UserData storedUser = userDAO.getUser(user.getUsername());

        if(!storedUser.password().equals(user.password())) {
            throw new DataAccessException("Password is incorrect");
        }

        String authToken = generateAuthToken();

        return new AuthData(authToken, user.getUsername());
    }

    private String generateAuthToken() {
        return "authToken 200";
    }
}
