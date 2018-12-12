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
    static int HEART_ENV = 0;
    static int LUNG_ENV = 1;
    static int BODY_ENV = 2;

    static double bodyCo2 = 45;
    static double bodyo2 = 60;
    static double lungCo2 = 40;
    static double lungo2 = 100;

}
