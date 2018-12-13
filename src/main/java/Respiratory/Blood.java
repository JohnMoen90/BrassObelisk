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

        System.out.println(pulmonaryBlood.toString());

        // Initialize Blood Environments
        initializeBloodEnvironments();


    }

    public void initializeBloodEnvironments(){

        // Set circulatory loop
        bloodInHeart.setNextEnvironment(bodyBlood);
        bodyBlood.setNextEnvironment(pulmonaryBlood);  // Here I skip the fact that the blood goes through the heart before the lungs
        pulmonaryBlood.setNextEnvironment(bloodInHeart);

        // Set initial bloodEnvironment values --> 0:heart, 1:lungs, 2:body
        setBloodEnvironmentPressures(bloodInHeart, BodyConfig.bodyo2, BodyConfig.bodyCo2);
        setBloodEnvironmentPressures(pulmonaryBlood, BodyConfig.lungo2, BodyConfig.lungCo2);
        setBloodEnvironmentPressures(bodyBlood, BodyConfig.bodyo2, BodyConfig.bodyCo2);

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
                    return bloodEnv.bloodQueue.peek().po2;
                } else if (reading.equals("pco2")) {
                    return bloodEnv.bloodQueue.peek().pco2;
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
            diffuse("min");
        }

        System.out.println(allBloodUnits.peek().po2);

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

                // Test a blood unit
                if (bloodUnit.bloodID == 2 & diffusionLength == 1) {
                    System.out.println("--------------------------------");
                    System.out.printf("Environment:         %d. %s\n", bloodUnit.getCurrentEnvironment().bloodEnvID, bloodUnit.getCurrentEnvironment().name);
                    System.out.printf("Environment co2:     %s\n", bloodUnit.getCurrentEnvironment().pco2);
                    System.out.printf("Environment o2:      %s\n", bloodUnit.getCurrentEnvironment().po2);
                    System.out.printf("o2 difference:       %.2f\n", o2Difference);
                    System.out.printf("co2 difference:      %.2f\n", co2Difference);
                    System.out.printf("BloodUnit o2:        %.2f\n", bloodUnit.getPo2());
                    System.out.printf("BloodUnit co2:       %.2f\n", bloodUnit.getPco2());
                    System.out.printf("Diffusion length:    %.2f\n", diffusionLength);
                    System.out.println("--------------------------------");

                }

            }
        }

    }


    // Set Environmental Variables
    public void setBloodEnvironmentPressures(BloodEnvironment bloodEnvironment, double po2Value, double pco2Value){

        System.out.printf("%s\n",bloodEnvironment.name);
        System.out.printf("po2Value - %.2f\n",po2Value);
        System.out.printf("pco2Value - %.2f\n",pco2Value);

        bloodEnvironment.setPo2(po2Value);
        bloodEnvironment.setPco2(pco2Value);

        System.out.printf("\n%s.po2value - %.2f\n",bloodEnvironment.name, po2Value);
        System.out.printf("\n%s.pco2value - %.2f\n",bloodEnvironment.name, pco2Value);



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
            this.bloodQueue = new LinkedList<>();
            this.bloodQueue.addAll(bloodQueue);
        }


        // Manage blood unit inventory
        public void add(BloodUnit bu) {
            bloodQueue.add(bu);
            if (this.bloodQueue.size() > this.environmentSize) {
                nextEnvironment.add(bloodQueue.remove());
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

        @Override
        public String toString(){

            String returnString = "";
            for (BloodUnit bu: bloodQueue) {
                returnString += bu.toString() + "\n";
            }

            return returnString;
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
            allBloodUnits.add(this);
        }

        @Override
        public String toString(){

            return "ID: " + bloodID + " PO2: " + this.po2  + " PCo2: " + this.pco2 + "...\n";
        }


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
