package sk.stuba.fei.uim.oop.board;

import lombok.Getter;
import lombok.Setter;
import sk.stuba.fei.uim.oop.tile.Tile;
import sk.stuba.fei.uim.oop.tile.TileType;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;


public class Board extends JPanel {

    @Getter
    @Setter
    private Tile[][] tiles;
    private int size;
    private final List<Point> pipes = new ArrayList<>();


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


    public void generateMaze() {
        boolean[] visited = new boolean[this.size * this.size];
        int start = (int) (Math.random() * this.size);
        this.dfs(start, 0, visited);
        setPipes();
    }

    private void setPipes() {
        for (int i = 0; i < pipes.size(); i++) {
            Point p = pipes.get(i);
            int row = p.x;
            int col = p.y;
            Tile tile = tiles[row][col];
            if (i == 0) {
                Point nextPipe = i < pipes.size() - 1 ? pipes.get(i + 1) : null;
                if (nextPipe != null && nextPipe.x == row) {
                    tile.setType(TileType.STRAIGHT_PIPE);
                } else {
                    tile.setType(TileType.KNEE_PIPE);
                }
            } else {
                Point prevPipe = pipes.get(i - 1);
                Point nextPipe = i < pipes.size() - 1 ? pipes.get(i + 1) : null;

                boolean isPrevInSameRow = prevPipe.x == row;
                boolean isPrevInSameCol = prevPipe.y == col;
                boolean isNextInSameRow = nextPipe != null && nextPipe.x == row;
                boolean isNextInSameCol = nextPipe != null && nextPipe.y == col;

                if (isPrevInSameRow && isNextInSameRow) {
                    tile.setType(TileType.STRAIGHT_PIPE);
                } else if (isPrevInSameCol && isNextInSameCol) {
                    tile.setType(TileType.STRAIGHT_PIPE);
                } else {
                    tile.setType(TileType.KNEE_PIPE);
                }
            }
        }
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
            pipes.add(new Point(row, col));
            dfs(nextRow, nextCol, visited);
        }
    }
}









