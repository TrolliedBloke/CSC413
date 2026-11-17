package edu.sfsu.csc413.chess.view;

import edu.sfsu.csc413.chess.engine.Game;
import edu.sfsu.csc413.chess.engine.GameStatus;
import edu.sfsu.csc413.chess.model.Board;
import edu.sfsu.csc413.chess.model.Color;
import edu.sfsu.csc413.chess.model.Move;
import edu.sfsu.csc413.chess.model.Piece;
import edu.sfsu.csc413.chess.model.PieceType;
import edu.sfsu.csc413.chess.model.Position;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Set;

public class SwingView implements BoardView {

    private GameController controller;
    private Game game;
    private JFrame frame;
    private BoardPanel boardPanel;
    private JLabel statusLabel;
    private JLabel messageLabel;
    private Position selected;

    @Override
    public void render(Game game) {
        this.game = game;

        if (statusLabel != null) {
            statusLabel.setText(statusText());
        }

        if (boardPanel != null) {
            boardPanel.repaint();
        }
    }

    @Override
    public void showMessage(String message) {
        if (messageLabel != null) {
            messageLabel.setText(message);
        }
    }

    @Override
    public void start(GameController controller) {
        this.controller = controller;

        if (SwingUtilities.isEventDispatchThread()) {
            buildWindow();
        } else {
            SwingUtilities.invokeLater(this::buildWindow);
        }
    }

    @Override
    public void close() {
        if (frame != null) {
            frame.dispose();
        }
    }

    private void buildWindow() {
        if (frame != null) {
            return;
        }

        frame = new JFrame("CSC 413 Chess");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout(8, 8));

        statusLabel = new JLabel(statusText());
        statusLabel.setBorder(BorderFactory.createEmptyBorder(8, 8, 0, 8));
        statusLabel.setFont(statusLabel.getFont().deriveFont(Font.BOLD, 16f));

        boardPanel = new BoardPanel();
        boardPanel.setPreferredSize(new Dimension(720, 720));

        JPanel buttonPanel = new JPanel();

        JButton undoButton = new JButton("Undo");
        undoButton.addActionListener(event -> {
            selected = null;
            controller.handle(new PlayerAction.Undo());
        });

        JButton quitButton = new JButton("Quit");
        quitButton.addActionListener(event ->
                controller.handle(new PlayerAction.Quit()));

        buttonPanel.add(undoButton);
        buttonPanel.add(quitButton);

        messageLabel = new JLabel(" ");
        messageLabel.setBorder(BorderFactory.createEmptyBorder(0, 8, 8, 8));

        frame.add(statusLabel, BorderLayout.NORTH);
        frame.add(boardPanel, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        JPanel messageContainer = new JPanel(new BorderLayout());
        messageContainer.add(buttonPanel, BorderLayout.NORTH);
        messageContainer.add(messageLabel, BorderLayout.SOUTH);
        frame.add(messageContainer, BorderLayout.SOUTH);

        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
    }

    private String statusText() {
        if (game == null) {
            return "Chess";
        }

        String side = game.sideToMove() == Color.WHITE ? "White" : "Black";

        return switch (game.status()) {
            case IN_PROGRESS -> side + " to move";
            case CHECK -> side + " to move — CHECK";
            case CHECKMATE -> "CHECKMATE";
            case STALEMATE -> "STALEMATE";
        };
    }

    private void squareClicked(int file, int rank) {
        if (game == null || game.isGameOver()) {
            return;
        }

        Position target = new Position(file, rank);
        Piece piece = game.board().pieceAt(target);

        if (selected == null) {
            if (piece != null && piece.color() == game.sideToMove()) {
                selected = target;
                boardPanel.repaint();
            } else {
                showMessage("Select one of your pieces.");
            }
            return;
        }

        if (selected.equals(target)) {
            selected = null;
            boardPanel.repaint();
            return;
        }

        Position from = selected;
        selected = null;

        controller.handle(new PlayerAction.Move(
                from.toString() + target.toString()));
    }

    private Set<Position> legalTargetsFrom(Position from) {
        Set<Position> targets = new HashSet<>();

        if (game == null) {
            return targets;
        }

        for (Move move : game.legalMoves()) {
            if (move.from().equals(from)) {
                targets.add(move.to());
            }
        }

        return targets;
    }

    private final class BoardPanel extends JPanel {

