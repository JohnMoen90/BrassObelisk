/**
 * The Body class manages the inner body components - blood, lungs, heart, and cns or central nervous system
 * All variables that the user can influence are imported from BodyConfig and mainGUI
 *
 * Some Scrap Code/Notes:
 *     private double Vo2 = (17 * bodyWeight) / 60; // ml perSecond
 *     private double Vco2 = (1.1 * bodyWeight) / 60; // ml perSecond
 *
 */
public class Body {

    // Initialize some variables
    private double bodyWeight = BodyConfig.bodyWeight;

    // "Body objects" - blood, lungs, cns, and heart
    private static Blood blood;
    private Lungs lungs;
    private Heart heart;
    private CNS cns;

    private int changeAssignersCounter; // To mark off one second refresh

    private double vo2; // Amount of O2 needed

    private boolean smoker;

    private String exerciseXP;
    private double exerciseXPMulitiplier;


    // Constructor
    Body(){
        changeAssignersCounter = 0;
        vo2 = 40;
        smoker = false;

        blood = new Blood(bodyWeight, BodyConfig.strokeVolume);
        this.lungs = new Lungs(blood);
        this.heart = new Heart(blood);
        this.cns = new CNS(heart, lungs);
    }


    /**
     * This function handles each of the components state changes over time, it is activated by
     */
    public void manageTurn(){
        calculateGasAssignment();
        lungs.breathe();
        heart.pumpBlood();      // Diffusion currently happens here

    }


    /**
     * Used by the GUI classes to access organ methods and variables
     * NOTE - Not intended to return the object itself, as in the object won't have a reference saved elsewhere
     * @return access to variables and methods from the inner Blood class
     */
    public Blood bloodReadings() { return blood; }
    public Lungs lungReadings()  { return lungs; }
    public Heart heartReadings() { return heart; }       // <-- Not all used now but for future expansion may be useful
    public CNS cnsReadings()     { return cns;   }


    public String getBreathStatus(){    // <-- For GUI
        return lungs.getBreathState();
    }

    public void calculateGasAssignment(){
        changeAssignersCounter++;
        if (changeAssignersCounter == 10) {
            setMetabolicRate();
            cns.resolveImbalance();
        }

    }

    public void setMetabolicRate(){

        blood.setMetabolicRate(
                -(vo2 /((double)heart.calculateBeatsPerSecond()/1.2)),
                vo2 /(calculateVco2()));
    }

    public double calculateVco2(){
        return (vo2/((double)heart.calculateBeatsPerSecond()/1.2)) * .125;
    }

    public boolean isSmoker() {
        return smoker;
    }

    public void setSmoker(boolean smoker) {
        this.smoker = smoker;
    }

    public String getExerciseXP() {
        return exerciseXP;
    }

    public void setExerciseXP(String exerciseXP) {
        this.exerciseXP = exerciseXP;
        setMetabolicRate();
    }

    public double getExerciseXPMulitiplier() {
        return exerciseXPMulitiplier;
    }

    public void setExerciseXPMulitiplier() {
        double multiplier = 1;
        if (exerciseXP.equals("No training")) {
            multiplier = 1;
        } else if (exerciseXP.equals("Little training")) {
            multiplier = .9;
        } else if (exerciseXP.equals("Moderate training")) {
            multiplier = .8;
        } else if (exerciseXP.equals("Highly Trained")) {
            multiplier = .7;
        }

        this.exerciseXPMulitiplier = multiplier;
    }


    /**
     * This class handles not only the lungs but the entire respiratory system basically
     */
    public class Lungs{

        private Blood blood;

        private double totalLungCapacity = BodyConfig.totalLungCapacity;
        private double conductingZoneMax = totalLungCapacity * .8;
        private double respiratoryZoneSize = totalLungCapacity * .2;    // <-- This is where gas exchange happens
        private double currentConductingZoneVolume;

        private double conductionZoneO2;
        private double conductionZoneCO2;
        private double respiratoryZoneO2;
        private double respiratoryZoneCO2;

        private boolean breathHeld;
        private boolean inhale;
        private boolean exhale;

        private double respiratoryRate;
        private double respiratoryRateSetPoint;
        private double respiratoryDepth;    //Percentage of conductionZone
        private double respiratoryDepthSetPoint;
        private double totalVentilation;
        private double totalVentilationSetpoint;
        private double breathLength;
        private double breathCounter;


        public Lungs(Blood blood) {
            this.blood = blood;
            this.inhale = true;
            this.exhale = false;
            this.breathHeld = false;
            currentConductingZoneVolume = conductingZoneMax *.2;

            respiratoryRate = 12;
            respiratoryRateSetPoint = respiratoryRate;

            respiratoryDepth = conductingZoneMax * .25;
            respiratoryDepthSetPoint = conductingZoneMax * respiratoryDepth;

            totalVentilationSetpoint = respiratoryRate * respiratoryDepth;


//
            respiratoryZoneO2 = respiratoryZoneSize * .2;
            respiratoryZoneCO2 = respiratoryZoneSize * .1;


        }

