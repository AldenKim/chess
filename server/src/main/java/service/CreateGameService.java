package service;

import chess.ChessGame;
import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import model.GameData;
import requests.CreateGameRequest;
import results.CreateGameResult;

public class CreateGameService {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    private int gameIdCounter;

    public CreateGameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
        this.gameIdCounter = 0;
    }

    public CreateGameResult createGame(String authToken, CreateGameRequest request) throws DataAccessException {
        try {
            if(!isValidAuthToken(authToken)) {
                return new CreateGameResult(null, "Error: Unauthorized");
            }

            gameIdCounter++;

            ChessGame chessGame = new ChessGame();

            GameData newGame = new GameData(gameIdCounter, null,null, request.gameName(), chessGame);
            GameData createdGame = gameDAO.createGame(newGame);
            return new CreateGameResult(createdGame.gameID(), null);
        } catch (DataAccessException e) {
            throw e;
        }
    }

    private boolean isValidAuthToken(String authToken) throws DataAccessException {
        return authDAO.getAuth(authToken) != null;
    }
}
