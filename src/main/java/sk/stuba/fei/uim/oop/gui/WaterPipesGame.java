package sk.stuba.fei.uim.oop.gui;
import sk.stuba.fei.uim.oop.controls.GameLogic;
import javax.swing.*;
import java.awt.*;

    public class WaterPipesGame {

        public WaterPipesGame() {
            JFrame frame = new JFrame("Water Pipes");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(610, 740);
            frame.setResizable(false);
            frame.setFocusable(true);
            frame.setLocationRelativeTo(null);
            frame.requestFocus();

            GameLogic logic = new GameLogic(frame);
            frame.addKeyListener(logic);

            JPanel header = new JPanel(new GridLayout(2, 2));
            header.setPreferredSize(new Dimension(700, 100));
            header.setBackground(Color.LIGHT_GRAY);

            JSlider slider = new JSlider(8, 12, 8);
            slider.setFocusable(false);
            slider.setBackground(Color.LIGHT_GRAY);
            slider.setPaintTicks(true);
            slider.setMajorTickSpacing(2);
            slider.setPaintLabels(true);
            slider.setSnapToTicks(true);
            slider.addChangeListener(logic);

            JLabel difficultyLabel = logic.getLabelLevel();
            difficultyLabel.setFont(new Font("Arial", Font.BOLD, 20));

            JButton buttonRestart = new JButton("Restart");
            buttonRestart.setFocusable(false);
            buttonRestart.addActionListener(logic);
            JButton buttonCheck = new JButton("Check");
            buttonCheck.setFocusable(false);
            buttonCheck.addActionListener(logic);

            header.add(difficultyLabel);
            header.add(slider);
            header.add(buttonRestart);
            header.add(buttonCheck);

            frame.add(header, BorderLayout.NORTH);
            frame.setVisible(true);
        }
    }

