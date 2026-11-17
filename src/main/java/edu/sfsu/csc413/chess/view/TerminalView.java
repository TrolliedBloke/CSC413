package edu.sfsu.csc413.chess.view;

import edu.sfsu.csc413.chess.engine.Game;
import edu.sfsu.csc413.chess.model.Color;

import java.io.PrintStream;
import java.util.Scanner;

public class TerminalView implements BoardView {

    private static final String BORDER = "    +---------------------+";

    private final TextBoardRenderer renderer =
            new TextBoardRenderer(PieceGlyphs.LETTERS);
    private final PrintStream out;
    private final Scanner in;

    public TerminalView() {
        this(System.out, new Scanner(System.in));
    }

    public TerminalView(PrintStream out, Scanner in) {
        this.out = out;
        this.in = in;
    }

    @Override
    public void render(Game game) {
        Color perspective = game.sideToMove();

        out.println();
        out.println("    " + perspective + " to move");
        out.println(BORDER);

        String board = renderer.render(game.board(), perspective);

        for (String line : board.split("\\R")) {
            out.println("    | " + line + " |");
        }

        out.println(BORDER);
        out.println("    Status: " + game.status());
        out.println();
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
