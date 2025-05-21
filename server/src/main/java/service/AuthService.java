package service;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.UserDAO;
import model.AuthData;
import model.UserData;

import java.util.UUID;

public class AuthService {

    private final AuthDAO authDAO;

    public AuthService(AuthDAO authDAO) {
        this.authDAO = authDAO;
    }


    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    public void addAuthData(AuthData authData) throws DataAccessException {
        authDAO.addAuthData(authData);
    }

    public boolean checkIfAuthExisits(String authToken) throws DataAccessException {
        return authDAO.checkIfAuthExists(authToken);
    }

    public void removeAuth(String authToken) throws DataAccessException {
        authDAO.removeAuth(authToken);
    }

    public String getUserByAuth(String authToken) throws DataAccessException {
        return authDAO.getUserByAuth(authToken);
    }

    public void clearAuths() throws DataAccessException {
        authDAO.clearAuths();
    }

}

