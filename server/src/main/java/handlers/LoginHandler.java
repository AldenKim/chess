package handlers;

import com.google.gson.Gson;
import dataAccess.DataAccessException;
import requests.LoginRequest;
import results.LoginResult;
import service.LoginService;
import spark.Request;
import spark.Response;

public class LoginHandler {
    private final LoginService loginService;
    private final Gson gson;

    public LoginHandler(LoginService loginService) {
        this.loginService = loginService;
        this.gson = new Gson();
    }

    public String login(Request req, Response res) {
        res.type("application/json");

        LoginRequest loginRequest = gson.fromJson(req.body(), LoginRequest.class);

        try {
            LoginResult loginResult = loginService.login(loginRequest);
            if (loginResult.message() != null) {
                res.status(401); // Unauthorized
            } else {
                res.status(200); // OK
            }
            return gson.toJson(loginResult);
        } catch (DataAccessException e) {
            res.status(500);
            return gson.toJson(new LoginResult(null, null, "Error: " + e.getMessage()));
        }
    }
}
