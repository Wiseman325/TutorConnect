package ell.one.tutorlink;

public class HelperClass {

    // Fields
    private String name, email, phoneNo, bio, specialization, rate;
    private String age, gender, interest; // New Student Fields

    // ✅ Getters and Setters for Existing Fields
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNo() { return phoneNo; }
    public void setPhoneNo(String phoneNo) { this.phoneNo = phoneNo; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public String getRate() { return rate; }
    public void setRate(String rate) { this.rate = rate; }

    public String getAge() { return age; }
    public void setAge(String age) { this.age = age; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getInterest() { return interest; }
    public void setInterest(String interest) { this.interest = interest; }

    // ✅ Default Constructor (Required for Firebase)
    public HelperClass() { }

    // ✅ Tutor Constructor
    public HelperClass(String name, String email, String phoneNo, String bio, String specialization, String rate) {
        this.name = name;
        this.email = email;
        this.phoneNo = phoneNo;
        this.bio = bio;
        this.specialization = specialization;
        this.rate = rate;
    }

    // ✅ Student Constructor
    public HelperClass(String name, String email, String phoneNo, String bio, String age, String gender, String interest) {
        this.name = name;
        this.email = email;
        this.phoneNo = phoneNo;
        this.bio = bio;
        this.age = age;
        this.gender = gender;
        this.interest = interest;
    }

    // ✅ Full Constructor (if needed for complete object creation)
    public HelperClass(String name, String email, String phoneNo, String studentNo, String username,
                       String bio, String specialization, String rate, String age, String gender, String interest) {
        this.name = name;
        this.email = email;
        this.phoneNo = phoneNo;
        this.bio = bio;
        this.specialization = specialization;
        this.rate = rate;
        this.age = age;
        this.gender = gender;
        this.interest = interest;
    }
}
