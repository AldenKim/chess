package dataAccess;

import model.GameData;

import java.util.HashMap;
import java.util.Map;

public class GameDAO {
    private Map<Integer, GameData> gameDataMap;

    public GameDAO() {
        gameDataMap = new HashMap<>();
    }

    public void clear() throws DataAccessException{
        gameDataMap.clear();
    }

    public void createGame(GameData game) throws DataAccessException{
        int gameID = game.gameID();
        if (gameDataMap.containsKey(gameID)) {
            throw new DataAccessException("Game already exists with ID: " + gameID);
        }
        gameDataMap.put(gameID, game);
    }

    public GameData getGame(int gameID) throws DataAccessException{
        GameData game = gameDataMap.get(gameID);
        if (game == null) {
            throw new DataAccessException("Game not found: " + gameID);
        }
        return game;
    }

    public Map<Integer, GameData> listGames() throws DataAccessException{
        return gameDataMap;
    }

    public void updateGame(int gameID, GameData updatedGameData) throws DataAccessException{
        if (!gameDataMap.containsKey(gameID)){
            throw new DataAccessException("Game not found with the following ID: " + gameID);
        }
        gameDataMap.put(gameID, updatedGameData);
    }

    public void deleteGame(int gameId) throws DataAccessException {
        if (!gameDataMap.containsKey(gameId)) {
            throw new DataAccessException("Game not found with ID: " + gameId);
        }
        gameDataMap.remove(gameId);
    }
}
