package sk.stuba.fei.uim.oop.tile;

import lombok.Getter;
import lombok.Setter;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.Random;
@Setter @Getter
public class Tile extends JPanel {
    private TileType type;
    private int rotation;
    private Random random;
    private int highlighted = 0;
    private boolean clickable = true;


    public Tile(int size) {
        super();
        this.type = TileType.EMPTY;
        this.setPreferredSize(new Dimension(size, size));
        JLabel label = new JLabel();
        this.add(label);
        this.setBorder(new LineBorder(Color.BLACK, 2));
        random = new Random();
        rotation = this.getRandom().nextInt(4);
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        this.setBackground(null);
        Color color = Color.black;
        Graphics2D g2d = (Graphics2D) g.create();
        AffineTransform old = g2d.getTransform();
        g2d.rotate(Math.toRadians(90 * getRotation()), getWidth() / 2.0, getHeight() / 2.0);
        switch (highlighted) {
            case 1:
                color = Color.BLUE;
                break;
            case 2:
                this.setBackground(Color.YELLOW);
                break;
            default:
                this.setBackground(null);
                break;
        }
        if (type.equals(TileType.EMPTY)) {
            this.setClickable(false);
        } else if (type.equals(TileType.STRAIGHT_PIPE)) {
            drawStraightPipe(g2d, color);
        } else if (type.equals(TileType.KNEE_PIPE)) {
            drawKneePipe(g2d, color);
        }
        g2d.setTransform(old);
        g2d.dispose();
    }

    public void rotate() {
        if (type == TileType.STRAIGHT_PIPE) {
            setRotation((getRotation() + 1) % 2);
        } else if (type == TileType.KNEE_PIPE) {
            setRotation((getRotation() + 1) % 4);
        }
    }

    private void drawKneePipe(Graphics g, Color color){
        g.setColor(color);
        g.fillRect(0, getHeight() / 3, getWidth() / 2, getHeight() / 3);
        g.fillRect(getWidth() / 3, 0, getWidth() / 3, getHeight() / 2);
        g.setColor(Color.GREEN);
        g.fillRect(0, getHeight() / 3 - getHeight() / 10, getWidth() / 8, getHeight() / 3 + getHeight() / 5);
        g.fillRect(getWidth() / 3 - getWidth() / 10, 0, getWidth() / 3 + getWidth() / 5, getHeight() / 8);
    }
    private void drawStraightPipe(Graphics g, Color color){
        g.setColor(color);
        g.fillRect(0, getHeight() / 3, getWidth(), getHeight() / 3);
        g.setColor(Color.GREEN);
        g.fillRect(0, getHeight() / 3 - getHeight() / 10, getWidth() / 8, getHeight() / 3 + getHeight() / 5);
        g.fillRect(getWidth() - getWidth() / 8, getHeight() / 3 - getHeight() / 10, getWidth() / 8, getHeight() / 3 + getHeight() / 5);
    }
}