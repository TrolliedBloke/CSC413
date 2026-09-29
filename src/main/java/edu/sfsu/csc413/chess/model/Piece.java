package edu.sfsu.csc413.chess.model;

import java.util.ArrayList;
import java.util.List;

public abstract class Piece {

    private final Color color;
    private final PieceType type;

    protected Piece(Color color, PieceType type) {
        this.color = color;
        this.type = type;
    }

    public Color color() {
        return color;
    }

    public PieceType type() {
        return type;
    }

    public char symbol() {
        char symbol = type.symbol();

        if (color == Color.BLACK) {
            return Character.toLowerCase(symbol);
        }

        return symbol;
    }

    public abstract List<Move> pseudoLegalMoves(Board board, Position from);

    public boolean attacks(Board board, Position from, Position target) {
        for (Move move : pseudoLegalMoves(board, from)) {
            if (move.to().equals(target)) {
                return true;
            }
        }

        return false;
    }

    protected List<Move> slidingMoves(Board board, Position from, int[][] directions) {
    List<Move> moves = new ArrayList<>();

    for (int[] direction : directions) {
        int fileOffset = direction[0];
        int rankOffset = direction[1];

        Position current = from.offsetOrNull(fileOffset, rankOffset);

        while (current != null) {
            Piece occupant = board.pieceAt(current);

            if (occupant == null) {
                moves.add(Move.quiet(from, current, this));
            } else {
                if (occupant.color() != color) {
                    moves.add(Move.capture(from, current, this, occupant));
                }

                break;
            }

            current = current.offsetOrNull(fileOffset, rankOffset);
        }
    }

    return moves;
}

protected List<Move> steppingMoves(Board board, Position from, int[][] offsets) {
    List<Move> moves = new ArrayList<>();

    for (int[] offset : offsets) {
        Position target = from.offsetOrNull(offset[0], offset[1]);

        if (target == null) {
            continue;
        }

        Piece occupant = board.pieceAt(target);

        if (occupant == null) {
            moves.add(Move.quiet(from, target, this));
        } else if (occupant.color() != color) {
            moves.add(Move.capture(from, target, this, occupant));
        }
    }

    return moves;
}

    @Override
    public String toString() {
        return String.valueOf(symbol());
    }
}