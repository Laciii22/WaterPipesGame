package sk.stuba.fei.uim.oop.controls;

import lombok.Getter;
import sk.stuba.fei.uim.oop.board.Board;
import sk.stuba.fei.uim.oop.tile.Tile;
import sk.stuba.fei.uim.oop.tile.TileType;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class GameLogic extends UniversalAdapter {
    public static final int INITIAL_SIZE = 8;
    private final JFrame mainGame;
    private Board currentBoard;
    @Getter
    private JLabel difficultyLabel;
    private int currentBoardSize;

    public GameLogic(JFrame mainGame) {
        this.mainGame = mainGame;
        this.currentBoardSize = INITIAL_SIZE;
        this.initializeNewBoard(this.currentBoardSize);
        this.mainGame.add(this.currentBoard);
        this.difficultyLabel = new JLabel();
        this.updateDifficultyLabel();
    }

    private void initializeNewBoard(int dimension) {
        this.currentBoard = new Board(dimension);
        this.currentBoard.addMouseMotionListener(this);
        this.currentBoard.addMouseListener(this);
    }

    private void updateDifficultyLabel() {
        this.difficultyLabel.setText("Difficulty: " + this.currentBoardSize);
        this.mainGame.revalidate();
        this.mainGame.repaint();
    }

    private void gameRestart() {
        this.mainGame.remove(this.currentBoard);
        this.initializeNewBoard(this.currentBoardSize);
        this.mainGame.add(this.currentBoard);
        this.updateDifficultyLabel();
        this.mainGame.revalidate();
        this.mainGame.repaint();
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (this.currentBoardSize != ((JSlider) e.getSource()).getValue()) {
            this.currentBoardSize = ((JSlider) e.getSource()).getValue();
            this.gameRestart();
            this.mainGame.setFocusable(true);
            this.mainGame.requestFocus();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        System.out.println(e);
        switch (e.getKeyCode()) {
            case KeyEvent.VK_R:
                System.out.println("Restarting game");
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
        if (!(current instanceof Tile)) {
            for (Component component : currentBoard.getComponents()) {
                if (component instanceof Tile) {
                    ((Tile) component).setHighlighted(false);
                }
            }
            return;
        } else {
            for (Component component : currentBoard.getComponents()) {
                if (component instanceof Tile) {
                    Tile tile = (Tile) component;
                    tile.setHighlighted(component == current && tile.isClickable());
                }
            }
        }
        this.currentBoard.repaint();
    }


    @Override
    public void mouseClicked(MouseEvent e) {
        Component current = currentBoard.findComponentAt(e.getX(), e.getY());
        if (!(current instanceof Tile)) {
            return;
        } else if (((Tile) current).isClickable()) {
            Tile tile = (Tile) current;
            tile.rotate();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Restart")) {
            this.gameRestart();
        } else if (e.getActionCommand().equals("Check")) {
            System.out.println("Checking");
            checkForWin();
        }
    }

    private void checkForWin() {
        for (Point pipe : currentBoard.getPipes()){
            int x = (int) pipe.getX();
            int y = (int) pipe.getY();
            Tile[][] tile = currentBoard.getTiles();
            if (tile[x][y].getTileType().equals(TileType.STRAIGHT_PIPE)){
                if (tile[x][y].getRotation() % 2 == tile[x][y].getRotationSolution()){
                    tile[x][y].setHighlighted(true);
                }
            }
            else if (tile[x][y].getRotation() == tile[x][y].getRotationSolution()){
                    tile[x][y].setHighlighted(true);
            } else {
                return;
            }
        }

    }
}
