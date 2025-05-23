package dataaccess;

import model.UserData;

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
            throw new UsernameTakenException("Error: already taken");
        }

    }

    @Override
    public UserData getUser(String username) {

        UserData user = users.get(username);

        if (user == null) {
            return null;
        } else {
            return user;
        }
    }

    @Override
    public void clearUsers() throws DataAccessException {
        users.clear();
    }
}
