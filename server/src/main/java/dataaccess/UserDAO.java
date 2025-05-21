package dataaccess;

import model.UserData;

public interface UserDAO {

    UserData addUser(UserData user) throws DataAccessException;

    UserData getUser(String username) throws DataAccessException;

    void clearUsers() throws DataAccessException;
}