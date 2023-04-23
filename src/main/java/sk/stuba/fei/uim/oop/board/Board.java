package sk.stuba.fei.uim.oop.board;
import lombok.Getter;
import sk.stuba.fei.uim.oop.tile.PipeRotation;
import sk.stuba.fei.uim.oop.tile.Tile;
import sk.stuba.fei.uim.oop.tile.TileType;
import javax.swing.*;
import java.awt.*;
import java.util.*;
public class Board extends JPanel {
    @Getter
    private Tile[][] tiles;
    @Getter
    private final Random random;
    private int size;
    @Getter
    private final Stack<Point> pipes = new Stack<>();

    public Board(int dimension) {
        random = new Random();
        this.initializeBoard(dimension);
        setPreferredSize(new Dimension(500, 500));
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
    private void generateMaze() {
        boolean[][] visited = new boolean[this.size][this.size];
        Stack<Point> stack = new Stack<>();
        int startRow = this.getRandom().nextInt(this.size);
        stack.push(new Point(startRow, 0));
        visited[startRow][0] = true;
        randomizedDfs(stack, visited);
        pipes.addAll(stack);
        setPipes();
    }

    private void randomizedDfs(Stack<Point> stack, boolean[][] visited) {
        int lastRow = this.getRandom().nextInt(this.size);
        while (true) {
            Point p = stack.peek();
            int row = p.x;
            int col = p.y;
            ArrayList<Point> neighbors = new ArrayList<>();
            if (row > 0 && !visited[row - 1][col]) {
                neighbors.add(new Point(row - 1, col));
            }
            if (row < size - 1 && !visited[row + 1][col]) {
                neighbors.add(new Point(row + 1, col));
            }
            if (col < size - 1 && !visited[row][col + 1]) {
                neighbors.add(new Point(row, col + 1));
            }
            if (col > 0 && !visited[row][col - 1]) {
                neighbors.add(new Point(row, col - 1));
            }

            if (!neighbors.isEmpty()) {
                Collections.shuffle(neighbors);
                Point neighbor = neighbors.get(0);
                int neighborRow = neighbor.x;
                int neighborCol = neighbor.y;
                visited[neighborRow][neighborCol] = true;
                stack.push(neighbor);
                if (neighborCol == this.size - 1 && neighborRow == lastRow) {
                    break;
                }
            } else {
                stack.pop();
                if (stack.isEmpty()) {
                    int newStartRow = this.getRandom().nextInt(this.size);
                    stack.push(new Point(newStartRow, 0));
                    visited[newStartRow][0] = true;
                }
            }
        }
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
                    tile.setRotation(PipeRotation.HORIZONTAL.getRotation());
                } else {
                    if (nextPipe != null && nextPipe.x < row) {
                        tile.setType(TileType.KNEE_PIPE);
                        tile.setRotation(PipeRotation.LEFT_UP.getRotation());
                    } else {
                        tile.setType(TileType.KNEE_PIPE);
                        tile.setRotation(PipeRotation.LEFT_DOWN.getRotation());
                    }
                }
                tile.setBorder(BorderFactory.createLineBorder(Color.GREEN, 6));
                tile.setClickable(false);
            } else if (i < pipes.size() - 1) {
                if (isPrevInSameRow && isNextInSameRow) {
                    tile.setType(TileType.STRAIGHT_PIPE);
                } else if (isPrevInSameCol && isNextInSameCol) {
                    tile.setType(TileType.STRAIGHT_PIPE);
                } else {
                    tile.setType(TileType.KNEE_PIPE);
                }
                tile.setClickable(true);
            } else {
                tile.setClickable(false);
                tile.setBorder(BorderFactory.createLineBorder(Color.RED, 6));
                if (prevPipe != null && prevPipe.x == row) {
                    tile.setType(TileType.STRAIGHT_PIPE);
                    tile.setRotation(PipeRotation.HORIZONTAL.getRotation());
                } else {
                    if (prevPipe != null && prevPipe.x < row) {
                        tile.setType(TileType.KNEE_PIPE);
                        tile.setRotation(PipeRotation.RIGHT_UP.getRotation());
                    } else {
                        tile.setType(TileType.KNEE_PIPE);
                        tile.setRotation(PipeRotation.RIGHT_DOWN.getRotation());
                    }
                }
            }
        }
    }

    public ArrayList<Point> getNeighbors(Point point) {
        int x = point.x;
        int y = point.y;
        ArrayList<Point> neighbors = new ArrayList<>();
        if (x > 0 && tiles[x - 1][y].getType() != TileType.EMPTY) {
            neighbors.add(new Point(x - 1, y));
        }
        if (x < size - 1 && tiles[x + 1][y].getType() != TileType.EMPTY) {
            neighbors.add(new Point(x + 1, y));
        }
        if (y > 0 && tiles[x][y - 1].getType() != TileType.EMPTY) {
            neighbors.add(new Point(x, y - 1));
        }
        if (y < size - 1 && tiles[x][y + 1].getType() != TileType.EMPTY) {
            neighbors.add(new Point(x, y + 1));
        }
        return neighbors;
    }
}









