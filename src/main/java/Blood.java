import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * The Blood class handles the transfer of nutrients and wastes throughout the body using two major components -
 *      BloodUnits          - Each representing roughly 10ml of blood, all moving and containing nutrients as one
 *      BloodEnvironment    - Feature-rich queues for the blood units that circulate contents starting at heart
 */
public class Blood {

    private Queue<BloodUnit> allBloodUnits = new LinkedList<>();
    private ArrayList<BloodEnvironment> allBloodEnvs = new ArrayList<>();

    private BloodEnvironment pulmonaryBlood;
    private BloodEnvironment diffusionZones;
    private BloodEnvironment arterialBlood;
    private BloodEnvironment venousBlood;
    private BloodEnvironment bloodInHeart;

    private static int bloodIDCounter;  // Each bloodUnit has an ID number for easy access

    private double strokeVolume;

    private String bloodUnitString;
    private double totalO2Consumed;
    private double totalCO2Produced;

    private int queuePositionsTraveled;
    private int bloodUnitTotal;
    private int circulations;



    /**
     * This constructor initializes all the blood units, splits the Blood Units into each bloo environment, and sets
     * all the starting variables from BodyConfig.
     * @param bodyWeight Determines how much blood there is.
     * @param strokeVolume Determines the size of the blood unit and how many blood units are pumped per pump
     */
    Blood(double bodyWeight, double strokeVolume){

        this.strokeVolume = strokeVolume;

        totalO2Consumed = 0;
        totalCO2Produced =0;

        bloodIDCounter = 1;

        queuePositionsTraveled = 0;
        circulations = 0;

        Queue<BloodUnit> bloodUnits = new LinkedList<>();   // A list for all bloodUnits before transfer into Temp Queue

        // bodyWeight * 70 is the blood volume, strokeVolume is the BloodUnit size, and the * ten converts ml to dl
        bloodUnitTotal = (int) Math.round((bodyWeight * 70) / strokeVolume) * 10;

        for (int i = 0; i < bloodUnitTotal; i++) {
            bloodUnits.add(new BloodUnit());
        }

        // Basically one initializer for bloodUnits and one for bloodEnvironments
        initializeBloodEnvironmentQueues(bloodUnits);
        initializeBloodEnvironments();

    }

    /**
     * Takes the blood units list generated in the constructor and distributes them amongst bloodEnvironments
     * @param bloodUnits
     */
    public void initializeBloodEnvironmentQueues(Queue<BloodUnit> bloodUnits){

        Queue<BloodUnit> tempQueue = new LinkedList<>();    // Temp queue for loading bloodUnits to blood environments
        int bloodUnitTotal = bloodUnits.size();

        // Lungs
        for (int i = 0; i < bloodUnits.size() * .1; i++) {    // Lungs contain 10% of the body's blood at any given time
            tempQueue.add(bloodUnits.remove());
        }
        pulmonaryBlood = new BloodEnvironment("pulmonary",BodyConfig.LUNG_ENV_ID, tempQueue);
        tempQueue.clear();


        // Heart
        for (int i = 0; i < Math.round(strokeVolume / 10); i++) {   // Stroke volume converted to decilitres
            tempQueue.add(bloodUnits.remove());
        }
        bloodInHeart = new BloodEnvironment("heart",BodyConfig.HEART_ENV_ID, tempQueue);
        tempQueue.clear();


        // Arterial Circulation - 30% of blood in free circulation
        for (int i = 0; i < Math.round(bloodUnits.size() * .3); i++) {   // Stroke volume converted to decilitres
            tempQueue.add(bloodUnits.remove());
        }
        arterialBlood = new BloodEnvironment("arteries",BodyConfig.ART_ENV_ID, tempQueue);
        tempQueue.clear();


        // Diffusion zones are any organ/capillary system that engages in gas exchange
        for (int i = 0; i < Math.round(bloodUnitTotal * .15); i++) {   // Around 20 percent of total blood excluding lungs
            tempQueue.add(bloodUnits.remove().setBothReadings(60,45));
        }
        diffusionZones = new BloodEnvironment("diffusionZones",BodyConfig.DIFFZONE_ENV_ID, tempQueue);
        tempQueue.clear();


        // Venous Circulation - 70% of blood in free circulation
        for (int i = 0; i < bloodUnits.size(); i++) {   // Stroke volume converted to decilitres
            tempQueue.add(bloodUnits.remove().setBothReadings(60,45));
        }
        venousBlood = new BloodEnvironment("veins",BodyConfig.VEIN_ENV_ID, tempQueue);
        tempQueue.clear();

        System.out.println(pulmonaryBlood.bloodQueue);
        System.out.println(bloodInHeart.bloodQueue);
        System.out.println(arterialBlood.bloodQueue);
        System.out.println(diffusionZones.bloodQueue);
        System.out.println(venousBlood.bloodQueue);

    }


