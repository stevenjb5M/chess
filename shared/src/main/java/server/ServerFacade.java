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

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public UserData registerUser(UserData user) throws ResponseException {
        var path = "/register";
        return this.makeRequest("POST", path, user, UserData.class);
    }

    public UserData loginUser(UserData user) throws ResponseException {
        var path = "/session";
        return this.makeRequest("POST", path, user, UserData.class);
    }

    public UserData logoutUser(UserData user) throws ResponseException {
        var path = "/session";
        return this.makeRequest("DELETE", path, user, UserData.class);
    }

    public GameData[] listGames() throws ResponseException {
        var path = "/game";
        record listGameResponse(GameData[] game) {
        }
        var response = this.makeRequest("GET", path, null, listGameResponse.class);
        return response.game;
    }

    public GameData createGame(GameData game) throws ResponseException {
        var path = "/game";
        return this.makeRequest("POST", path, game, GameData.class);
    }

    public GameData joinGame(GameData game) throws ResponseException {
        var path = "/game";
        return this.makeRequest("PUT", path, game, GameData.class);
    }

    public void clearDataBase() throws ResponseException {
        var path = "/db";
        this.makeRequest("DELETE", path, null, null);
    }




//    public void deletePet(int id) throws ResponseException {
//        var path = String.format("/pet/%s", id);
//        this.makeRequest("DELETE", path, null, null);
//    }
//
//    public void deleteAllPets() throws ResponseException {
//        var path = "/pet";
//        this.makeRequest("DELETE", path, null, null);
//    }
//
//    public UserData[] listPets() throws ResponseException {
//        var path = "/pet";
//        record listPetResponse(UserData[] pet) {
//        }
//        var response = this.makeRequest("GET", path, null, listPetResponse.class);
//        return response.pet();
//    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

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
