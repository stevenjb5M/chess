package dataaccess;

import model.UserData;

import java.util.HashMap;

public class SQLUserDAO implements UserDAO {
    final private HashMap<String, UserData> users = new HashMap<>();


    @Override
    public UserData addUser(UserData user) throws DataAccessException {
        return null;
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return null;
    }

    @Override
    public void clearUsers() throws DataAccessException {

    }
}
