package service;

import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import model.UserData;

public class JoinGameService {

    private GameDAO gameDAO;

    public JoinGameService(GameDAO gameDAO) {
        this.gameDAO = gameDAO;
    }

    public void joinGame(String gameID, UserData user) throws DataAccessException {

    }
}
