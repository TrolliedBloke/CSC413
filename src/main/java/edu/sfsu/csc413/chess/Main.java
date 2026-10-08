/**
 * Entry point.
 *
 * <p>At M0 this does nothing but prove the toolchain works. It grows into the
 * real launcher as the engine appears underneath it.
 */
package edu.sfsu.csc413.chess;

import edu.sfsu.csc413.chess.engine.Game;
import java.util.List;
import edu.sfsu.csc413.chess.view.PieceGlyphs;
import edu.sfsu.csc413.chess.view.TextBoardRenderer;

public final class Main {

    public static void main(String[] args) {

    Game game = new Game();

    TextBoardRenderer renderer =
            new TextBoardRenderer(PieceGlyphs.LETTERS);

    // Print the starting board
    System.out.println(renderer.render(game.board()));

    // Play two moves
    for (String notation : List.of("e2e4", "e7e5")) {
        game.play(game.findLegalMove(notation).orElseThrow());
    }

    // Print the board after both moves
    System.out.println(renderer.render(game.board()));

    // Undo the last move
    game.undoLastMove();

    // Print the board after undoing
    System.out.println(renderer.render(game.board()));
}

    private Main() {
    }
}