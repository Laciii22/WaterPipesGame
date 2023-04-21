package sk.stuba.fei.uim.oop.controls;

import lombok.Getter;
import lombok.Setter;
import sk.stuba.fei.uim.oop.board.Board;
import sk.stuba.fei.uim.oop.tile.PipeRotation;
import sk.stuba.fei.uim.oop.tile.Tile;
import sk.stuba.fei.uim.oop.tile.TileType;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.*;

public class GameLogic extends UniversalAdapter {
    public static final int INITIAL_SIZE = 8;
    private final JFrame mainGame;
    private Board currentBoard;
    @Getter @Setter
    private int level = 1;
    @Getter
    private JLabel labelLevel;
    private int currentBoardSize;

    public GameLogic(JFrame mainGame) {
        this.mainGame = mainGame;
        this.currentBoardSize = INITIAL_SIZE;
        this.initializeNewBoard(this.currentBoardSize);
        this.mainGame.add(this.currentBoard);
        this.labelLevel = new JLabel();
        this.updateLevelLabel();
    }

    private void updateLevelLabel() {
        this.labelLevel.setText(" Level: " + this.getLevel() + " Board size: " + this.currentBoardSize);
        this.mainGame.revalidate();
        this.mainGame.repaint();
    }

    private void initializeNewBoard(int dimension) {
        this.currentBoard = new Board(dimension);
        this.currentBoard.addMouseMotionListener(this);
        this.currentBoard.addMouseListener(this);
    }

