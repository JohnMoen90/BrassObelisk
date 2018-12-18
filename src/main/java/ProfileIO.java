import java.sql.*;
import java.util.ArrayList;

/**
 * Handles all database interaction
 */
public class ProfileIO {

    // Database Location
    static final String DB_URL = "jdbc:sqlite:databases/profiles";


    /**
     * Gets all the profiles and returns an arrayList of them
     * @return
     */
    static public ArrayList<Profile> getAllProfiles() {

        final String getAllSql = "SELECT * FROM user_profiles ORDER BY profile_name";
        ArrayList<Profile> allProfiles = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(DB_URL);
             PreparedStatement statement = connection.prepareStatement(getAllSql)) {

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                allProfiles.add(new Profile(
                        resultSet.getString("profile_name"),
                        resultSet.getInt("age"),
                        resultSet.getInt("weight"),
                        resultSet.getInt("fitness_level"),
                        resultSet.getBoolean("smoker")));
            }


        } catch (SQLException sqle) {
            System.out.println("Something happened...");
            sqle.printStackTrace();
        }

        return allProfiles;

    }

    /**
     * Adds new profile to database
     * @param profile to add
     * @return boolean to check if it worked
     */
    static public boolean addNewProfile(Profile profile) {

        final String addProfileSql = "INSERT INTO user_profiles VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement addProfilePS = conn.prepareStatement(addProfileSql)) {
            addProfilePS.setString(1, profile.getProfileName());
            addProfilePS.setInt(2, profile.getAge());
            addProfilePS.setInt(3, profile.getWeight());
            addProfilePS.setInt(4, profile.getFitnessLevel());
            addProfilePS.setBoolean(5, profile.isSmoker());
            addProfilePS.execute();

        } catch (SQLException sqle) {

            if (sqle.getMessage().contains("unique constraint")) {
                System.out.println("This product is already in the Database!");
                return false;
            }

            System.out.println("Something happened...");
            sqle.printStackTrace();
            return false;
        }

        return true;

    }

    /**
     * Deletes the selected profile
     * @param profile to delete
     * @return boolean to check if it worked
     */
    public static boolean deleteProfile(Profile profile) {

            String deleteProfileSql = "DELETE FROM user_profiles WHERE profile_name LIKE ?";

            try (Connection conn = DriverManager.getConnection(DB_URL) ;
                 PreparedStatement deleteProfilePS = conn.prepareStatement(deleteProfileSql)) {

                deleteProfilePS.setString(1, profile.getProfileName());
                return deleteProfilePS.execute();

            }

            catch (SQLException sqle) {
                System.out.println("Something happened...");
                sqle.printStackTrace();
            }

            return false;


    }
}
