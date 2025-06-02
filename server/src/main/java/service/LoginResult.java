package service;

import server.RegisterResult;

public class LoginResult extends RegisterResult {

    public LoginResult(String username, String authToken) {
        super(username, authToken);
    }
}