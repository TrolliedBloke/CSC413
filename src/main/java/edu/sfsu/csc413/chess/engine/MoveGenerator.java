package edu.sfsu.csc413.chess.engine;

import edu.sfsu.csc413.chess.model.Board;
import edu.sfsu.csc413.chess.model.Color;
import edu.sfsu.csc413.chess.model.Move;
import edu.sfsu.csc413.chess.model.Piece;
import edu.sfsu.csc413.chess.model.Position;
import java.util.ArrayList;

import java.util.List;

/**
 * Generates the moves a side may play in a given position.
 *
 * <p>This class exists because of a question no single piece can answer. A
 * piece knows how it moves; it does not know the rule that applies to every
 * piece at once: <strong>you may not leave your own king attacked.</strong>
 * That rule needs the whole board, and it arrives in M5. This week the class
 * is where the generation loop lives, moved out of {@code Game}, so that when
 * the rule arrives it has a home.
 *
 * <p>Notice what this class is not. It holds no state: no board, no side to
 * move, no history. Everything it needs arrives as a parameter, so its methods
 * are {@code static} and it cannot be instantiated. Ask it the same question
 * about the same board twice and you get the same answer.
 *
 * <p>Nothing outside the {@code engine} package needs this class. The view and
 * {@code Main} talk to {@link Game}, which decides what "legal" means. That is
 * why one of the two methods below has no access modifier.
 */
public final class MoveGenerator {

    private MoveGenerator() {
    }

    /**
     * Every move {@code color} may legally play in the given position.
     *
     * <p>At M4 this is {@link #pseudoLegalMoves} unfiltered: the right colour
     * and the right geometry. M5 adds the one global rule, king safety, here
     * and only here. {@code Game.legalMoves()} calls this and does not change
     * when it tightens.
     */
    public static List<Move> legalMoves(Board board, Color color) {
    List<Move> legalMoves = new ArrayList<>();

    for (Move move : pseudoLegalMoves(board, color)) {
        board.apply(move);

        boolean leavesKingInCheck = isInCheck(board, color);

        board.undo(move);

        if (!leavesKingInCheck) {
            legalMoves.add(move);
        }
    }

    return legalMoves;
}


    /**
     * Every geometrically possible move for {@code color}, king safety aside.
     *
     * <p>This is the loop from session 6 §3 and M3's {@code Game.legalMoves()},
     * moved here unchanged. Note how little it knows: it never asks what kind
     * of piece it is looking at. It asks each piece for its own moves.
     *
     * <p>Package-private on purpose. Only {@code Game} and this class's tests,
     * both in {@code engine}, may call it. A view that wants the moves asks
     * {@code Game}.
     */
    static List<Move> pseudoLegalMoves(Board board, Color color) {

    List<Move> moves = new ArrayList<>();

    for (Position from : board.positionsOf(color)) {
        moves.addAll(board.pieceAt(from).pseudoLegalMoves(board, from));
    }

    return moves;
}

/**
 * Returns whether the king of {@code color} is currently attacked by
 * the opposing color. A side with no king is not considered in check.
 */


/**
 * Returns whether any piece of {@code attacker} attacks {@code target}.
 */


/**
 * Returns true if any piece of {@code attacker} attacks {@code target}.
 */

/**
 * Returns the current game status for {@code sideToMove}.
 *
 * <p>This method is implemented in the next step. For now, it is only
 * present so the project can compile while the rest of M6 is added.
 *
 * @param board the current board
 * @param sideToMove the side whose turn it is
 * @return the current game status
 */
public static GameStatus status(Board board, Color sideToMove) {
    boolean inCheck = isInCheck(board, sideToMove);
    boolean hasLegalMoves = !legalMoves(board, sideToMove).isEmpty();

    if (inCheck && !hasLegalMoves) {
        return GameStatus.CHECKMATE;
    } else if (!inCheck && !hasLegalMoves) {
        return GameStatus.STALEMATE;
    } else if (inCheck) {
        return GameStatus.CHECK;
    } else {
        return GameStatus.IN_PROGRESS;
    }
}
public static boolean isAttacked(
        Board board,
        Position target,
        Color attacker) {

    for (Position from : board.positionsOf(attacker)) {
        Piece piece = board.pieceAt(from);

        if (piece.attacks(board, from, target)) {
            return true;
        }
    }

    return false;
}


/**
 * Returns true if the king of {@code color} is attacked by the opposing
 * color. A side without a king is not in check.
 */
public static boolean isInCheck(Board board, Color color) {
    Position king = board.kingPosition(color);

    if (king == null) {
        return false;
    }

    return isAttacked(board, king, color.opposite());
}


}
