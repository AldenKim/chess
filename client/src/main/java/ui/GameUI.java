package ui;

public class GameUI {
    public static void run() {
        displayChessBoardFromWhite();
        System.out.println();
        displayChessBoardFromBlack();
    }

    private static void displayChessBoardFromWhite() {
        System.out.println("   a  b  c  d  e  f  g  h ");
        for (int i = 1; i <= 8; i++) {
            System.out.print(i + " ");
            for (int j = 0; j < 8; j++) {
                if ((i + j) % 2 == 0) {
                    System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREY + "   ");
                } else {
                    System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY + "   ");
                }
            }
            System.out.println(EscapeSequences.RESET_BG_COLOR);
        }
    }

    private static void displayChessBoardFromBlack() {
        System.out.println("   h  g  f  e  d  c  b  a ");
        for (int i = 1; i <= 8; i++) {
            System.out.print(i + " ");
            for (int j = 8; j >= 1; j--) {
                if ((i + j) % 2 == 0) {
                    System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.SET_TEXT_COLOR_RED + " p ");
                } else {
                    System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.SET_TEXT_COLOR_BLUE + " P ");
                }
                System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE);
            }
            System.out.println(EscapeSequences.RESET_BG_COLOR);
        }
    }

    public static void main(String[] args) {
        displayChessBoardFromWhite();
        System.out.println();
        displayChessBoardFromBlack();
    }
}