        public void breathe(){


            // If the breath is not held
            if (!breathHeld) {

                // Set breath size and counter
                breathLength = (600 / respiratoryRate) * .5; // <-- converting from min to 10ths of a second
                breathCounter++;

                // If
                if (breathCounter >= breathLength) {
                    breathCounter -= breathLength;
                    if (inhale) {
                        exhale = true;
                        inhale = false;
                        setTotalVentilation();
                    } else {
                        inhale = true;
                        exhale = true;
                    }
                } else {
                    if (inhale) {
                        expandLungs(respiratoryDepth/breathLength);
                    } else {
                        contractLungs(respiratoryDepth/breathLength);
                    }


                }
            }




        }

        private void setTotalVentilation() {
            totalVentilation = respiratoryRate * respiratoryDepth;
            if (totalVentilation < 3.2) {
                blood.setLungValues(40,-5);
            } else if (totalVentilation < 4) {
                blood.setLungValues(45,-6);
            } if (totalVentilation < 5) {
                blood.setLungValues(50,-7);
            } if (totalVentilation < 7) {
                blood.setLungValues(55,-8);
            } if (totalVentilation < 9) {
                blood.setLungValues(60,-10);
            } else {
                blood.setLungValues(65,-11);
            }
        }

        public void expandLungs(double totalTickLungExpansion){
            conductionZoneO2 += totalTickLungExpansion * .2;
            currentConductingZoneVolume += totalTickLungExpansion;
        }

        public void contractLungs(double totalTickLungContraction){
            if(conductionZoneO2 > 0) {
                conductionZoneO2 -= totalTickLungContraction * .5;
            }
            if (conductionZoneCO2 > 0) {
                conductionZoneO2 -= totalTickLungContraction * .5;

            }
        }

        public String getBreathState(){
            if (breathHeld) {
                return "Breath held";
            } else {
                return inhale ? "Inhale" : "Exhale";
            }
        }

        public double getGasReadings(String gas) {
            if (gas.equals("o2")) {
                return conductionZoneO2 + respiratoryZoneO2;
            } else {
                return conductionZoneCO2 + respiratoryZoneCO2;
            }
        }

        public void increaseRespiratoryDepthValue(){
            respiratoryDepth += .25;

        }


        public void setRespiratoryRate(double respiratoryRate) {
            this.respiratoryRate = respiratoryRate;
        }

        public double getRespiratoryRate() {
            return respiratoryRate;
        }

        public double getRespiratoryDepth() {
            return respiratoryDepth;
        }

        public void setRespiratoryDepth(double respiratoryDepth) {
            this.respiratoryDepth = respiratoryDepth;
        }
    }

    /**
     * This class handles heart rate and sends some signals to the CNS
     */
    private class Heart{

        private int beatsPerMinute = 70; // BeatsPerMinute
        private int heartRate; // milliseconds
        private int bloodPumpCounter;

        Blood blood;

        Heart(Blood blood) {
            this.blood = blood;
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
         * This x10 is the BPSecond
         * @return
         */
        private int calculateHeartRate(){   // <-- Float is fine here since the evaluation is >=
            return (600 / beatsPerMinute);
        }
        private int calculateBeatsPerSecond(){return (beatsPerMinute / 60);}

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

        Heart heart;
        Lungs lungs;

        double arterialO2;
        double arterialCO2;

        int resolutionCounter;

        CNS(Heart heart, Lungs lungs){
            this.heart = heart;
            this.lungs = lungs;
        }

        public void getHeartReadings(){
            arterialO2 = blood.getReading(0,"O2");
            arterialCO2 = blood.getReading(0,"CO2");
        }

        public void resolveImbalance(){
            getHeartReadings();

            if (resolutionCounter > 0) {
                resolutionCounter--;
            } else if (arterialO2 < 100) {
                resolutionCounter += 10;
                for (int i = 0; i < 100 - arterialO2; i++) {

                    if (i % 2 == 0){
                        increaseBreathDepth();}
                    else {
                        increaseBreathRate();
                    }
                }

            }

            if (lungs.respiratoryRate > lungs.respiratoryRateSetPoint) {
                lungs.respiratoryRate -= .25;
            }
            if (lungs.respiratoryDepth > lungs.respiratoryDepthSetPoint) {
                lungs.respiratoryRate -= .25;
            }
            }
        }

        public void increaseBPM(){

        }

        public void increaseBreathRate(){
            lungs.respiratoryRate += 1;
        }

        public void increaseBreathDepth(){
            lungs.increaseRespiratoryDepthValue();
        }

    }

