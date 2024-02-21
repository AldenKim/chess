package server;

import dataAccess.*;
import handlers.*;
import service.*;
import spark.*;

import java.nio.file.Paths;

public class Server {
    private static final UserDAO userDAO = new MemoryUserDAO();
    private static final AuthDAO authDAO = new MemoryAuthDAO();
    private static final GameDAO gameDAO = new MemoryGameDAO();

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.

        RegisterHandler registerHandler = new RegisterHandler(new RegisterService(userDAO, authDAO));
        Spark.post("/user", registerHandler::register);

        LoginHandler loginHandler = new LoginHandler(new LoginService(userDAO, authDAO));
        Spark.post("/session", loginHandler::login);

        LogoutHandler logoutHandler = new LogoutHandler(new LogoutService(authDAO));
        Spark.delete("/session", logoutHandler::logout);

        CreateGameHandler createGameHandler = new CreateGameHandler(new CreateGameService(gameDAO, authDAO));
        Spark.post("/game", createGameHandler::createGame);

        JoinGameHandler joinGameHandler = new JoinGameHandler(new JoinGameService(gameDAO, authDAO));
        Spark.put("/game", joinGameHandler::joinGame);

        ClearApplicationService clearApplicationService = new ClearApplicationService(userDAO, gameDAO, authDAO);
        ClearHandler clearHandler = new ClearHandler(clearApplicationService);
        Spark.delete("/db", (req, res) -> clearHandler.clearDatabase(res));

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