    private void gameRestart() {
        this.mainGame.remove(this.currentBoard);
        this.initializeNewBoard(this.currentBoardSize);
        this.mainGame.add(this.currentBoard);
        this.mainGame.revalidate();
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (this.currentBoardSize != ((JSlider) e.getSource()).getValue()) {
            this.currentBoardSize = ((JSlider) e.getSource()).getValue();
            this.setLevel(1);
            this.updateLevelLabel();
            this.gameRestart();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_R:
                this.setLevel(1);
                this.updateLevelLabel();
                this.gameRestart();
                break;
            case KeyEvent.VK_ESCAPE:
                this.mainGame.dispose();
                break;
            case KeyEvent.VK_ENTER:
                checkForWin();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        Component current = currentBoard.findComponentAt(e.getX(), e.getY());
        currentBoard.repaint();
        if (!(current instanceof Tile)) {
            for (Component component : currentBoard.getComponents()) {
                if (component instanceof Tile) {
                    ((Tile) component).setHighlighted(0);
                }
            }
        } else {
            for (Component component : currentBoard.getComponents()) {
                if (component instanceof Tile) {
                    Tile tile = (Tile) component;
                    tile.setHighlighted(component == current ? 2 : 0);
                }
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        Component current = currentBoard.findComponentAt(e.getX(), e.getY());
        if (current instanceof Tile) {
            Tile tile = (Tile) current;
            if (tile.isClickable()) {
                tile.rotate();
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Restart")) {
            this.setLevel(1);
            this.updateLevelLabel();
            this.gameRestart();
        } else if (e.getActionCommand().equals("Check")) {
            this.checkForWin();
        }
    }

    private void checkForWin() {
        Set<Point> visited = new HashSet<>();
        Point firstPipe = currentBoard.getPipes().get(0);
        checkForWinRec(firstPipe, visited);
        Point lastPipe = currentBoard.getPipes().get(currentBoard.getPipes().size() - 1);
        Tile lastPipeTile = currentBoard.getTiles()[lastPipe.x][lastPipe.y];
        if (lastPipeTile.getHighlighted()==1) {
            gameRestart();
            addLevel();
        }
        currentBoard.repaint();
    }

    private boolean checkForWinRec(Point current, Set<Point> visited) {
        visited.add(current);
        Tile currentTile = currentBoard.getTiles()[current.x][current.y];
        currentTile.setHighlighted(1);
        if (current ==  currentBoard.getPipes().get(currentBoard.getPipes().size() - 1)) {
            return true;
        }
        ArrayList<Point> neighbors = currentBoard.getNeighbors(current);
        boolean solved = false;

        for (Point neighbor : neighbors) {
            Tile neighborTile = currentBoard.getTiles()[neighbor.x][neighbor.y];
            currentTile = currentBoard.getTiles()[current.x][current.y];
            if (!visited.contains(neighbor) && isCorrectlyRotated(currentTile, neighborTile, current, neighbor)) {
                solved = checkForWinRec(neighbor, visited) || solved;
            }
        }
        return solved;
    }

    private boolean isCorrectlyRotated(Tile prevTile, Tile currentTile, Point prevPoint, Point currentPoint) {
        int prevRotation = prevTile.getRotation();
        int currentRotation = currentTile.getRotation();
        if (prevTile.getType() == TileType.STRAIGHT_PIPE && currentTile.getType() == TileType.STRAIGHT_PIPE) {
            if (prevPoint.x == currentPoint.x) {
                return (prevRotation == PipeRotation.HORIZONTAL.getRotation()) &&
                        (currentRotation == PipeRotation.HORIZONTAL.getRotation());
            } else if (prevPoint.y == currentPoint.y) {
                return (prevRotation == PipeRotation.VERTICAL.getRotation()) &&
                        (currentRotation == PipeRotation.VERTICAL.getRotation());
            }
        } else if (prevTile.getType() == TileType.KNEE_PIPE && currentTile.getType() == TileType.STRAIGHT_PIPE) {
            if (prevPoint.x == currentPoint.x && prevPoint.y < currentPoint.y) {
                return (prevRotation == PipeRotation.RIGHT_UP.getRotation()
                        || prevRotation == PipeRotation.RIGHT_DOWN.getRotation())
                        && (currentRotation == PipeRotation.HORIZONTAL.getRotation());
            } else if (prevPoint.x == currentPoint.x && prevPoint.y > currentPoint.y) {
                return (prevRotation == PipeRotation.LEFT_UP.getRotation()
                        || prevRotation == PipeRotation.LEFT_DOWN.getRotation())
                        && (currentRotation == PipeRotation.HORIZONTAL.getRotation());
            } else if (prevPoint.y == currentPoint.y && prevPoint.x < currentPoint.x) {
                return (prevRotation == PipeRotation.RIGHT_DOWN.getRotation()
                        || prevRotation == PipeRotation.LEFT_DOWN.getRotation())
                        && (currentRotation == PipeRotation.VERTICAL.getRotation());
            } else if (prevPoint.y == currentPoint.y && prevPoint.x > currentPoint.x) {
                return (prevRotation == PipeRotation.LEFT_UP.getRotation()
                        || prevRotation == PipeRotation.RIGHT_UP.getRotation())
                        && (currentRotation == PipeRotation.VERTICAL.getRotation());
            }
        } else if (prevTile.getType() == TileType.STRAIGHT_PIPE && currentTile.getType() == TileType.KNEE_PIPE) {
            if (prevPoint.x == currentPoint.x && prevPoint.y > currentPoint.y) {
                return (prevRotation == PipeRotation.HORIZONTAL.getRotation())
                        && (currentRotation == PipeRotation.RIGHT_UP.getRotation()
                        || currentRotation == PipeRotation.RIGHT_DOWN.getRotation());
            } else if (prevPoint.x == currentPoint.x && prevPoint.y < currentPoint.y) {
                return (prevRotation == PipeRotation.HORIZONTAL.getRotation())
                        && (currentRotation == PipeRotation.LEFT_UP.getRotation()
                        || currentRotation == PipeRotation.LEFT_DOWN.getRotation());
            } else if (prevPoint.y == currentPoint.y && prevPoint.x > currentPoint.x) {
                return (prevRotation == PipeRotation.VERTICAL.getRotation())
                        && (currentRotation == PipeRotation.RIGHT_DOWN.getRotation()
                        || currentRotation == PipeRotation.LEFT_DOWN.getRotation());
            } else if (prevPoint.y == currentPoint.y && prevPoint.x < currentPoint.x) {
                return (prevRotation == PipeRotation.VERTICAL.getRotation())
                        && (currentRotation == PipeRotation.LEFT_UP.getRotation()
                        || currentRotation == PipeRotation.RIGHT_UP.getRotation());
            }
        } else {
            if (prevPoint.x == currentPoint.x && prevPoint.y < currentPoint.y) {
                return (prevRotation == PipeRotation.RIGHT_UP.getRotation()
                        || prevRotation == PipeRotation.RIGHT_DOWN.getRotation())
                        && (currentRotation == PipeRotation.LEFT_UP.getRotation()
                        || currentRotation == PipeRotation.LEFT_DOWN.getRotation());
            } else if (prevPoint.x == currentPoint.x && prevPoint.y > currentPoint.y) {
                return (prevRotation == PipeRotation.LEFT_UP.getRotation()
                        || prevRotation == PipeRotation.LEFT_DOWN.getRotation())
                        && (currentRotation == PipeRotation.RIGHT_UP.getRotation()
                        || currentRotation == PipeRotation.RIGHT_DOWN.getRotation());
            } else if (prevPoint.y == currentPoint.y && prevPoint.x < currentPoint.x) {
                return (prevRotation == PipeRotation.RIGHT_DOWN.getRotation()
                        || prevRotation == PipeRotation.LEFT_DOWN.getRotation())
                        && (currentRotation == PipeRotation.LEFT_UP.getRotation()
                        || currentRotation == PipeRotation.RIGHT_UP.getRotation());
            } else if (prevPoint.y == currentPoint.y && prevPoint.x > currentPoint.x) {
                return (prevRotation == PipeRotation.LEFT_UP.getRotation()
                        || prevRotation == PipeRotation.RIGHT_UP.getRotation())
                        && (currentRotation == PipeRotation.RIGHT_DOWN.getRotation()
                        || currentRotation == PipeRotation.LEFT_DOWN.getRotation());
            }
        }
        return false;
    }

    private void addLevel() {
        this.setLevel(this.getLevel() + 1);
        this.updateLevelLabel();
    }
}
