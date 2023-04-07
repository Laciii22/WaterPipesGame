package sk.stuba.fei.uim.oop.gui;

import sk.stuba.fei.uim.oop.board.Board;
import sk.stuba.fei.uim.oop.controls.GameLogic;
import sk.stuba.fei.uim.oop.tile.Tile;

import javax.swing.*;
import java.awt.*;

    public class WaterPipesGame {

        private JPanel board;
        private Board gameBoard;

        public WaterPipesGame() {
            JFrame frame = new JFrame("Water Pipes");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(610, 733);
            frame.setResizable(false);
            frame.setFocusable(true);
            frame.setLocationRelativeTo(null);
            frame.requestFocus();

            GameLogic logic = new GameLogic(frame);
            frame.addKeyListener(logic);

            JPanel header = new JPanel(new GridLayout(2, 2));
            header.setPreferredSize(new Dimension(700, 100));
            header.setBackground(Color.LIGHT_GRAY);

            JSlider slider = new JSlider(8, 16, 8);
            slider.setBackground(Color.LIGHT_GRAY);
            slider.setPaintTicks(true);
            slider.setMajorTickSpacing(4);
            slider.setPaintLabels(true);
            slider.setSnapToTicks(true);
            slider.addChangeListener(logic);



            JLabel difficultyLabel = new JLabel("Difficulty");
            difficultyLabel.setFont(new Font("Arial", Font.BOLD, 20));

            JButton restartButton = new JButton("Restart");
            JButton checkButton = new JButton("Check");

            header.add(difficultyLabel);
            header.add(slider);
            header.add(restartButton);
            header.add(checkButton);

            frame.add(header, BorderLayout.NORTH);


            frame.setVisible(true);
        }
    }