    /**
     * This method focuses on non-bloodQueue bloodEnvironment variables
     */
    public void initializeBloodEnvironments(){

        // Set circulatory loop
        bloodInHeart.setNextEnvironment(arterialBlood);
        arterialBlood.setNextEnvironment(diffusionZones);
        diffusionZones.setNextEnvironment(venousBlood);
        venousBlood.setNextEnvironment(pulmonaryBlood);
        pulmonaryBlood.setNextEnvironment(bloodInHeart);

        setMetabolicRate(BodyConfig.initVO2, BodyConfig.initVCO2);
        setLungValues(BodyConfig.initLungO2, BodyConfig.initLungCO2);
        pulmonaryBlood.setDiffusionZone(true);
        diffusionZones.setDiffusionZone(true);

        // Set initial bloodEnvironment values
//        setBloodEnvironmentPressures(bloodInHeart, false, 0.0, 0.0);
//        setBloodEnvironmentPressures(arterialBlood, false, 0.0, 0.0);
//        setBloodEnvironmentPressures(diffusionZones, true,BodyConfig.diffusionZone02perBU * diffusionZones.bloodQueue.size(),
//                BodyConfig.diffusionZoneC02perBU * diffusionZones.bloodQueue.size());
//        setBloodEnvironmentPressures(venousBlood, false, 0.0, 0.0);
//        setBloodEnvironmentPressures(pulmonaryBlood, true, BodyConfig.alveolarZoneO2, 0);

        // Save environments into list
        allBloodEnvs.add(bloodInHeart);
        allBloodEnvs.add(arterialBlood);
        allBloodEnvs.add(diffusionZones);
        allBloodEnvs.add(venousBlood);
        allBloodEnvs.add(pulmonaryBlood);

//        // Set diffusionZone gas values
//        System.out.println("DiffZone O2: " + diffusionZones.diffusionZoneO2);
//        System.out.println("DiffZone CO2: " +diffusionZones.diffusionZoneCO2);

        // Set bloodEnvironment size limits and initial co2/02 values
        for (BloodEnvironment bloodEnvironment: allBloodEnvs) {
            bloodEnvironment.setEnvironmentSize(bloodEnvironment.getSize());
        }

    }


    /**
     * This is a temporary way of getting pO2 and pCO2 readings from blood queues.
     * @param bloodEnvID The bloodEnvironment you'd like to query
     * @param reading A choice between pO2 and Pco2 TODO add an enum for these values
     * @return the value in mmHg
     */
    public double getReading(int bloodEnvID, String reading){

        //
        for (BloodEnvironment bloodEnv : allBloodEnvs) {
            if (bloodEnv.getBloodEnvID() == bloodEnvID) {
                if (reading.equals("o2")) {
                    return bloodEnv.bloodQueue.peek().po2;
                } else if (reading.equals("co2")) {
                    return bloodEnv.bloodQueue.peek().pco2;
                }
            }
        }
        return 0.0; // This should never happen
    }

    /**
     * Moves blood from heart to next bloodEnvironment, starting a chain reaction facilitated by add() method
     */
    public void circulate(){

        int BUsPerPump = bloodInHeart.getSize();
        for (int i = 0; i < BUsPerPump; i++) {
            handleCirculationCounter();
            bloodInHeart.nextEnvironment.add(bloodInHeart.bloodQueue.remove());
            diffuse();

        }

    }

    public void handleCirculationCounter(){
        queuePositionsTraveled++;
        if (queuePositionsTraveled == bloodUnitTotal) {
            System.out.printf("allbloodunits: %d\n",allBloodUnits.size());
            queuePositionsTraveled = 0;
            circulations++;
        }

    }

    public void setMetabolicRate(double o2Value, double co2Value){
        diffusionZones.setGasAssigner("o2", o2Value);
        diffusionZones.setGasAssigner("co2", co2Value);
    }

    public void setLungValues(double o2Value, double co2Value){
        pulmonaryBlood.setGasAssigner("o2", o2Value);
        pulmonaryBlood.setGasAssigner("co2", co2Value);
    }

