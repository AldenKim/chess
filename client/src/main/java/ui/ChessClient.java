package ui;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class ChessClient {
    private static final Scanner scanner = new Scanner(System.in);
    private static final String LOGGED_OUT_PREFIX = "[LOGGED-OUT] >>> ";

    public static void pre_loginUI() {
        System.out.println("Welcome to the Chess Game\n");
        while (true) {
            System.out.println("Options:");
            System.out.println("1. Help");
            System.out.println("2. Quit");
            System.out.println("3. Login");
            System.out.println("4. Register");

            System.out.print(LOGGED_OUT_PREFIX);
            String userInput = scanner.nextLine().trim().toLowerCase();

            switch (userInput) {
                case "1":
                case "help":
                    displayHelpText();
                    break;
                case "2":
                case "quit":
                    quitProgram();
                    break;
                case "3":
                case "login":
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

    public static void post_loginUI() {
        System.out.println("PLACE-HOLDER");
    }

    private static void displayHelpText() {
        System.out.println("Help - Possible commands");
        System.out.println("Quit - Exit Program, quit playing chess");
        System.out.println("Login - To play");
        System.out.println("Register - Create an account");
    }

    private static void quitProgram() {
        System.out.println("Leaving Game. Bye!");
        System.exit(0);
    }

    private static void register() {
        System.out.println("Please provide registration information:");
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

            String data = "username=" + username + "&password=" + password + "&email=" + email;
            conn.getOutputStream().write(data.getBytes());
            conn.connect();

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                System.out.println("Registration successful!");
                post_loginUI();
            } else {
                System.out.println("Registration failed. Please try again later.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
