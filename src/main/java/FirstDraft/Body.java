package FirstDraft;

import java.util.HashMap;

public class Body {

    private Blood blood;
    private Heart heart;
    private Lungs lungs;

    private HashMap<String, Organ> organs = new HashMap<>();

    private TurnManager turnManager;

    Body(){
        blood = new Blood();

        organs.put(Heart.getName(), new Heart(blood));
        organs.put(Lungs.getName(), new Lungs(blood));

        turnManager = new TurnManager(organ, blood);
    }

}
