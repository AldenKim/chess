package dataAccess;

import model.AuthData;

import java.util.HashMap;
import java.util.Map;

public class AuthDAO {
    private Map<String, AuthData> authDataMap;

    public AuthDAO() {
        authDataMap = new HashMap<>();
    }

    public void clear() throws DataAccessException {
        authDataMap.clear();
    }

    public void createAuth(AuthData auth) throws DataAccessException {
        String authToken = auth.authToken();
        if (authDataMap.containsKey(authToken)) {
            throw new DataAccessException("Authorization already exists with the authToken: " + authToken);
        }
        authDataMap.put(authToken, auth);
    }

    public AuthData getAuth(String authToken) throws DataAccessException {
        AuthData auth = authDataMap.get(authToken);
        if(auth == null) {
            throw new DataAccessException("Auth not found with the token: " + authToken);
        }
        return auth;
    }

    public void deleteAuth (String authToken) throws DataAccessException {
        if (!authDataMap.containsKey(authToken)) {
            throw new DataAccessException("Authorization not found with token: " + authToken);
        }
        authDataMap.remove(authToken);
    }
}
