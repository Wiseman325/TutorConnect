package ell.one.tutorlink.models;

public class BookingModel {
    private String bookingId;
    private String tutorId;
    private String tutorName;
    private String date;
    private String startTime;
    private String endTime;
    private String status;

    public BookingModel() {}

    public BookingModel(String bookingId, String tutorId, String tutorName, String date, String startTime, String endTime, String status) {
        this.bookingId = bookingId;
        this.tutorId = tutorId;
        this.tutorName = tutorName;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
    }

    public String getBookingId() {
        return bookingId;
    }

    public String getTutorId() {
        return tutorId;
    }

    public String getTutorName() {
        return tutorName;
    }

    public String getDate() {
        return date;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getStatus() {
        return status;
    }
}
