package dataAccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
             PreparedStatement statement = conn.prepareStatement("INSERT INTO games (gameData) VALUES (?)")) {
            statement.setString(1, serializeGame(gameData.game()));
            statement.executeUpdate();
            return gameData;
        } catch (SQLException e) {
            throw new DataAccessException("Error while creating game: " + e.getMessage());
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement statement = conn.prepareStatement("SELECT gameData FROM games WHERE gameID = ?")) {
            statement.setInt(1, gameID);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String json = resultSet.getString("gameData");
                    ChessGame game = deserializeGame(json);
                    return new GameData(gameID, null, null, null, game);
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
             PreparedStatement statement = conn.prepareStatement("SELECT gameID, gameData FROM games")) {
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int gameID = resultSet.getInt("gameID");
                    String json = resultSet.getString("gameData");
                    ChessGame game = deserializeGame(json);
                    games.add(new GameData(gameID, null, null, null, game));
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
             PreparedStatement statement = conn.prepareStatement("UPDATE games SET gameData = ? WHERE gameID = ?")) {
            statement.setString(1, serializeGame(updatedGameData.game()));
            statement.setInt(2, gameID);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error while updating game: " + e.getMessage());
        }
    }

    private String serializeGame(ChessGame game) {
        return gson.toJson(game);
    }

    private ChessGame deserializeGame(String json) {
        return gson.fromJson(json, ChessGame.class);
    }

    private static final String[] CREATE_GAMES_TABLE_QUERY = {
            "CREATE TABLE IF NOT EXISTS games (" +
                    "gameID INT AUTO_INCREMENT PRIMARY KEY, " +
                    "gameData TEXT NOT NULL" +
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
