package handlers;

import com.google.gson.Gson;
import dataAccess.DataAccessException;
import results.LogoutResult;
import service.LogoutService;
import spark.Request;
import spark.Response;

public class LogoutHandler {
    private final LogoutService logoutService;
    private final Gson gson;

    public LogoutHandler(LogoutService logoutService) {
        this.logoutService = logoutService;
        this.gson = new Gson();
    }

    public String logout(Request req, Response res) {
        res.type("application/json");

        String authToken = req.headers("authorization");

        try {
            LogoutResult logoutResult = logoutService.logout(authToken);
            if(logoutResult.message() != null) {
                res.status(401); //Unauthorized
            } else {
                res.status(200); //OK
            }
            return gson.toJson(logoutResult);
        } catch (DataAccessException e) {
            res.status(500);
            return gson.toJson(new LogoutResult("Error: " + e.getMessage()));
        }
    }
}
