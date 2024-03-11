package ui;

import com.google.gson.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ServerFacade {
    private static final String BASE_URL = "http://localhost:8080";

    public static String login(String username, String password) {
        try {
            URL url = new URL(BASE_URL + "/session");
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
                String error = conn.getResponseMessage();
                System.out.println("Login failed: " + error);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String register(String username, String password, String email) {
        try {
            URL url = new URL(BASE_URL + "/user");
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
                System.out.println("Registration successful!");
                return authToken;
            } else {
                String error = conn.getResponseMessage();
                System.out.println("Registration failed: " + error);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean logout(String authToken) {
        try {
            URL url = new URL(BASE_URL + "/session");
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
                String error = conn.getResponseMessage();
                System.out.println("Logout failed: " + error);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean createGame(String authToken, String gameName) {
        try {
            URL url = new URL(BASE_URL + "/game");
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
                String error = conn.getResponseMessage();
                System.out.println("Failed to create game: " + error);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void listGames(String authToken) {
        try {
            URL url = new URL(BASE_URL + "/game");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("authorization", authToken);
            conn.connect();

            int responseCode = conn.getResponseCode();
            if(responseCode == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                int gameNumber = 0;
                while((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JsonObject responseObject = JsonParser.parseString(response.toString()).getAsJsonObject();
                JsonArray gamesArray = responseObject.getAsJsonArray("games");
                if (gamesArray != null) {
                    for (JsonElement gameElement : gamesArray) {
                        JsonObject gameObject = gameElement.getAsJsonObject();
                        String gameName = gameObject.get("gameName").getAsString();
                        String whiteUsername = getStringOrNull(gameObject, "whiteUsername");
                        String blackUsername = getStringOrNull(gameObject, "blackUsername");
                        System.out.print("Game #: " + gameNumber + ", Game Name: " + gameName);
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
                        gameNumber++;
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
}
