package dataAccess;

import dataAccess.UserDAO;
import model.UserData;

import java.util.Collection;
import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {
    final private HashMap<String, UserData> users = new HashMap<>();

    @Override
    public UserData addUser(UserData user) throws DataAccessException {

        UserData existingUser = users.get(user.username());

        if (existingUser == null) {
            users.put(user.username(),user);
            return user;
        } else {
            throw new DataAccessException("Error: already taken");
        }

    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return null;
    }

    @Override
    public void clearUsers() throws DataAccessException {
        users.clear();
    }
}
