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

}

