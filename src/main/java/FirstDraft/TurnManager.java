package FirstDraft;

public class TurnManager {

    private Blood blood;
    private Heart heart;
    private Lungs lungs;

    public void turnManager() {
        blood = new Blood();
        heart = new Heart(blood);
        lungs = new Lungs(blood);

    }



    public void manageTurn(){
        if (heart.functioning) {

        }


    }

}
