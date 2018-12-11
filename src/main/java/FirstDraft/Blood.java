package FirstDraft;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Blood {

    // Init value result sets
    ResultSet nutrients;
    ResultSet wastes;



    /**
     * Healthy individuals at sea level usually exhibit oxygen saturation values between 96% and 99%,
     * and should be above 94%. At 1,600 meters' altitude (about one mile high) oxygen saturation
     * should be above 92%. An SaO2 (arterial oxygen saturation) value below 90% causes hypoxia
     * (which can also be caused by anemia).
     */
    private double o2Saturation;

    /**
     * In a life-threatening situation, CO2 will increase in concentration from 350 parts per million
     * to about 4% (a hundredfold increase) , whereas oxygen would have to decrease from 21% to below 10%,
     * only a 50% change.
     */
    private double co2Saturation;

    Blood(ResultSet nutrients, ResultSet wastes) {
        this.nutrients = nutrients;
        this.wastes = wastes;

        bloodInit();


        o2Saturation = .970;    // Percentage blood saturation
        co2Saturation = 0.00035; //PPMillion
    }

    private void bloodInit() {
        try {
            while (nutrients.next()) {

            }
        }
        catch (SQLException sqle) {
            System.out.println("Something happened...");
            sqle.printStackTrace();
        }
    }

    public double getO2Saturation() {
        return o2Saturation;
    }

    public void setO2Saturation(double o2Saturation) {
        this.o2Saturation = o2Saturation;
    }

    public double getCo2Saturation() {
        return co2Saturation;
    }

    public void setCo2Saturation(double co2Saturation) {
        this.co2Saturation = co2Saturation;
    }

}
