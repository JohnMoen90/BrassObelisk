package Respiratory;

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
    private ArrayList<DiffusionEnvironment> allBloodEnvs = new ArrayList<>();

    private DiffusionEnvironment pulmonaryBlood;
    private DiffusionEnvironment bodyBlood;
    private DiffusionEnvironment bloodInHeart;

    // Diffusion rates  TODO: Move these to BodyConfig
    private double co2DiffusionRate = .2;
    private double o2DiffusionRate = .08;

    private static int bloodIDCounter;  // Each bloodUnit has an ID number for easy access
    private static int diffusionCounter;

    private double strokeVolume;


    /**
     * This constructor initializes all the blood units, splits the Blood Units into each bloo environment, and sets
     * all the starting variables from BodyConfig.
     * @param bodyWeight Determines how much blood there is.
     * @param strokeVolume Determines the size of the blood unit and how many blood units are pumped per pump
     */
    Blood(double bodyWeight, double strokeVolume){

        this.strokeVolume = strokeVolume;
        diffusionCounter = 0;
        bloodIDCounter = 1;

        Queue<BloodUnit> bloodUnits = new LinkedList<>();   // A list for all bloodUnits before transfer into Temp Queue

        // bodyWeight * 70 is the blood volume, strokeVolume is the BloodUnit size, and the * ten converts ml to dl
        int BUCount = (int) Math.round((bodyWeight * 70) / strokeVolume) * 10;
        for (int i = 0; i < BUCount; i++) {
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


        // Lungs
        for (int i = 0; i < bloodUnits.size() * .1; i++) {    // Lungs contain 10% of the body's blood at any given time
            tempQueue.add(bloodUnits.remove());
        }
        pulmonaryBlood = new DiffusionEnvironment("pulmonary",BodyConfig.LUNG_ENV, tempQueue);
        tempQueue.clear();


        // Heart
        for (int i = 0; i < Math.round(strokeVolume / 10); i++) {   // Stroke volume converted to decilitres
            tempQueue.add(bloodUnits.remove());
        }
        bloodInHeart = new DiffusionEnvironment("heart",BodyConfig.HEART_ENV, tempQueue);
        tempQueue.clear();


        // Body
        for (int i = 0; i < bloodUnits.size(); i++) {   // Put everything left in body environment
            tempQueue.add(bloodUnits.remove());
        }
        bodyBlood = new DiffusionEnvironment("body",BodyConfig.BODY_ENV, tempQueue);
    }


    /**
     * This method focuses on non-bloodQueue bloodEnvironment variables
     */
    public void initializeBloodEnvironments(){

        // Set circulatory loop
        bloodInHeart.setNextEnvironment(bodyBlood);
        bodyBlood.setNextEnvironment(pulmonaryBlood);  // Here I skip the fact that the blood goes through the heart before the lungs
        pulmonaryBlood.setNextEnvironment(bloodInHeart);

        // Set initial bloodEnvironment values
        setBloodEnvironmentPressures(bloodInHeart, BodyConfig.bodyo2, BodyConfig.bodyCo2);
        setBloodEnvironmentPressures(pulmonaryBlood, BodyConfig.lungo2, BodyConfig.lungCo2);
        setBloodEnvironmentPressures(bodyBlood, BodyConfig.bodyo2, BodyConfig.bodyCo2);

        // Save environments into list
        allBloodEnvs.add(bloodInHeart);
        allBloodEnvs.add(bodyBlood);
        allBloodEnvs.add(pulmonaryBlood);

        // Set bloodEnvironment size limits and initial co2/02 values
        for (DiffusionEnvironment bloodEnvironment: allBloodEnvs) {
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
        for (DiffusionEnvironment bloodEnv : allBloodEnvs) {
            if (bloodEnv.getBloodEnvID() == bloodEnvID) {
                if (reading.equals("po2")) {
                    return bloodEnv.bloodQueue.peek().po2;
                } else if (reading.equals("pco2")) {
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
            bloodInHeart.nextEnvironment.add(bloodInHeart.bloodQueue.remove());
        }

    }



    /**
     * This method equalizes gas pressures between every blood unit and their environment
     */
    public void diffuse(){
        diffusionCounter++;

        // Loop through every blood unit
        for (DiffusionEnvironment bloodEnvironment: allBloodEnvs) {
            for (BloodUnit bloodUnit: bloodEnvironment.bloodQueue) {

                // Add one to number of diffusions
                bloodUnit.timesDiffused++;

                // Calculate the difference between pressure values
                double co2Difference = Math.abs(bloodUnit.getPco2() - bloodEnvironment.pco2);
                double o2Difference = Math.abs(bloodUnit.getPo2() - bloodEnvironment.po2);

                // Equalize CO2
                if (bloodUnit.getPco2() > bloodEnvironment.pco2) {
                    bloodUnit.setPco2((bloodUnit.getPco2() - .1 - (co2Difference * co2DiffusionRate)));
                } else if (bloodUnit.getPco2() < bloodEnvironment.pco2) {
                    bloodUnit.setPco2((bloodUnit.getPco2() + .1 + (co2Difference * co2DiffusionRate)));
                }

                // Equalize O2
                if (bloodUnit.getPo2() > bloodEnvironment.po2) {
                    bloodUnit.setPo2((bloodUnit.getPo2() - .01 - (o2Difference * o2DiffusionRate)));
                } else if (bloodUnit.getPo2() < bloodEnvironment.po2) {
                    bloodUnit.setPo2((bloodUnit.getPo2() + .01 + (o2Difference * co2DiffusionRate)));
                }

                // Prints bloodUnit stats for the same bloodUnit each second for testing reasons
                if (bloodUnit.bloodID == 10 && bloodUnit.timesDiffused%10 == 0) {
                    diffusionCounter -= 1000;
                    System.out.println("--------------------------------");
                    System.out.printf("Environment:         %d. %s\n", bloodUnit.getCurrentEnvironment().bloodEnvID, bloodUnit.getCurrentEnvironment().name);
                    System.out.printf("Environment co2:     %s\n", bloodUnit.getCurrentEnvironment().pco2);
                    System.out.printf("Environment o2:      %s\n", bloodUnit.getCurrentEnvironment().po2);
                    System.out.printf("BloodUnit ID:        %s\n", bloodUnit.bloodID);
                    System.out.printf("BloodUnit o2:        %.2f\n", bloodUnit.getPo2());
                    System.out.printf("BloodUnit co2:       %.2f\n", bloodUnit.getPco2());
                    System.out.printf("Times diffused:      %d\n", bloodUnit.getTimesDiffused());
                    System.out.printf("o2 difference:       %.2f\n", o2Difference);
                    System.out.printf("co2 difference:      %.2f\n", co2Difference);
                    System.out.println("--------------------------------");
                }

                // Only print on normal diffusion calls



            }
        }

    }


    /**
     *
     * @param bloodEnvironment the bloodEnvironment you'd like to set values to
     * @param po2Value  the oxygen value, a 0 means don't change current value
     * @param pco2Value the co2Value, a 0 means don't change the current value
     */
    public void setBloodEnvironmentPressures(DiffusionEnvironment bloodEnvironment, double po2Value, double pco2Value){
        if (po2Value != 0) {
            bloodEnvironment.setPo2(po2Value);
        }
        if (pco2Value != 0) {
            bloodEnvironment.setPco2(pco2Value);
        }
    }



    /**
     * This class is basically a feature rich Queue which equalizes gas pressures with contained bloodUnits
     */
    private class DiffusionEnvironment {

        private Queue<BloodUnit> bloodQueue;
        private DiffusionEnvironment nextEnvironment;
        private int environmentSize;
        private String name;    // Helpful to access specific bloodEnvironment sometimes
        private int bloodEnvID; // Another way to access specific bloodEnvironments; heart - 0, lungs - 1, body - 2
        private double po2;
        private double pco2;


        DiffusionEnvironment(String name, int bloodEnvID, Queue<BloodUnit> bloodQueue){
            this.name = name;
            this.bloodEnvID = bloodEnvID;

            this.bloodQueue = new LinkedList<>();   //<-- This technique allows the tempQueue in
            this.bloodQueue.addAll(bloodQueue);     //    initializeBloodEnvironment() to be reused
        }


        /**
         * Whenever a bloodUnit is added to Queue, the head of the Queue is pushed to the nextEnvironment
         * @param bloodUnit Usually the head of the preceding bloodEnvironment
         */
        public void add(BloodUnit bloodUnit) {
            bloodQueue.add(bloodUnit);
            if (this.bloodQueue.size() > this.environmentSize) {
                nextEnvironment.add(bloodQueue.remove());
            }
        }

        /**
         * Get bloodQueue size
         * @return the size of the bloodEnvironment's bloodQueue
         */
        public int getSize(){
            return this.bloodQueue.size();
        }

        public double getPo2() {
            return po2;
        }

        public void setPo2(double po2) {
            this.po2 = po2;
        }

        public double getPco2() {
            return pco2;
        }

        public void setPco2(double pco2) {
            this.pco2 = pco2;
        }

        public DiffusionEnvironment getNextEnvironment() {
            return nextEnvironment;
        }

        public void setNextEnvironment(DiffusionEnvironment nextEnvironment) {
            this.nextEnvironment = nextEnvironment;
        }

        public int getEnvironmentSize() {
            return environmentSize;
        }

        public void setEnvironmentSize(int environmentSize) {
            this.environmentSize = environmentSize;
        }

        public int getBloodEnvID() {
            return bloodEnvID;
        }

        public void setBloodEnvID(int bloodEnvID) {
            this.bloodEnvID = bloodEnvID;
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

        BloodUnit(){
            this.po2 = 100; // mmHg
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
        public DiffusionEnvironment getCurrentEnvironment(){

            for (DiffusionEnvironment bloodEnvironment: allBloodEnvs) {
                for (BloodUnit bu : bloodEnvironment.bloodQueue) {
                    if (bu.equals(this)) {
                        return bloodEnvironment;
                    }
                }
            }

            return null;
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

        public void setTimesDiffused(int timesDiffused) {
            this.timesDiffused = timesDiffused;
        }
    }
}
