package dataAccess;

import model.AuthData;

import java.util.HashMap;

public interface AuthDAO {
    void clear() throws DataAccessException;

    AuthData createAuth(AuthData auth) throws DataAccessException;

    AuthData getAuth(String authToken) throws DataAccessException;

    void deleteAuth(String authToken) throws DataAccessException;
}
