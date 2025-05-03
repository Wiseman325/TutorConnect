package ell.one.tutorlink;

public class TutorModel {
    private String name;
    private String specialization;
    private String rate;
    private String bio;

    public TutorModel() {
        // Required empty constructor for Firebase
    }

    public TutorModel(String name, String specialization, String rate, String bio) {
        this.name = name;
        this.specialization = specialization;
        this.rate = rate;
        this.bio = bio;
    }

    public String getName() {
        return name;
    }

    public String getSpecialization() {
        return specialization;
    }

    public String getRate() {
        return rate;
    }

    public String getBio() {
        return bio;
    }
}
