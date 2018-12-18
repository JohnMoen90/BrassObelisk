import javax.swing.*;

public class displayGUI extends JFrame{
    private JPanel displayPanel;
    private JTextArea bloodUnitTextArea;
    private JLabel arterialO2Label;
    private JLabel arterialCO2Label;
    private JLabel venousO2Label;
    private JLabel venousCO2Label;
    private JLabel breathStatusLabel;
    private JTextArea cnsTextArea;
    private JLabel circulationPercentLabel;
    private JLabel circulationCountLabel;
    private JLabel totalVentilationLabel;
    private JLabel alveolarVentilationLabel;
    private JLabel lungO2AssignerLabel;
    private JLabel lungCO2AssignerLabel;
    private JLabel activityLevelLabel;
    private JLabel diffZoneO2AssignerLabel;
    private JLabel beatsPerMinuteLabel;
    private JLabel diffZoneCO2AssignerLabel;
    private JLabel headerStringLabel;

    private Body body;
    private mainGUI mainGUI;  //<-- not used in this implementation


    private int getReadingsCounter;


    displayGUI(Body body, mainGUI mainGUI){

        this.body = body;
        this.mainGUI = mainGUI;

        getReadingsCounter = 0;

        setContentPane(displayPanel);



        pack();
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//        allLabels.add(arterialO2Label);
//        allLabels.add(arterialCO2Label);
//        allLabels.add(venousO2Label);
//        allLabels.add(venousCO2Label);
//        allLabels.add(o2ConsumptionLabel);
//        allLabels.add(co2ConsumptionLabel);
//        allLabels.add(totalO2BurnedLabel);
//        allLabels.add(totalCO2ProducedLabel);
//
//        Font testFont = new java.awt.Font(Font.SANS_SERIF, Font.PLAIN, 32);
//
//        for (JLabel label: allLabels) {
//            label.setFont(testFont);
//        }

        getReadings();

    }


    /**
     * Gets all the readings from the body object and applies them to GUI
     */
    public void getReadings(){
        getReadingsCounter++;
        if (getReadingsCounter == 5) {
            getReadingsCounter -= 5;

            arterialO2Label.setText(String.format("%.2f", body.bloodReadings().getReading(0, "o2")));
            arterialCO2Label.setText(String.format("%.2f", body.bloodReadings().getReading(0, "co2")));
            venousO2Label.setText(String.format("%.2f", body.bloodReadings().getReading(3, "o2")));
            venousCO2Label.setText(String.format("%.2f", body.bloodReadings().getReading(3, "co2")));

            beatsPerMinuteLabel.setText(String.format("%d",body.heartReadings().getBeatsPerMinute()));
            circulationCountLabel.setText(String.format("%d", body.bloodReadings().getCirculations()));
            double percentage = ((double)body.bloodReadings().getQueuePositionsTraveled()/
                                (double) body.bloodReadings().getBloodUnitTotal())*100.00;
            circulationPercentLabel.setText(String.format("%.2f %s", percentage,"%"));

            bloodUnitTextArea.setText(body.bloodReadings().getBloodUnitString(10));
            cnsTextArea.setText(body.cnsReadings().getCNSText());

            activityLevelLabel.setText(activityLevelToString());
//            diffZoneO2AssignerLabel.setText(String.format("%.2f",body.bloodReadings().getDiffusionZoneAssigners(2, "o2")));
            diffZoneO2AssignerLabel.setText(String.format("%.2f",BodyConfig.currentVO2));
            diffZoneCO2AssignerLabel.setText(String.format("%.2f",body.bloodReadings().getDiffusionZoneAssigners(2, "co2")));


            breathStatusLabel.setText(body.getBreathStatus());
            totalVentilationLabel.setText(String.format("%.2f",body.lungReadings().getTotalVentilation()));
            alveolarVentilationLabel.setText(String.format("%.2f",body.lungReadings().getTotalVentilation() * .2));
            totalVentilationLabel.setText(String.format("%.2f",body.lungReadings().getTotalVentilation()));
            lungO2AssignerLabel.setText(String.format("%.2f",body.bloodReadings().getDiffusionZoneAssigners(4, "o2")));
            lungCO2AssignerLabel.setText(String.format("%.2f",body.bloodReadings().getDiffusionZoneAssigners(4, "co2")));
        }
    }

    /**
     * Converts activity level to appropriate string
     * @return string that matches activity levels
     */
    public String activityLevelToString(){
        if (BodyConfig.activityLevel == 1) {
            return "Rest";
        } else if (BodyConfig.activityLevel < 4) {
            return "Mild";
        } else if (BodyConfig.activityLevel < 7) {
            return "Moderate";
        } else if (BodyConfig.activityLevel < 10) {
            return "Strenuous";
        } else {
            return "Peak";
        }
    }

}
