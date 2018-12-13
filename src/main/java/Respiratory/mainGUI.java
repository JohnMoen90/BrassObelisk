package Respiratory;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class mainGUI extends JFrame {

    // Items in GUI
    private JPanel mainPanel;
    private JComboBox<String> phyTrainingComboBox;
    private String[] phyTrainingOptions = {"No training", "Little Training", "Moderate Training", "Highly Trained"};
    private JButton StartButton;
    private JCheckBox smokerCheckBox;
    private JTextField bodyWieghtTextField;
    private boolean running;

    // Connection to displayGUI
    private displayGUI displayGUI;

    // The Body
    private Body body;

    // Hopefully simulation thread



    mainGUI() {

        // Initialize body and displayGUI
        this.body = new Body();
        this.displayGUI = new displayGUI(body, this);

        // JPanel initialization
        setContentPane(mainPanel);
        pack();
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // Set up Phy Training ComboBox
        for (String option : phyTrainingOptions) {
            phyTrainingComboBox.addItem(option);
        }


        // Set up the timer
        new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (running) {
                    body.manageTurn();
                    displayGUI.getReadings();
                }
            }
        }).start();


        StartButton.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {

            }
        });


        // add start button listener
        StartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (!running) {
                    StartButton.setText("Stop");
                    running = true;
                } else {
                    StartButton.setText("Start");
                    running = false;
                }


            }
        });


    }




}





