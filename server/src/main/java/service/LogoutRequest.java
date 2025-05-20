package service;

public class LogoutRequest {
    private String authToken;

    public LogoutRequest(String authToken) {
        this.authToken = authToken;
    }

    public String getAuthToken() {
        return authToken;
    }

}