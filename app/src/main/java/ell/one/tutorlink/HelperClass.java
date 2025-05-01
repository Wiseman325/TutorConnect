package ell.one.tutorlink;

public class HelperClass {

    String name, email,phoneNo, studentNo, username;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getStudentNo() {
        return studentNo;
    }

    public void setStudentNo(String studentNo) {
        this.studentNo = studentNo;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public HelperClass(String name, String email, String phoneNo, String studentNo, String username) {
        this.name = name;
        this.email = email;
        this.phoneNo = phoneNo;
        this.studentNo = studentNo;
        this.username = username;
    }

    public HelperClass() {
    }
}
