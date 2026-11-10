package edu.sfsu.csc413.chess.view;

import edu.sfsu.csc413.chess.engine.Game;
import edu.sfsu.csc413.chess.engine.GameStatus;
import edu.sfsu.csc413.chess.model.Color;
import edu.sfsu.csc413.chess.model.Move;
import edu.sfsu.csc413.chess.model.PieceType;

import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Connects the player's decisions to the game.
 */
public class GameController {

    private final Game game;
    private final BoardView view;

    public GameController(Game game, BoardView view) {
        this.game = game;
        this.view = view;
    }

    /** The game being played. */
    public Game game() {
        return game;
    }

    /**
     * Greets the player, draws the opening position, and starts the view.
     */
    public void start() {
        view.showMessage("Welcome to chess!");
        view.render(game);
        view.start(this);
    }

    /**
     * Asks for and handles actions until the player quits or the game ends.
     */
    public void runLoop() {
        while (!game.isGameOver()) {
            PlayerAction action = view.nextAction(game);
            handle(action);

            if (action instanceof PlayerAction.Quit) {
                return;
            }
        }
    }

    /**
     * Carries out one player action and redraws when appropriate.
     */
    public void handle(PlayerAction action) {
        switch (action) {
            case PlayerAction.Move move -> handleMove(move.notation());

            case PlayerAction.Undo undo -> {
                if (game.undoLastMove().isEmpty()) {
                    view.showMessage("Nothing to undo.");
                }
            }

            case PlayerAction.ShowMoves showMoves -> {
                String moves = game.legalMoves()
                        .stream()
                        .map(Move::toString)
                        .sorted()
                        .collect(Collectors.joining(" "));

                view.showMessage(game.legalMoves().size()
                        + " legal moves: " + moves);
            }

            case PlayerAction.Quit quit -> {
                view.showMessage("Goodbye.");
                view.close();
                return;
            }
        }

        view.render(game);

        if (game.isGameOver()) {
            announceResult();
            view.close();
        }
    }

    private void handleMove(String notation) {
        Optional<Move> move = game.findLegalMove(notation);

        if (move.isEmpty() && notation.length() == 4) {
            PieceType choice = view.promotionChoice(game);
            char promotionLetter =
                    Character.toLowerCase(choice.symbol());

            move = game.findLegalMove(notation + promotionLetter);
        }

        if (move.isPresent()) {
            game.play(move.get());
        } else {
            view.showMessage("Not a legal move: '" + notation + "'.");
        }
    }

    private void announceResult() {
        GameStatus status = game.status();

        if (status == GameStatus.CHECKMATE) {
            Color winner = game.sideToMove().opposite();
            view.showMessage("Checkmate. " + winner + " wins.");
        } else if (status == GameStatus.STALEMATE) {
            view.showMessage("Stalemate. The game is a draw.");
        }
    }
}