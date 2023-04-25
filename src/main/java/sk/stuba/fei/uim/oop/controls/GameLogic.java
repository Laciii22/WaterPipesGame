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
    private static final int INITIAL_SIZE = 8;
    private static final String RESTART = "Restart";
    private static final String CHECK = "Check";
    private final JFrame mainGame;
    private Board currentBoard;
    @Getter
    @Setter
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
                System.exit(0);
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
                    ((Tile) component).setHover(false);
                }
            }
        } else {
            for (Component component : currentBoard.getComponents()) {
                if (component instanceof Tile) {
                    Tile tile = (Tile) component;
                    tile.setHover(component == current);
                }
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        this.setDefaultColor();
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
        if (e.getActionCommand().equals(RESTART)) {
            this.setLevel(1);
            this.updateLevelLabel();
            this.gameRestart();
        } else if (e.getActionCommand().equals(CHECK)) {
            this.checkForWin();
        }
    }

    private void checkForWin() {
        Set<Point> visited = new HashSet<>();
        Point firstPipe = currentBoard.getPipes().get(0);
        checkForWinRec(firstPipe, visited);
        Point lastPipe = currentBoard.getPipes().get(currentBoard.getPipes().size() - 1);
        Tile lastPipeTile = currentBoard.getTiles()[lastPipe.x][lastPipe.y];
        if (lastPipeTile.isHighlighted()) {
            gameRestart();
            addLevel();
        }
        currentBoard.repaint();
    }

    private boolean checkForWinRec(Point current, Set<Point> visited) {
        visited.add(current);
        Tile currentTile = currentBoard.getTiles()[current.x][current.y];
        currentTile.setHighlighted(true);
        if (current == currentBoard.getPipes().get(currentBoard.getPipes().size() - 1)) {
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
        if (prevTile.getType() == TileType.STRAIGHT_PIPE && currentTile.getType() == TileType.STRAIGHT_PIPE) {
            return straightPipesConnection(prevTile, currentTile, prevPoint, currentPoint);
        } else if (prevTile.getType() == TileType.KNEE_PIPE && currentTile.getType() == TileType.STRAIGHT_PIPE) {
            return kneeStraightPipeConnection(prevTile, currentTile, prevPoint, currentPoint);
        } else if (prevTile.getType() == TileType.STRAIGHT_PIPE && currentTile.getType() == TileType.KNEE_PIPE) {
            return kneeStraightPipeConnection(currentTile, prevTile, currentPoint, prevPoint);
        } else if (prevTile.getType() == TileType.KNEE_PIPE && currentTile.getType() == TileType.KNEE_PIPE) {
            return kneePipesConnection(prevTile, currentTile, prevPoint, currentPoint);
        }
        return false;
    }

    private boolean straightPipesConnection(Tile prevTile, Tile currentTile, Point prevPoint, Point currentPoint) {
        int prevRotation = prevTile.getRotation();
        int currentRotation = currentTile.getRotation();
        if (prevPoint.x == currentPoint.x) {
            return (prevRotation == PipeRotation.HORIZONTAL.getRotation() && currentRotation == PipeRotation.HORIZONTAL.getRotation());
        } else if (prevPoint.y == currentPoint.y) {
            return (prevRotation == PipeRotation.VERTICAL.getRotation() && currentRotation == PipeRotation.VERTICAL.getRotation());
        }
        return false;
    }

    private boolean kneeStraightPipeConnection(Tile prevTile, Tile currentTile, Point prevPoint, Point currentPoint) {
        int prevRotation = prevTile.getRotation();
        int currentRotation = currentTile.getRotation();
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
        return false;
    }

    private boolean kneePipesConnection(Tile prevTile, Tile currentTile, Point prevPoint, Point currentPoint) {
        int prevRotation = prevTile.getRotation();
        int currentRotation = currentTile.getRotation();
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
        return false;
    }

    private void setDefaultColor() {
        for (int i = 0; i < currentBoard.getTiles().length; i++) {
            for (int j = 0; j < currentBoard.getTiles()[i].length; j++) {
                currentBoard.getTiles()[i][j].setHighlighted(false);
            }
        }
        currentBoard.repaint();
    }

    private void addLevel() {
        this.setLevel(this.getLevel() + 1);
        this.updateLevelLabel();
    }
}
