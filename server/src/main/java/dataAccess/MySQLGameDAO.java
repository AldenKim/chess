package dataAccess;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import model.GameData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MySQLGameDAO implements GameDAO{

    private final Gson gson;

    public MySQLGameDAO() throws DataAccessException{
        gson = new Gson();
        configureDatabase();
    }

    @Override
    public void clear() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement statement = conn.prepareStatement("TRUNCATE TABLE games")) {
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error while clearing game data: " + e.getMessage());
        }
    }

    @Override
    public GameData createGame(GameData gameData) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement statement = conn.prepareStatement("INSERT INTO games (gameID, whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?, ?)")) {
            statement.setInt(1, gameData.gameID());
            statement.setString(2, gameData.whiteUsername());
            statement.setString(3, gameData.blackUsername());
            statement.setString(4, gameData.gameName());
            statement.setString(5, serializeGame(gameData.game()));
            statement.executeUpdate();
            return gameData;
        } catch (SQLException e) {
            throw new DataAccessException("Error while creating game: " + e.getMessage());
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement statement = conn.prepareStatement("SELECT whiteUsername, blackUsername, gameName, game FROM games WHERE gameID = ?")) {
            statement.setInt(1, gameID);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String whiteUsername = resultSet.getString("whiteUsername");
                    String blackUsername = resultSet.getString("blackUsername");
                    String gameName = resultSet.getString("gameName");
                    String json = resultSet.getString("game");
                    ChessGame game = deserializeGame(json);
                    return new GameData(gameID, whiteUsername, blackUsername, gameName, game);
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error while getting game: " + e.getMessage());
        }
    }

    @Override
    public GameData[] listGames() throws DataAccessException {
        List<GameData> games = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement statement = conn.prepareStatement("SELECT gameID, whiteUsername, blackUsername, gameName, game FROM games")) {
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int gameID = resultSet.getInt("gameID");
                    String whiteUsername = resultSet.getString("whiteUsername");
                    String blackUsername = resultSet.getString("blackUsername");
                    String gameName = resultSet.getString("gameName");
                    String json = resultSet.getString("game");
                    ChessGame game = deserializeGame(json);
                    games.add(new GameData(gameID, whiteUsername, blackUsername, gameName, game));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error while listing games: " + e.getMessage());
        }
        return games.toArray(new GameData[0]);
    }

    @Override
    public void updateGame(int gameID, GameData updatedGameData) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement statement = conn.prepareStatement("UPDATE games SET whiteUsername = ?, blackUsername = ?, gameName = ?, game = ? WHERE gameID = ?")) {
            statement.setString(1, updatedGameData.whiteUsername());
            statement.setString(2, updatedGameData.blackUsername());
            statement.setString(3, updatedGameData.gameName());
            statement.setString(4, serializeGame(updatedGameData.game()));
            statement.setInt(5, gameID);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error while updating game: " + e.getMessage());
        }
    }

    private String serializeGame(ChessGame game) {
        JsonObject gameJson = new JsonObject();

        gameJson.add("chessBoard", gson.toJsonTree(game.getBoard()));

        if(game.getTeamTurn() == null){
            gameJson.addProperty("turn", "null");
        } else {
            gameJson.addProperty("turn", game.getTeamTurn().toString());
        }

        if (game.getLastMove() != null) {
            gameJson.add("lastMove", gson.toJsonTree(game.getLastMove()));
        }
        return gameJson.toString();
    }

    private ChessGame deserializeGame(String json) {
        JsonObject gameJson = gson.fromJson(json, JsonObject.class);

        ChessBoard chessBoard = gson.fromJson(gameJson.get("chessBoard"), ChessBoard.class);

        String color = gameJson.get("turn").getAsString();
        ChessGame.TeamColor turn = null;

        if(Objects.equals(color, "WHITE")) {
            turn = ChessGame.TeamColor.WHITE;
        } else if (Objects.equals(color, "BLACK")) {
            turn = ChessGame.TeamColor.BLACK;
        }

        ChessMove lastMove = null;
        if (gameJson.has("lastMove")) {
            lastMove = gson.fromJson(gameJson.get("lastMove"), ChessMove.class);
        }

        ChessGame game = new ChessGame();
        game.setBoard(chessBoard);
        game.setTeamTurn(turn);
        game.setLastMove(lastMove);

        return game;
    }

    private static final String[] CREATE_GAMES_TABLE_QUERY = {
            "CREATE TABLE IF NOT EXISTS games (" +
                    "gameID INT PRIMARY KEY, " +
                    "whiteUsername VARCHAR(255), " +
                    "blackUsername VARCHAR(255), " +
                    "gameName VARCHAR(255) NOT NULL, " +
                    "game TEXT NOT NULL" +
                    ")"
    };

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();

        try (Connection conn = DatabaseManager.getConnection()) {
            for (String query : CREATE_GAMES_TABLE_QUERY) {
                try (PreparedStatement statement = conn.prepareStatement(query)) {
                    statement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error configuring database: " + e.getMessage());
        }
    }
}
