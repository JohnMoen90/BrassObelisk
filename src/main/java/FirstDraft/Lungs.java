package FirstDraft;

public class Lungs {

    Blood blood;
    boolean functioning;

    int breathsPerMinute;
    double o2perturn;
    double co2perturn;


    Lungs(Blood blood) {
        this.blood = blood;
    }

    public void bloodExchange() {

        calculatePerTurn();

        // carbon dioxide
        double co2Saturation = blood.getCo2Saturation();
        blood.setCo2Saturation(co2Saturation + co2perturn);

        // oxygen
        double o2Saturation = blood.getO2Saturation();
        blood.setO2Saturation(o2Saturation + o2perturn);
    }

    private void calculatePerTurn() {

    }

}
