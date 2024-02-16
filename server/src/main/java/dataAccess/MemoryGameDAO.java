package dataAccess;

import model.GameData;

import java.util.HashMap;

public class MemoryGameDAO implements GameDAO{
    private final HashMap<String, GameData> gameDataMap = new HashMap<>();

    @Override
    public void clear() throws DataAccessException {
        gameDataMap.clear();
    }

    @Override
    public GameData createGame(GameData game) throws DataAccessException {
        gameDataMap.put(String.valueOf(game.gameID()), game);
        return game;
    }

    @Override
    public GameData getGame(String gameID) throws DataAccessException {
        return gameDataMap.get(gameID);
    }

    @Override
    public GameData[] listGames() throws DataAccessException {
        return gameDataMap.values().toArray(new GameData[0]);
    }

    @Override
    public void updateGame(String gameID, GameData updatedGameData) throws DataAccessException {
        gameDataMap.put(gameID, updatedGameData);
    }
}
