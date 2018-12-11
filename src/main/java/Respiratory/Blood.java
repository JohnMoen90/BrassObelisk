package Respiratory;

import java.util.ArrayList;

public class Blood {

    // Blood counter for blood unit IDs
    private static int bloodIDCounter;

    // Initialize blood unit lists
    private ArrayList<BloodUnit> bloodUnits = new ArrayList<>();
    private ArrayList<BloodUnit> allBloodUnits = new ArrayList<>();
    private ArrayList<BloodEnvironment> allBloodEnvs = new ArrayList<>();

    // Initialize blood environments
    private BloodEnvironment bodyBlood = new BloodEnvironment("body",BodyConfig.BODY_ENV);
    private BloodEnvironment pulmonaryBlood = new BloodEnvironment("pulmonary",BodyConfig.LUNG_ENV);
    private BloodEnvironment bloodinHeart = new BloodEnvironment("heart",BodyConfig.HEART_ENV);

    // Constructor
    Blood(double bodyWeight, double strokeVolume){

        bloodIDCounter = 1;
        int BUCount = (int) Math.round((bodyWeight * 70) / strokeVolume) * 10;

        // Generate blood object for each decaliter of blood
        for (int i = 0; i < BUCount; i++) {
            bloodUnits.add(new BloodUnit());
            allBloodUnits.add(new BloodUnit());
            bloodIDCounter++;
        }

        // Set circulatory loop
        bloodinHeart.setNextEnvironment(bodyBlood);
        bodyBlood.setNextEnvironment(pulmonaryBlood);  // Here I skip the fact that the blood goes through the heart before the lungs
        pulmonaryBlood.setNextEnvironment(bloodinHeart);


        // Save environments into list
        allBloodEnvs.add(bloodinHeart);
        allBloodEnvs.add(bodyBlood);
        allBloodEnvs.add(pulmonaryBlood);

        // %10 BUs in lungs
        for (int i = 0; i < BUCount * .1; i++) {
            pulmonaryBlood.add(bloodUnits.remove(0));
        }

        // strokeVolume * 10 in Heart
        for (int i = 0; i < Math.round(strokeVolume * 10); i++) {
            bloodinHeart.add(bloodUnits.remove(0));
        }

        // Rest in body
        for (int i = 0; i < bloodUnits.size(); i++) {
            bodyBlood.add(bloodUnits.remove(0));
        }

        // Set bloodEnvironment size limits
        bloodinHeart.setEnvironmentSize(bloodinHeart.getSize());
        bodyBlood.setEnvironmentSize(bodyBlood.getSize());
        pulmonaryBlood.setEnvironmentSize(pulmonaryBlood.getSize());

    }


    public double getReading(int bloodEnvID, String reading){
        for (BloodEnvironment bloodEnv : allBloodEnvs) {
            if (bloodEnv.getBloodEnvID() == bloodEnvID) {
                if (reading.equals("po2")) {
                    return bloodEnv.bloodQueue.get(0).po2;
                } else if (reading.equals("o2")) {
                    return bloodEnv.bloodQueue.get(0).pco2;
                }
            }
        }
        return 0.0;
    }

    //Circulate blood
    public void circulate(){
        int BUsPerPump = bloodinHeart.getSize();

        for (int i = 0; i < BUsPerPump; i++) {
            bloodinHeart.nextEnvironment.add(bloodinHeart.pop());
        }

    }


    // Set Environmental Variables
    public void setBloodEnvironments(){

    }


    //


    // Blood Environment
    private class BloodEnvironment{

        // Research Data types, Queue
        private ArrayList<BloodUnit> bloodQueue = new ArrayList<>();
        private BloodEnvironment nextEnvironment;
        private int environmentSize;
        private String name;
        private int bloodEnvID;
        private double po2;
        private double pco2;

        BloodEnvironment(String name, int bloodEnvID){
            this.name = name;
            this.bloodEnvID = bloodEnvID;
        }

        // Manage blood unit inventory
        public void add(BloodUnit bu) {
            this.bloodQueue.add(bu);
            if (this.bloodQueue.size() != this.environmentSize) {
                nextEnvironment.add(pop());
            }
        }


        public BloodUnit pop() {
            return this.bloodQueue.remove(0);
        }

        public void push(BloodUnit bu) {
            this.bloodQueue.add(bu);
        }

        public int getSize(){
            return this.bloodQueue.size();
        }

        public void printBloodQueue(){
            String pstring = name + "\n----------";
            for (int i = 0; i < getSize(); i++) {
                pstring += bloodQueue.get(i).toString();
            }

            System.out.println(pstring);

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
