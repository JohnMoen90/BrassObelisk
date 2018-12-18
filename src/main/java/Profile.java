/**
 * Simple profile class to store information from database
 */
public class Profile {

    private String profileName;
    private int age;
    private int weight;
    private int fitnessLevel;
    private boolean smoker;

    Profile(String profileName, int age, int weight, int fitnessLevel, boolean smoker){

        this.profileName = profileName;
        this.age = age;
        this.weight = weight;
        this.fitnessLevel = fitnessLevel;
        this.smoker = smoker;
    }


    public String getProfileName() {
        return profileName;
    }


    public int getAge() {
        return age;
    }

    public int getWeight() {
        return weight;
    }

    public int getFitnessLevel() {
        return fitnessLevel;
    }


    public boolean isSmoker() {
        return smoker;
    }

}
