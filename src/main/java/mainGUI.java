import javax.print.attribute.standard.NumberUp;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

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
    private JTextField ageTextField;
    private JComboBox profilesComboBox;
    private JButton saveNewProfileButton;
    private JTextField firstNameTextField;
    private JTextField lastNameTextField;
    private JTextField bodyWeightTextField;
    private JButton exitProgramButton;
    private boolean running;

    private ArrayList<Profile> profiles;

    private displayGUI displayGUI;
    private Body body;

    private int beatsperMinute;
    private int diffusionRate;
    private int breathingRate;

//    private int refreshRate = 500;

    mainGUI() {

        this.body = new Body();
        this.displayGUI = new displayGUI(body, this);
        profiles = ProfileIO.getAllProfiles();

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
                displayGUI.getReadings();
                body.manageTurn();
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

        smokerCheckBox.addChangeListener(e -> {
            if (!smokerCheckBox.isSelected()) {
                body.setSmoker(true);
            } else {
                body.setSmoker(false);
            }
        });

        phyTrainingComboBox.addActionListener(e -> {
            BodyConfig.exerciseXP = (String) phyTrainingComboBox.getSelectedItem();

        });

        exitProgramButton.addActionListener(e -> {

        });

    }


    public void addNewProile() {

        try {
            int age = Integer.parseInt(ageTextField.getText());
            int weight = Integer.parseInt(ageTextField.getText());
            profiles.add(new Profile(
                    firstNameTextField.getText(),
                    lastNameTextField.getText(),
                    age,
                    weight,
                    phyTrainingComboBox.getSelectedIndex(),
                    smokerCheckBox.isSelected()));
        }
        catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(rootPane,"Please only use numeric fields in age and weight fields");
        }

    }



}





