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

    @Getter
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
        int tileSize = 50;
        for (int row = 0; row < dimension; row++) {
            for (int col = 0; col < dimension; col++) {
                this.tiles[row][col] = new Tile(tileSize);
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
            Point prevPipe = i > 0 ? pipes.get(i - 1) : null;
            Point nextPipe = i < pipes.size() - 1 ? pipes.get(i + 1) : null;
            boolean isPrevInSameRow = prevPipe != null && prevPipe.x == row;
            boolean isPrevInSameCol = prevPipe != null && prevPipe.y == col;
            boolean isNextInSameRow = nextPipe != null && nextPipe.x == row;
            boolean isNextInSameCol = nextPipe != null && nextPipe.y == col;
            if (i == 0) {
                if (nextPipe != null && nextPipe.x == row) {
                    tile.setType(TileType.STRAIGHT_PIPE);
                    tile.setRotation(0);
                    tile.setRotationSolution(0);
                } else {
                    if (nextPipe != null && nextPipe.x <  row) {
                        tile.setType(TileType.KNEE_PIPE);
                        tile.setRotation(0);
                        tile.setRotationSolution(0);
                    }
                    else {
                        tile.setType(TileType.KNEE_PIPE);
                        tile.setRotation(3);
                        tile.setRotationSolution(3);
                    }
                }
                tile.setClickable(false);
            } else {
                if ((isPrevInSameRow && isNextInSameRow)) {
                    tile.setType(TileType.STRAIGHT_PIPE);
                    tile.setRotationSolution(0);
                }
                else if (isPrevInSameCol && isNextInSameCol){
                    tile.setType(TileType.STRAIGHT_PIPE);
                    tile.setRotationSolution(1);
                } else {
                    if (isPrevInSameRow && isNextInSameCol && prevPipe.y < col && nextPipe.x < row) {
                        tile.setType(TileType.KNEE_PIPE);
                        tile.setRotationSolution(0);
                        System.out.println("vlavo hore" + row + col);
                    } else if (isPrevInSameRow && isNextInSameCol) {
                        tile.setType(TileType.KNEE_PIPE);
                        System.out.println("Vlavo dole" + row + col);
                        tile.setRotationSolution(3);
                    } else if (isPrevInSameCol && isNextInSameRow && prevPipe.x > row) {
                        tile.setType(TileType.KNEE_PIPE);
                        System.out.println("Vpravo hore" + row + col);
                        tile.setRotationSolution(2);
                    } else if (isPrevInSameCol && isNextInSameRow) {
                        System.out.println("vpravo dole" + row + col);
                        tile.setType(TileType.KNEE_PIPE);
                        tile.setRotationSolution(1);
                    } else {
                        if (isPrevInSameRow) {
                            tile.setType(TileType.STRAIGHT_PIPE);
                            tile.setRotationSolution(1);
                            tile.setRotation(1);
                        } else {
                            tile.setType(TileType.KNEE_PIPE);
                            if (prevPipe != null && prevPipe.x < row) {
                                tile.setRotation(1);
                                tile.setRotationSolution(1);
                            }
                            else {
                                tile.setRotation(2);
                                tile.setRotationSolution(2);
                            }
                        }
                        tile.setClickable(false);
                    }
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









