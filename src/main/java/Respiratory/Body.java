package Respiratory;

/**
 * The Body class manages the inner body components - blood, lungs, heart, and cns or central nervous system
 * All variables that the user can influence are imported from BodyConfig and mainGUI
 */
public class Body {

    // Initialize some variables
    private double bodyWeight = BodyConfig.bodyWeight;
    private double Vo2 = (17 * bodyWeight) / 60; // ml perSecond
    private double Vco2 = (1.1 * bodyWeight) / 60; // ml perSecond

    // "Body objects" - blood, lungs, cns, and heart
    private static Blood blood;
    private Lungs lungs;
    private Heart heart;
    private CNS cns;


    // Constructor
    Body(){
        blood = new Blood(bodyWeight, BodyConfig.strokeVolume);
        this.lungs = new Lungs();
        this.heart = new Heart();
        this.cns = new CNS();
    }


    /**
     * This function handles each of the components state changes over time, it is activated by
     */
    public void manageTurn(){

        blood.diffuse();
        heart.pumpBlood();

    }


    /**
     * Used by the GUI classes to access organ methods and variables
     * NOTE - Not intended to return the object itself, as in the object won't have a reference saved elsewhere
     * @return access to variables and methods from the inner Blood class
     */
    public Blood bloodReadings() { return blood; }
    public Lungs lungReadings()  { return lungs; }
    public Heart heartReadings() { return heart; }
    public CNS cnsReadings()     { return cns;   }


    /**
     * This class handles not only the lungs but the entire respiratory system basically
     */
    private class Lungs{

        private double totalLungCapacity = BodyConfig.totalLungCapacity;
        private double conductingZone = totalLungCapacity * .8;
        private double respiratoryZone = totalLungCapacity * .2;    // <-- This is where gas exchange happens


        public Lungs() {

        }
    }

    /**
     * This class handles heart rate and sends some signals to the CNS
     */
    private class Heart{

        private int beatsPerMinute = 70; // BeatsPerMinute
        private int heartRate; // milliseconds
        private int bloodPumpCounter;

        Heart() {
            heartRate = calculateHeartRate();
            bloodPumpCounter = 0;
        }

        public void pumpBlood(){
            // Controls speed of heart pumping
            bloodPumpCounter++;
            if (bloodPumpCounter >= calculateHeartRate()) {
                bloodPumpCounter -= calculateHeartRate();
                blood.circulate();
            }

        }

        /**
         * Converts the BPM to to appropriate counter value, which is .1 seconds each tick.
         * @return
         */
        private int calculateHeartRate(){   // <-- Float is fine here since the evaluation is >=
            return (600 / beatsPerMinute);
        }

        public int getHeartRate() {
            return heartRate;
        }

        public void setHeartRate(int heartRate) {
            this.heartRate = heartRate;
        }
    }

    /**
     * This class handles negative feedback loops and links lung/heart behavior with nutrient/waste levels in blood
     */
    private class CNS{

    }
}
