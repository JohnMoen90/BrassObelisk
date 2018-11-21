package FirstDraft;

public class Heart implements Organ {

    private boolean functioning;
    private static String name = "heart";
    private Blood blood;

    public Heart(Blood blood){
        this.blood = blood;

    }

    public static String getName() {
        return name;
    }


    @Override
    public void bloodExchange() {

    }

    public boolean isFunctioning() {
        return functioning;
    }

    public void setFunctioning(boolean functioning) {
        this.functioning = functioning;
    }
}
