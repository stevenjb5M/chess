package service;

import model.GameData;

import java.util.Collection;

public class ListGamesResult {
    private Collection<GameData> games;

    public ListGamesResult(Collection<GameData> games) {
        this.games = games;
    }

    public Collection<GameData> getGames() {
        return games;
    }

}