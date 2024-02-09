package service;

import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import model.GameData;

public class CreateGameService {

    private GameDAO gameDAO;

    public CreateGameService(GameDAO gameDAO) {
        this.gameDAO = gameDAO;
    }

    public void createGame(GameData gameData) throws DataAccessException {

    }
}