    public double getDiffusionZoneAssigners(int bloodEnvironmentID, String gas) {
        for (BloodEnvironment bloodEnvironment : allBloodEnvs) {
            if (bloodEnvironment.bloodEnvID == bloodEnvironmentID) {
                if (gas.equals("o2")) {
                    return bloodEnvironment.o2Assigner;
                } else {
                    return bloodEnvironment.co2Assigner;
                }
            }
        }
        return 0.0;
    }

    /**
     * This method equalizes gas pressures between every blood unit and their environment
     */
    public void diffuse(){

        // Loop through every blood unit
        for (BloodEnvironment bloodEnvironment: allBloodEnvs) {
            for (BloodUnit bloodUnit : bloodEnvironment.bloodQueue) {
                if (bloodEnvironment.isDiffusionZone()) {
                    bloodUnit.diffuse();
                    // Add one to number of diffusions
                    bloodUnit.timesDiffused++;

                    /** I am keeping this here so you can seen my old diffusion algorithm
                     *
                     *
                     *    Calculate the difference between pressure values
                     *    double co2Difference = Math.abs(bloodUnit.getPco2() - bloodEnvironment.diffusionZoneCO2perBU);
                     *   double o2Difference = Math.abs(bloodUnit.getPo2() - bloodEnvironment.diffusionZoneO2perBU);
                     *
                     *    Equalize CO2
                     *   if (bloodUnit.getPco2() > bloodEnvironment.diffusionZoneCO2perBU) {
                     *       bloodEnvironment.setDiffusionZoneCO2(bloodEnvironment.getDiffusionZoneCO2() + .5);
                     *       bloodUnit.setPco2(bloodUnit.getPco2() - .5);
                     *   } else if (bloodUnit.getPco2() < bloodEnvironment.diffusionZoneCO2perBU) {
                     *       bloodUnit.setPco2(bloodUnit.getPco2() + .5);
                     *       bloodEnvironment.setDiffusionZoneCO2(bloodEnvironment.getDiffusionZoneCO2() - .5);
                     *   }
                     *
                     *
                     *   // Equalize O2
                     *   if (bloodUnit.getPo2() > bloodEnvironment.diffusionZoneO2perBU) {
                     *       bloodEnvironment.setDiffusionZoneO2(bloodEnvironment.getDiffusionZoneO2() + .5);
                     *       bloodUnit.setPo2(bloodUnit.getPo2() - .5);
                     *   } else if (bloodUnit.getPo2() < bloodEnvironment.diffusionZoneO2perBU) {
                     *       bloodUnit.setPo2(bloodUnit.getPo2() + .5);
                     *       bloodEnvironment.setDiffusionZoneO2(bloodEnvironment.getDiffusionZoneO2() - .5);
                     *   }
                     *
                     */

                }
            }
        }
    }


//    /**
//     *
//     * @param bloodEnvironment the bloodEnvironment you'd like to set values to
//     * @param po2Value  the oxygen value, a 0 means don't change current value
//     * @param pco2Value the co2Value, a 0 means don't change the current value
//     */
//    public void setBloodEnvironmentPressures(BloodEnvironment bloodEnvironment, boolean diffusionZone, double po2Value, double pco2Value){
//        bloodEnvironment.setDiffusionZone(diffusionZone);
//        if (diffusionZone) {
//            if (po2Value != 0) {
//                bloodEnvironment.setDiffusionZoneO2(po2Value);
//            }
//            if (pco2Value != 0) {
//                bloodEnvironment.setDiffusionZoneCO2(pco2Value);
//            }
//        }
//    }
//    public void setBloodEnvironmentPressures(String bloodEnvironmentString, double po2Value, double pco2Value){
//
//        for (BloodEnvironment bloodEnvironment: allBloodEnvs) {
//            if (bloodEnvironment.name.equals(bloodEnvironmentString)) {
//                if (po2Value != 0) {
//                    bloodEnvironment.setDiffusionZoneO2(po2Value);
//                }
//                if (pco2Value != 0) {
//                    bloodEnvironment.setDiffusionZoneCO2(pco2Value);
//                }
//            }
//        }
//
//
//    }


