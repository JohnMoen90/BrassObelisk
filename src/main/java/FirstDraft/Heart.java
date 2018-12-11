package FirstDraft;

public class Heart {

    private boolean functioning;
    private static String name = "heart";
    private Blood blood;

    public Heart(Blood blood){
        this.blood = blood;

    }

//    @Override
//    public static String getName() {
//        return name;
//    }


//    @Override
//    public void bloodExchange() {
//
//    }

    public boolean isFunctioning() {
        return functioning;
    }

    public void setFunctioning(boolean functioning) {
        this.functioning = functioning;
    }
}
