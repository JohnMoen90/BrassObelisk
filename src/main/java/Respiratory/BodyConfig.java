package Respiratory;

public class BodyConfig {

    // Basic starting values for simulation, templates will be loaded here
    static double bodyWeight = 70.0; // kg
    static double strokeVolume = 70.0; // ml
    static double totalLungCapacity = 6.0; // L

    // Derived Stats
//    static double bloodVolume = 70.00 * bodyWeight; // ml
    static double alveolarVentilation = totalLungCapacity;


    // Blood environment
    static int HEART_ENV_ID = 0;
    static int ART_ENV_ID = 1;
    static int DIFFZONE_ENV_ID = 2;
    static int VEN_ENV_ID = 3;
    static int LUNG_ENV_ID = 4;

    static double diffZoneCo2 = 45;
    static double diffZoneO2 = 60;
    static double lungCo2 = 40;
    static double lungO2 = 100;

}
