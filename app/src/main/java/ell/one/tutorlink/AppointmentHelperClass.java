package ell.one.tutorlink;

public class AppointmentHelperClass {
    String name, service, visit_reason, symptoms_experienced, blood_type, key;

    public AppointmentHelperClass() {
    }

    public AppointmentHelperClass(String name, String service, String blood_type, String visit_reason, String symptoms_experienced) {
        this.name = name;
        this.service = service;
        this.blood_type = blood_type;
        this.visit_reason = visit_reason;
        this.symptoms_experienced = symptoms_experienced;
    }

    public String getBlood_type() {
        return blood_type;
    }

    public void setBlood_type(String blood_type) {
        this.blood_type = blood_type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getVisit_reason() {
        return visit_reason;
    }

    public void setVisit_reason(String visit_reason) {
        this.visit_reason = visit_reason;
    }

    public String getSymptoms_experienced() {
        return symptoms_experienced;
    }

    public void setSymptoms_experienced(String symptoms_experienced) {
        this.symptoms_experienced = symptoms_experienced;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
