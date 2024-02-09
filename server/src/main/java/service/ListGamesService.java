package service;

import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import model.GameData;

public class ListGamesService {

    private GameDAO gameDAO;

    public ListGamesService(GameDAO gameDAO) {
        this.gameDAO = gameDAO;
    }

    public GameData[] listGames() throws DataAccessException {
        return new GameData[0];
    }
}
