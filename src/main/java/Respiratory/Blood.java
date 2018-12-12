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
    private Queue<BloodUnit> tempQueue = new LinkedList<>();
    private ArrayList<BloodEnvironment> allBloodEnvs = new ArrayList<>();

    // Initialize blood environments
    private BloodEnvironment pulmonaryBlood;
    private BloodEnvironment bodyBlood;
    private BloodEnvironment bloodinHeart;

    // Constructor
    Blood(double bodyWeight, double strokeVolume){

        bloodIDCounter = 1;
        int BUCount = (int) Math.round((bodyWeight * 70) / strokeVolume) * 10;

        // Generate blood object for each decilitre of blood
        for (int i = 0; i < BUCount; i++) {
            bloodUnits.add(new BloodUnit());
            allBloodUnits.add(new BloodUnit());
            bloodIDCounter++;
        }
        System.out.println("Total BUs: " + bloodUnits.size());


        // %10 BUs in lungs
        for (int i = 0; i < BUCount * .1; i++) {

            tempQueue.add(bloodUnits.remove());
        }
        pulmonaryBlood = new BloodEnvironment("pulmonary",BodyConfig.LUNG_ENV, tempQueue);
        tempQueue.clear();


        // strokeVolume * 10 in Heart
        for (int i = 0; i < Math.round(strokeVolume / 10); i++) {
            System.out.println("Total BUs: " + bloodUnits.size());
            System.out.println("Temp BUs: " + tempQueue.size());
            tempQueue.add(bloodUnits.remove());
        }
        bloodinHeart = new BloodEnvironment("pulmonary",BodyConfig.LUNG_ENV, tempQueue);
        tempQueue.clear();


        // Rest in body
        for (int i = 0; i < bloodUnits.size(); i++) {
            tempQueue.add(bloodUnits.remove());
        }
        bodyBlood = new BloodEnvironment("body",BodyConfig.BODY_ENV, tempQueue);
        tempQueue.clear();


//        System.out.println("Total BUs: " + bloodUnits.size());
//        System.out.println("Body BUs: " + bodyBlood.getSize());

        // Set circulatory loop
        bloodinHeart.setNextEnvironment(bodyBlood);
        bodyBlood.setNextEnvironment(pulmonaryBlood);  // Here I skip the fact that the blood goes through the heart before the lungs
        pulmonaryBlood.setNextEnvironment(bloodinHeart);


        // Save environments into list

        allBloodEnvs.add(bloodinHeart);
        allBloodEnvs.add(bodyBlood);
        allBloodEnvs.add(pulmonaryBlood);

        // Set bloodEnvironment size limits
        bloodinHeart.setEnvironmentSize(bloodinHeart.getSize());
        bodyBlood.setEnvironmentSize(bodyBlood.getSize());
        pulmonaryBlood.setEnvironmentSize(pulmonaryBlood.getSize());

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
        int BUsPerPump = bloodinHeart.getSize();

        for (int i = 0; i < BUsPerPump; i++) {
            bloodinHeart.nextEnvironment.add(bloodinHeart.bloodQueue.remove());
        }

    }


    // Set Environmental Variables
    public void setBloodEnvironments(){

    }


    //


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

        BloodEnvironment(String name, int bloodEnvID, Queue<BloodUnit> bloodQueue){
            this.name = name;
            this.bloodEnvID = bloodEnvID;
            this.bloodQueue = bloodQueue;
        }



        // Manage blood unit inventory
        public void add(BloodUnit bu) {
            bloodQueue.add(bu);

            //
//            if (this.bloodQueue.size() > this.environmentSize) {
//                nextEnvironment.bloodQueue.add(bloodQueue.remove());
//            }
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
