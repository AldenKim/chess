package dataAccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySQLUserDAO implements UserDAO{

    public MySQLUserDAO() throws DataAccessException  {
        configureDatabase();
    }

    @Override
    public void clear() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement statement = conn.prepareStatement("TRUNCATE TABLE users")) {
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error while clearing user data: " + e.getMessage());
        }
    }

    @Override
    public UserData createUser(UserData user) throws DataAccessException {
        try(Connection conn = DatabaseManager.getConnection();
        PreparedStatement statement = conn.prepareStatement("INSERT INTO users (username, password, email) VALUES (?, ?, ?)")) {
            String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());

            statement.setString(1, user.username());
            statement.setString(2, hashedPassword);
            statement.setString(3, user.email());
            statement.executeUpdate();
            return user;
        } catch (SQLException e) {
            throw new DataAccessException("Error while creating user: " + e.getMessage());
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement statement = conn.prepareStatement("SELECT * FROM users WHERE username = ?")) {
            statement.setString(1, username);
            try(ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String retrievedUsername = resultSet.getString("username");
                    String password = resultSet.getString("password");
                    String email = resultSet.getString("email");
                    return new UserData(retrievedUsername, password, email);
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error while fetching user: " + e.getMessage());
        }
    }

    private static final String[] CREATE_USERS_TABLE_QUERY = {
            "CREATE TABLE IF NOT EXISTS users (" +
                    "username VARCHAR(50) NOT NULL, " +
                    "password VARCHAR(255) NOT NULL, " +
                    "email VARCHAR(100) NOT NULL, " +
                    "PRIMARY KEY (username)" +
                    ")"
    };

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();

        try (Connection conn = DatabaseManager.getConnection()) {
            for (String query : CREATE_USERS_TABLE_QUERY) {
                try (PreparedStatement statement = conn.prepareStatement(query)) {
                    statement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error configuring database: " + e.getMessage());
        }
    }
}
