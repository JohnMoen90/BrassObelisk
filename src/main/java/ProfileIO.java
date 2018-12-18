import java.sql.*;
import java.util.ArrayList;

public class ProfileIO {

    static final String DB_URL = "jdbc:sqlite:databases/profiles.sqlite";


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
                        resultSet.getInt("fitness_lvl"),
                        resultSet.getBoolean("smoker")));
            }


        } catch (SQLException sqle) {
            System.out.println("Something happened...");
            sqle.printStackTrace();
        }

        return allProfiles;

    }

    public boolean addNewProfile(Profile profile) {

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
//        }
//
//        public boolean editProduct(String productToEdit, int changeToQuantity) {
//
//            final String editProductSql = "UPDATE inventory SET quantity = ? WHERE name like ? ";
//
//            try (Connection conn = DriverManager.getConnection(DBConfig.db_url);
//                 PreparedStatement editProductPS = conn.prepareStatement(editProductSql)) {
//
//                editProductPS.setInt(1, changeToQuantity);
//                editProductPS.setString(2,productToEdit);
//                return editProductPS.execute();
//
//            }
//
//            catch (SQLException sqle) {
//                System.out.println("Something happened");
//                sqle.printStackTrace();
//
//            }
//
//            return false;
//        }
//
//        public boolean deleteProduct(String productToDelete) {
//
//            // Create the prepared string
//            String deleteProductSql = "DELETE FROM inventory WHERE name LIKE ?";
//
//            try (Connection conn = DriverManager.getConnection(DBConfig.db_url) ;
//                 PreparedStatement deleteProductPS = conn.prepareStatement(deleteProductSql)) {
//
//                deleteProductPS.setString(1, productToDelete);
//                return deleteProductPS.execute();
//
//            }
//
//            catch (SQLException sqle) {
//                System.out.println("Something happened...");
//                sqle.printStackTrace();
//            }
//
//            return false;
//        }

//
//
    }
}
