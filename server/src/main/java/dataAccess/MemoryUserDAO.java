package dataAccess;

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
        users.put(user.username(), user);
        return user;
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return users.get(username);
    }

    @Override
    public void updateUser(UserData user) throws DataAccessException {
        users.put(user.username(), user);
    }

    @Override
    public void deleteUser(String username) throws DataAccessException {
        users.remove(username);
    }
}
