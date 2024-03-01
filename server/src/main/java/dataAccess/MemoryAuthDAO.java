package dataAccess;

import model.AuthData;

import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO{
    private final HashMap<String, AuthData> authDataMap = new HashMap<>();

    @Override
    public void clear() throws DataAccessException {
        authDataMap.clear();
    }

    @Override
    public AuthData createAuth(AuthData auth) throws DataAccessException {
        if (authDataMap.containsKey(auth.authToken())) {
            throw new DataAccessException("Username already exists");
        } else {
            authDataMap.put(auth.authToken(), auth);
            return auth;
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return authDataMap.get(authToken);
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        authDataMap.remove(authToken);
    }
}
