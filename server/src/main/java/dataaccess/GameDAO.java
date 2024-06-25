package dataaccess;

import model.GameData;

public interface GameDAO {
    void clear() throws DataAccessException;

    GameData createGame(GameData game) throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    GameData[] listGames() throws DataAccessException;

    void updateGame(int gameID, GameData updatedGameData) throws DataAccessException;
}
