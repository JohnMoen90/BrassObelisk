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

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }


    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getFitnessLevel() {
        return fitnessLevel;
    }

    public void setFitnessLevel(int fitnessLevel) {
        this.fitnessLevel = fitnessLevel;
    }

    public boolean isSmoker() {
        return smoker;
    }

    public void setSmoker(boolean smoker) {
        this.smoker = smoker;
    }
}