        BoardPanel() {
            setBackground(java.awt.Color.BLACK);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent event) {
                    int squareSize = Math.min(getWidth(), getHeight()) / 8;

                    if (squareSize <= 0) {
                        return;
                    }

                    int file = event.getX() / squareSize;
                    int screenRank = event.getY() / squareSize;
                    int rank = 7 - screenRank;

                    if (Position.isOnBoard(file, rank)) {
                        squareClicked(file, rank);
                    }
                }
            });
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);

            if (game == null) {
                return;
            }

            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            int squareSize = Math.min(getWidth(), getHeight()) / 8;
            Board board = game.board();
            Set<Position> legalTargets = selected == null
                    ? Set.of()
                    : legalTargetsFrom(selected);

            Position checkedKing = null;
            GameStatus status = game.status();

            if (status == GameStatus.CHECK
                    || status == GameStatus.CHECKMATE) {
                checkedKing = board.kingPosition(game.sideToMove());
            }

            for (int screenRank = 0; screenRank < 8; screenRank++) {
                for (int file = 0; file < 8; file++) {
                    int rank = 7 - screenRank;
                    int x = file * squareSize;
                    int y = screenRank * squareSize;
                    Position position = new Position(file, rank);

                    boolean lightSquare = (file + rank) % 2 == 0;
                    g.setColor(lightSquare
                            ? new java.awt.Color(238, 238, 210)
                            : new java.awt.Color(118, 150, 86));
                    g.fillRect(x, y, squareSize, squareSize);

                    if (selected != null && selected.equals(position)) {
                        g.setColor(new java.awt.Color(255, 215, 0, 170));
                        g.fillRect(x, y, squareSize, squareSize);
                    } else if (legalTargets.contains(position)) {
                        g.setColor(new java.awt.Color(255, 255, 100, 170));
                        g.fillRect(x, y, squareSize, squareSize);
                    }

                    if (checkedKing != null && checkedKing.equals(position)) {
                        g.setColor(new java.awt.Color(220, 60, 60, 180));
                        g.fillRect(x, y, squareSize, squareSize);
                    }

                    Piece piece = board.pieceAt(position);

                    if (piece != null) {
                        drawPiece(g, piece, x, y, squareSize);
                    }

                    g.setColor(new java.awt.Color(60, 60, 60, 180));
                    g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));

                    if (rank == 0) {
                        g.drawString(
                                String.valueOf((char) ('a' + file)),
                                x + 4,
                                y + squareSize - 4);
                    }

                    if (file == 0) {
                        g.drawString(
                                String.valueOf(rank + 1),
                                x + 4,
                                y + 14);
                    }
                }
            }

            g.dispose();
        }

        private void drawPiece(
                Graphics2D g,
                Piece piece,
                int x,
                int y,
                int squareSize) {

            String glyph = pieceGlyph(piece);
            Font font = new Font(
                    "Serif",
                    Font.BOLD,
                    Math.max(24, (int) (squareSize * 0.72)));

            g.setFont(font);
            FontMetrics metrics = g.getFontMetrics();

            int textX = x + (squareSize - metrics.stringWidth(glyph)) / 2;
            int textY = y
                    + (squareSize - metrics.getHeight()) / 2
                    + metrics.getAscent();

            java.awt.Color mainColor = piece.color() == Color.WHITE
                    ? java.awt.Color.WHITE
                    : java.awt.Color.BLACK;

            java.awt.Color outlineColor = piece.color() == Color.WHITE
                    ? java.awt.Color.BLACK
                    : java.awt.Color.WHITE;

            g.setColor(outlineColor);
            g.setStroke(new BasicStroke(3f));
            g.drawString(glyph, textX - 1, textY);
            g.drawString(glyph, textX + 1, textY);
            g.drawString(glyph, textX, textY - 1);
            g.drawString(glyph, textX, textY + 1);

            g.setColor(mainColor);
            g.drawString(glyph, textX, textY);
        }

        private String pieceGlyph(Piece piece) {
            boolean white = piece.color() == Color.WHITE;

            return switch (piece.type()) {
                case KING -> white ? "♔" : "♚";
                case QUEEN -> white ? "♕" : "♛";
                case ROOK -> white ? "♖" : "♜";
                case BISHOP -> white ? "♗" : "♝";
                case KNIGHT -> white ? "♘" : "♞";
                case PAWN -> white ? "♙" : "♟";
            };
        }
    }
}
