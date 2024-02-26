package dataAccess;

import model.AuthData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySQLAuthDAO implements AuthDAO {

    public MySQLAuthDAO() throws DataAccessException {

    }

    @Override
    public void clear() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement statement = conn.prepareStatement("TRUNCATE TABLE auth")) {
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error while clearing auth data: " + e.getMessage());
        }
    }

    @Override
    public AuthData createAuth(AuthData auth) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement statement = conn.prepareStatement("INSERT INTO auth (auth_token, username) VALUES (?, ?)")) {
            statement.setString(1, auth.authToken());
            statement.setString(2, auth.username());
            statement.executeUpdate();
            return auth;
        } catch (SQLException e) {
            throw new DataAccessException("Error while creating the auth data: " + e.getMessage());
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement statement = conn.prepareStatement("SELECT * FROM auth WHERE auth_token = ?")) {
            statement.setString(1, authToken);
            try (ResultSet resultSet = statement.executeQuery()) {
                if(resultSet.next()) {
                    String username = resultSet.getString("username");
                    return new AuthData(authToken, username);
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error while fetching auth data: " + e.getMessage());
        }
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {

    }
}
