package dataAccess;

import model.UserData;

import java.util.HashMap;

public interface UserDAO {
    HashMap<String, UserData> userDataMap = new HashMap<>();

    void clear() throws DataAccessException;

    UserData createUser(UserData user) throws DataAccessException;

    UserData getUser(String username) throws DataAccessException;

    void updateUser(UserData user) throws DataAccessException;

    void deleteUser(String username) throws DataAccessException;
}
