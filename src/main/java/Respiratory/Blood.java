package Respiratory;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Blood {

    // Blood counter for blood unit IDs
    private static int bloodIDCounter;

    // Initialize blood unit lists
    private Queue<BloodUnit> bloodUnits = new LinkedList<>();
    private Queue<BloodUnit> allBloodUnits = new LinkedList<>();
    private ArrayList<BloodEnvironment> allBloodEnvs = new ArrayList<>();

    // Initialize blood environments
    private BloodEnvironment pulmonaryBlood;
    private BloodEnvironment bodyBlood;
    private BloodEnvironment bloodInHeart;

    // Diffusion rates
    private double co2DiffusionRate = .2;
    private double o2DiffusionRate = .08;


    // Constructor
    Blood(double bodyWeight, double strokeVolume){

        // Set some local variables
        bloodIDCounter = 1;
        int BUCount = (int) Math.round((bodyWeight * 70) / strokeVolume) * 10;
        Queue<BloodUnit> tempQueue = new LinkedList<>();

        // Generate blood object for each decilitre of blood
        for (int i = 0; i < BUCount; i++) {
            bloodUnits.add(new BloodUnit());
            allBloodUnits.add(new BloodUnit());
            bloodIDCounter++;
        }


        // %10 BUs in lungs
        for (int i = 0; i < BUCount * .1; i++) {
            tempQueue.add(bloodUnits.remove());
        }
        pulmonaryBlood = new BloodEnvironment("pulmonary",BodyConfig.LUNG_ENV, tempQueue);
        tempQueue.clear();


        // strokeVolume * 10 in Heart
        for (int i = 0; i < Math.round(strokeVolume / 10); i++) {
            tempQueue.add(bloodUnits.remove());
        }
        bloodInHeart = new BloodEnvironment("heart",BodyConfig.HEART_ENV, tempQueue);
        tempQueue.clear();


        // Rest in body
        for (int i = 0; i < bloodUnits.size(); i++) {
            tempQueue.add(bloodUnits.remove());
        }
        bodyBlood = new BloodEnvironment("body",BodyConfig.BODY_ENV, tempQueue);
        tempQueue.clear();


        // Initialize Blood Environments
        initializeBloodEnvironments();


    }

    public void initializeBloodEnvironments(){

        // Set circulatory loop
        bloodInHeart.setNextEnvironment(bodyBlood);
        bodyBlood.setNextEnvironment(pulmonaryBlood);  // Here I skip the fact that the blood goes through the heart before the lungs
        pulmonaryBlood.setNextEnvironment(bloodInHeart);

        // Set initial bloodEnvironment values --> 0:heart, 1:lungs, 2:body
        setBloodEnvironmentPressures(0, BodyConfig.bodyo2, BodyConfig.bodyCo2);
        setBloodEnvironmentPressures(1, BodyConfig.lungo2, BodyConfig.lungCo2);
        setBloodEnvironmentPressures(2, BodyConfig.bodyo2, BodyConfig.bodyCo2);

        // Save environments into list
        allBloodEnvs.add(bloodInHeart);
        allBloodEnvs.add(bodyBlood);
        allBloodEnvs.add(pulmonaryBlood);

        // Set bloodEnvironment size limits and initial co2/02 values
        for (BloodEnvironment bloodEnvironment: allBloodEnvs) {
            bloodEnvironment.setEnvironmentSize(bloodEnvironment.getSize());
        }

    }



    public double getReading(int bloodEnvID, String reading){

        //
        for (BloodEnvironment bloodEnv : allBloodEnvs) {
            if (bloodEnv.getBloodEnvID() == bloodEnvID) {
                if (reading.equals("po2")) {
                    return bloodEnv.bloodQueue.remove().po2;
                } else if (reading.equals("o2")) {
                    return bloodEnv.bloodQueue.remove().pco2;
                }
            }
        }
        return 0.0;
    }

    //Circulate blood
    public void circulate(){
        int BUsPerPump = bloodInHeart.getSize();

        for (int i = 0; i < BUsPerPump; i++) {
            bloodInHeart.nextEnvironment.add(bloodInHeart.bloodQueue.remove());
        }

    }

    // Diffuse blood unit gas pressures into current environment
    public void diffuse(String diffusionLengthSelector){

        // Set diffusionLength
        double diffusionLength = 1;
        if (diffusionLengthSelector.equals("min")) {
            diffusionLength = .1;
        }


        // Loop through each environment
        for (BloodEnvironment bloodEnvironment: allBloodEnvs) {

            // Loop through each blood unit
            for (BloodUnit bloodUnit: bloodEnvironment.bloodQueue) {

                // Calculate the difference between pressure values
                double co2Difference = Math.abs(bloodUnit.getPco2() - bloodEnvironment.pco2);
                double o2Difference = Math.abs(bloodUnit.getPo2() - bloodEnvironment.po2);

                // If there is more CO2 in blood
                if (bloodUnit.getPco2() > bloodEnvironment.pco2) {
                    bloodUnit.setPco2((bloodUnit.getPco2() - .1 - (co2Difference * co2DiffusionRate * diffusionLength)));
                // If there is more CO2 in environment
                } else if (bloodUnit.getPco2() < bloodEnvironment.pco2) {
                    bloodUnit.setPco2((bloodUnit.getPco2() + .1 + (co2Difference * co2DiffusionRate * diffusionLength)));
                }

                // If there is more O2 in blood
                if (bloodUnit.getPo2() > bloodEnvironment.po2) {
                    bloodUnit.setPo2((bloodUnit.getPo2() - .1 - (o2Difference * o2DiffusionRate * diffusionLength)));
                // If there is more O2 in environment
                } else if (bloodUnit.getPo2() < bloodEnvironment.po2) {
                    bloodUnit.setPo2((bloodUnit.getPo2() + .1 + (o2Difference * co2DiffusionRate * diffusionLength)));
                }

            }
        }

    }


    // Set Environmental Variables
    public void setBloodEnvironmentPressures(int bloodEnvironmentID, double po2Value, double pco2Value){

        for (BloodEnvironment bloodEnvironment: allBloodEnvs) {
            if (bloodEnvironment.getBloodEnvID() == bloodEnvironmentID) {
                bloodEnvironment.setPo2(po2Value);
                bloodEnvironment.setPco2(pco2Value);
            }
        }

    }



    // Blood Environment
    private class BloodEnvironment{

        // Research Data types, Queue
        private Queue<BloodUnit> bloodQueue;
        private BloodEnvironment nextEnvironment;
        private int environmentSize;
        private String name;
        private int bloodEnvID;
        private double po2;
        private double pco2;

        // Constructor
        BloodEnvironment(String name, int bloodEnvID, Queue<BloodUnit> bloodQueue){
            this.name = name;
            this.bloodEnvID = bloodEnvID;
            this.bloodQueue = bloodQueue;
        }


        // Manage blood unit inventory
        public void add(BloodUnit bu) {
            bloodQueue.add(bu);
            if (this.bloodQueue.size() > this.environmentSize) {
                nextEnvironment.bloodQueue.add(bloodQueue.remove());
            }
        }

        // Get the size of the queue
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

        public BloodEnvironment getNextEnvironment() {
            return nextEnvironment;
        }

        public void setNextEnvironment(BloodEnvironment nextEnvironment) {
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



    // Innerclass for blood units
    private class BloodUnit{
        private int bloodID;
        private double po2;
        private double pco2;

        BloodUnit(){
            this.po2 = 100; // mmHg
            this.pco2 = 40; // mmHg
            this.bloodID = bloodIDCounter + 1;
        }

        @Override
        public String toString(){

            return "ID: " + bloodID + " PO2: " + this.po2  + " PCo2: " + this.pco2 + "...\n";
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
    }
}
