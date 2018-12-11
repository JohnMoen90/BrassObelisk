package Respiratory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class mainGUI extends JFrame {

    // Items in GUI
    private JPanel optionsPanel;
    private JCheckBox smokerCheckBox;
    private JComboBox<String> phyTrainingComboBox;
    private String[] phyTrainingOptions = {"No training", "Little Training", "Moderate Training", "Highly Trained"};
    private JTextField textField1;
    private JButton StartButton;
    private JPanel mainPanel;
    private JPanel resultsPanel;
    private JLabel PvCO2Label;
    private JLabel PvO2Label;
    private JLabel PaO2Label;
    private JLabel PaCO2Label;
    private boolean running;

    // The Body
    private Body body;
    private double PvO2Value;
    private double PvCO2Value;
    private double PaO2Value;
    private double PaCO2Value;

    mainGUI() {

//        this.body = new Body();

        setContentPane(optionsPanel);
        pack();
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        for (String option : phyTrainingOptions) {
            phyTrainingComboBox.addItem(option);
        }

        while (running) {

            // This is where I link together program and GUI



            PvCO2Label.setText(String.format("%f",PvCO2Value));
            PvO2Label.setText(String.format("%f",PvO2Value));
            PaCO2Label.setText(String.format("%f",PaCO2Value));
            PaO2Label.setText(String.format("%f",PaO2Value));
        }

        StartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (!running) {
                    running = true;
                    StartButton.setText("Stop");
                } else {
                    running = false;
                    StartButton.setText("Start");
                }
            }
        });

    }
}


