package FirstDraft;

import java.sql.ResultSet;
import java.util.HashMap;

public class Body {

    // Data from database
    HashMap<String, ResultSet> startValues;

    // Body Values
    private float bodyTemp;

    // Organs & Blood
    private Blood blood;
    private Heart heart;
    private Lungs lungs;
    private HashMap<String, Organ> organs = new HashMap<>();

    // Turn Manager
    private TurnManager turnManager;

    Body(){
        this.startValues = DatabaseIO.getTemplateResultSet();
        blood = new Blood(startValues.get("nutrients"),startValues.get("wastes"));

//        organs.put(Heart.getName(), new Heart(blood));
//        organs.put(Lungs.getName(), new Lungs(blood));
//
//        turnManager = new TurnManager(organ, blood);
    }

}
