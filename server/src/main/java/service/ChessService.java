package service;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import dataAccess.UserDAO;
import model.AuthData;
import model.GameData;
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

    public GameData createGame(GameData game) throws DataAccessException {
        return gameDAO.createGame(game);
    }

    public GameData getGame(String gameID) throws DataAccessException {
        return gameDAO.getGame(gameID);
    }

    public GameData[] listGames() throws DataAccessException {
        return gameDAO.listGames();
    }

    public void updateGame(String gameID, GameData updatedGameData) throws DataAccessException {
        gameDAO.updateGame(gameID, updatedGameData);
    }

    public AuthData createAuth(AuthData auth) throws DataAccessException {
        return authDAO.createAuth(auth);
    }

    public AuthData getAuth(String authToken) throws DataAccessException {
        return authDAO.getAuth(authToken);
    }

    public void deleteAuth(String authToken) throws DataAccessException {
        authDAO.deleteAuth(authToken);
    }

    public void clear() throws DataAccessException {
        userDAO.clear();
        gameDAO.clear();
        authDAO.clear();
    }
}
