package edu.sfsu.csc413.chess.view;

import edu.sfsu.csc413.chess.engine.Game;
import edu.sfsu.csc413.chess.model.Board;
import edu.sfsu.csc413.chess.model.Color;
import edu.sfsu.csc413.chess.model.Piece;
import edu.sfsu.csc413.chess.model.Position;

import java.io.PrintStream;
import java.util.Scanner;

public class AnsiView implements BoardView {

    private static final String RESET = "\u001B[0m";
    private static final String LIGHT_SQUARE = "\u001B[48;5;223m";
    private static final String DARK_SQUARE = "\u001B[48;5;137m";
    private static final String WHITE_PIECE = "\u001B[38;5;15m";
    private static final String BLACK_PIECE = "\u001B[38;5;0m";
    private static final String BOLD = "\u001B[1m";

    private final PrintStream out;
    private final Scanner in;

    public AnsiView() {
        this(System.out, new Scanner(System.in));
    }

    public AnsiView(PrintStream out, Scanner in) {
        this.out = out;
        this.in = in;
    }

    @Override
    public void render(Game game) {
        boolean flipped = game.sideToMove() == Color.BLACK;
        Board board = game.board();

        out.println();
        out.println("    " + game.sideToMove() + " to move");
        out.println(border());

        for (int row = 0; row < Position.BOARD_SIZE; row++) {
            int rank = flipped ? row : Position.BOARD_SIZE - 1 - row;
            StringBuilder line = new StringBuilder();
            line.append(" ").append(rank + 1).append(" |");

            for (int column = 0;
                 column < Position.BOARD_SIZE;
                 column++) {

                int file = flipped
                        ? Position.BOARD_SIZE - 1 - column
                        : column;

                Piece piece = board.pieceAt(new Position(file, rank));
                String background = (file + rank) % 2 == 0
                        ? LIGHT_SQUARE
                        : DARK_SQUARE;

                String foreground = piece == null
                        ? BLACK_PIECE
                        : piece.color() == Color.WHITE
                                ? WHITE_PIECE
                                : BLACK_PIECE;

                String glyph = piece == null
                        ? " "
                        : String.valueOf(piece.symbol());

                line.append(background)
                        .append(foreground)
                        .append(BOLD)
                        .append(" ")
                        .append(glyph)
                        .append(" ")
                        .append(RESET)
                        .append("|");
            }

            line.append(" ").append(rank + 1);
            out.println(line);
        }

        out.println(border());

        StringBuilder files = new StringBuilder("     ");
        for (int column = 0;
             column < Position.BOARD_SIZE;
             column++) {

            int file = flipped
                    ? Position.BOARD_SIZE - 1 - column
                    : column;

            files.append(" ")
                    .append((char) ('a' + file))
                    .append("  ");
        }

        out.println(files);
        out.println("    Status: " + game.status());
        out.println();
    }

    private String border() {
        StringBuilder line = new StringBuilder("    +");

        for (int i = 0; i < Position.BOARD_SIZE; i++) {
            line.append("---+");
        }

        return line.toString();
    }

    @Override
    public void showMessage(String message) {
        out.println(message);
    }

    @Override
    public void start(GameController controller) {
        controller.runLoop();
    }

    @Override
    public PlayerAction nextAction(Game game) {
        out.print("Your move (e.g. e2e4, 'undo', 'moves', 'quit'): ");
        out.flush();

        return PlayerAction.parse(
                in.hasNextLine() ? in.nextLine() : "quit");
    }
}
