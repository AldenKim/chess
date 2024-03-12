package ui;

import java.util.Scanner;

public class ChessClient {
    private static final Scanner scanner = new Scanner(System.in);
    private static final String LOGGED_OUT_PREFIX = "[LOGGED-OUT] >>> ";
    private static final String LOGGED_IN_PREFIX = "[LOGGED-IN] >>> ";
    private static boolean isLoggedIn = false;

    public ChessClient() {
        pre_loginUI();
    }

    private static void pre_loginUI() {
        System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE);
        System.out.print(EscapeSequences.SET_BG_COLOR_BLACK);
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

    private static void post_loginUI(String authToken) {
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
                    createGame(authToken);
                    break;
                case "4":
                case "list games":
                    listGames(authToken);
                    break;
                case "5":
                case "join game":
                    joinGame(authToken);
                    break;
                case "6":
                case "join observer":
                    joinObserver(authToken);
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

    private static void login() {
        System.out.println("\nPlease provide correct login information:");
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        String loginSuccessAndAuth = ServerFacade.login(username, password);
        if(loginSuccessAndAuth != null) {
            System.out.println("\nLogged in as: " + EscapeSequences.SET_TEXT_COLOR_GREEN+ username);
            System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE);
            isLoggedIn = true;
            post_loginUI(loginSuccessAndAuth);
        }
    }

    private static void register() {
        System.out.println("\nPlease provide registration information:");
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();

        String registerSuccessAndAuth = ServerFacade.register(username, password, email);
        if(registerSuccessAndAuth!=null) {
            System.out.println("\nLogged in as: " + EscapeSequences.SET_TEXT_COLOR_GREEN+ username);
            System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE);
            isLoggedIn = true;
            post_loginUI(registerSuccessAndAuth);
        }
    }

    private static void logout(String authToken) {
        boolean logoutSuccess = ServerFacade.logout(authToken);
        if (logoutSuccess) {
            isLoggedIn = false;
            pre_loginUI();
        }
    }

     private static void createGame(String authToken) {
        System.out.println("\nEnter the name of the new game:");
        String gameName = scanner.nextLine();

        boolean createGameSuccess = ServerFacade.createGame(authToken, gameName);
        if (createGameSuccess) {
            post_loginUI(authToken);
        }
    }

    private static void listGames(String authToken) {
        System.out.println("List of Games: \n");
        ServerFacade.listGames(authToken);
    }

    private static void joinGame(String authToken) {
        System.out.println("Enter Game Number: ");
        int gameNum = Integer.parseInt(scanner.nextLine());
        System.out.println("Do you want to play as white or black?: ");
        String userColor = scanner.nextLine();

        ServerFacade.joinGame(gameNum, userColor, authToken);
    }

    private static void joinObserver(String authToken) {
        System.out.println("Enter Game Number: ");
        int gameNum = Integer.parseInt(scanner.nextLine());

        ServerFacade.joinObserver(gameNum, authToken);
    }
}
