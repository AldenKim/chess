package dataAccess;

import model.GameData;

public interface GameDAO {
    void clear() throws DataAccessException;

    GameData createGame(GameData game) throws DataAccessException;

    GameData getGame(String gameID) throws DataAccessException;

    GameData[] listGames() throws DataAccessException;

    void updateGame(String gameID, GameData updatedGameData) throws DataAccessException;
}
