package FirstDraft;

public class TurnManager {


    private final Organ[] organs;
    private final Blood blood;

    TurnManager(Organ[] organs, Blood blood) {

        this.organs = organs;
        this.blood = blood;
    }


    public void manageTurn(){


        for (Organ organ : organs) {
            if (organ.isFunctioning()){}


        }


    }

}
