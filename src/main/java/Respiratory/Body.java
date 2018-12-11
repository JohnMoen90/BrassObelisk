package Respiratory;

public class Body {

    // Initialize some variables
    private double bodyWeight = BodyConfig.bodyWeight;
    private Blood blood; // ml
    private double Vo2 = (17 * bodyWeight) / 60; // ml perSecond
    private double Vco2 = (1.1 * bodyWeight) / 60; // ml perSecond

    // Variables to store readings from blood
    private double PvO2;
    private double PvCO2;
    private double PaO2;
    private double PaCO2;


    // Constructor
    Body(){
        this.blood = new Blood(bodyWeight, BodyConfig.strokeVolume);
    }


    public void manageTurn(){
        blood.circulate();
        getReadings();
    }


    public void getReadings(){

        PvO2 = blood.getReading(2,"po2");
        PvCO2 = blood.getReading(2,"pco2");

        PvO2 = blood.getReading(0,"po2");
        PvCO2 = blood.getReading(0,"pco2");

    }


    private class Lungs{

        private double tlc = BodyConfig.totalLungCapacity;
        double currentLungVolume;



    }

    private class Heart{


    }

    private class CNS{


    }
}
