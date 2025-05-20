package dataAccess;

import dataAccess.UserDAO;
import model.UserData;

import java.util.Collection;
import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {
    final private HashMap<Integer, UserData> users = new HashMap<>();

    @Override
    public UserData addUser(UserData user) throws DataAccessException {
        users.put(3,user);
        return user;
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return null;
    }
}
