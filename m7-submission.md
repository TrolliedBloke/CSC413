# M7 Submission

## 1. Group

Alone.

## 2. Tools

ChatGPT/Codex using GPT-5.6.

## 3. Other view

The second view is started with `java -cp target/classes edu.sfsu.csc413.chess.Main terminal`.

## 4. Runs

### Run 1

Branch: `m7-run-1`  
Commit: `f4adad0`

Request: Add a graphical 2D view of the board using Swing. I want to play by clicking a piece and then clicking the square to move it to. Show whose turn it is and messages like check, checkmate, stalemate, and illegal moves. Add an Undo button that sends new PlayerAction.Undo(). Always choose a queen for promotion. Follow AGENTS.md. Do not modify model/, engine/, factory/, BoardView, PlayerAction, GameController, ConsoleView, EmojiView, or pom.xml.

Change: Added `SwingView` and the `swing` command-line option.

### Run 2

Branch: `m7-run-2`  
Commit: `ed342da`

Request: Add a second playable BoardView called TerminalView using only the JDK. It must display the chessboard with a clear border, show pieces and coordinates, flip the board orientation so the side to move is displayed from the bottom, and allow a complete game through GameController. It must send moves, undo, show-moves, and quit through PlayerAction and GameController. Follow AGENTS.md. Do not modify model/, engine/, factory/, BoardView, PlayerAction, GameController, ConsoleView, EmojiView, or pom.xml.

Change: Added `TerminalView` and the `terminal` command-line option.

### Run 3

Branch: `m7-run-3`  
Commits: `8cc7f42`, `c08a2f4`

Request: Add a third playable BoardView called AnsiView using only the JDK. It must draw a bordered board with ANSI-colored square backgrounds, distinguish white and black pieces with colors, orient the board from the side to move, and support complete games through GameController. It must send moves, undo, show-moves, and quit through PlayerAction and GameController. Follow AGENTS.md. Do not modify model/, engine/, factory/, BoardView, PlayerAction, GameController, ConsoleView, EmojiView, or pom.xml.

Change: Added and tested `AnsiView`. I did not keep it in final `main`.

## 5. AGENTS.md

I added the rule that Swing must build its window and return without calling `controller.runLoop()`. I also clarified that a modal dialog for one question, such as promotion, is allowed. This prevents the event-driven Swing view from being confused with the console game loop.

`CLAUDE.md` contains `@AGENTS.md`.

## 6. Review

`SwingView.java:142-170`: `squareClicked` converts board clicks into positions and sends moves through `PlayerAction.Move`. I checked it manually and with the debugger. Kept.

`SwingView.java:173-182`: `legalTargetsFrom` obtains destinations from `game.legalMoves()` instead of calculating chess rules. I checked the highlighted legal destinations. Kept.

`SwingView.java:234-235`: the view uses `game.status()` to identify check and checkmate. I checked this by playing Fool's Mate. Kept.

`TerminalView.java:28-61`: the view uses the side-to-move perspective, the controller loop, and `PlayerAction.parse`. I tested Fool's Mate through terminal input. Kept.

## 7-11. Comparison

The half-finished move state lives in SwingView's `selected` field. TerminalView has no half-finished selection because the scanner reads a complete move string.

SwingView asks `game.legalMoves()` in `SwingView.java:173-182`. TerminalView sends typed actions to GameController, which asks Game for legal moves.

Both views use the controller's default promotion choice, which is a queen.

TerminalView is easier to change because it uses plain text and the controller loop. SwingView is more complex because it handles painting, mouse events, coordinate conversion, and window lifecycle. I kept both views.

## 12. Applied

I applied the Run 3 idea of making the side-to-move perspective explicit. The final `TerminalView.java:29` uses `game.sideToMove()` to determine the perspective. I kept the simpler bordered terminal design instead of the ANSI colors.

## 13. Debugger evidence

I found no real runtime bug. I set a breakpoint in `SwingView.squareClicked` at line 143 using `jdb`.

For the first click on e2, I predicted `file = 4`, `rank = 1`, and `selected = null`. The debugger showed exactly those values. After the click, `selected` became `"e2"`.

The Swing window froze while the breakpoint was active because the debugger paused the `AWT-EventQueue-0` thread. Continuing the debugger restored the window.

## 14. By hand

Commit: `502b076 M7 by hand: change selected-square highlight`

I changed the selected-square highlight in `SwingView.java` from gold, `new java.awt.Color(255, 215, 0, 170)`, to orange, `new java.awt.Color(255, 140, 0, 170)`. I read the Swing painting code to verify that this color is used for the selected square. Afterward, `./mvnw clean test` passed all 83 tests.
