package dataaccess;

import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO{
    private final HashMap<String, UserData> users = new HashMap<>();

    @Override
    public void clear() throws DataAccessException{
        users.clear();
    }

    @Override
    public UserData createUser(UserData user) throws DataAccessException {
        if (users.containsKey(user.username())) {
            throw new DataAccessException("Username already exists");
        } else {
            users.put(user.username(), user);
            return user;
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return users.get(username);
    }
}
