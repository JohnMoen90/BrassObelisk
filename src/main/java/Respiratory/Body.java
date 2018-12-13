package Respiratory;

public class Body {

    // Initialize some variables
    private Blood blood; // ml
    private double bodyWeight = BodyConfig.bodyWeight;
    private double Vo2 = (17 * bodyWeight) / 60; // ml perSecond
    private double Vco2 = (1.1 * bodyWeight) / 60; // ml perSecond


    // Constructor
    Body(){
        this.blood = new Blood(bodyWeight, BodyConfig.strokeVolume);
    }


    // Manage the reality of passing time each turn
    public void manageTurn(){
        blood.circulate();
        blood.diffuse("maj");

    }

    // Give access to blood
    public Blood bloodReadings()  {   // <-- Doesn't start w/ verb
        return blood;
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
