package FirstDraft;

import java.sql.*;
import java.util.HashMap;

public class DatabaseIO {

    private static String DB_URL = "jdb:sqlite:databases/DefaultTemplate.sqlite";
    private static String[] TABLE_NAMES = {"body", "nutrients", "wastes"};

    public static HashMap<String, ResultSet> getTemplateResultSet() {

        String getResultSetSql = "SELECT * FROM ?";
        HashMap<String, ResultSet> resultSets = new HashMap<>();


        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement getResultSetPS = conn.prepareStatement(getResultSetSql)) {

            for (String tableName : TABLE_NAMES) {
                getResultSetPS.setString(1, tableName);
                resultSets.put(tableName, getResultSetPS.executeQuery());
            }

        }
        catch (SQLException sqle){
            System.out.println("Something happened...");
            sqle.printStackTrace();
        }

        return resultSets;
    }


}