    /**
     * Builds the display string in the displayGUI for bloodUnits. I always use bloodUnit #10
     * @param bloodUnitID I always use 10, arbitrary choice
     * @return The string, ready to print
     */
    public String getBloodUnitString(int bloodUnitID) {
        String tempBloodUnitString = "";
        for (BloodUnit bloodUnit: allBloodUnits){
            if (bloodUnit.bloodID == bloodUnitID) {
                tempBloodUnitString += String.format("--------------------------------\n");
                tempBloodUnitString += String.format("Environment:     %s\n", bloodUnit.getCurrentEnvironment().name);
                tempBloodUnitString += String.format("                                \n");
                tempBloodUnitString += String.format("o2Assigner:      %.2f\n", bloodUnit.getCurrentEnvironment().o2Assigner);
                tempBloodUnitString += String.format("BloodUnit o2/t:  %.2f\n", bloodUnit.perTickO2Change);
                tempBloodUnitString += String.format("BloodUnit o2:    %.2f\n", bloodUnit.getPo2());
                tempBloodUnitString += String.format("                                \n");
                tempBloodUnitString += String.format("co2Assigner:     %.2f\n", bloodUnit.getCurrentEnvironment().co2Assigner);
                tempBloodUnitString += String.format("BloodUnit co2/t: %.2f\n", bloodUnit.perTickCO2Change);
                tempBloodUnitString += String.format("BloodUnit co2:   %.2f\n", bloodUnit.getPco2());
                tempBloodUnitString += String.format("                                \n");
                tempBloodUnitString += String.format("Times diffused:  %d\n", bloodUnit.getTimesDiffused());
                tempBloodUnitString += String.format("BloodUnit ID:    %s\n", bloodUnit.bloodID);
                tempBloodUnitString += String.format("                                \n");
                tempBloodUnitString += String.format("--------------------------------\n");
                bloodUnitString = tempBloodUnitString;
            }
        }
        return bloodUnitString;

    }



    public int getCirculations() {
        return circulations;
    }


    public int getQueuePositionsTraveled(){
        return queuePositionsTraveled;
    }


    public int getBloodUnitTotal(){
        return bloodUnitTotal;
    }


    /**
     * This class is basically a feature rich Queue which equalizes gas pressures with contained bloodUnits
     */
    private class BloodEnvironment {

        private Queue<BloodUnit> bloodQueue;
        private BloodEnvironment nextEnvironment;
        private int environmentSize;
        private String name;    // Helpful to access specific bloodEnvironment sometimes
        private int bloodEnvID; // Another way to access specific bloodEnvironments; heart - 0, lungs - 1, body - 2
        private boolean diffusionZone;
        private double o2Assigner;
        private double co2Assigner;

        private double totalO2Diffused;
        private double totalCO2Diffused;


        BloodEnvironment(String name, int bloodEnvID, Queue<BloodUnit> bloodQueue){
            this.name = name;
            this.bloodEnvID = bloodEnvID;

            this.bloodQueue = new LinkedList<>();   //<-- This technique allows the tempQueue in
            this.bloodQueue.addAll(bloodQueue);     //    initializeBloodEnvironment() to be reused

            this.o2Assigner = 0.0;  // <-- for the non diffusion zone bloodEnvironments
            this.co2Assigner = 0.0;
        }


        /**
         * Whenever a bloodUnit is added to Queue, the head of the Queue is pushed to the nextEnvironment
         * @param bloodUnit Usually the head of the preceding bloodEnvironment
         */
        public void add(BloodUnit bloodUnit) {
            bloodQueue.add(bloodUnit);
            if (this.bloodQueue.size() > this.environmentSize) {
                if (nextEnvironment.isDiffusionZone()) {
                    nextEnvironment.add(nextEnvironment.gasAssigner(bloodQueue.remove()));
                } else {
                    nextEnvironment.add(bloodQueue.remove());
                }
            }
        }

        /**
         * Assigns per turn value change for blood units entering the queue
         * @param bu takes a boodUnit and assigns its perTick change
         * @return
         */
        private BloodUnit gasAssigner(BloodUnit bu) {
            bu.setPerTickO2Change(o2Assigner / bloodQueue.size());
            bu.setPerTickCO2Change(co2Assigner / bloodQueue.size());
            return bu;
        }

        /**
         * Get bloodQueue size
         * @return the size of the bloodEnvironment's bloodQueue
         */
        public int getSize(){
            return this.bloodQueue.size();
        }

        @Override
        public String toString(){
            return this.name;
        }


