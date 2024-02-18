package results;

import model.GameData;

public record ListGamesResult(GameData[] games, String errorMessage) { }
