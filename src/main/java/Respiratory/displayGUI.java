package Respiratory;

import javax.swing.*;

public class displayGUI extends JFrame{
    private JPanel displayPanel;
    private JTextArea bloodUnitTextArea;
    private JLabel arterialO2Label;
    private JLabel arterialCO2Label;
    private JLabel venousO2Label;
    private JLabel venousCO2Label;
    private JLabel o2ConsumptionLabel;
    private JLabel co2ConsumptionLabel;
    private JLabel totalO2BurnedLabel;
    private JLabel TotalCO2Produced;
    private JLabel PvO2Label;
    private JLabel PaO2Label;
    private JLabel PvCO2Label;
    private JLabel PaCO2Label;

    // Variables for body and mainGUI
    private Body body;
    private mainGUI mainGUI;

    // Variables to be displayed
    private double PvO2Value;
    private double PvCO2Value;
    private double PaO2Value;
    private double PaCO2Value;


    displayGUI(Body body, mainGUI mainGUI){

        this.body = body;
        this.mainGUI = mainGUI;

        setContentPane(displayPanel);
        pack();
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

//        getReadings();




    }


    public void getReadings(){


        PvO2Value = body.bloodReadings().getReading(2,"po2");
        PvCO2Value = body.bloodReadings().getReading(2,"pco2");

        PvO2Value = body.bloodReadings().getReading(0,"po2");
        PvCO2Value = body.bloodReadings().getReading(0,"pco2");

        PvCO2Label.setText(String.format("%f",PvCO2Value));
        PvO2Label.setText(String.format("%f",PvO2Value));
        PaCO2Label.setText(String.format("%f",PaCO2Value));
        PaO2Label.setText(String.format("%f",PaO2Value));
    }
}
