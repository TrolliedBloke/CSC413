
# Instructions for coding agents

This is a chess engine built with MVC. The model and the controller were
written by hand and are finished. Your job in M7 is to add views only.

## Do

- Add new views under
  `src/main/java/edu/sfsu/csc413/chess/view/`.
- Implement the `BoardView` interface.
- Read `BoardView`, `ConsoleView`, `EmojiView`, `TextBoardRenderer`,
  `PlayerAction`, and `GameController` before writing a view.
- Read game state only through the `Game` methods used by the existing views:
  `board()`, `sideToMove()`, `legalMoves()`, `status()`, and `history()`.
- Send every player action to the controller:
  `controller.handle(new PlayerAction.Move("e2e4"))`.
- Add a command-line case in `Main.java` for each new view.
- Use only Java standard-library classes. Do not add dependencies.
- For Swing, `start(GameController)` must build the window and return
  immediately. It must not call `controller.runLoop()`.
- A modal dialog asking one question, such as which piece to use for
  promotion, is allowed.

## Don't

- Do not change anything under `model/`, `engine/`, or `factory/`.
- Do not change `BoardView`, `PlayerAction`, `GameController`,
  `ConsoleView`, or `EmojiView`.
- Do not call `game.play()` or `game.undoLastMove()` from a view.
  Only the controller changes the game.
- Do not implement chess rules in a view. Legal moves must come from
  `game.legalMoves()`, and check, checkmate, and stalemate must come from
  `game.status()`.
- Do not call `controller.runLoop()` from a Swing view.
- Do not add or change dependencies in `pom.xml`.
- Do not change tests or delete tests.

## Check your work

- Run `./mvnw test`; all existing tests must remain green.
- Run `./mvnw -q compile`.
- Confirm `git diff --stat submit-m6 HEAD` lists only allowed M7 files.
- Search new view files for `game.play` and `undoLastMove`; there must be
  no matches.
- Search the Swing view for `runLoop`; there must be no matches.
- Play Fool's Mate:
  `f2f3`, `e7e5`, `g2g4`, `d8h4`.
- Test one illegal move and the Undo action.
- Do not claim the Swing view works without opening and testing it.

## Stop and ask

If a file listed under "Don't" must change, stop and explain why instead
of changing it.


