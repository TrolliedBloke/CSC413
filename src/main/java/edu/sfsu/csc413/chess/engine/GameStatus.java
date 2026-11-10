package edu.sfsu.csc413.chess.engine;

/**
 * Describes the current status of a chess game.
 */
public enum GameStatus {
    IN_PROGRESS,
    CHECK,
    CHECKMATE,
    STALEMATE;

    /**
     * Returns whether this status means the game has ended.
     *
     * @return true for checkmate or stalemate
     */
    public boolean isGameOver() {
        return this == CHECKMATE || this == STALEMATE;
    }
}