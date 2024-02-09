package dataAccess;

import model.GameData;
import model.UserData;

import java.util.HashMap;
import java.util.Map;

public class UserDAO {
    private Map<String, UserData> userDataMap;

    public UserDAO(){
        userDataMap = new HashMap<>();
    }

    public void clear() throws DataAccessException{
        userDataMap.clear();
    }

    public void createUser(UserData user) throws DataAccessException{
        if(userDataMap.containsKey(user.getUsername())){
            throw new DataAccessException("User already exists with username: " + user.getUsername());
        }
        userDataMap.put(user.getUsername(), user);
    }

    public UserData getUser (String username) throws DataAccessException{
        if(userDataMap.containsKey(username)){
            return userDataMap.get(username);
        }
        else{
            throw new DataAccessException("User not found: " + username);
        }
    }

    public void updateUser(UserData user) throws DataAccessException {
        if (!userDataMap.containsKey(user.getUsername())) {
            throw new DataAccessException("User not found with username: " + user.getUsername());
        }
        userDataMap.put(user.getUsername(), user);
    }
}
