package server;

import com.google.gson.Gson;
import exception.ErrorResponse;
import exception.ResponseException;
import model.GameData;
import model.UserData;

import java.io.*;
import java.net.*;

public class ServerFacade {

    private final String serverUrl;
    public String authToken;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public RegisterResult registerUser(UserData user) throws ResponseException {
        RegisterRequest request = new RegisterRequest(user.username(), user.password(), user.email());

        var path = "/user";
        return this.makeRequest("POST", path, request, RegisterResult.class);
    }

    public LoginResult loginUser(UserData user) throws ResponseException {
        LoginRequest request = new LoginRequest(user.username(), user.password());

        var path = "/session";
        return this.makeRequest("POST", path, request, LoginResult.class);
    }

    public void logoutUser(String authToken) throws ResponseException {
        var path = "/session";
        this.makeRequest("DELETE", path, authToken, null);
    }

    public ListGamesResult listGames() throws ResponseException {
        var path = "/game";
        return this.makeRequest("GET", path, null, ListGamesResult.class);
    }

    public CreateGameResult createGame(CreateGameRequest request) throws ResponseException {
        var path = "/game";
        return this.makeRequest("POST", path, request, CreateGameResult.class);
    }

    public void joinGame(JoinGameRequest game) throws ResponseException {
        var path = "/game";
        this.makeRequest("PUT", path, game, null);
    }

    public void clearDataBase() throws ResponseException {
        var path = "/db";
        this.makeRequest("DELETE", path, null, null);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            if (authToken != null) {
                http.setRequestProperty("Authorization", authToken);
            }

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (ResponseException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }


    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            try (InputStream respErr = http.getErrorStream()) {
                if (respErr != null) {
                    throw ResponseException.fromJson(respErr);
                }
            }

            throw new ResponseException(status, "other failure: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }


    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