        /**
         * Sets how much gas is taken/given to the passing BU
         * @return
         */
        public void setGasAssigner(String gasToAssign, double assignmentValue) {
            if (gasToAssign.equals("o2")) {
                o2Assigner = assignmentValue;
                System.out.println(assignmentValue);
            } else {
                co2Assigner = assignmentValue;
            }
        }



        public void setNextEnvironment(BloodEnvironment nextEnvironment) {
            this.nextEnvironment = nextEnvironment;
        }

        public void setEnvironmentSize(int environmentSize) {
            this.environmentSize = environmentSize;
        }

        public int getBloodEnvID() {
            return bloodEnvID;
        }

        public boolean isDiffusionZone() {
            return diffusionZone;
        }

        public void setDiffusionZone(boolean diffusionZone) {
            this.diffusionZone = diffusionZone;
        }

        /**
         * Old diffusion Algorithm, for your reading pleasure
         *
         *
         *         public void metabolize() {
         *             if (this.equals(diffusionZones)) {
         *                 setDiffusionZoneO2(getDiffusionZoneO2() - 10);
         *                 totalO2Consumed += 10;
         *                 setDiffusionZoneCO2(getDiffusionZoneCO2() + (9));
         *                 totalCO2Produced += 9;
         *             }
         *         }
         */


        public double getTotalO2Diffused() {
            return totalO2Diffused;
        }

        public void setTotalO2Diffused(double totalO2Diffused) {
            this.totalO2Diffused = totalO2Diffused;
        }

        public double getTotalCO2Diffused() {
            return totalCO2Diffused;
        }

        public void setTotalCO2Diffused(double totalCO2Diffused) {
            this.totalCO2Diffused = totalCO2Diffused;
        }
    }



    /**
     * Blood units represent 10 ml of blood and each have their own nutrient/waste values
     * They can be accessed from their bloodEnvironment bloodQueue positions or by their ID numbers
     */
    private class BloodUnit{
        private int timesDiffused;
        private int bloodID;
        private double po2;
        private double pco2;

        private double perTickO2Change;
        private double perTickCO2Change;


        BloodUnit(){
            this.po2 = 100; // ml/L     <-- All blood starts w/ arterial stats but changes during bloodEnvironment init
            this.pco2 = 40; // mmHg
            this.bloodID = bloodIDCounter;
            this.timesDiffused =0;
            bloodIDCounter++;
            allBloodUnits.add(this);
        }



        /**
         * Useful for testing
         * @return the bloodEnvironment a bloodUnit is currently in
         */
        public BloodEnvironment getCurrentEnvironment(){

            for (BloodEnvironment bloodEnvironment: allBloodEnvs) {
                for (BloodUnit bu : bloodEnvironment.bloodQueue) {
                    if (bu.equals(this)) {
                        return bloodEnvironment;
                    }
                }
            }

            return null;
        }


        @Override
        public String toString(){
            return "BU: "+ this.bloodID;
        }

        public void setPerTickO2Change(double perTickO2Change) {
            this.perTickO2Change = perTickO2Change;
        }

        public void setPerTickCO2Change(double perTickCO2Change) {
            this.perTickCO2Change = perTickCO2Change;
        }

        /**
         * Changes current gas contents by an amount determined by the bloodEnvironment
         *  Possible o2 ranges: 40-100
         *  possible c02 ranges: 40-47
         */
        public void diffuse(){
            BloodEnvironment ce = getCurrentEnvironment();

            if (po2 < 100 && getCurrentEnvironment().o2Assigner > 0) {po2 += perTickO2Change;}
            if (po2 > 40 && getCurrentEnvironment().o2Assigner < 0) {po2 += perTickO2Change;}
            ce.setTotalO2Diffused(ce.getTotalO2Diffused() + perTickO2Change);

            if (po2 < 50 && getCurrentEnvironment().o2Assigner > 0) {po2 += perTickO2Change;}
            if (po2 > 40 && getCurrentEnvironment().o2Assigner < 0) {po2 += perTickO2Change;}
            ce.setTotalCO2Diffused(ce.getTotalCO2Diffused() + perTickCO2Change);
        }

        public double getPo2() {
            return po2;
        }

        public void setPo2(double env_po2) {
            this.po2 = env_po2;
        }

        public double getPco2() {
            return pco2;
        }

        public void setPco2(double pco2) {
            this.pco2 = pco2;
        }

        public int getTimesDiffused() {
            return timesDiffused;
        }

        public BloodUnit setBothReadings(int i, int i1) {
            setPo2(i);
            setPco2(i1);
            return this;
        }
    }
}
