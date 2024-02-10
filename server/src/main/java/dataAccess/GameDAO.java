package dataAccess;

import model.GameData;

public interface GameDAO {
    void clear() throws DataAccessException;

    void createGame(GameData game) throws DataAccessException;

    void getGame(String gameID) throws DataAccessException;

    GameData[] listGames() throws DataAccessException;

    void updateGame(String gameID, GameData updatedGameData) throws DataAccessException;
}
