import javax.swing.*;
import java.util.ArrayList;

public class displayGUI extends JFrame{
    private JPanel displayPanel;
    private JTextArea bloodUnitTextArea;
    private JLabel arterialO2Label;
    private JLabel arterialCO2Label;
    private JLabel venousO2Label;
    private JLabel venousCO2Label;
    private JLabel co2ConsumptionLabel;
    private JLabel totalO2BurnedLabel;
    private JLabel totalCO2ProducedLabel;
    private JLabel breathStatusLabel;
    private JLabel o2InLungsLabel;
    private JLabel co2InLungsLabel;
    private JLabel HeaderStringLabel;
    private JTextArea cnsTextArea;
    private JLabel circulationPercentLabel;
    private JLabel circulationCountLabel;
    private ArrayList<JLabel> allLabels;
    private JLabel totalVentilationLabel;
    private JLabel alveolarVentilationLabel;
    private JLabel lungO2AssignerLabel;
    private JLabel lungCO2AssignerLabel;
    private JLabel ActivityLevelLabel;
    private JLabel diffZoneO2AssignerLabel;
    private JLabel beatsPerMinuteLabel;
    private JLabel diffZoneCO2AssignerLabel;


    // Variables for body and mainGUI
    private Body body;
    private mainGUI mainGUI;

    // Variables to be displayed
    private double PvO2Value;
    private double PvCO2Value;
    private double PaO2Value;
    private double PaCO2Value;

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

            diffZoneO2AssignerLabel.setText(String.format("%.2f",body.bloodReadings().getDiffusionZoneAssigners(2, "o2")));
            diffZoneCO2AssignerLabel.setText(String.format("%.2f",body.bloodReadings().getDiffusionZoneAssigners(2, "co2")));


            breathStatusLabel.setText(body.getBreathStatus());
            totalVentilationLabel.setText(String.format("%.2f",body.lungReadings().getTotalVentilation()));
            alveolarVentilationLabel.setText(String.format("%.2f",body.lungReadings().getTotalVentilation() * .2));
            totalVentilationLabel.setText(String.format("%.2f",body.lungReadings().getTotalVentilation()));
            lungO2AssignerLabel.setText(String.format("%.2f",body.bloodReadings().getDiffusionZoneAssigners(4, "o2")));
            lungCO2AssignerLabel.setText(String.format("%.2f",body.bloodReadings().getDiffusionZoneAssigners(4, "co2")));
        }
    }
}
