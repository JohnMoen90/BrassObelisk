package Respiratory;

public class Body {

    // Initialize some variables
    private double bodyWeight = BodyConfig.bodyWeight;
    Blood blood; // ml
    private double Vo2 = (17 * bodyWeight) / 60; // ml perSecond
    private double Vco2 = (1.1 * bodyWeight) / 60; // ml perSecond

    // Variables to store readings from blood



    // Constructor
    Body(){
        this.blood = new Blood(bodyWeight, BodyConfig.strokeVolume);
    }


    public void manageTurn(){
        blood.circulate();
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
