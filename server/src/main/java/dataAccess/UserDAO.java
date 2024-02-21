package dataAccess;

import model.UserData;

import java.util.HashMap;

public interface UserDAO {

    void clear() throws DataAccessException;

    UserData createUser(UserData user) throws DataAccessException;

    UserData getUser(String username) throws DataAccessException;
}
