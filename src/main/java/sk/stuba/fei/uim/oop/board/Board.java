package sk.stuba.fei.uim.oop.board;

import lombok.Getter;
import lombok.Setter;
import sk.stuba.fei.uim.oop.controls.GameLogic;
import sk.stuba.fei.uim.oop.tile.Tile;
import sk.stuba.fei.uim.oop.tile.TileType;

import javax.swing.*;
import java.awt.*;
import java.util.*;


public class Board extends JPanel {

    @Getter
    @Setter
    private Tile[][] tiles;

    private int size;


    public Board(int dimension) {
        this.initializeBoard(dimension);
        setPreferredSize(new Dimension());
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
    }

    private void initializeBoard(int dimension) {
        this.tiles = new Tile[dimension][dimension];
        this.setLayout(new GridLayout(dimension, dimension));
        this.size = dimension;
        int tileSize = 50; // set the tile size here
        for (int row = 0; row < dimension; row++) {
            for (int col = 0; col < dimension; col++) {
                this.tiles[row][col] = new Tile(tileSize); // pass the tile size to the Tile constructor
                this.add(tiles[row][col]);
            }
        }
        generateMaze();
    }


    private void updateBoard() {
        for (int row = 0; row < this.size; row++) {
            for (int col = 0; col < this.size; col++) {
                this.tiles[row][col].repaint();
            }
        }
    }
    public void generateMaze() {
        boolean[] visited = new boolean[this.size * this.size];
        int start = (int) (Math.random() * this.size);
        this.dfs(start, 0, visited);
    }

    public void dfs(int row, int col, boolean[] visited) {
        visited[row * size + col] = true;
        ArrayList<Integer> neighbors = new ArrayList<>();
        if (row > 0 && !visited[(row - 1) * size + col]) {
            neighbors.add((row - 1) * size + col);
        }
        if (row < size - 1 && !visited[(row + 1) * size + col]) {
            neighbors.add((row + 1) * size + col);
        }
        if (col < size - 1 && !visited[row * size + col + 1]) {
            neighbors.add(row * size + col + 1);
        }
        if (!neighbors.isEmpty()) {
            Collections.shuffle(neighbors);
            int next = neighbors.get(0);
            int nextRow = next / size;
            int nextCol = next % size;
            if (nextRow == row) {
                if (nextCol == col + 1) {
                    tiles[row][col].setType(TileType.STRAIGHT_PIPE);
                    tiles[row][col + 1].setType(TileType.STRAIGHT_PIPE);
                }
            } else if (nextRow == row + 1) {
                tiles[row][col].setType(TileType.KNEE_PIPE);
                tiles[row + 1][col].setType(TileType.KNEE_PIPE);
            } else if (nextRow == row - 1) {
                tiles[row][col].setType(TileType.KNEE_PIPE);
                tiles[row - 1][col].setType(TileType.KNEE_PIPE);
            }
            dfs(nextRow, nextCol, visited);
        }
    }


}







