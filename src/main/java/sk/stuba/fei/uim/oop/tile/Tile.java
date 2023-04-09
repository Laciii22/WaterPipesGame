package sk.stuba.fei.uim.oop.tile;

import lombok.Getter;
import lombok.Setter;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.Random;

public class Tile extends JPanel {
    @Setter @Getter
    private TileType type;
    @Setter @Getter
    private int rotation;
    @Setter @Getter
    private boolean highlighted;

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        AffineTransform old = g2d.getTransform();
        g2d.rotate(Math.toRadians(90*getRotation()), getWidth()/2, getHeight()/2); // uses the rotation variable
        if (type.equals(TileType.EMPTY)) {
            g.setColor(Color.WHITE);
            g.fillRect(0,0, getWidth(), getHeight());
        }
        else if (type.equals(TileType.STRAIGHT_PIPE)) {
            g.setColor(Color.BLUE);
            g.fillRect(0,getHeight()/3, getWidth(), getHeight()/3);
            g.setColor(Color.GREEN);
            g.fillRect(0, getHeight()/3 -getHeight()/10, getWidth()/8, getHeight()/3+getHeight()/5);
            g.fillRect(getWidth()-getWidth()/8, getHeight()/3 -getHeight()/10, getWidth()/8, getHeight()/3+getHeight()/5);

        }
        else if (type.equals(TileType.KNEE_PIPE)) {
            g.setColor(Color.BLUE);
            g.fillRect(0,getHeight()/3, getWidth()/2, getHeight()/3);
            g.fillRect(getWidth()/3,0, getWidth()/3, getHeight()/2);
            g.setColor(Color.GREEN);
            g.fillRect(0, getHeight()/3 -getHeight()/10, getWidth()/8, getHeight()/3+getHeight()/5);
            g.fillRect(getWidth()/3-getWidth()/10, 0, getWidth()/3+getWidth()/5, getHeight()/8);
        }
        else if (type.equals(TileType.TEST)) {
            g.setColor(Color.RED);
            g.fillRect(0,0, getWidth(), getHeight());
        }

        if (highlighted) {
            g2d.setColor(Color.YELLOW);
            g2d.setStroke(new BasicStroke(getWidth()/5));
            g2d.drawRect(0, 0, getWidth(), getHeight());
        }

        g2d.setTransform(old);
    }

    public Tile(int size) {
        super();
        this.type = TileType.EMPTY;
        this.setPreferredSize(new Dimension(size, size));
        JLabel label = new JLabel();
        this.add(label);
        this.setBorder(new LineBorder(Color.BLACK, 2));
        Random random = new Random();
        int rotation = random.nextInt(4);
        setRotation(rotation);
    }

    public void rotate() {
        setRotation((getRotation() + 1) % 4);
        repaint();
    }
}
