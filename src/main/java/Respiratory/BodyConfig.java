package Respiratory;

public class BodyConfig {

    // Basic starting values for simulation, templates will be loaded here
    static double bodyWeight = 70.0; // kg
    static double strokeVolume = 70.0; // ml

    // Derived Stats
//    static double bloodVolume = 70.00 * bodyWeight; // ml

    // Blood environment variables
    static final int HEART_ENV_ID = 0;
    static final int ART_ENV_ID = 1;
    static final int DIFFZONE_ENV_ID = 2;
    static final int VEIN_ENV_ID = 3;
    static final int LUNG_ENV_ID = 4;

    static double secO2Consumption = .4167; // ml/sec
    static double tickO2Consumption = secO2Consumption * 10; // ml/sec

    static double diffusionZone02perBU = 80;
    static double diffusionZoneC02perBU = 540;

    // Respiratory Variables
    static double environmentO2 = 20.93; //% oxygen
    static double environmentCO2 = 0.05; // % CO2

    static double totalLungCapacity = 6.0; // L
    static double conductionZoneCapacity = totalLungCapacity * .8;
    static double alveolarZoneCapacity = totalLungCapacity * .2;

    static double totalLung02 = (totalLungCapacity * environmentO2) * 1000;
    static double conductionZoneO2 = totalLung02 * .8;
    static double alveolarZoneO2 = totalLung02 * .2;

    static double totalLungC02 = (totalLungCapacity * environmentO2) * 1000;
    static double conductionZoneCO2 = totalLung02 * .8;
    static double alveolarZoneCO2 = totalLung02 * .2;

    // Blood Unit variables
    static double bloodUnitMax02Saturation = 203; //ml/L


}
