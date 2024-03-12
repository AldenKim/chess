package ui;

public class GameUI {
    public static void run() {
        displayChessBoardFromWhite();
        System.out.println();
        displayChessBoardFromBlack();
    }

    private static void displayChessBoardFromWhite() {
        System.out.println("   a\u2003 b\u2003 c\u2003 d\u2003 e\u2003 f\u2003 g\u2003 h\u2003 ");
        for (int i = 1; i <= 8; i++) {
            System.out.print(i + " ");
            for (int j = 0; j < 8; j++) {
                if ((i + j) % 2 == 0) {
                    System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.EMPTY);
                } else {
                    System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.EMPTY);
                }
            }
            System.out.println(EscapeSequences.RESET_BG_COLOR);
        }
    }

    private static void displayChessBoardFromBlack() {
        System.out.println("   h\u2003 g\u2003 f\u2003 e\u2003 d\u2003 c\u2003 b\u2003 a\u2003 ");
        for (int i = 1; i <= 8; i++) {
            System.out.print(i + " ");
            for (int j = 8; j >= 1; j--) {
                if ((i + j) % 2 == 0) {
                    System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.EMPTY);
                } else {
                    System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.EMPTY);
                }
            }
            System.out.println(EscapeSequences.RESET_BG_COLOR);
        }
    }
}
