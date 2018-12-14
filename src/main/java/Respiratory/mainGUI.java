package Respiratory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

    private int beatsperMinute;
    private int diffusionRate;
    private int breathingRate;

//    private int refreshRate = 500;

    mainGUI() {

        this.body = new Body();
        this.displayGUI = new displayGUI(body, this);
        Counter counter = new Counter();

        // Set default values from bodyConfig
        beatsperMinute = 70;
        diffusionRate = 2;

        setContentPane(mainPanel);
        pack();
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        for (String option : phyTrainingOptions) {
            phyTrainingComboBox.addItem(option);
        }

        // Basically controls how fast each component of the simulation runs
        new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            if (running) {
                body.manageTurn();
//                displayGUI.getReadings();
            }
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

    public class Counter{

        private int count;

        public Counter(){
            count = 0;
        }

        public int getCount() {
            return count;
        }

        public void addOne(){
            count++;
        }

        public void reset(){
            count = 0;
        }

        public void setCount(int count) {
            this.count = count;
        }
    }


}





