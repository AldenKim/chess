package handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import requests.JoinGameRequest;
import results.JoinGameResult;
import service.JoinGameService;
import spark.Request;
import spark.Response;

public class JoinGameHandler {
    private final JoinGameService joinGameService;
    private final Gson gson;

    public JoinGameHandler(JoinGameService joinGameService) {
        this.joinGameService = joinGameService;
        this.gson = new Gson();
    }

    public String joinGame(Request req, Response res) {
        res.type("application/json");

        JoinGameRequest joinGameRequest = gson.fromJson(req.body(), JoinGameRequest.class);
        String authToken = req.headers("authorization");

        if (joinGameRequest == null || joinGameRequest.gameID() == null || joinGameRequest.gameID() < 1 || (joinGameRequest.playerColor() == null
                || !joinGameRequest.playerColor().equalsIgnoreCase("WHITE") && !joinGameRequest.playerColor().equalsIgnoreCase("BLACK"))) {
            res.status(400); // Bad Request
            return gson.toJson(new JoinGameResult("Error: Bad Request"));
        }

        try {
            JoinGameResult joinGameResult = joinGameService.joinGame(authToken, joinGameRequest);
            if (joinGameResult.message() != null) {
                if (joinGameResult.message().contains("Unauthorized")) {
                    res.status(401); // Unauthorized
                } else if (joinGameResult.message().contains("already taken")) {
                    res.status(403); // Forbidden
                } else {
                    res.status(500); // Internal Server Error
                }
            }
            return gson.toJson(joinGameResult);
        } catch (DataAccessException e) {
            res.status(500);
            return gson.toJson(new JoinGameResult("Error: " + e.getMessage()));
        }
    }
}
