package dataaccess;

import model.GameData;

import java.util.HashMap;

public class MemoryGameDAO implements GameDAO{
    private final HashMap<Integer, GameData> gameDataMap = new HashMap<>();

    @Override
    public void clear() throws DataAccessException {
        gameDataMap.clear();
    }

    @Override
    public GameData createGame(GameData game) throws DataAccessException {
        if (gameDataMap.containsKey(game.gameID())) {
            throw new DataAccessException("Username already exists");
        } else {
            gameDataMap.put(game.gameID(), game);
            return game;
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return gameDataMap.get(gameID);
    }

    @Override
    public GameData[] listGames() throws DataAccessException {
        return gameDataMap.values().toArray(new GameData[0]);
    }

    @Override
    public void updateGame(int gameID, GameData updatedGameData) throws DataAccessException {
        gameDataMap.put(gameID, updatedGameData);
    }
}
