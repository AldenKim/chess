package dataAccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import javax.xml.crypto.Data;

public interface DataAccess {
    void clear() throws DataAccessException;

    void createUser(UserData user) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;

    void createGame(GameData game) throws DataAccessException;
    GameData getGame(String gameID) throws DataAccessException;
    GameData[] listGames() throws DataAccessException;
    void updateGame(String gameID, GameData updatedGameData) throws DataAccessException;

    void createAuth(AuthData auth) throws DataAccessException;
    AuthData getAuth (String authToken) throws DataAccessException;
    void deleteAuth(String authToken) throws DataAccessException;
}
