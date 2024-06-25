package ui;

import com.google.gson.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ServerFacade {
    private static final String BASE_URL = "http://localhost:";
    private int portNumb;
    public Map<Integer, Integer> gameNumberToIdMap;
    public ServerFacade(int port) {
        this.gameNumberToIdMap = new HashMap<>();
        this.portNumb = port;
    }
    private void helpForError(HttpURLConnection conn) {
        InputStreamReader inputStreamReader = new InputStreamReader(conn.getErrorStream());
        JsonObject errorResponse = JsonParser.parseReader(inputStreamReader).getAsJsonObject();
        String errorMessage = errorResponse.get("message").getAsString();

        System.out.println(errorMessage);
    }

    private boolean helpForError2 (HttpURLConnection conn) {
        InputStreamReader inputStreamReader = new InputStreamReader(conn.getErrorStream());
        JsonObject errorResponse = JsonParser.parseReader(inputStreamReader).getAsJsonObject();
        String errorMessage = errorResponse.get("message").getAsString();

        System.out.println(errorMessage);
        return false;
    }

    public String login(String username, String password) {
        try {
            URL url = new URL(BASE_URL + portNumb + "/session");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-type", "application/json");

            JsonObject loginData = new JsonObject();
            loginData.addProperty("username", username);
            loginData.addProperty("password", password);

            String jsonData = new Gson().toJson(loginData);
            conn.getOutputStream().write(jsonData.getBytes());
            conn.connect();

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                String authToken = conn.getHeaderField("Authorization");
                System.out.println(EscapeSequences.ERASE_SCREEN);
                System.out.println("Login successful");
                return authToken;
            } else {
                helpForError(conn);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String register(String username, String password, String email) {
        try {
            URL url = new URL(BASE_URL +portNumb+ "/user");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");

            JsonObject registrationData = new JsonObject();
            registrationData.addProperty("username", username);
            registrationData.addProperty("password", password);
            registrationData.addProperty("email", email);

            String jsonData = new Gson().toJson(registrationData);
            conn.getOutputStream().write(jsonData.getBytes());
            conn.connect();

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                String authToken = conn.getHeaderField("Authorization");
                System.out.println(EscapeSequences.ERASE_SCREEN);
                System.out.println("Registration successful!");
                return authToken;
            } else {
                helpForError(conn);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean logout(String authToken) {
        try {
            URL url = new URL(BASE_URL +portNumb+ "/session");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE");
            conn.setRequestProperty("Authorization", authToken);
            conn.connect();

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                System.out.print(EscapeSequences.SET_TEXT_COLOR_GREEN);
                System.out.println("Logout successful");
                System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE);
                return true;
            } else {
                helpForError(conn);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean createGame(String authToken, String gameName) {
        try {
            URL url = new URL(BASE_URL + portNumb + "/game");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("authorization", authToken);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");

            JsonObject gameData = new JsonObject();
            gameData.addProperty("gameName", gameName);

            String jsonData = new Gson().toJson(gameData);
            conn.getOutputStream().write(jsonData.getBytes());
            conn.connect();

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                System.out.println("Game created successfully!");
                return true;
            } else {
                helpForError(conn);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void listGames(String authToken) {
        try {
            URL url = new URL(BASE_URL +portNumb+ "/game");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("authorization", authToken);
            conn.connect();

            int responseCode = conn.getResponseCode();
            if(responseCode == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JsonObject responseObject = JsonParser.parseString(response.toString()).getAsJsonObject();
                JsonArray gamesArray = responseObject.getAsJsonArray("games");
                if (gamesArray != null) {
                    int gameNumbered = 1;
                    gameNumberToIdMap.clear();
                    for (JsonElement gameElement : gamesArray) {
                        JsonObject gameObject = gameElement.getAsJsonObject();
                        int gameID = gameObject.get("gameID").getAsInt();
                        String gameName = gameObject.get("gameName").getAsString();
                        String whiteUsername = getStringOrNull(gameObject, "whiteUsername");
                        String blackUsername = getStringOrNull(gameObject, "blackUsername");
                        gameNumberToIdMap.put(gameNumbered, gameID);
                        System.out.print("Game #: " + gameNumbered + ", Game Name: " + gameName);
                        System.out.print(", Players: ");
                        if(whiteUsername == null) {
                            System.out.print("White Player is Empty, ");
                        } else {
                            System.out.print(whiteUsername + ", ");
                        }

                        if(blackUsername == null) {
                            System.out.println("Black Player is Empty");
                        } else {
                            System.out.println(blackUsername);
                        }
                        gameNumbered++;
                    }
                } else {
                    System.out.println("No games found.");
                }
            } else {
                String error = conn.getResponseMessage();
                System.out.println("Failed to list games: " + error);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getStringOrNull(JsonObject jsonObject, String key) {
        JsonElement element = jsonObject.get(key);
        return element != null && !element.isJsonNull() ? element.getAsString() : null;
    }

    public boolean connect(int gameID, String whiteOrBlack, String authToken) {
        try {
            URL url = new URL(BASE_URL + portNumb + "/game");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("authorization", authToken);
            conn.setDoOutput(true);

            JsonObject joinGameData = new JsonObject();
            joinGameData.addProperty("playerColor", whiteOrBlack.toUpperCase());
            int actualGameId = -1;
            if(gameNumberToIdMap.get(gameID) != null) {
                actualGameId = gameNumberToIdMap.get(gameID);
            }
            joinGameData.addProperty("gameID", actualGameId);

            String jsonData = new Gson().toJson(joinGameData);
            conn.getOutputStream().write(jsonData.getBytes());
            conn.connect();

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                System.out.println("Joined game successfully!");
                return true;
            } else {
                helpForError2(conn);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
