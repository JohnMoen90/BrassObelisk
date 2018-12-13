package Respiratory;

import javax.swing.*;

/**
 * This GUI contains the Body object which is used to calculate the output on displayGUI, and gives the user control of
 * the body object with different initialization and on the fly variable manipulation options (does that make sense?)
 */
public class mainGUI extends JFrame {

    private JPanel mainPanel;
    private JComboBox<String> phyTrainingComboBox;
    private String[] phyTrainingOptions = {"No training", "Little Training", "Moderate Training", "Highly Trained"};
    private JButton StartButton;
    private JCheckBox smokerCheckBox;
    private JTextField bodyWieghtTextField;
    private boolean running;

    private displayGUI displayGUI;
    private Body body;

    mainGUI() {

        this.body = new Body();
        this.displayGUI = new displayGUI(body, this);

        setContentPane(mainPanel);
        pack();
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        for (String option : phyTrainingOptions) {
            phyTrainingComboBox.addItem(option);
        }

        // Basically controls how fast the simulation runs, each
        new Timer(1000, e -> {
            if (running) {
                body.manageTurn();
                displayGUI.getReadings();
            }
        }).start();

        // Event Listeners
        StartButton.addActionListener(e -> {
            if (!running) {
                StartButton.setText("Stop");
                running = true;
            } else {
                StartButton.setText("Start");
                running = false;
            }
        });


    }

}





