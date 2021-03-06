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

    private int fitnessLevel;


    // Constructor
    Body(){
        changeAssignersCounter = 0;
        vo2 = 40;
        smoker = false;
        fitnessLevel = 0;

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
            changeAssignersCounter = 0;
            setMetabolicRate();
            cns.resolveImbalance();
        }

    }

    /**
     * This is suppose to set the rate of change in the diffusion zones, however does not seem to work and I can not
     * figure out why....
     */
    public void setMetabolicRate(){
        setVO2();
        blood.setMetabolicRate(
                vo2 * Math.abs(heart.calculateBeatsPerSecond() - 1.2),
                vo2 /(calculateVco2()));
        BodyConfig.currentVO2 = -vo2 * (1.2 / heart.calculateBeatsPerSecond());

    }

    private void setVO2() {
        vo2 = BodyConfig.initVO2 * (1 +(.15 * BodyConfig.fitnessLevel));
    }

    public double calculateVco2(){
        return (vo2 * (1.2 / heart.calculateBeatsPerSecond())) * .125;
    }

    /**
     * Not Currently used but it is another implementation
     * @return multiplier that corresponds with fitness value
     */
    public double getFitnessLevelMulitiplier() {

        if (fitnessLevel == 0) {
            return 1;
        } else if (fitnessLevel == 1) {
            return .9;
        } else if (fitnessLevel == 2) {
            return .8;
        } else {
            return .7;
        }

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

            // SOme initial values based on average resting rates
            currentConductingZoneVolume = conductingZoneMax *.2;

            respiratoryRate = 12;
            respiratoryRateSetPoint = respiratoryRate;

            respiratoryDepth = conductingZoneMax * .25;
            respiratoryDepthSetPoint = conductingZoneMax * respiratoryDepth;

            totalVentilationSetpoint = respiratoryRate * respiratoryDepth;

            respiratoryZoneO2 = respiratoryZoneSize * .2;
            respiratoryZoneCO2 = respiratoryZoneSize * .1;


        }

        /**
         * In the current implementation the result here is mostly cosmetic, but in the future
         * the actual size of the lung can be saved at any given moment and one could calculate
         * how much oxygen is in reserve
         */
        public void breathe(){


            // If the breath is not held
            if (!breathHeld) {

                // Set breath size and counter
                breathLength = (600/respiratoryRate) * .5; // <-- converting from min to 10ths of a second
                breathCounter++;

                // When the breath counter is reached, switch direction and reset counter
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
                        expandLungs(respiratoryDepth/breathLength); // Not currently useful
                    } else {
                        contractLungs(respiratoryDepth/breathLength);
                    }


                }
            }


        }

        /**
         * Total ventilation, rate * depth, determines the o2 assignment value in lungs
         */
        private void setTotalVentilation() {
            totalVentilation = respiratoryRate * respiratoryDepth;
            totalVentilation = smoker ? totalVentilation : totalVentilation * .8;
            if (totalVentilation < 3.2) {
                blood.setLungValues(40,-5);
            } else if (totalVentilation < 4) {
                blood.setLungValues(43,-6);
            } if (totalVentilation < 5) {
                blood.setLungValues(46,-7);
            } if (totalVentilation < 7) {
                blood.setLungValues(49,-8);
            } if (totalVentilation < 9) {
                blood.setLungValues(52,-10);
            } else {
                blood.setLungValues(55,-11);
            }
        }

        /**
         * Not useful in current implementation
         * @param totalTickLungExpansion
         */
        public void expandLungs(double totalTickLungExpansion){
            conductionZoneO2 += totalTickLungExpansion * .2;
            currentConductingZoneVolume += totalTickLungExpansion;
        }


        /**
         * Not useful in current implementation
         * @param totalTickLungContraction
         */
        public void contractLungs(double totalTickLungContraction){
            if(conductionZoneO2 > 0) {
                conductionZoneO2 -= totalTickLungContraction * .5;
            }
            if (conductionZoneCO2 > 0) {
                conductionZoneO2 -= totalTickLungContraction * .5;

            }
        }

        /**
         * Gives the appropriate string to the display GUI
         * @return return
         */
        public String getBreathState(){
            if (breathHeld) {
                return "Breath held";
            } else {
                return inhale ? "Inhale" : "Exhale";
            }
        }

        /**
         * Not in current implementation
         * @param gas string of the gas needed, always "o2" or "co2"
         * @return
         */
        public double getGasReadings(String gas) {
            if (gas.equals("o2")) {
                return conductionZoneO2 + respiratoryZoneO2;
            } else {
                return conductionZoneCO2 + respiratoryZoneCO2;
            }
        }


        public double getTotalVentilation(){return totalVentilation;}
    }

    /**
     * This class handles heart rate and sends some signals to the CNS
     */
    public class Heart{

        private int beatsPerMinute; // BeatsPerMinute
        private int heartRate; // milliseconds
        private int bloodPumpCounter;

        Blood blood;

        Heart(Blood blood) {
            this.blood = blood;
            heartRate = calculateHeartRate();
            beatsPerMinute = 70;
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
            return (beatsPerMinute /60) * 10;
        }
        private int calculateBeatsPerSecond(){return (beatsPerMinute / 60);}

        public int getHeartRate() {
            return heartRate;
        }

        public void setHeartRate(int heartRate) {
            this.heartRate = heartRate;
        }

        public int getBeatsPerMinute(){
            return beatsPerMinute;
        }
    }

    /**
     * This class handles negative feedback loops and links lung/heart behavior with nutrient/waste levels in blood
     * CNS stands for central nervous system
     */
    public class CNS{

        Heart heart;
        Lungs lungs;

        double arterialO2;
        double arterialCO2;

        int resolutionCounter;
        String cnsTextString;

        CNS(Heart heart, Lungs lungs){
            this.heart = heart;
            this.lungs = lungs;
            resolutionCounter = 0;
        }

        public void getHeartReadings(){
            arterialO2 = blood.getReading(0,"o2");
            arterialCO2 = blood.getReading(0,"co2");
        }

        /**
         * This method is supposed to tweak the lungs and heart to respond to lowered co2 and o2 levels, it doesn't though!
         */
        public void resolveImbalance(){
            resolutionCounter++;
            getHeartReadings();


            if (resolutionCounter == 5) {   // <--- Just to slow down the descent into madness that happens every time
                String cnsString = "--------------\n";
                resolutionCounter = 0;
                if (arterialO2 < 95) {
                    lungs.respiratoryDepth += .19;
                    cnsString += "Increasing Respiratory Depth\n";
                }

                if (arterialO2 > 99 && lungs.respiratoryDepthSetPoint < lungs.respiratoryDepth) {
                    lungs.respiratoryDepth -= .19;
                    cnsString += "Decreasing Respiratory Depth\n";
                }

                if (arterialO2 < 85 && lungs.respiratoryRateSetPoint < lungs.respiratoryRate) {
                    lungs.respiratoryRate++;
                    cnsString += "Increasing Respiratory Rate\n";
                } else if (arterialO2 > 92) {
                    lungs.respiratoryRate--;
                    cnsString += "Decreasing Respiratory Rate\n";
                }

                if (arterialO2 < 80) {
                    heart.beatsPerMinute++;
                    cnsString += "Increasing Heart Rate\n";
                }

                if (arterialO2 > 90) {
                    heart.beatsPerMinute--;
                    cnsString += "Decreasing Heart Rate\n";
                }
                cnsTextString = cnsString;

            }


        }

        /**
         * For the CNS stream in the main GUI
         * @return
         */
        public String getCNSText(){
            return cnsTextString;
        }

        }


    }

