package ui;

import model.UserData;
import server.RegisterResult;
import  ui.State;
import com.google.gson.Gson;
import exception.ResponseException;
import server.ServerFacade;

import java.util.Arrays;

public class PostLoginClient extends Client {
    private final ServerFacade server;
    private final String serverUrl;
    private State state = State.LOGGED_IN;
    private Repl repl;

    public PostLoginClient(String serverUrl, Repl repl) {
        this.serverUrl = serverUrl;
        server = new ServerFacade(serverUrl);
        this.repl = repl;
    }

    @Override
    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "list" -> listGames();
//                case "signout" -> signOut();
//                case "adopt" -> adoptPet(params);
//                case "adoptall" -> adoptAllPets();
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String listGames(String... params) throws ResponseException {

        throw new ResponseException(400, "Expected: <yourname>");
    }

    public String help() {
        return """
                - list
                - adopt <pet id>
                - rescue <name> <CAT|DOG|FROG|FISH>
                - adoptAll
                - signOut
                - quit
                """;
    }

}
