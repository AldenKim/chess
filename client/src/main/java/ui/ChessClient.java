package ui;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class ChessClient {
    private static final Scanner scanner = new Scanner(System.in);
    private static final String LOGGED_OUT_PREFIX = "[LOGGED-OUT] >>> ";
    private static final String LOGGED_IN_PREFIX = "[LOGGED-IN] >>> ";
    private static boolean isLoggedIn = false;

    public static void pre_loginUI() {
        System.out.println("\nWelcome to the Chess Game");
        while (!isLoggedIn) {
            System.out.println("\nOptions:");
            System.out.println("1. Help");
            System.out.println("2. Quit");
            System.out.println("3. Login");
            System.out.println("4. Register");

            System.out.print(LOGGED_OUT_PREFIX);
            String userInput = scanner.nextLine().trim().toLowerCase();

            switch (userInput) {
                case "1":
                case "help":
                    displayHelpTextPre();
                    break;
                case "2":
                case "quit":
                    quitProgram();
                    break;
                case "3":
                case "login":
                    login();
                    break;
                case "4":
                case "register":
                    register();
                    break;
                default:
                    System.out.println("Invalid input, Please try again.");
                    break;
            }
        }
    }

    public static void post_loginUI(String authToken) {
        System.out.println("\nPost Login Options:");
        System.out.println("1. Help");
        System.out.println("2. Logout");
        System.out.println("3. Create Game");
        System.out.println("4. List Games");
        System.out.println("5. Join Game");
        System.out.println("6. Join Observer");

        while(true) {
            System.out.println();
            System.out.print(LOGGED_IN_PREFIX);
            String userInput = scanner.nextLine().toLowerCase();

            switch (userInput) {
                case "1":
                case "help":
                    displayHelpTextPost();
                    break;
                case "2":
                case "logout":
                    logout(authToken);
                    break;
                case "3":
                case "create game":

                    break;
                case "4":
                case "list games":
                    break;
                case "5":
                case "join game":
                    break;
                case "6":
                case "join observer":
                    break;
            }
        }
    }

    private static void displayHelpTextPre() {
        System.out.println("Help - Possible commands");
        System.out.println("Quit - Exit Program, quit playing chess");
        System.out.println("Login - To play");
        System.out.println("Register - Create an account");
    }

    private static void displayHelpTextPost() {
        System.out.println("1. Help - Possible commands");
        System.out.println("2. Logout - Logout and quit from program");
        System.out.println("3. Create Game - Possible commands");
        System.out.println("4. List Games - Possible commands");
        System.out.println("5. Join Game - Possible commands");
        System.out.println("6. Join Observer - Possible commands");
    }

    private static void quitProgram() {
        System.out.println("Leaving Game. Bye!");
        System.exit(0);
    }

    public static void login() {
        System.out.println("\nPlease provide correct login information:");
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        try {
            URL url = new URL("http://localhost:8080/session");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            conn.addRequestProperty("Content-type", "application/json");

            JsonObject loginData = new JsonObject();
            loginData.addProperty("username", username);
            loginData.addProperty("password", password);

            var jsonData = new Gson().toJson(loginData);
            conn.getOutputStream().write(jsonData.getBytes());
            conn.connect();

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                String authToken = conn.getHeaderField("authorization");
                System.out.println("Login successful");
                System.out.println("Logged in as: " + username);
                post_loginUI(authToken);
                isLoggedIn = true;
            } else {
                String error = conn.getResponseMessage();
                System.out.println("Login failed: " + error);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void register() {
        System.out.println("\nPlease provide registration information:");
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();

        try {
            URL url = new URL("http://localhost:8080/user");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            conn.addRequestProperty("Content-Type", "application/json");

            JsonObject registrationData = new JsonObject();
            registrationData.addProperty("username", username);
            registrationData.addProperty("password", password);
            registrationData.addProperty("email", email);

            var jsonData = new Gson().toJson(registrationData);
            conn.getOutputStream().write(jsonData.getBytes());
            conn.connect();

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                String authToken = conn.getHeaderField("authorization");
                System.out.println("Registration successful!");
                System.out.println("Logged in as: " + username);
                post_loginUI(authToken);
                isLoggedIn = true;
            } else {
                String error = conn.getResponseMessage();
                System.out.println("Registration failed: " + error);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void logout(String authToken) {
        try {
            URL url = new URL("http://localhost:8080/session");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE");
            conn.setRequestProperty("authorization", authToken);
            conn.connect();

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                System.out.println("Logout successful");
                isLoggedIn = false;
                pre_loginUI();
            } else {
                String error = conn.getResponseMessage();
                System.out.println("Logout failed: " + error);
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public static void createGame(String authToken) {

    }
}
