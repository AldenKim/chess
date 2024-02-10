package service;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import dataAccess.UserDAO;
import model.UserData;

public class ChessService {

    private final UserDAO userDAO;
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public ChessService(UserDAO userDAO, GameDAO gameDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public UserData createUser(UserData user) throws DataAccessException {
        return userDAO.createUser(user);
    }

    public UserData getUser(String username) throws DataAccessException {
        return userDAO.getUser(username);
    }

    public void updateUser(UserData user) throws DataAccessException {
        userDAO.updateUser(user);
    }

    public void deleteUser(String username) throws DataAccessException {
        userDAO.deleteUser(username);
    }
}
